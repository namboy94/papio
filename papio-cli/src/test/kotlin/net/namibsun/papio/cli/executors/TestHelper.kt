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