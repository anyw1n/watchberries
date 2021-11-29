package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.data.CurrencyUtils
import alexeyzhizhensky.watchberries.data.LocaleUtils
import alexeyzhizhensky.watchberries.data.Price
import alexeyzhizhensky.watchberries.data.ThemeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val currencyUtils: CurrencyUtils,
    private val themeUtils: ThemeUtils,
    private val localeUtils: LocaleUtils
) : BaseViewModel<SettingsViewModel.Event>() {

    val currencyFlow = currencyUtils.stateFlow
    val themeFlow = themeUtils.stateFlow
    val localeFlow = localeUtils.stateFlow

    fun changeCurrency(currency: Price.Currency) {
        currencyUtils.setValue(currency)
    }

    fun changeTheme(theme: ThemeUtils.Theme) {
        if (theme == themeFlow.value) return
        themeUtils.setValue(theme)
    }

    fun changeLocale(locale: LocaleUtils.SupportedLocale) {
        if (locale == localeFlow.value) return
        localeUtils.setValue(locale)
        _eventFlow.tryEmit(Event.RecreateActivity)
    }

    sealed class Event : BaseViewModel.Event {

        object RecreateActivity : Event()
    }
}
