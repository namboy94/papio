/*
This file is part of papio.

papio is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

papio is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with papio.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.namibsun.papio.lib.money

import java.math.BigDecimal

/**
 * Class that models a generic monetary value.
 * A Value object is immutable.
 * All changing operations yield a new Value object and do not affect the original object.
 * @param value: The value of the Value as a BigDecimal, which allows arbitrary precision without rounding errors
 * @param currency: The currency of the Value
 */
@Suppress("EqualsOrHashCode")
data class Value(val value: BigDecimal, val currency: Currency) {

    /**
     * Generates a Value object from a String and a currency.
     * @param value: The String to parse a value from
     * @param currency: The currency of the value
     * @throws NumberFormatException: If the String does not contain a valid value
     */
    constructor(value: String, currency: Currency) : this(BigDecimal(value), currency)

    /**
     * Adds two Values and returns the sum
     * @param value: The value with which to create a sum with
     * @return The sum of the two values
     */
    operator fun plus(value: Value): Value {
        val converted = CurrencyConverter.convertValue(value.value, value.currency, this.currency)
        val sum = this.value.add(converted)
        return Value(sum, this.currency)
    }

    /**
     * Subtracts a value from this value and returns the result
     * @param value: The value to subtract from this value
     * @return: The result of the subtraction
     */
    operator fun minus(value: Value): Value {
        return this + !value
    }

    /**
     * Multiplies the value by an integer value
     * @param multiplicand: The value with which to multiply this value
     * @return: A new Value object whose value is the product of this value and the multiplicand
     */
    operator fun times(multiplicand: Int): Value {
        return Value(this.value.times(BigDecimal(multiplicand)), this.currency)
    }

    /**
     * Negates the value of this Value object. Equivalent to multiplying with -1
     * @return The negated value
     */
    operator fun not(): Value {
        return this * -1
    }

    /**
     * Overrides the data class equals method to use the compareTo instead of equals method of the BigDecimal
     * class. This ensures that 0.00 and 0.0 are treated the same.
     * @param other: The object to compare to
     * @return true if the objects are equal, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (other is Value) {
            this.currency == other.currency && this.value.compareTo(other.value) == 0
        } else {
            false
        }
    }

    /**
     * Converts this Value to another currency
     * @param currency: The currency to which to convert to
     * @return The converted Value
     */
    fun convert(currency: Currency? = null): Value {
        return if (currency == null || currency == this.currency) {
            Value(this.value, this.currency)
        } else {
            Value(CurrencyConverter.convertValue(this.value, this.currency, currency), currency)
        }
    }

    /**
     * Formats the value into a configurable human-readable String
     * @param useCurrencySymbol: Specifies if the currency's symbol (e.g. €) should be used or the currency's name (EUR)
     * @param decimalSymbol: The symbol to use as a decimal point. Defaults to a decimal point.
     * @param currencySymbolPositionBack: Specifies if the currency symbol is displayed before or after the value
     * @param overrideAccuracy: Overrides the inherent accuracy of a currency
     */
    fun format(
            useCurrencySymbol: Boolean = false,
            decimalSymbol: String = ".",
            currencySymbolPositionBack: Boolean = false,
            overrideAccuracy: Int? = null): String {

        val currencySymbol = if (useCurrencySymbol) {
            this.currency.symbol
        } else {
            this.currency.name
        }

        val accuracy = overrideAccuracy ?: this.currency.displayAccuracy
        val formatted = this.value
                .setScale(accuracy, BigDecimal.ROUND_HALF_UP)
                .toString()
                .replace(".", decimalSymbol)

        return if (currencySymbolPositionBack) {
            "$formatted $currencySymbol"
        } else {
            "$currencySymbol $formatted"
        }
    }

    /**
     * Creates a string representation of the value that is automatically used in string interpolation
     * @return The String representation of the value
     */
    override fun toString(): String {
        return this.format()
    }

    /**
     * Serializes the value
     * @return The serialized String of the value
     */
    fun serialize(): String {
        return "${this.currency.name}:${this.value}"
    }

    /**
     * Contains static methods
     */
    companion object {

        /**
         * Generates a Value object from a serialized String
         * @param serialized: The serialized String
         * @return The generated Value object
         * @throws IllegalArgumentException If the currency does not exist
         * @throws IndexOutOfBoundsException If there aren't two individual sections for the currency and the value
         * @throws NumberFormatException If the value is not a valid number
         */
        fun deserialize(serialized: String): Value {
            val split = serialized.split(":")

            val currency = Currency.valueOf(split[0])
            val value = BigDecimal(split[1])

            return Value(value, currency)
        }
    }
}
