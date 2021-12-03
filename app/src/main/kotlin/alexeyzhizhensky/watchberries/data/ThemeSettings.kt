package alexeyzhizhensky.watchberries.data

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeSettings @Inject constructor(
    sharedPrefsRepository: SharedPrefsRepository
) : Settings<ThemeSettings.Theme>(sharedPrefsRepository) {

    override val clazz: Class<Theme> = Theme::class.java
    override val key: String = "THEME"
    override val defaultValue: Theme = Theme.SystemDefault

    override fun setValue(newValue: Theme) {
        AppCompatDelegate.setDefaultNightMode(newValue.mode)
        super.setValue(newValue)
    }

    enum class Theme(val mode: Int) {

        SystemDefault(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
        Light(AppCompatDelegate.MODE_NIGHT_NO),
        Dark(AppCompatDelegate.MODE_NIGHT_YES);

        companion object {

            val values = values()
        }
    }
}
