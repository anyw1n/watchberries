package alexeyzhizhensky.watchberries.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PriceDao {

    @Query("SELECT * FROM prices WHERE product_sku = :sku ORDER BY datetime")
    suspend fun getBySku(sku: Int): List<PriceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prices: List<PriceEntity>)

    @Query("DELETE FROM prices WHERE product_sku = :sku")
    suspend fun deleteAllWithSku(sku: Int)
}
