package alexeyzhizhensky.watchberries.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo

class WbConnectivityManagerLegacy(
    private val context: Context
) : WbConnectivityManager(context) {

    override fun getConnectStatus(): Boolean? =
        connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting

    override fun subscribe() {
        context.registerReceiver(
            networkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun unsubscribe() {
        context.unregisterReceiver(networkReceiver)
    }

    private val networkReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val available =
                intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
                    ?.isConnectedOrConnecting ?: isConnected
            _networkAvailability.tryEmit(available)
        }
    }
}
