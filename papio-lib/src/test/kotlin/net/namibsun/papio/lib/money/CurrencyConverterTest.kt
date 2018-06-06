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

import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Tests the CurrencyExchanger singleton
 */
class CurrencyConverterTest {

    /**
     * Checks that the CurrencyExchanger object provides exchange rates for all currently supported
     * currencies.
     */
    @Test
    fun testInitialization() {
        assertTrue(CurrencyConverter.isValid())
        CurrencyConverter.update()
        assertTrue(CurrencyConverter.isValid())

        for (currency in Currency.values()) {
            assertTrue(currency in CurrencyConverter.getExchangeRateData())
        }
        assertEquals(BigDecimal("1.0"), CurrencyConverter.getExchangeRateData()[Currency.EUR])
        assertTrue(CurrencyConverter.getExchangeRateData()[Currency.ZAR]!! > BigDecimal("1.0"))
        assertTrue(CurrencyConverter.getExchangeRateData()[Currency.BTC]!! < BigDecimal("1.0"))
    }

    /**
     * Tests all currencies
     */
    @Test
    fun testAllCurrencies() {
        val value = Value("100.00", Currency.EUR)
        for (currency in Currency.values()) {
            val converted = value.convert(currency)
            assertEquals(currency, converted.currency)
            if (currency == Currency.EUR) {
                assertEquals(value.value, converted.value)
            } else {
                assertNotEquals(value.value, converted.value)
            }
        }
    }

    /**
     * Tests converting a bitcoin value to euro
     */
    @Test
    fun testConvertingBitcoin() {
        val converted = CurrencyConverter.convertValue(BigDecimal("0.1"), Currency.BTC, Currency.EUR)
        assertTrue(converted > BigDecimal(10)) // Assumes BTC is worth more than 100 €
    }

    /**
     * Tests using the cache functionality of the CurrencyConverter
     */
    @Test
    fun testUsingCache() {

        CurrencyConverter.networkDisabled = true

        assertNotEquals(CurrencyConverter.getExchangeRateData()[Currency.BTC], BigDecimal("100"))

        CurrencyConverter.setCache(mutableMapOf(Currency.BTC to BigDecimal("100")))
        CurrencyConverter.update(true)
        assertTrue(Currency.BTC in CurrencyConverter.getExchangeRateData())
        assertEquals(CurrencyConverter.getExchangeRateData()[Currency.BTC], BigDecimal("100"))

        CurrencyConverter.networkDisabled = false

        CurrencyConverter.update(true)
        assertNotEquals(CurrencyConverter.getExchangeRateData()[Currency.BTC], BigDecimal("100"))

        assertEquals(CurrencyConverter.generateCache(), CurrencyConverter.getExchangeRateData())
    }

    /**
     * Tests making use of the cache file
     */
    @Test
    fun testUsingCacheFile() {
        CurrencyConverter.setCacheFile(File("cache"))
        CurrencyConverter.update(true)
        val rates = CurrencyConverter.getExchangeRateData()
        val fileIS = FileInputStream("cache")
        val objIS = ObjectInputStream(fileIS)
        @Suppress("UNCHECKED_CAST")
        val cached = objIS.readObject() as MutableMap<Currency, BigDecimal>
        assertEquals(cached, rates)
        CurrencyConverter.setCacheFile(null)
        File("cache").delete()
    }
}