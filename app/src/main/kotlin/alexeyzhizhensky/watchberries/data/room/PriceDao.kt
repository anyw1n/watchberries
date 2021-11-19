package alexeyzhizhensky.watchberries.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceDao {

    @Query("SELECT * FROM prices WHERE product_sku = :sku ORDER BY datetime")
    fun getBySku(sku: Int): Flow<List<PriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prices: List<PriceEntity>)

    @Query("DELETE FROM prices WHERE product_sku = :sku")
    suspend fun deleteAllWithSku(sku: Int)
}
