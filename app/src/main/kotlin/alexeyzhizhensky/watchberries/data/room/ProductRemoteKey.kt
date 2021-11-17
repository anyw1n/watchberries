package alexeyzhizhensky.watchberries.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_remote_keys")
data class ProductRemoteKey(
    @PrimaryKey val sku: Int,
    val prevPage: Int?,
    val nextPage: Int?
)
