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