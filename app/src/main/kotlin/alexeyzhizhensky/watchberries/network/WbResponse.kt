package alexeyzhizhensky.watchberries.network

import alexeyzhizhensky.watchberries.data.WbException

data class WbResponse(
    val code: Int,
    val message: String
) {

    fun checkCode() {
        when (code) {
            Codes.PARSE_ERROR.ordinal -> throw WbException.Server.Parse
            Codes.ALREADY_ADDED.ordinal -> throw WbException.Server.AlreadyAdded
            Codes.DB_ERROR.ordinal -> throw WbException.Server.Database
        }
    }

    enum class Codes {
        OK, PARSE_ERROR, ALREADY_ADDED, DB_ERROR
    }
}
