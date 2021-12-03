package alexeyzhizhensky.watchberries.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SortSettings @Inject constructor(
    sharedPrefsRepository: SharedPrefsRepository
) : Settings<Sort>(sharedPrefsRepository) {

    override val clazz: Class<Sort> = Sort::class.java
    override val key: String = "SORT"
    override val defaultValue: Sort = Sort.DEFAULT
}
