package net.namibsun.papio.lib.core
import org.junit.Test
import org.junit.Assert.assertEquals


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
}
