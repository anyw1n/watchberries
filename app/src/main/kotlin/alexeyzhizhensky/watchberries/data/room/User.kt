package alexeyzhizhensky.watchberries.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val token: String,
    val key: UUID
)
