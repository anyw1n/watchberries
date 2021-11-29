package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.data.Sort
import alexeyzhizhensky.watchberries.data.SortUtils
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SortBottomSheetViewModel @Inject constructor(
    private val sortUtils: SortUtils
) : ViewModel() {

    fun getSort() = sortUtils.stateFlow.value

    fun changeSort(sort: Sort) = sortUtils.setValue(sort)
}
