package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.ActionMode
import net.namibsun.papio.cli.BaseExecutor
import net.namibsun.papio.cli.Config
import net.namibsun.papio.cli.HelpException
import net.namibsun.papio.lib.db.DbHandler
import java.io.File

/**
 * Class that implements Backup functionality
 */
class BackupExecutor : BaseExecutor {

    /**
     * Starts the backup if no action mode was supplied and args is empty
     * @param args: The command line arguments to parse
     * @param dbHandler: The database handler to use
     * @param mode: The mode for which to execute
     * @throws HelpException: If the user input is invalid and the root help message should be printed
     */
    override fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?) {

        if (mode != null || args.isNotEmpty()) {
            throw HelpException()
        } else {
            val dbBackup = File(Config.papioPath.toString(), "backup-${System.currentTimeMillis().toInt()}.db")
            Config.dbPath.copyTo(dbBackup, true)
            println("Backup created: $dbBackup")
        }
    }
}
