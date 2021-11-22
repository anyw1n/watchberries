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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    connectivityManager: WbConnectivityManager,
    private val productRepository: ProductRepository,
    private val priceRepository: PriceRepository
) : ViewModel() {

    private val _productFlow = MutableStateFlow<Product?>(null)
    val productFlow = _productFlow.asStateFlow()

    private val _pricesFlow = MutableSharedFlow<List<Price>>()
    val pricesFlow = _pricesFlow.asSharedFlow()

    private val _uiStateFlow = MutableStateFlow(UiState.NotLoading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        viewModelScope.apply {
            launch {
                connectivityManager.networkAvailability
                    .collectLatest { connected ->
                        if (connected) {
                            productFlow.value?.sku?.let(this@ProductDetailViewModel::updateProduct)
                        } else {
                            eventChannel.send(Event.ShowToast(R.string.network_connection_lost))
                        }
                    }
            }
        }
    }

    fun loadProduct(sku: Int) = viewModelScope.apply {
        launch {
            _productFlow.emitAll(productRepository.getProductFlow(sku))
        }

        launch {
            _pricesFlow.emitAll(priceRepository.getPricesFlow(sku))
        }

        updateProduct(sku)
    }

    private fun updateProduct(sku: Int) = viewModelScope.launch {
        _uiStateFlow.emit(UiState.Loading)
        try {
            productRepository.updateProduct(sku)
            priceRepository.updatePrices(sku)
        } catch (exception: WbException) {
            eventChannel.send(Event.ShowException(exception))
        }
        _uiStateFlow.emit(UiState.NotLoading)
    }

    fun updateProduct() {
        viewModelScope.launch {
            try {
                productFlow.value?.sku?.let(this@ProductDetailViewModel::updateProduct)
                    ?: throw WbException.ProductNotFound
            } catch (exception: WbException) {
                eventChannel.send(Event.ShowException(exception))
            }
        }
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
                eventChannel.send(Event.ProductDeleted)
            } ?: throw WbException.ProductNotFound
        } catch (exception: WbException) {
            eventChannel.send(Event.ShowException(exception))
        }
    }

    enum class UiState {
        Loading, NotLoading
    }

    sealed class Event {

        object ProductDeleted : Event()

        data class ShowToast(@StringRes val textRes: Int) : Event()

        data class ShowException(val exception: WbException) : Event()
    }
}
