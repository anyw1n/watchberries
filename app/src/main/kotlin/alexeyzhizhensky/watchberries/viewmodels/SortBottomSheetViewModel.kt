package alexeyzhizhensky.watchberries.viewmodels

import alexeyzhizhensky.watchberries.data.Sort
import alexeyzhizhensky.watchberries.data.SortSettings
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SortBottomSheetViewModel @Inject constructor(
    private val sortSettings: SortSettings
) : ViewModel() {

    fun getSort() = sortSettings.stateFlow.value

    fun changeSort(sort: Sort) = sortSettings.setValue(sort)
}
