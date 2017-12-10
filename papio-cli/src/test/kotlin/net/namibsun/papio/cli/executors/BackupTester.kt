package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.Config
import net.namibsun.papio.cli.execute
import net.namibsun.papio.cli.prepareDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

/**
 * Tests the backup root action of the CLI
 */
class BackupTester {

    /**
     * Sets up the config file paths for testing and stops System exits
     */
    @Before
    fun setUp() {
        Config.papioPath = File("papio-testdir")
        Config.dbPath = File("papio-testdir/data.db")
        prepareDatabase().close()
    }

    /**
     * Deletes the temporary config files
     */
    @After
    fun deleteConfig() {
        Config.papioPath.deleteRecursively()
    }

    /**
     * Tests simply creating a backup
     */
    @Test
    fun testCreatingBackup() {
        assertEquals(1, Config.papioPath.listFiles().size)
        execute(listOf("backup").toTypedArray())
        assertEquals(2, Config.papioPath.listFiles().size)
    }
}