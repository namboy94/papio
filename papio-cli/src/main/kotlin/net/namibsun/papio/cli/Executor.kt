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

import net.namibsun.papio.lib.db.DbHandler
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace

/**
 * Base executor interface that specifies the execute() method and various helper methods
 */
interface BaseExecutor {

    /**
     * Executes the Executor for a specified mode
     * @param args: The command line arguments to parse
     * @param dbHandler: The database handler to use
     * @param mode: The mode for which to execute
     */
    fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?)

    /**
     * Handles a parser error. On error, a helpful message will be output and the program will exit.
     * @param parser: The parser to use
     * @param args: The command line arguments to parse
     * @return The parsed namespace
     */
    fun handleParserError(parser: ArgumentParser, args: Array<String>): Namespace {
        return try {
            parser.parseArgs(args)
        } catch (e: ArgumentParserException) {
            parser.handleError(e)
            throw AbortException("")
        }
    }

    /**
     * Prompts a yes/no question to the user
     * Takes the cliConfirm flag in the Config into consideration.
     * @param message: The message to show the user
     * @return True if the user answered with 'y', false otherwise
     *         Always returns true if the cliConfirm Config flag is set to false
     */
    fun getUserConfirmation(message: String): Boolean {

        return if (Config.cliConfirm) {
            println("$message (y/n)")
            var response = readLine()!!.toLowerCase()
            while (response !in listOf("y", "n")) {
                println("Please enter 'y' or 'n'")
                response = readLine()!!.toLowerCase()
            }
            response == "y"
        } else {
            Config.autoResponse == "y"
        }
    }
}

/**
 * Interface that acts as a common base for Executors that implement all ActionModes.
 */
interface FullExecutor : BaseExecutor {

    /**
     * Automatically delegates the execution to the new methods specified in this interface
     * @param args: The command line arguments to parse
     * @param dbHandler: The database handler to use
     * @param mode: The mode for which to execute
     * @throws HelpException: If the user input is invalid and the root help message should be printed
     */
    override fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?) {
        when (mode) {
            ActionMode.LIST -> this.executeList(args, dbHandler)
            ActionMode.DISPLAY -> this.executeDisplay(args, dbHandler)
            ActionMode.CREATE -> this.executeCreate(args, dbHandler)
            ActionMode.DELETE -> this.executeDelete(args, dbHandler)
            null -> throw HelpException()
        }
    }

    /**
     * Executes the 'create' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    fun executeCreate(args: Array<String>, dbHandler: DbHandler)

    /**
     * Executes the 'delete' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    fun executeDelete(args: Array<String>, dbHandler: DbHandler)

    /**
     * Executes the 'list' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    fun executeList(args: Array<String>, dbHandler: DbHandler)

    /**
     * Executes the 'display' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    fun executeDisplay(args: Array<String>, dbHandler: DbHandler)
}