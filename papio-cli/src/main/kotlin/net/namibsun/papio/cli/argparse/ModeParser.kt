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

package net.namibsun.papio.cli.argparse

/**
 * Handles the initial parsing of the modes
 * @param args: The Command line arguments
 */
class ModeParser(args: Array<String>) {

    /**
     * The command line arguments as a mutable list
     */
    private val args = args.toMutableList()

    /**
     * Parses the command line arguments for their modes.
     * Should the parsing encounter an invalid argument combination, a help message is printed and the
     * program exits.
     * @return The command line arguments without the mode arguments, the root mode, the action mode
     */
    fun parse(): Triple<Array<String>, RootMode, ActionMode?> {

        var first: RootMode? = null
        var second: ActionMode? = null

        try {
            first = RootMode.valueOf(this.args[0].toUpperCase())
            this.args.removeAt(0)
        } catch (e: IndexOutOfBoundsException) {
            HelpPrinter().printAndExit()
        } catch (e: IllegalArgumentException) {
            HelpPrinter().printAndExit()
        }
        first = first!!

        try {
            second = ActionMode.valueOf(this.args[0].toUpperCase())
            this.args.removeAt(0)
        } catch (e: IndexOutOfBoundsException) {
        } catch (e: IllegalArgumentException) {
        }

        if (second != null) {
            if (second !in modeMap[first]!!) {
                HelpPrinter().printAndExit()
            }
        }

        return Triple(this.args.toTypedArray(), first, second)
    }
}