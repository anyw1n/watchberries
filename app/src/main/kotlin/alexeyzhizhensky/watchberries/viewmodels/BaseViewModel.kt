package alexeyzhizhensky.watchberries.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

open class BaseViewModel<E : BaseViewModel.Event> : ViewModel() {

    protected val _eventFlow = MutableSharedFlow<E>(extraBufferCapacity = 1)
    val eventFlow = _eventFlow.asSharedFlow()

    interface Event
}
