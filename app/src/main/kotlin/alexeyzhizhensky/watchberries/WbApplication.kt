package alexeyzhizhensky.watchberries

import alexeyzhizhensky.watchberries.data.ThemeUtils
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WbApplication : Application() {

    @Inject
    lateinit var notificationManager: WbNotificationManager

    @Inject
    lateinit var themeUtils: ThemeUtils

    override fun onCreate() {
        super.onCreate()
        notificationManager.clearNotifications()
        themeUtils.setTheme(themeUtils.themeFlow.value)
    }
}
