package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.utils.Utils
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleUtils @Inject constructor(
    sharedPrefsRepository: SharedPrefsRepository
) : Utils<LocaleUtils.SupportedLocale>(sharedPrefsRepository) {

    override val clazz: Class<SupportedLocale> = SupportedLocale::class.java
    override val key: String = KEY
    override val defaultValue: SupportedLocale = SupportedLocale.Default

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

        private const val KEY = "LOCALE"

        fun getLocalizedContext(baseContext: Context): Context {
            val supportedLocale = SharedPrefsRepository.get(
                baseContext,
                KEY,
                SupportedLocale.Default,
                SupportedLocale::class.java
            )
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
