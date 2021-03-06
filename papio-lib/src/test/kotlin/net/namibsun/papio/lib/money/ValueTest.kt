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
import java.lang.NumberFormatException
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Tests the Value class
 */
class ValueTest {

    /**
     * Tests initializing a Value object and checks that the correct value and currency is stored
     */
    @Test
    fun testInitialization() {
        val value = Value("100", Currency.EUR)
        assertEquals(BigDecimal("100"), value.value)
        assertEquals(Currency.EUR, value.currency)
    }

    @Test
    fun testInvalidNumbers() {
        for (value in listOf("1,3", "1:3", "100a", "", "10-1")) {
            try {
                Value(value, Currency.EUR)
                fail()
            } catch (e: NumberFormatException) {
            }
        }
    }

    /**
     * Adds two monetary values and checks that they have been added correctly
     */
    @Test
    fun testAddingValues() {
        val valueOne = Value("100", Currency.EUR)
        val valueTwo = Value("200", Currency.EUR)
        val sum = valueOne + valueTwo
        assertEquals(BigDecimal("300"), sum.value)
        assertEquals(Currency.EUR, sum.currency)
    }

    /**
     * Subtracts a value from another value and check if the subtraction has been done successfully
     */
    @Test
    fun testSubtractingValues() {
        val valueOne = Value("100", Currency.EUR)
        val valueTwo = Value("200", Currency.EUR)
        val value = valueOne - valueTwo
        assertEquals(BigDecimal("-100"), value.value)
        assertEquals(Currency.EUR, value.currency)
    }

    /**
     * Tests if the equality of a Value object is calculated correctly
     */
    @Test
    fun testEquality() {
        val value = Value("100", Currency.EUR)
        assertEquals(value, value)
        assertEquals(value, Value("100", Currency.EUR))
        assertEquals(value, Value("100.00", Currency.EUR))
        assertNotEquals(value, Value("200", Currency.EUR))
        assertNotEquals(value, Value("100", Currency.USD))
    }

    /**
     * Tests converting a value to another currency
     */
    @Test
    fun testConvertingValueToAnotherCurrency() {
        val value = Value("100", Currency.EUR)
        val converted = value.convert(Currency.USD)
        val expected = CurrencyConverter.convertValue(BigDecimal("100"), Currency.EUR, Currency.USD)

        assertEquals(expected, converted.value)
        assertEquals(Currency.USD, converted.currency)
    }

    /**
     * Tests formatting a Value object as a string
     */
    @Test
    fun testFormattingValue() {

        // Test defaults
        assertEquals(
                Value("1", Currency.EUR).format(),
                Value("1", Currency.EUR).format(false, ".", false, null)
        )

        // Test method options
        assertEquals("EUR 100.00", Value("100", Currency.EUR).format(false, ".", false, null))
        assertEquals("US$ 0.00", Value("0", Currency.USD).format(true, ".", false, null))
        assertEquals("ZAR -100,00", Value("-100", Currency.ZAR).format(false, ",", false, null))
        assertEquals("1.23 NAD", Value("1.2345", Currency.NAD).format(false, ".", true, null))
        assertEquals("EUR 1.23450", Value("1.234501234", Currency.EUR).format(false, ".", false, 5))

        // Test special situations
        assertEquals("BTC 0.12345678", Value("0.12345678123", Currency.BTC).format())
        assertEquals(
                "€ 0.1",
                Value("0.0988", Currency.EUR).format(useCurrencySymbol = true, overrideAccuracy = 1)
        )
    }

    /**
     * Tests serialization of Value objects
     */
    @Test
    fun testSerialization() {
        val value = Value("100.12345", Currency.EUR)
        val serialized = value.serialize()
        val deSerialized = Value.deserialize(serialized)
        assertEquals(value, deSerialized)
    }

    /**
     * Tests adding values with two differing values
     */
    @Test
    fun testAddingValuesInDifferingCurrencies() {
        val euro = Value("0", Currency.EUR)
        val bitcoin = Value("1", Currency.BTC)
        val sum = euro + bitcoin
        assertTrue(sum.value > BigDecimal(100)) // Assumes a BTC value above 100€
        assertEquals(sum.currency, Currency.EUR)
    }
}
