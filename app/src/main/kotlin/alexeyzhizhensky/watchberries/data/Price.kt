package alexeyzhizhensky.watchberries.data

import java.time.LocalDateTime

data class Price(
    val datetime: LocalDateTime,
    val value: Float,
    val currency: Currency
) {

    enum class Currency {
        RUB, BYN;

        companion object {

            val values = values()
        }
    }
}
