package alexeyzhizhensky.watchberries.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class WbConnectivityManagerDefault(context: Context) : WbConnectivityManager(context) {

    override fun getConnectStatus(): Boolean? {
        val network = connectivityManager?.activeNetwork
        return connectivityManager?.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
            _networkAvailability.tryEmit(
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            )
        }

        override fun onLost(network: Network) {
            _networkAvailability.tryEmit(false)
        }
    }
}
