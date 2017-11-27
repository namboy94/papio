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

package net.namibsun.papio.lib.core

/**
 * The MoneyValue class defines a monetary value in a particular currency.
 * @param value: The value of the MoneyValue object
 * @param currency: The currency of the monetary value
 */
class MoneyValue(private var value: Int, private var currency: Currency) {

    /**
     * Gets the value of the MoneyValue object in the specified currency.
     * @param currency: The currency in which to get the value
     */
    fun getValue(currency: Currency? = null): Int {

        return if (currency == null) {
            this.value
        } else {
            CurrencyExchanger.update()
            CurrencyExchanger.convertValue(this.value, this.currency, currency)
        }
    }

    /**
     * Method that enables retrieving the currency of this MoneyValue
     * @return The current currency of this MoneyValue object
     */
    fun getCurrency(): Currency {
        return this.currency
    }

    /**
     * Adds another monetary value to this one. Can be called using the '+' operator.
     * @param value: The Value to add to this MoneyValue object
     * @return The result of the addition as a new MoneyValue object
     */
    operator fun plus(value: MoneyValue): MoneyValue {
        val newValue = this.value + value.getValue(this.currency)
        return MoneyValue(newValue, this.currency)
    }

    /**
     * Subtracts another monetary value from this one. Can be called using the '-' operator.
     * @param value: The value to subtract from this MoneyValue object
     * @return The result of the subtraction as a new MoneyValue object
     */
    operator fun minus(value: MoneyValue): MoneyValue {
        val newValue = this.value - value.getValue(this.currency)
        return MoneyValue(newValue, this.currency)
    }

    /**
     * Converts the currency of this MoneyValue object and adjusts the value according to current exchange rates.
     * @param currency: The currency into which to convert the MoneyValue
     */
    fun convert(currency: Currency) {
        CurrencyExchanger.update()
        this.value = CurrencyExchanger.convertValue(this.value, this.currency, currency)
        this.currency = currency
    }
}
