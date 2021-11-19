package alexeyzhizhensky.watchberries.data.room

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.Price
import android.graphics.Color
import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(
        value = ["sku"],
        unique = true
    )]
)
data class Product(
    @PrimaryKey val id: Int = 0,
    val sku: Int,
    val brand: String,
    val title: String,
    @Embedded val lastPrice: Price,
    val trend: Trend
) {

    @Ignore
    val imageUrl = IMAGE_URL.format(sku / GROUP_SIZE, sku)

    val shopUri: Uri get() = Uri.parse(SHOP_URL.format(sku))

    enum class Trend(
        val drawableId: Int,
        val color: Int
    ) {

        DOWNWARD(R.drawable.ic_baseline_trending_down_24, Color.GREEN),
        STABLE(R.drawable.ic_baseline_trending_flat_24, Color.BLACK),
        UPWARD(R.drawable.ic_baseline_trending_up_24, Color.RED);
    }

    private companion object {

        const val GROUP_SIZE = 10000
        const val IMAGE_URL = "https://images.wbstatic.net/big/new/%d0000/%d-1.jpg"
        const val SHOP_URL = "https://by.wildberries.ru/catalog/%d/detail.aspx?targetUrl=WP"
    }
}
