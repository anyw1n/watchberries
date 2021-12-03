package alexeyzhizhensky.watchberries.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencySettings @Inject constructor(
    sharedPrefsRepository: SharedPrefsRepository
) : Settings<Price.Currency>(sharedPrefsRepository) {

    override val clazz: Class<Price.Currency> = Price.Currency::class.java
    override val key: String = "CURRENCY"
    override val defaultValue: Price.Currency = Price.Currency.RUB
}
