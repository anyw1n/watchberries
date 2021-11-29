package alexeyzhizhensky.watchberries.utils

import alexeyzhizhensky.watchberries.data.SharedPrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Utils<T>(
    private val sharedPrefsRepository: SharedPrefsRepository
) {

    protected abstract val clazz: Class<T>
    protected abstract val key: String
    protected abstract val defaultValue: T

    private val _stateFlow by lazy { MutableStateFlow(getValue()) }
    val stateFlow by lazy { _stateFlow.asStateFlow() }

    private fun getValue() = sharedPrefsRepository.get(key, defaultValue, clazz)

    open fun setValue(newValue: T) {
        _stateFlow.tryEmit(newValue)
        sharedPrefsRepository.set(key, newValue)
    }
}
