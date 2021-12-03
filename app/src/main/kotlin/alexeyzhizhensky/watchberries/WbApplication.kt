package alexeyzhizhensky.watchberries

import alexeyzhizhensky.watchberries.data.LocaleSettings
import alexeyzhizhensky.watchberries.data.ThemeSettings
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WbApplication : Application() {

    @Inject
    lateinit var notificationManager: WbNotificationManager

    @Inject
    lateinit var themeSettings: ThemeSettings

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocaleSettings.getLocalizedContext(it) })
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager.clearNotifications()
        themeSettings.setValue(themeSettings.stateFlow.value)
    }
}
