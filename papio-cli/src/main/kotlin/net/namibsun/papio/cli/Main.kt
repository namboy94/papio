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

import net.namibsun.papio.cli.argparse.ActionMode
import net.namibsun.papio.cli.argparse.ModeParser
import net.namibsun.papio.cli.argparse.RootMode
import net.namibsun.papio.cli.executors.CategoryExecutor
import net.namibsun.papio.cli.executors.TransactionExecutor
import net.namibsun.papio.cli.executors.TransactionPartnerExecutor
import net.namibsun.papio.cli.executors.WalletExecutor
import net.namibsun.papio.lib.db.DbHandler
import java.io.File
import java.sql.DriverManager

/**
 * The main entry point of the program
 */
fun main(args: Array<String>) {

    val papioDir = File(System.getProperty("user.home"), ".papio")
    val papioDb = File(papioDir.toString(), "data.db")

    if (!papioDir.isDirectory && !papioDir.exists()) {
        papioDir.mkdirs()
    } else if (papioDir.exists() && !papioDir.isDirectory) {
        println("Could not create .papio directory. File exists.")
        System.exit(1)
    }
    val connection = DriverManager.getConnection("jdbc:sqlite:$papioDb")
    val dbHandler = DbHandler(connection)

    val modeParser = ModeParser(args)

    val trimmedArgs = modeParser.parse()
    val rootMode = modeParser.rootMode!!
    val actionMode = modeParser.actionMode!!

    val executor = when (rootMode) {
        RootMode.WALLET -> WalletExecutor(actionMode, trimmedArgs, dbHandler)
        RootMode.CATEGORY -> CategoryExecutor(actionMode, trimmedArgs, dbHandler)
        RootMode.TRANSACTIONPARTNER -> TransactionPartnerExecutor(actionMode, trimmedArgs, dbHandler)
        RootMode.TRANSACTION -> TransactionExecutor(actionMode, trimmedArgs, dbHandler)
    }

    when (actionMode) {
        ActionMode.LIST -> executor.executeList()
        ActionMode.DISPLAY -> executor.executeDisplay()
        ActionMode.CREATE -> executor.executeCreate()
        ActionMode.DELETE -> executor.executeDelete()
    }
}
