/*
Copyright 2016 Hermann Krumrey <hermann@krumreyh.com>

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

import net.namibsun.papio.lib.exceptions.CurrencyConversionError
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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
     * Can be set to true to disable any network operations.
     * Originally added to enable testing the caching functionality.
     */
    var networkDisabled = false

    /**
     * The current exchange rates with Euro as the base currency
     */
    private var exchangeRates = mutableMapOf(Currency.EUR to BigDecimal("1.0"))

    /**
     * A file that can be used to store cached exchange rate values
     */
    private var cacheFile: File? = null

    /**
     * A cache storing exchange rate data. Can be set by using the setCache() method.
     * Will only be used as fallback in case a currency exchange rate could not be found.
     * Useful for offline modes.
     * The generateCache() method overwrites this variable with the current value represented
     * in the exchangeRates variable.
     */
    private var cache = mutableMapOf(Currency.EUR to BigDecimal("1.0"))

    /**
     * A UNIX timestamp that keeps track of when the exchange rates were updated last
     */
    private var updated: Long = 0

    /**
     * Flag that is set to true while updating
     */
    private var updating: Boolean = true

    /**
     * Flag that is set to true once an update has completed
     */
    private var valid: Boolean = false

    /**
     * @return The internal exchange rate data map
     */
    fun getExchangeRateData(): MutableMap<Currency, BigDecimal> {
        return this.exchangeRates
    }

    /**
     * Loads the cache data from the cache file
     */
    private fun loadCache() {
        if (this.cacheFile != null && this.cacheFile!!.exists()) {
            val file = FileInputStream(this.cacheFile)
            val obj = ObjectInputStream(file)
            @Suppress("UNCHECKED_CAST")
            this.cache = obj.readObject() as MutableMap<Currency, BigDecimal>
        } else {
            this.cache = mutableMapOf(Currency.EUR to BigDecimal("1.0"))
        }
    }

    /**
     * Store the current cache data in the cache file
     */
    private fun storeCache() {

        if (this.cacheFile != null) {
            val file = FileOutputStream(this.cacheFile)
            val obj = ObjectOutputStream(file)
            obj.writeObject(this.cache)
            file.close()
            obj.close()
        }
    }

    /**
     * Sets the cache file. This file will be used to store cached exchange rates
     * @param cacheFile: The file in which to store the cached data
     */
    fun setCacheFile(cacheFile: File?) {
        this.cacheFile = cacheFile
        this.loadCache()
    }

    /**
     * Sets the cache variable as a fallback for currency exchange rates that could not be found
     * @param cache: The cache to set
     */
    fun setCache(cache: MutableMap<Currency, BigDecimal>) {
        this.cache = cache
    }

    /**
     * Sets the cache to the current exchange rates and return that value.
     * @return The new cache value
     */
    fun generateCache(): Map<Currency, BigDecimal> {
        this.cache = this.exchangeRates
        return this.cache
    }

    /**
     * Checks if the values are currently valid
     * @return true if the values are valid. Else, return false.
     */
    fun isValid(): Boolean {
        // First, Check if all currencies are in the exchange rate data
        // The make sure that no update is running currently and the update() method deemed the stat valid
        return if (Currency.values().any { it !in this.exchangeRates }) false else this.valid && !this.updating
    }

    /**
     * Updates the exchange rate data. Will only update if more than a minute has passed since
     * the last update or if the force variable is set to true.
     * @param force: Forces an update if set to true
     * @param reset: Resets the internal exchange rate data before updating
     */
    fun update(force: Boolean = false, reset: Boolean = false) {
        this.updating = true
        this.valid = true // Might be set to false by the individual update* methods

        if (reset) {
            this.exchangeRates = mutableMapOf(Currency.EUR to BigDecimal("1.0"))
        }

        if (force or ((System.currentTimeMillis() - this.updated) > 60000)) {
            this.updated = System.currentTimeMillis()
            this.updateFiatCurrencyExchangeRates()
            this.updateCryptoCurrencyExchangeRates()
        } else {
            this.logger.fine("Skipping exchange rate update")
        }
        this.cache = this.exchangeRates
        this.storeCache()
        this.updating = false
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
                this.handleMissingExchangeRateData(currency)
            } catch (e: NumberFormatException) {
                this.handleMissingExchangeRateData(currency)
            }
        }
        this.exchangeRates[Currency.EUR] = BigDecimal("1.0") // Make sure Euro is set to 1.0
    }

    /**
     * Updates the exchange rate data for crypto-currencies
     */
    private fun updateCryptoCurrencyExchangeRates() {

        val one = BigDecimal("1.0")

        val url = "https://api.coinmarketcap.com/v1/ticker/?convert=EUR"
        val melonUrl = "https://api.coinmarketcap.com/v1/ticker/melon/?convert=EUR" // Add Melon through own url
        val exchangeRateData = this.getUrlData(url) + this.getUrlData(melonUrl)
        for (currency in Currency.getAllCryptoCurrencies()) {

            try {
                var price = exchangeRateData.split("\"symbol\": \"${currency.name}\"")[1]
                price = price.split("\"price_eur\": \"")[1].split("\"")[0]
                this.exchangeRates[currency] = one.divide(BigDecimal(price), 128, RoundingMode.HALF_UP)
            } catch (e: IndexOutOfBoundsException) {
                this.handleMissingExchangeRateData(currency)
            } catch (e: NumberFormatException) {
                this.handleMissingExchangeRateData(currency)
            }
        }
    }

    /**
     * Handles supplying exchange rate data if fetching them from the internet did not work
     * @param currency: The currency for which to use the cached value
     */
    private fun handleMissingExchangeRateData(currency: Currency) {

        when (currency) {
            Currency.EUR -> { }
            in this.cache -> {
                this.logger.info("Using cached value for $currency.")
                this.exchangeRates[currency] = this.cache[currency]!!
            }
            else -> {
                this.logger.warning("No valid exchange rate data for $currency.")
            }
        }
    }

    /**
     * Puts in a GET request to a URL
     * @param url: The URL to which to send the GET request
     * @return The response body of the GET request
     */
    private fun getUrlData(url: String): String {

        if (this.networkDisabled) {
            return ""
        }

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

        this.update()

        if (!this.isValid()) {
            this.logger.warning("Converting while values are invalid!")
        }

        if (source == destination) {
            return value
        }

        try {
            val sourceEuroValue = this.exchangeRates[source]!! // Not null
            val destinationEuroValue = this.exchangeRates[destination]!! // Not null
            return value.divide(sourceEuroValue, 128, RoundingMode.HALF_UP).times(destinationEuroValue)
        } catch (e: NullPointerException) {
            throw CurrencyConversionError(source, destination)
        }
    }
}
