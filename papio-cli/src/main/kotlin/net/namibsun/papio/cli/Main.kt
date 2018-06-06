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

package net.namibsun.papio.cli

import net.namibsun.papio.cli.executors.BackupExecutor
import net.namibsun.papio.cli.executors.CategoryExecutor
import net.namibsun.papio.cli.executors.ExpenseExecutor
import net.namibsun.papio.cli.executors.TransactionExecutor
import net.namibsun.papio.cli.executors.TransactionPartnerExecutor
import net.namibsun.papio.cli.executors.WalletExecutor
import net.namibsun.papio.cli.executors.TransferExecutor
import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.money.CurrencyConverter
import java.io.File
import java.sql.DriverManager
import java.util.logging.Level
import java.util.logging.LogManager

/**
 * The main entry point of the program.
 * Calls the execute method and wraps it in a try/catch block
 * @param args: The command line arguments passed to this program
 */
fun main(args: Array<String>) {

    CurrencyConverter.setCacheFile(File(Config.papioPath, "rates"))

    val rootLogger = LogManager.getLogManager().getLogger("")
    rootLogger.level = Level.INFO
    for (h in rootLogger.handlers) {
        h.level = Level.SEVERE
    }

    try {
        execute(args)
    } catch (e: HelpException) {
        e.printHelpAndExit()
    } catch (e: AbortException) {
        println(e.message)
        System.exit(1)
    }
}

/**
 * Executes the CLI program
 * @param args: The command line arguments passed to this program
 */
fun execute(args: Array<String>) {

    val dbHandler = prepareDatabase()

    val parsed = parseModes(args)
    val trimmedArgs = parsed.first
    val rootMode = parsed.second
    val actionMode = parsed.third

    val executor = when (rootMode) {
        RootMode.WALLET -> WalletExecutor()
        RootMode.CATEGORY -> CategoryExecutor()
        RootMode.TRANSACTIONPARTNER -> TransactionPartnerExecutor()
        RootMode.TRANSACTION -> TransactionExecutor()
        RootMode.BACKUP -> BackupExecutor()
        RootMode.TRANSFER -> TransferExecutor()
        RootMode.EXPENSE -> ExpenseExecutor()
    }
    executor.execute(trimmedArgs, dbHandler, actionMode)
}

/**
 * Initializes the local .papio directory and database file.
 * @return The database handler connected to the SQLite database file
 */
fun prepareDatabase(): DbHandler {

    if (!Config.papioPath.isDirectory && !Config.papioPath.exists()) {
        Config.papioPath.mkdirs()
    } else if (Config.papioPath.exists() && !Config.papioPath.isDirectory) {
        println("Could not create .papio directory. File exists.")
        System.exit(1)
    }
    val connection = DriverManager.getConnection("jdbc:sqlite:${Config.dbPath}")
    return DbHandler(connection)
}

/**
 * Parses the command line arguments for their modes.
 * Should the parsing encounter an invalid argument combination, a help message is printed and the
 * program exits.
 * @return The command line arguments without the mode arguments, the root mode, the action mode
 * @throws HelpException: If the user input is invalid and the root help message should be printed
 */
fun parseModes(args: Array<String>): Triple<Array<String>, RootMode, ActionMode?> {

    val argsList = args.toMutableList()

    val first: RootMode
    var second: ActionMode? = null

    try {
        first = RootMode.valueOf(argsList[0].toUpperCase())
        argsList.removeAt(0)
    } catch (e: IndexOutOfBoundsException) {
        throw HelpException()
    } catch (e: IllegalArgumentException) {
        throw HelpException()
    }

    try {
        second = ActionMode.valueOf(argsList[0].toUpperCase())
        argsList.removeAt(0)
    } catch (e: IndexOutOfBoundsException) {
    } catch (e: IllegalArgumentException) {
    }

    if (second != null) {
        if (second !in modeMap[first]!!) {
            throw HelpException()
        }
    }

    return Triple(argsList.toTypedArray(), first, second)
}
