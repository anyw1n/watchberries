package alexeyzhizhensky.watchberries.data

import java.time.LocalDateTime

data class Price(
    val datetime: LocalDateTime,
    val value: Int
)
