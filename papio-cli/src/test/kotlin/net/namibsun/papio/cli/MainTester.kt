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

package net.namibsun.papio.cli

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Class that tests the functions in the Main module of the CLI
 */
class MainTester {

    /**
     * Tests some edge cases of the mode parser
     */
    @Test
    fun testParser() {
        try {
            parseModes(arrayOf())
            fail()
        } catch (e: HelpException) {}
        try {
            parseModes(arrayOf("blargh"))
            fail()
        } catch (e: HelpException) {}
        assertEquals(null, parseModes(arrayOf("wallet")).third)
    }
}