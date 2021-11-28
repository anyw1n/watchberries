package alexeyzhizhensky.watchberries.data

import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeUtils @Inject constructor(
    private val sharedPrefsRepository: SharedPrefsRepository
) {

    private val _themeFlow = MutableStateFlow(sharedPrefsRepository.getTheme())
    val themeFlow = _themeFlow.asStateFlow()

    fun setTheme(theme: Theme) {
        AppCompatDelegate.setDefaultNightMode(theme.mode)
        sharedPrefsRepository.saveTheme(theme)
        _themeFlow.tryEmit(theme)
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
