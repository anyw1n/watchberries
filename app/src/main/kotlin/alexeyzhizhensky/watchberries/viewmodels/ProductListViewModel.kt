package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.ProductRepository
import alexeyzhizhensky.watchberries.data.SortSettings
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.network.WbConnectivityManager
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    connectivityManager: WbConnectivityManager,
    private val productRepository: ProductRepository,
    private val sortSettings: SortSettings
) : BaseViewModel<ProductListViewModel.Event>() {

    val pagingDataFlow = productRepository.observePaginated()

    init {
        viewModelScope.apply {
            launch {
                connectivityManager.networkAvailability
                    .collectLatest { connected ->
                        val event = if (connected) {
                            Event.RefreshProducts
                        } else {
                            Event.ShowToast(R.string.network_connection_lost)
                        }
                        _eventFlow.emit(event)
                    }
            }

            launch {
                sortSettings.stateFlow.collectLatest {
                    _eventFlow.emit(Event.RefreshProducts)
                }
            }
        }
    }

    fun addProduct(sku: Int) = viewModelScope.launch {
        try {
            productRepository.addSku(sku)
            _eventFlow.emit(Event.RefreshProducts)
        } catch (exception: WbException) {
            _eventFlow.emit(Event.ShowException(exception))
        }
    }

    sealed class Event : BaseViewModel.Event {

        object RefreshProducts : Event()
        data class ShowToast(@StringRes val textRes: Int) : Event()
        data class ShowException(val exception: WbException) : Event()
    }
}
