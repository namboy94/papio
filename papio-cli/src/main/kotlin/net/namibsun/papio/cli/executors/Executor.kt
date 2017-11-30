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

package net.namibsun.papio.cli.executors

import net.namibsun.papio.lib.db.DbHandler
import net.sourceforge.argparse4j.inf.ArgumentParser
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace

/**
 * Interface that acts as a common base class of all Executors
 */
interface Executor {

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
            System.exit(1)
            null!! // Can't be reached
        }
    }

    /**
     * Prompts a yes/no question to the user
     * @param message: The message to show the user
     * @return True if the user answered with 'y', false otherwise
     */
    fun getUserConfirmation(message: String): Boolean {
        println("$message (y/n)")
        var response = readLine()!!.toLowerCase()
        while (response !in listOf("y", "n")) {
            println("Please enter 'y' or 'n'")
            response = readLine()!!.toLowerCase()
        }
        return response == "y"
    }
}