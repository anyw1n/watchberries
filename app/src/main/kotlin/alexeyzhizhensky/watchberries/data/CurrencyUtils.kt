package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.utils.Utils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyUtils @Inject constructor(
    sharedPrefsRepository: SharedPrefsRepository
) : Utils<Price.Currency>(sharedPrefsRepository) {

    override val clazz: Class<Price.Currency> = Price.Currency::class.java
    override val key: String = "CURRENCY"
    override val defaultValue: Price.Currency = Price.Currency.RUB
}
