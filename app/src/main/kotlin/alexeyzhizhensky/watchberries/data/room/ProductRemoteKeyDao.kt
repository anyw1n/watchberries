package alexeyzhizhensky.watchberries.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ProductRemoteKeyDao {

    @Query("SELECT * FROM product_remote_keys WHERE sku = :sku LIMIT 1")
    suspend fun getBySku(sku: Int): ProductRemoteKey?

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(remoteKeys: List<ProductRemoteKey>)

    @Query("DELETE FROM product_remote_keys")
    suspend fun deleteAll()
}
