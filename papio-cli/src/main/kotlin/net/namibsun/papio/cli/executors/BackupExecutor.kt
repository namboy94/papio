package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.argparse.ActionMode
import net.namibsun.papio.cli.argparse.HelpPrinter
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
     */
    override fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?) {

        if (mode != null || args.isNotEmpty()) {
            HelpPrinter().printAndExit()
        } else {
            val papioDir = File(System.getProperty("user.home"), ".papio")
            val papioDb = File(papioDir.toString(), "data.db")
            val dbBackup = File(papioDir.toString(), "backup-${System.currentTimeMillis().toInt()}.db")
            papioDb.copyTo(dbBackup, true)
            println("Backup created: $dbBackup")
        }
    }
}
