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

package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.ActionMode
import net.namibsun.papio.cli.Config
import net.namibsun.papio.cli.HelpException
import net.namibsun.papio.cli.execute
import net.namibsun.papio.cli.prepareDatabase
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Tests the backup root action of the CLI
 */
class BackupTester : TestHelper() {

    /**
     * Tests simply creating a backup
     */
    @Test
    fun testCreatingBackup() {
        assertEquals(1, Config.papioPath.listFiles().size)
        execute(listOf("backup").toTypedArray())
        assertEquals(2, Config.papioPath.listFiles().size)
    }

    /**
     * Tests using too many arguments
     */
    @Test
    fun testGivingTooManyArguments() {
        try {
            execute(listOf("backup", "something").toTypedArray())
            fail()
        } catch (e: HelpException) {
        }
        try {
            BackupExecutor().execute(arrayOf("something"), prepareDatabase(), null)
            fail()
        } catch (e: HelpException) {
        }
        try {
            BackupExecutor().execute(arrayOf("something"), prepareDatabase(), ActionMode.LIST)
            fail()
        } catch (e: HelpException) {
        }
    }
}