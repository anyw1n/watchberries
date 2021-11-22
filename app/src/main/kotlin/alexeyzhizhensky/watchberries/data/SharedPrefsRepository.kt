package alexeyzhizhensky.watchberries.data

import android.content.Context
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

    private fun saveSort(sort: Sort) =
        sharedPrefs.edit().putString(SORT_KEY, gson.toJson(sort)).apply()

    private companion object {

        const val SHARED_PREFS_NAME = "alexeyzhizhensky.watchberries.preferences"

        const val SORT_KEY = "SORT"
    }
}
