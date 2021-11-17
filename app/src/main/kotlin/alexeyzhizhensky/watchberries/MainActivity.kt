package alexeyzhizhensky.watchberries

import alexeyzhizhensky.watchberries.network.WbConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var connectivityManager: WbConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectivityManager.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unsubscribe()
    }
}
