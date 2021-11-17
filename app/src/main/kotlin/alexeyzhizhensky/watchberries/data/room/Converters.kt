package alexeyzhizhensky.watchberries.data.room

import androidx.room.TypeConverter
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun localDateTimeToString(localDateTime: LocalDateTime): String = localDateTime.toString()

    @TypeConverter
    fun stringToLocalDateTime(string: String): LocalDateTime = LocalDateTime.parse(string)
}
