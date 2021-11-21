package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.ProductRepository
import alexeyzhizhensky.watchberries.data.SharedPrefsRepository
import alexeyzhizhensky.watchberries.data.WbException
import alexeyzhizhensky.watchberries.network.WbConnectivityManager
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    connectivityManager: WbConnectivityManager,
    private val productRepository: ProductRepository,
    private val sharedPrefsRepository: SharedPrefsRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

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
                        eventChannel.send(event)
                    }
            }

            launch {
                sharedPrefsRepository.sort.collectLatest {
                    eventChannel.send(Event.RefreshProducts)
                }
            }
        }
    }

    fun addProduct(sku: Int) = viewModelScope.launch {
        try {
            productRepository.addSku(sku)
            eventChannel.send(Event.RefreshProducts)
        } catch (exception: WbException) {
            eventChannel.send(Event.ShowException(exception))
        }
    }

    sealed class Event {

        object RefreshProducts : Event()
        data class ShowToast(@StringRes val textRes: Int) : Event()
        data class ShowException(val exception: WbException) : Event()
    }
}
