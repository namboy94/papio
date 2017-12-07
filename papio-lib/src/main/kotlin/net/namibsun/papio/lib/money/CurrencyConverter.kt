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

import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URL
import java.util.Scanner
import java.util.logging.Logger

/**
 * Singleton object that handles the exchanging of monetary values from one currency to another.
 */
object CurrencyConverter {

    /**
     * A logger for this class
     */
    private val logger = Logger.getLogger(CurrencyConverter::class.java.name)

    /**
     * The current exchange rates with Euro as the base currency
     */
    val exchangeRates = mutableMapOf(Currency.EUR to BigDecimal("1.0"))

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
     * Updates the exchange rate data. Will only update if more than a minute has passed since
     * the last update or if the force variable is set to true.
     * @param force: Forces an update if set to true
     */
    fun update(force: Boolean = false) {
        if (force or ((System.currentTimeMillis() - this.updated) > 60000)) {
            this.updated = System.currentTimeMillis()
            this.updateFiatCurrencyExchangeRates()
            this.updateCryptoCurrencyExchangeRates()
        } else {
            this.logger.fine("Skipping exchange rate update")
        }
    }

    /**
     * Updates the exchange rate data for Fiat currencies
     */
    private fun updateFiatCurrencyExchangeRates() {

        val url = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
        val exchangeRateData = this.getUrlData(url)

        for (currency in Currency.getAllFiatCurrencies()) {

            // Format example: <Cube currency='USD' rate='1.1952'/>
            try {
                val splitter = if (currency == Currency.NAD) {
                    Currency.ZAR.name
                } else {
                    currency.name
                }

                var rate = exchangeRateData.split("$splitter' rate='")[1]
                rate = rate.split("'")[0]
                this.exchangeRates[currency] = BigDecimal(rate)
            } catch (e: IndexOutOfBoundsException) {
                if (currency != Currency.EUR) {
                    this.logger.warning("Currency $currency not found in XML data.")
                    this.exchangeRates[currency] = BigDecimal("1.0") // If currency not found, set to 1.0
                }
            } catch (e: NumberFormatException) {
                this.logger.warning("Invalid exchange rate value for $currency in XML data.")
                this.exchangeRates[currency] = BigDecimal("1.0") // If currency rate not valid, set to 1.0
            }
        }
        this.exchangeRates[Currency.EUR] = BigDecimal("1.0") // Make sure Euro is set to 1.0
    }

    /**
     * Updates the exchange rate data for crypto-currencies
     */
    private fun updateCryptoCurrencyExchangeRates() {

        for (currency in Currency.getAllCryptoCurrencies()) {
            val url = "https://api.cryptonator.com/api/ticker/EUR-" + currency.name
            val exchangeRateData = this.getUrlData(url)
            val price = exchangeRateData.split("\"price\":\"")[1].split("\"")[0]
            try {
                this.exchangeRates[currency] = BigDecimal(price)
            } catch (e: NumberFormatException) {
                this.logger.warning("Invalid exchange rate value for $currency in JSON data.")
                this.exchangeRates[currency] = BigDecimal("1.0") // If currency rate not valid, set to 1.0
            }
        }
    }

    /**
     * Puts in a GET request to a URL
     * @param url: The URL to which to send the GET request
     * @return The response body of the GET request
     */
    private fun getUrlData(url: String): String {
        return try {
            val connection = URL(url).openConnection()
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

    /**
     * Converts a value from one currency to another.
     * @param value: The value to convert
     * @param source: The source currency from which to convert from
     * @param destination: The destination currency into which to convert to
     * @return The converted value as a BigDecimal
     */
    fun convertValue(value: BigDecimal, source: Currency, destination: Currency): BigDecimal {

        if (source == destination) {
            return value
        }

        val sourceEuroValue = this.exchangeRates[source]!! // Not null
        val destinationEuroValue = this.exchangeRates[destination]!! // Not null

        return value.divide(sourceEuroValue, 128, RoundingMode.HALF_UP).times(destinationEuroValue)
    }
}