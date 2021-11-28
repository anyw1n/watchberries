package alexeyzhizhensky.watchberries

import alexeyzhizhensky.watchberries.data.LocaleUtils
import alexeyzhizhensky.watchberries.network.WbConnectivityManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var connectivityManager: WbConnectivityManager

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleUtils.getLocalizedContext(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityManager.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unsubscribe()
    }
}
