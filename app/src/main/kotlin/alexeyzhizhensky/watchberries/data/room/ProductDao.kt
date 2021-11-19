package alexeyzhizhensky.watchberries.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    fun observePaginated(): PagingSource<Int, Product>

    @Query("SELECT * FROM products  WHERE sku = :sku LIMIT 1")
    suspend fun getBySku(sku: Int): Product

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}
