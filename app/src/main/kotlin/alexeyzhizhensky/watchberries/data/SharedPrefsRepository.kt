package alexeyzhizhensky.watchberries.data

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    private val _sort = MutableStateFlow(getSort())
    val sort = _sort.asStateFlow()

    private fun getSort() =
        sharedPrefs.getString(SORT_KEY, null)?.let { gson.fromJson(it, Sort::class.java) }
            ?: Sort.DEFAULT.also(::saveSort)

    fun setSort(new: Sort) {
        _sort.tryEmit(new)
        saveSort(new)
    }

    private fun saveSort(sort: Sort) = sharedPrefs.edit { putString(SORT_KEY, gson.toJson(sort)) }

    fun getTheme() = sharedPrefs.getString(THEME_KEY, null)?.let { ThemeUtils.Theme.valueOf(it) }
        ?: ThemeUtils.Theme.SystemDefault.also(::saveTheme)

    fun saveTheme(theme: ThemeUtils.Theme) = sharedPrefs.edit { putString(THEME_KEY, theme.name) }

    fun getLocale() =
        sharedPrefs.getString(LOCALE_KEY, null)?.let { LocaleUtils.SupportedLocale.valueOf(it) }
            ?: LocaleUtils.SupportedLocale.Default.also(::saveLocale)

    fun saveLocale(locale: LocaleUtils.SupportedLocale) =
        sharedPrefs.edit { putString(LOCALE_KEY, locale.name) }

    companion object {

        private const val SHARED_PREFS_NAME = "alexeyzhizhensky.watchberries.preferences"

        private const val SORT_KEY = "SORT"
        private const val THEME_KEY = "THEME"
        private const val LOCALE_KEY = "LOCALE"

        fun getLocale(context: Context) =
            with(context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)) {
                getString(LOCALE_KEY, null)?.let { LocaleUtils.SupportedLocale.valueOf(it) }
                    ?: LocaleUtils.SupportedLocale.Default.also {
                        edit { putString(LOCALE_KEY, it.name) }
                    }
            }
    }
}
