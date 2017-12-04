package net.namibsun.papio.lib.money.new

open class BaseValue(val value: Int, val postDecimal: Int, val currency: BaseCurrency) {

    constructor(value: String, currency: BaseCurrency) : this(
            nonDecimalize(value),
            decimalPosition(value),
            currency
    )

    operator fun plus(value: BaseValue): BaseValue {
        val converted = convert(value.value, value.postDecimal, value.currency, this.currency)
        val postDecimal = maxOf(converted.postDecimal, this.postDecimal)
        val valueOne = this.value * 10 * (postDecimal - this.postDecimal)
        val valueTwo = converted.value * 10 * (postDecimal - converted.postDecimal)
        return BaseValue(valueOne + valueTwo, postDecimal, this.currency)
    }

    operator fun minus(value: BaseValue): BaseValue {
        return this + !value
    }

    operator fun times(multiplicand: Int): BaseValue {
        return BaseValue(this.value * multiplicand, this.postDecimal, this.currency)
    }

    operator fun not(): BaseValue {
        return BaseValue(-1 * this.value, this.postDecimal, this.currency)
    }

    // format
    // toString

    fun convert(currency: BaseCurrency? = null): BaseValue {
        return if (currency == null) {
            BaseValue(this.value, this.postDecimal, this.currency)
        } else {
            convert(this.value, this.postDecimal, this.currency, currency)
        }
    }

    fun format(symbol: DecimalSymbol = DecimalSymbol.POINT, currencyPosition: CurrencyPosition = CurrencyPosition.FRONT): String {

    }

    override fun toString(): String {
        return this.format()
    }

    fun serialize(): String {
        return ""
    }

    companion object {

        fun deserialize(serialized: String): BaseValue {

        }

    }

    fun toDouble(): Double {

    }
}

enum class DecimalSymbol {
    COMMA,
    POINT
}

enum class CurrencyPosition {
    FRONT,
    BACK
}

fun convert(value: Int, postDecimal: Int, source: BaseCurrency, dest: BaseCurrency): BaseValue {
    return BaseValue(1,2, source)
}

fun nonDecimalize(value: String): Int {

}

fun decimalPosition(value: String): Int {

}