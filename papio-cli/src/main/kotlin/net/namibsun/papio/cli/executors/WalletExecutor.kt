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

import net.namibsun.papio.cli.argparse.ActionMode
import net.namibsun.papio.lib.db.DbHandler
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException

class WalletExecutor(
        private val mode: ActionMode,
        private val args: Array<String>,
        private val dbHandler: DbHandler) : Executor {

    /**
     * Executes the 'create' option
     */
    override fun executeCreate() {
        val parser = ArgumentParsers.newFor("papio wallet create").build().defaultHelp(true)
        parser.addArgument("name").help("A")
        parser.addArgument("--currency").choices(listOf("A")).setDefault("A").help("A")
        parser.addArgument("--initial-value").type(Int::class.java).setDefault(0).help("B")

        try {
            val result = parser.parseArgs(this.args)
            val existing = this.dbHandler.getWallet(result.getString("name"))
            if (existing != null) {
                println("Wallet ${result.getString("name")} exists!")
                System.exit(1)
            } else {
                val wallet = this.dbHandler.createWallet(
                        result.getString("name"),
                        result.getInt("initial-value"),
                        result.getString("name")
                )
                println("Created wallet ${} with initial value")
            }
        } catch (e: ArgumentParserException) {
            parser.handleError(e)
            System.exit(1)
        }

    }

    /**
     * Executes the 'delete' option
     */
    override fun executeDelete() {}

    /**
     * Executes the 'list' option
     */
    override fun executeList() {}

    /**
     * Executes the 'display' option
     */
    override fun executeDisplay() {}
}