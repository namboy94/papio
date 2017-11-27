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

import java.io.IOException
import java.net.URL
import java.util.Scanner
import java.util.logging.Logger

/**
 * Singleton object that handles the exchanging of monetary values from one currency to another.
 */
object CurrencyExchanger {

    /**
     * A logger for this class
     */
    private val logger = Logger.getLogger(CurrencyExchanger::class.java.name)

    /**
     * The current exchange rates with Euro as the base currency
     */
    private var exchangeRates = mutableMapOf(Currency.EUR to 1.0)

    /**
     * A UNIX timestamp that keeps track of when the exchange rates were updated last
     */
    private var updated: Long = 0

    /**
     * Initializes the singleton by updating the exchange rate data
     */
    init {
        this.update()
    }

    /**
     * Getter method that allows the retrieval of the internal exchange rates.
     * @return The exchange rates relative to the Euro
     */
    fun getExchangeRates(): Map<Currency, Double> {
        return this.exchangeRates
    }

    /**
     * Updates the exchange rate data. Will only update if more than a minute has passed since
     * the last update or if the force variable is set to true.
     * @param force: Forces an update if set to true
     */
    fun update(force: Boolean = false) {
        if (force or ((System.currentTimeMillis() - this.updated) > 60000)) {

            this.updated = System.currentTimeMillis()
            val exchangeRateData = this.fetchRateData()

            for (currency in Currency.values()) {

                // Format example: <Cube currency='USD' rate='1.1952'/>
                try {
                    var rate = exchangeRateData.split("${currency.name}' rate='")[1]
                    rate = rate.split("'")[0]
                    this.exchangeRates[currency] = rate.toDouble()
                } catch (e: IndexOutOfBoundsException) {
                    if (currency != Currency.EUR) {
                        this.logger.warning("Currency $currency not found in XML data.")
                        this.exchangeRates[currency] = 1.0 // If currency not found, set to 1.0
                    }
                } catch (e: NumberFormatException) {
                    this.logger.warning("Invalid exchange rate value for $currency in XML data.")
                    this.exchangeRates[currency] = 1.0 // If currency rate not valid long, set to 1.0
                }
            }

            this.exchangeRates[Currency.EUR] = 1.0 // Make sure Euro is set to 1.0
        } else {
            this.logger.fine("Skipping exchange rate update")
        }
    }

    /**
     * Converts a value from one currency to another.
     * @param value: The value to convert
     * @param source: The source currency from which to convert from
     * @param destination: The destination currency into which to convert to
     * @return The converted value
     */
    fun convertValue(value: Int, source: Currency, destination: Currency): Int {

        if (source == destination) {
            return value
        }

        val sourceEuroValue = this.exchangeRates[source]!! // Not null
        val destinationEuroValue = this.exchangeRates[destination]!! // Not null

        return ((value / sourceEuroValue) * destinationEuroValue).toInt()
    }

    /**
     * Fetches current exchange rate data from the ECB website.
     * @return The exchange rate XML data as a String
     */
    private fun fetchRateData(): String {

        return try {

            val url = URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")
            val connection = url.openConnection()
            val inputStream = connection.getInputStream()
            val scanner = Scanner(inputStream).useDelimiter("\\A")

            var result = ""
            while (scanner.hasNext()) {
                result += scanner.next()
            }
            result
        } catch (e: IOException) {
            ""
        }
    }
}