package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.data.LocaleUtils
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localeUtils: LocaleUtils
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val eventFlow = _eventFlow.asSharedFlow()

    val localeFlow = localeUtils.localeFlow

    fun changeLocale(locale: LocaleUtils.SupportedLocale) {
        if (locale == localeFlow.value) return
        localeUtils.setLocale(locale)
        _eventFlow.tryEmit(Event.ShowToast(R.string.message_restart_app))
    }

    sealed class Event {

        data class ShowToast(@StringRes val textRes: Int) : Event()
    }
}
