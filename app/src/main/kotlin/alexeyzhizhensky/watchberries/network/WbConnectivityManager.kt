package alexeyzhizhensky.watchberries.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull

abstract class WbConnectivityManager(context: Context) {

    protected val connectivityManager =
        ContextCompat.getSystemService(context, ConnectivityManager::class.java)
    abstract val isConnected: Boolean?

    protected val _networkAvailability by lazy { MutableStateFlow(isConnected) }
    val networkAvailability by lazy { _networkAvailability.filterNotNull().drop(1) }

    abstract fun subscribe()

    abstract fun unsubscribe()
}
