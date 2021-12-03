package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.data.room.PriceDao
import alexeyzhizhensky.watchberries.data.room.PriceEntity
import alexeyzhizhensky.watchberries.data.room.WbDatabase
import alexeyzhizhensky.watchberries.network.WbApiService
import alexeyzhizhensky.watchberries.utils.suspend
import androidx.room.withTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepository @Inject constructor(
    currencySettings: CurrencySettings,
    private val service: WbApiService,
    private val priceDao: PriceDao,
    private val db: WbDatabase
) {

    private val currencyFlow = currencySettings.stateFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPricesFlow(sku: Int) = priceDao.getBySku(sku).mapLatest { list ->
        list.map { it.price }
    }

    suspend fun updatePrices(sku: Int) {
        val prices = service.getPrices(sku, currencyFlow.value).suspend()

        db.withTransaction {
            priceDao.deleteAllWithSku(sku)
            priceDao.insertAll(prices.map { PriceEntity(sku, it) })
        }
    }
}
