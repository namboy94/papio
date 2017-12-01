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