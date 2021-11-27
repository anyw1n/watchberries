package alexeyzhizhensky.watchberries.data

import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleUtils @Inject constructor(
    private val sharedPrefsRepository: SharedPrefsRepository
) {

    private val _localeFlow = MutableStateFlow(sharedPrefsRepository.getLocale())
    val localeFlow = _localeFlow.asStateFlow()

    fun setLocale(supportedLocale: SupportedLocale) {
        _localeFlow.tryEmit(supportedLocale)
        sharedPrefsRepository.saveLocale(supportedLocale)
    }

    enum class SupportedLocale(
        val code: String
    ) {

        Default("default"),
        English("en"),
        Russian("ru");

        companion object {

            val values = values().toList()
        }
    }

    companion object {

        fun getLocalizedContext(baseContext: Context): Context {
            val supportedLocale = SharedPrefsRepository.getLocale(baseContext)
            return if (supportedLocale == SupportedLocale.Default) {
                baseContext
            } else {
                val locale = Locale(supportedLocale.code)
                Locale.setDefault(locale)
                val config = Configuration(baseContext.resources.configuration).apply {
                    setLocale(locale)
                }
                baseContext.createConfigurationContext(config)
            }
        }
    }
}
