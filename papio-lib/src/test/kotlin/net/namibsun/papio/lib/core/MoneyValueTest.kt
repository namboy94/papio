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
import org.junit.Test
import kotlin.test.assertEquals


/**
 * Tests the MoneyValue class
 */
class MoneyValueTest {

    /**
     * Tests initializing a MoneyValue object and checks that the correct value and currency is stored
     */
    @Test
    fun testInitialization() {
        val value = MoneyValue(100, Currency.EUR)
        assertEquals(100, value.getValue())
        assertEquals(100, value.getValue(Currency.EUR))
        assertEquals(Currency.EUR, value.getCurrency())
    }

    /**
     * Adds two monetary values and checks that they have been added correctly
     */
    @Test
    fun testAddingValues() {
        val valueOne = MoneyValue(100, Currency.EUR)
        val valueTwo = MoneyValue(200, Currency.EUR)
        val value = valueOne + valueTwo
        assertEquals(300, value.getValue())
        assertEquals(Currency.EUR, value.getCurrency())
    }

    /**
     * Subtracts a value from another value and check if the subtraction has been done successfully
     */
    @Test
    fun testSubtractingValues() {
        val valueOne = MoneyValue(100, Currency.EUR)
        val valueTwo = MoneyValue(200, Currency.EUR)
        val value = valueOne - valueTwo
        assertEquals(-100, value.getValue())
        assertEquals(Currency.EUR, value.getCurrency())
    }

    /**
     * Tests retrieving the value in another currency
     */
    @Test
    fun testGettingValueInOtherCurrency() {
        val value = MoneyValue(100, Currency.EUR)
        val converted = (100 * CurrencyExchanger.getExchangeRates()[Currency.USD]!!).toInt()
        assertEquals(converted, value.getValue(Currency.USD))
    }

    /**
     * Tests converting a value to another currency
     */
    @Test
    fun testConvertingValueToAnotherCurrency() {
        val value = MoneyValue(100, Currency.EUR)
        value.convert(Currency.USD)
        val rates = CurrencyExchanger.getExchangeRates()

        assertEquals((100 * rates[Currency.USD]!!).toInt(), value.getValue(Currency.USD))
        assertEquals(value.getValue(), value.getValue(Currency.USD))
        assertEquals(Currency.USD, value.getCurrency())
    }
}