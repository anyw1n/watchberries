package alexeyzhizhensky.watchberries.data

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    fun <T> get(key: String, defaultValue: T, clazz: Class<T>): T =
        sharedPrefs.getString(key, null)?.let { gson.fromJson(it, clazz) }
            ?: defaultValue.also { set(key, it) }

    fun <T> set(key: String, value: T) = sharedPrefs.edit { putString(key, gson.toJson(value)) }

    companion object {

        private const val SHARED_PREFS_NAME = "alexeyzhizhensky.watchberries.preferences"

        fun <T> get(context: Context, key: String, defaultValue: T, clazz: Class<T>): T =
            with(context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)) {
                val gson = Gson()
                getString(key, null)?.let { gson.fromJson(it, clazz) }
                    ?: defaultValue.also { edit { putString(key, gson.toJson(it)) } }
            }
    }
}
