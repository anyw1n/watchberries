package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.data.CurrencySettings
import alexeyzhizhensky.watchberries.data.LocaleSettings
import alexeyzhizhensky.watchberries.data.Price
import alexeyzhizhensky.watchberries.data.ThemeSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val currencySettings: CurrencySettings,
    private val themeSettings: ThemeSettings,
    private val localeSettings: LocaleSettings
) : BaseViewModel<SettingsViewModel.Event>() {

    val currencyFlow = currencySettings.stateFlow
    val themeFlow = themeSettings.stateFlow
    val localeFlow = localeSettings.stateFlow

    fun changeCurrency(currency: Price.Currency) {
        currencySettings.setValue(currency)
    }

    fun changeTheme(theme: ThemeSettings.Theme) {
        if (theme == themeFlow.value) return
        themeSettings.setValue(theme)
    }

    fun changeLocale(locale: LocaleSettings.SupportedLocale) {
        if (locale == localeFlow.value) return
        localeSettings.setValue(locale)
        _eventFlow.tryEmit(Event.RecreateActivity)
    }

    sealed class Event : BaseViewModel.Event {

        object RecreateActivity : Event()
    }
}
