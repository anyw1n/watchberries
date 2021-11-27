package alexeyzhizhensky.watchberries

import alexeyzhizhensky.watchberries.data.LocaleUtils
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WbApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocaleUtils.getLocalizedContext(it) })
    }
}
