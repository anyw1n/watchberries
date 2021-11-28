package alexeyzhizhensky.watchberries.utils

import alexeyzhizhensky.watchberries.data.WbException
import android.content.Context
import android.text.format.DateUtils
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.paging.PagingState
import com.github.mikephil.charting.charts.LineChart
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.suspend(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener(continuation::resume)
    addOnFailureListener {
        val exception = when (it) {
            is IOException -> WbException.InternetConnection(it)
            else -> WbException.Unknown(it)
        }
        continuation.resumeWithException(exception)
    }
}

fun <Key : Any, Value : Any> PagingState<Key, Value>.anchorItemOrNull() =
    anchorPosition?.let(::closestItemToPosition)

inline fun <reified T> GsonBuilder.registerDeserializer(
    crossinline deserializer: (JsonElement) -> T
): GsonBuilder {
    registerTypeAdapter(T::class.java, JsonDeserializer { json, _, _ -> deserializer(json) })
    return this
}

inline fun <reified T> GsonBuilder.registerSerializer(
    crossinline serializer: (T) -> String
): GsonBuilder {
    registerTypeAdapter(
        T::class.java,
        JsonSerializer<T> { src, _, _ -> JsonPrimitive(serializer(src)) }
    )
    return this
}

fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.toast(@StringRes textRes: Int) =
    Toast.makeText(this, textRes, Toast.LENGTH_SHORT).show()

suspend fun <T : Any> Call<T>.suspend(): T {
    val response = suspendResponse()

    if (!response.isSuccessful) throw HttpException(response).toWbException()

    return response.body() ?: throw WbException.Server.Response
}

suspend fun <T : Any> Call<T>.suspendResponse(): Response<T> = withContext(Dispatchers.IO) {
    suspendCancellableCoroutine {
        it.invokeOnCancellation { cancel() }
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                it.resume(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val wbException = when (t) {
                    is ConnectException -> WbException.InternetConnection(t)
                    else -> WbException.Unknown(t)
                }
                it.resumeWithException(wbException)
            }
        })
    }
}

fun HttpException.toWbException() = when (code()) {
    HttpURLConnection.HTTP_NOT_FOUND -> WbException.Http.PageNotFound(this)
    HttpURLConnection.HTTP_UNAVAILABLE -> WbException.Http.Unavailable(this)
    HttpURLConnection.HTTP_INTERNAL_ERROR -> WbException.Http.Internal(this)
    else -> WbException.Http.Other(this)
}

fun getRelativeDateTime(context: Context, localDateTime: LocalDateTime): CharSequence =
    DateUtils.getRelativeDateTimeString(
        context,
        localDateTime.toEpochSecond(ZoneOffset.UTC) * DateUtils.SECOND_IN_MILLIS,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.WEEK_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    )

fun LineChart.setYAxisMinimum(min: Float) {
    axisLeft.axisMinimum = min
    axisRight.axisMinimum = min
}

fun LineChart.resetYAxisMinimum() {
    axisLeft.resetAxisMinimum()
    axisRight.resetAxisMinimum()
}

fun Context.getColorFromTheme(@AttrRes colorAttr: Int) = TypedValue().also {
    theme.resolveAttribute(colorAttr, it, true)
}.data
