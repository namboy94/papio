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
     * @param mode: The mode for which to execute (Should be null for this class)
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
