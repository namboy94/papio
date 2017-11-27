package net.namibsun.papio.lib.core

import net.namibsun.papio.lib.exceptions.CurrencyNotFoundException

/**
 * Singleton object that handles the exchanging of monetary values from one currency to another.
 */
object CurrencyExchanger {

    /**
     * Initializes the singleton by updating the exchange rate data
     */
    init {
        this.update()
    }

    /**
     * The current exchange rates with Euro as the base currency
     */
    private var exchangeRates = hashMapOf(Currency.EUR to 1.0)

    /**
     * A UNIX timestamp that keeps track of when the exchange rates were updated last
     */
    private var updated: Long = 0

    /**
     * Updates the exchange rate data. Will only update if more than a minute has passed since
     * the last update.
     */
    fun update() {
        if ((this.updated - System.currentTimeMillis()) > 60) {
            // update
            this.updated = System.currentTimeMillis()
        }
    }

    /**
     * Converts a value from one currency to another.
     * @param value: The value to convert
     * @param source: The source currency from which to convert from
     * @param destination: The destination currency into which to convert to
     * @return The converted value
     * @throws CurrencyNotFoundException: If a currency does not exist in the exchange rate data
     *                                    This should in theory never happen.
     */
    fun convertValue(value: Int, source: Currency, destination: Currency): Int {

        if (source == destination) {
            return value
        }

        val sourceEuroValue = this.exchangeRates[source] ?: throw CurrencyNotFoundException(source)
        val destinationEuroValue = this.exchangeRates[destination] ?: throw CurrencyNotFoundException(destination)

        return ((value / sourceEuroValue) * destinationEuroValue).toInt()

    }
}