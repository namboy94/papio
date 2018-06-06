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

import net.namibsun.papio.cli.Config
import net.namibsun.papio.cli.prepareDatabase
import org.junit.After
import org.junit.Before
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

/**
 * Abstract class that contains common operations of a unit testing class
 * Sets up the config singleton and handles cleanup
 */
abstract class TestHelper {

    /**
     * Sets the Config singleton values
     */
    init {
        Config.papioPath = File("papio-testdir")
        Config.dbPath = File("papio-testdir/data.db")
        Config.cliConfirm = false
    }

    /**
     * The database handler to use while testing
     */
    protected val dbHandler = prepareDatabase()

    /**
     * The OutputStream that replaces stdout for checking print output
     */
    protected var out = ByteArrayOutputStream()

    /**
     * Sets the standard output stream to this.out
     */
    @Before
    fun setUp() {
        System.setOut(PrintStream(this.out))
    }

    /**
     * Closes the database connection and deletes the temporary test files
     */
    @After
    fun tearDown() {
        this.dbHandler.close()
        Config.papioPath.deleteRecursively()
    }
}