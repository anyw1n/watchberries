package alexeyzhizhensky.watchberries.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.util.UUID

class Converters {

    @TypeConverter
    fun uuidToString(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun stringToUuid(string: String): UUID = UUID.fromString(string)

    @TypeConverter
    fun localDateTimeToString(localDateTime: LocalDateTime): String = localDateTime.toString()

    @TypeConverter
    fun stringToLocalDateTime(string: String): LocalDateTime = LocalDateTime.parse(string)

    @TypeConverter
    fun intListToString(list: List<Int>): String = Gson().toJson(list)

    @TypeConverter
    fun stringToIntList(string: String): List<Int> {
        val typeToken = object : TypeToken<List<Int>>() {}
        return Gson().fromJson(string, typeToken.type)
    }
}
