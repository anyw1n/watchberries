package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.Price
import alexeyzhizhensky.watchberries.data.PriceRepository
import alexeyzhizhensky.watchberries.data.ProductRepository
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.data.room.Product
import alexeyzhizhensky.watchberries.network.WbConnectivityManager
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    connectivityManager: WbConnectivityManager,
    private val productRepository: ProductRepository,
    private val priceRepository: PriceRepository
) : BaseViewModel<ProductDetailViewModel.Event>() {

    private val _productFlow = MutableStateFlow<Product?>(null)
    val productFlow = _productFlow.asStateFlow()

    private val _pricesFlow = MutableStateFlow<List<Price>>(emptyList())
    val pricesFlow = _pricesFlow.asStateFlow()

    private val _uiStateFlow = MutableStateFlow(UiState.NotLoading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.apply {
            launch {
                connectivityManager.networkAvailability
                    .collectLatest { connected ->
                        if (connected) {
                            updateProductInfo()
                        } else {
                            _eventFlow.emit(Event.ShowToast(R.string.network_connection_lost))
                        }
                    }
            }
        }
    }

    fun loadProduct(sku: Int) = viewModelScope.apply {
        _uiStateFlow.tryEmit(UiState.Loading)

        launch {
            productRepository.getProductFlow(sku).collect {
                val lastValue = productFlow.value
                _productFlow.emit(it)
                if (lastValue == null) updateProductInfo()
            }
        }

        launch {
            _pricesFlow.emitAll(priceRepository.getPricesFlow(sku))
        }
    }

    fun updateProductInfo() = viewModelScope.launch {
        _uiStateFlow.emit(UiState.Loading)
        try {
            val product = productFlow.value ?: throw WbException.ProductNotFound
            productRepository.updateProduct(product)
            priceRepository.updatePrices(product.sku)
        } catch (exception: WbException) {
            _eventFlow.emit(Event.ShowException(exception))
        }
        _uiStateFlow.emit(UiState.NotLoading)
    }

    fun openProductPage(context: Context) = productFlow.value?.shopUri?.let {
        val intent = Intent(Intent.ACTION_VIEW, it)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun removeProduct() = viewModelScope.launch {
        try {
            productFlow.value?.let {
                productRepository.deleteSku(it.sku)
                _eventFlow.emit(Event.ProductDeleted)
            } ?: throw WbException.ProductNotFound
        } catch (exception: WbException) {
            _eventFlow.emit(Event.ShowException(exception))
        }
    }

    enum class UiState {
        Loading, NotLoading
    }

    sealed class Event : BaseViewModel.Event {

        object ProductDeleted : Event()

        data class ShowToast(@StringRes val textRes: Int) : Event()

        data class ShowException(val exception: WbException) : Event()
    }
}
