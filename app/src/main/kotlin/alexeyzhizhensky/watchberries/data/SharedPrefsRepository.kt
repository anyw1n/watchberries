package alexeyzhizhensky.watchberries.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    private val _sort = MutableStateFlow(
        sharedPrefs.getString(SORT_KEY, null)?.let { Sort.fromString(it) }
            ?: Sort.DEFAULT.also { saveSort(it) }
    )
    val sort: StateFlow<Sort> = _sort

    suspend fun setSort(new: Sort) {
        _sort.emit(new)
        saveSort(new)
    }

    private fun saveSort(sort: Sort) =
        sharedPrefs.edit().putString(SORT_KEY, sort.toString()).apply()

    private companion object {

        const val SHARED_PREFS_NAME = "alexeyzhizhensky.watchberries.preferences"

        const val SORT_KEY = "SORT"
    }
}
