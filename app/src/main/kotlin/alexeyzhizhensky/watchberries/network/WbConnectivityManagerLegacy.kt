package alexeyzhizhensky.watchberries.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.net.ConnectivityManager.EXTRA_NETWORK_INFO
import android.net.NetworkInfo

class WbConnectivityManagerLegacy(
    private val context: Context
) : WbConnectivityManager(context) {

    override fun getConnectStatus(): Boolean? = connectivityManager?.let {
        val networkInfo = it.activeNetworkInfo ?: return false
        networkInfo.isConnectedOrConnecting
    }

    override fun subscribe() {
        context.registerReceiver(networkReceiver, IntentFilter(CONNECTIVITY_ACTION))
    }

    override fun unsubscribe() {
        context.unregisterReceiver(networkReceiver)
    }

    private val networkReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val available = intent.getParcelableExtra<NetworkInfo>(EXTRA_NETWORK_INFO)
                ?.isConnectedOrConnecting ?: isConnected
            _networkAvailability.tryEmit(available)
        }
    }
}
