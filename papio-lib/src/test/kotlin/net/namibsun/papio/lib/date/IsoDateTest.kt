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

package net.namibsun.papio.lib.date

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Class that tests the IsoDate class
 */
class IsoDateTest {

    /**
     * Tests if the IsoDate class accepts valid dates
     */
    @Test
    fun testValidDates() {
        for (date in listOf(
                "2017-01-01",
                "1970-01-01",
                "1000-10-10",
                "2017-01-31",
                "2017-02-28",
                "2016-02-29",
                "2017-03-31",
                "2017-04-30",
                "2017-05-31",
                "2017-06-30",
                "2017-07-31",
                "2017-08-31",
                "2017-09-30",
                "2017-10-31",
                "2017-11-30",
                "2017-12-31"
        )) {
            try {
                IsoDate(date)
            } catch (e: IllegalArgumentException) {
                fail()
            }
        }
    }

    /**
     * Tests if the IsoDate rejects invalid dates
     */
    @Test
    fun testInvalidDates() {
        for (date in listOf(
                "01.01.1970",
                "2017-01-0A",
                "2017-0A-01",
                "201A-01-01",
                "ABC",
                "2017:01:01",
                "2017/01/01",
                "01-01-2017",
                "2017-13-01",
                "2017-13-01",
                "2017-01-32",
                "2017-02-29",
                "2016-02-30",
                "2017-03-32",
                "2017-04-31",
                "2017-05-32",
                "2017-06-31",
                "2017-07-32",
                "2017-08-32",
                "2017-09-31",
                "2017-10-32",
                "2017-11-31",
                "2017-12-32",
                "2017-01-1",
                "2017-1-01",
                "1-01-01"
        )) {
            try {
                IsoDate(date)
                fail()
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    /**
     * Tests the getDay, getMonth and getYear method
     */
    @Test
    fun testDatePartGetting() {
        val date = IsoDate("2017-01-02")
        assertEquals(date.getDay(), "02")
        assertEquals(date.getMonth(), "01")
        assertEquals(date.getYear(), "2017")
    }
}