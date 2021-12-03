package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.data.room.ProductDao
import alexeyzhizhensky.watchberries.network.SkuRequest
import alexeyzhizhensky.watchberries.network.WbApiService
import alexeyzhizhensky.watchberries.utils.suspend
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    currencySettings: CurrencySettings,
    private val userRepository: UserRepository,
    private val remoteMediator: ProductsRemoteMediator,
    private val service: WbApiService,
    private val productDao: ProductDao
) {

    private val currencyFlow = currencySettings.stateFlow

    @OptIn(ExperimentalPagingApi::class)
    fun observePaginated() = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            maxSize = MAX_SIZE
        ),
        remoteMediator = remoteMediator
    ) {
        productDao.observePaginated()
    }.flow

    fun getProductFlow(sku: Int) = productDao.getBySku(sku)

    suspend fun updateProduct(product: Product) {
        service.getProduct(product.sku, currencyFlow.value).suspend().apply {
            productDao.update(copy(id = product.id))
        }
    }

    suspend fun addSku(sku: Int) {
        val user = userRepository.getUser()
        service.addSku(user.id, user.key, SkuRequest(sku)).suspend().apply {
            checkCode()
        }
    }

    suspend fun deleteSku(sku: Int) {
        val user = userRepository.getUser()
        service.deleteSku(user.id, user.key, SkuRequest(sku)).suspend().apply {
            checkCode()
        }
    }

    private companion object {

        const val PAGE_SIZE = 20
        const val MAX_SIZE = PAGE_SIZE * 10
    }
}
