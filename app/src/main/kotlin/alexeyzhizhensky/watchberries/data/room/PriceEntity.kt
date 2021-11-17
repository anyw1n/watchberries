package alexeyzhizhensky.watchberries.data.room

import alexeyzhizhensky.watchberries.data.Price
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prices",
    foreignKeys = [ForeignKey(
        entity = Product::class,
        parentColumns = ["sku"],
        childColumns = ["product_sku"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(
        value = ["product_sku", "datetime"],
        unique = true
    )]
)
data class PriceEntity(
    @ColumnInfo(name = "product_sku") val productSku: Int,
    @Embedded val price: Price,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
