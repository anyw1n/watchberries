package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.LocaleUtils
import androidx.annotation.StringRes
import alexeyzhizhensky.watchberries.data.ThemeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeUtils: ThemeUtils,
    private val localeUtils: LocaleUtils
) : BaseViewModel<SettingsViewModel.Event>() {

    val themeFlow = themeUtils.themeFlow

    val localeFlow = localeUtils.localeFlow

    fun changeTheme(theme: ThemeUtils.Theme) {
        if (theme == themeFlow.value) return
        themeUtils.setTheme(theme)
    }

    fun changeLocale(locale: LocaleUtils.SupportedLocale) {
        if (locale == localeFlow.value) return
        localeUtils.setLocale(locale)
        _eventFlow.tryEmit(Event.ShowToast(R.string.message_restart_app))
    }

    sealed class Event : BaseViewModel.Event {

        data class ShowToast(@StringRes val textRes: Int) : Event()
    }
}
