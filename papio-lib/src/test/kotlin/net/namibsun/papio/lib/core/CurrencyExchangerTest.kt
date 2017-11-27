package net.namibsun.papio.lib.core

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests the CurrencyExchanger singleton
 */
class CurrencyExchangerTest {

    /**
     * Checks that the CurrencyExchanger object provides exchange rates for all currently supported
     * currencies.
     */
    @Test
    fun testInitialization() {
        val rates = CurrencyExchanger.getExchangeRates()
        for (currency in Currency.values()) {
            assertTrue(currency in rates)
        }
        assertEquals(1.0, rates[Currency.EUR])
    }

}