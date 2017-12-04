package net.namibsun.papio.lib.money.new

open class CurrencyValue(val value: Int, val postDecimal: Int, val currency: BaseCurrency) {

    constructor(value: String, currency: BaseCurrency) :
            this(
                    getValueFromString(),
                    getValueFromString(),
                    currency
            )

    companion object {
        private fun getValueFromString(): Int {
            return 1
        }
    }

    // plus
    // minus
    // times
    // convert
    // format
    // toString

}
class FiatValue(value: Int, currency: FiatCurrency) : CurrencyValue(value, 2, currency)
class CryptoValue(value: Int, postDecimal: Int, currency: CryptoCurrency) : CurrencyValue(value, postDecimal, currency)