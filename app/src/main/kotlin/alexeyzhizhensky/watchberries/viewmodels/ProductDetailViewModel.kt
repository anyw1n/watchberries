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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
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
    val productFlow: StateFlow<Product?> = _productFlow

    private val _pricesFlow = MutableSharedFlow<List<Price>>()
    val pricesFlow: SharedFlow<List<Price>> = _pricesFlow

    private val _uiStatesFlow: MutableStateFlow<UiStates> =
        MutableStateFlow(UiStates(UiState.NotLoading, UiState.NotLoading))
    val uiStatesFlow: StateFlow<UiStates> = _uiStatesFlow

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        viewModelScope.apply {
            launch {
                connectivityManager.networkAvailability
                    .filterNotNull()
                    .collectLatest {
                        if (!it) {
                            eventChannel.send(Event.ShowToast(R.string.network_connection_lost))
                        }
                    }
            }
        }
    }

    fun loadProduct(sku: Int) = viewModelScope.apply {
        _uiStatesFlow.tryEmit(UiStates(UiState.Loading, UiState.Loading))

        launch {
            val product = productRepository.getProduct(sku)
            _productFlow.emit(product)
            _uiStatesFlow.emit(uiStatesFlow.value.copy(productState = UiState.NotLoading))
        }

        launch {
            priceRepository.getPricesFlow(sku).collectLatest {
                _pricesFlow.emit(it)
                if (uiStatesFlow.value.pricesState == UiState.Loading) {
                    _uiStatesFlow.emit(uiStatesFlow.value.copy(pricesState = UiState.NotLoading))
                }
            }
        }

        launch {
            try {
                priceRepository.updatePrices(sku)
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

    data class UiStates(
        val productState: UiState,
        val pricesState: UiState
    )

    enum class UiState {
        Loading, NotLoading
    }

    sealed class Event {

        data class ShowToast(@StringRes val textRes: Int) : Event()
        data class ShowException(val exception: WbException) : Event()
    }
}
