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
     * The Root mode of the parsed arguments
     */
    var rootMode: RootMode? = null

    /**
     * The Action mode of the parsed arguments
     */
    var actionMode: ActionMode? = null

    /**
     * The other mode of the parsed arguments
     */
    var otherMode: OtherMode? = null

    /**
     * Parses the command line arguments for their modes
     * After running this method, the rootMode and actionMode variables will be set to the correct
     * values. Should the parsing encounter an invalid argument, a help message is printed and the
     * program exits.
     * @return The command line arguments without the mode arguments
     */
    fun parse(): Array<String> {
        try {

            val firstArg = this.args[0].toUpperCase()

            if (firstArg in OtherMode.values().map { it.name }) {
                this.otherMode = OtherMode.valueOf(firstArg)
                this.args.removeAt(0)
            } else {
                val secondArg = this.args[1].toUpperCase()
                this.rootMode = RootMode.valueOf(firstArg)
                this.actionMode = ActionMode.valueOf(secondArg)
                this.args.removeAt(0)
                this.args.removeAt(0)
            }
        } catch (e: IllegalArgumentException) {
            this.printHelpMessageAndExit()
        } catch (e: IndexOutOfBoundsException) {
            this.printHelpMessageAndExit()
        }

        return this.args.toTypedArray()
    }

    /**
     * Prints a Help message and exits
     */
    private fun printHelpMessageAndExit() {
        println("papio\n" +
                "Options:\n\n" +
                "wallet             (create|delete|list|display) ...\n" +
                "category           (create|delete|list|display) ...\n" +
                "transactionpartner (create|delete|list|display) ...\n" +
                "transaction        (create|delete|list|display) ...\n" +
                "backup\n" +
                "transfer")
        System.exit(1)
    }
}