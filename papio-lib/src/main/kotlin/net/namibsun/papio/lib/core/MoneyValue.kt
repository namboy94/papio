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
import net.namibsun.papio.lib.exceptions.CurrencyNotFoundException


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
    fun getValue(currency: Currency): Int {
        return this.value
    }

    /**
     * Adds another monetary value to this one.
     * @param value: The Value to addd to this MoneyValue object
     */
    fun add(value: MoneyValue) {
        value.convert(this.currency)
        this.value += this.value
    }

    /**
     * Converts the currency of this MoneyValue object and adjusts the value according to current exchange rates.
     * @param currency: The currency into which to convert the MoneyValue
     */
    fun convert(currency: Currency) {
        this.value = this.convertValue(this.value, this.currency, currency)
        this.currency = currency
    }

    /**
     * Converts a value from one currency to another.
     * @param value: The value to convert
     * @param source: The source currency from which to convert from
     * @param destination: The destination currency into which to convert to
     * @return The converted value
     */
    private fun convertValue(value: Int, source: Currency, destination: Currency): Int {

        if (source == destination) {
            return value
        }

        val exchangeRates = this.getUsdExchangeRates()
        val sourceUsdValue = exchangeRates[source] ?: throw CurrencyNotFoundException(source)
        val destinationUsdValue = exchangeRates[destination] ?: throw CurrencyNotFoundException(destination)

        return ((value / sourceUsdValue) * destinationUsdValue).toInt()

    }

    /**
     * Retrieves the current exchange rates relative to the US Dollar.
     * @return A map mapping currencies to exchange rates
     */
    private fun getUsdExchangeRates(): Map<Currency, Float> {
        return hashMapOf()
    }

}