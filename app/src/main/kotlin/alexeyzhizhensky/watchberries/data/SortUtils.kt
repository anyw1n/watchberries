package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.utils.Utils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SortUtils @Inject constructor(
    sharedPrefsRepository: SharedPrefsRepository
) : Utils<Sort>(sharedPrefsRepository) {

    override val clazz: Class<Sort> = Sort::class.java
    override val key: String = "SORT"
    override val defaultValue: Sort = Sort.DEFAULT
}
