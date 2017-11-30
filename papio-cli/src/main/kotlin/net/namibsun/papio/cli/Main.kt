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

import net.namibsun.papio.cli.argparse.ModeParser
import net.namibsun.papio.cli.argparse.RootMode
import net.namibsun.papio.cli.executors.CategoryExecutor
import net.namibsun.papio.cli.executors.TransactionExecutor
import net.namibsun.papio.cli.executors.TransactionPartnerExecutor
import net.namibsun.papio.cli.executors.WalletExecutor

/**
 * The main entry point of the program
 */
fun main(args: Array<String>) {

    val modeParser = ModeParser(args)

    val trimmedArgs = modeParser.parse()
    val rootMode = modeParser.rootMode!!
    val actionMode = modeParser.actionMode!!

    val executor = when (rootMode) {
        RootMode.WALLET -> WalletExecutor(actionMode, trimmedArgs)
        RootMode.CATEGORY -> CategoryExecutor(actionMode, trimmedArgs)
        RootMode.TRANSACTIONPARTNER -> TransactionPartnerExecutor(actionMode, trimmedArgs)
        RootMode.TRANSACTION -> TransactionExecutor(actionMode, trimmedArgs)
    }

    executor.execute()
}
