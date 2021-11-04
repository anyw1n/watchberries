package alexeyzhizhensky.watchberries.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class User(
    @PrimaryKey val id: Int,
    val token: String,
    val key: UUID,
    @ColumnInfo(name = "last_sync") val lastSync: LocalDateTime,
    val skus: List<Int>
)
