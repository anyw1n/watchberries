package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.data.SharedPrefsRepository
import alexeyzhizhensky.watchberries.data.Sort
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SortBottomSheetViewModel @Inject constructor(
    private val sharedPrefsRepository: SharedPrefsRepository
) : ViewModel() {

    fun getSort() = sharedPrefsRepository.sort.value

    fun changeSort(sort: Sort) = sharedPrefsRepository.setSort(sort)
}
