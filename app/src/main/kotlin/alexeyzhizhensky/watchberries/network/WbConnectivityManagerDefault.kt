package alexeyzhizhensky.watchberries.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class WbConnectivityManagerDefault(context: Context) : WbConnectivityManager(context) {

    override val isConnected: Boolean?
        get() = connectivityManager?.let {
            val network = it.activeNetwork ?: return false
            it.getNetworkCapabilities(network)?.hasCapability(NET_CAPABILITY_INTERNET)
        }

    override fun subscribe() {
        connectivityManager?.registerDefaultNetworkCallback(networkCallback)
    }

    override fun unsubscribe() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            _networkAvailability.tryEmit(networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET))
        }

        override fun onLost(network: Network) {
            _networkAvailability.tryEmit(false)
        }
    }
}
