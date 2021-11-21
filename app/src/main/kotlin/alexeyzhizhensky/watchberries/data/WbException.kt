package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.R
import android.content.Context
import androidx.annotation.StringRes
import retrofit2.HttpException

sealed class WbException(
    @StringRes val messageRes: Int,
    override val cause: Throwable?,
    private val pleaseTryLater: Boolean = true
) : Exception(cause) {

    fun getMessage(context: Context): String {
        val message = context.getString(messageRes)
        return if (pleaseTryLater) context.getString(
            R.string.please_try_later,
            message
        ) else message
    }

    class InternetConnection(cause: Throwable) :
        WbException(R.string.internet_connection_exception, cause)

    class OutOfBounds(cause: Throwable) : WbException(R.string.index_out_of_bounds, cause, false)

    object InvalidSku : WbException(R.string.invalid_sku, null, false)

    object ProductNotFound : WbException(R.string.product_not_found, null, false)

    class Unknown(cause: Throwable) : WbException(R.string.unknown_error, cause, false)

    sealed class Http(
        @StringRes messageRes: Int,
        override val cause: HttpException,
        pleaseTryLater: Boolean = true
    ) : WbException(messageRes, cause, pleaseTryLater) {

        class PageNotFound(cause: HttpException) : Http(R.string.page_not_found_exception, cause)

        class Unavailable(cause: HttpException) : Http(R.string.unavailable_exception, cause)

        class Internal(cause: HttpException) : Http(R.string.internal_server_error, cause)

        class Other(cause: HttpException) : Http(R.string.unknown_server_error, cause)
    }

    sealed class Server(
        @StringRes messageRes: Int,
        pleaseTryLater: Boolean = true
    ) : WbException(messageRes, null, pleaseTryLater) {

        object Response : Server(R.string.server_response_exception)

        object Parse : Server(R.string.server_parse_error)

        object AlreadyAdded : Server(R.string.already_added_error, false)

        object Database : Server(R.string.server_db_error)
    }
}
