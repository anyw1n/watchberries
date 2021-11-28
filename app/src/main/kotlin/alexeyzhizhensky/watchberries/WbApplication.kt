package alexeyzhizhensky.watchberries

import alexeyzhizhensky.watchberries.data.LocaleUtils
import alexeyzhizhensky.watchberries.data.ThemeUtils
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WbApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocaleUtils.getLocalizedContext(it) })

    @Inject
    lateinit var themeUtils: ThemeUtils

    override fun onCreate() {
        super.onCreate()
        themeUtils.setTheme(themeUtils.themeFlow.value)
    }
}
