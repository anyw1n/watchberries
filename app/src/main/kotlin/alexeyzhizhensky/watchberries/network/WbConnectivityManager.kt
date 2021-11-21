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
    protected val isConnected: Boolean? get() = getConnectStatus()

    protected val _networkAvailability = MutableStateFlow(isConnected)
    val networkAvailability = _networkAvailability.filterNotNull().drop(1)

    protected abstract fun getConnectStatus(): Boolean?

    abstract fun subscribe()

    abstract fun unsubscribe()
}
