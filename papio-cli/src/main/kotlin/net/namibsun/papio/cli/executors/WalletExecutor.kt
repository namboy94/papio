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

import net.namibsun.papio.cli.AbortException
import net.namibsun.papio.cli.FullExecutor
import net.namibsun.papio.lib.exceptions.CurrencyConversionError
import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.Value
import net.sourceforge.argparse4j.ArgumentParsers

/**
 * Executor for the Wallet root action
 * Handles the management of wallets in the database
 */
class WalletExecutor : FullExecutor {

    /**
     * Executes the 'create' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeCreate(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli wallet create").build().defaultHelp(true)
        parser.addArgument("name")
                .help("The name of the wallet.")
        parser.addArgument("--currency")
                .choices(Currency.values().map { it.name }).setDefault("EUR")
                .help("The currency of the wallet.")
        parser.addArgument("--initial-value").setDefault("0")
                .help("The initial value of the wallet in cents.")

        val result = this.handleParserError(parser, args)

        val name = result.getString("name")

        val value = try {
            Value(result.getString("initial_value"), Currency.valueOf(result.getString("currency")))
        } catch (e: NumberFormatException) {
            throw AbortException("${result.getString("initial_value")} is not a valid monetary value")
        }

        val existing = Wallet.get(dbHandler, name)

        if (existing != null) {
            throw AbortException("Wallet\n$existing\nalready exists")
        } else {
            val wallet = Wallet.create(dbHandler, name, value)
            println("Wallet\n$wallet\nwas created successfully.")
        }
    }

    /**
     * Executes the 'delete' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDelete(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli wallet delete").build().defaultHelp(true)
        parser.addArgument("identifier").help("The name or ID of the wallet")
        val result = this.handleParserError(parser, args)

        val wallet = Wallet.get(dbHandler, result.getString("identifier"))
        if (wallet != null) {
            val confirm = this.getUserConfirmation(
                    "Delete wallet\n$wallet\nand all transactions in it?"
            )
            if (confirm) {
                wallet.delete(dbHandler)
                println("Wallet has been deleted")
            } else {
                println("Deleting wallet cancelled")
            }
        } else {
            throw AbortException("Wallet ${result.getString("identifier")} does not exist")
        }
    }

    /**
     * Executes the 'list' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeList(args: Array<String>, dbHandler: DbHandler) {

        var total = Value("0", Currency.EUR)
        var conversionError = false

        for (wallet in Wallet.getAll(dbHandler)) {
            println(wallet.toString(dbHandler))
            try {
                total += wallet.getBalance(dbHandler)
            } catch (e: CurrencyConversionError) {
                conversionError = true
            }
        }
        println("-------------------------")
        if (conversionError) {
            println("Total: ?")
        } else {
            println("Total: $total")
        }
    }

    /**
     * Executes the 'display' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDisplay(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli wallet display").build().defaultHelp(true)
        parser.addArgument("identifier").help("The name or ID of the wallet")
        parser.addArgument("-t", "--transactions")
                .type(Int::class.java).setDefault(-1)
                .help("Sets the amount of transactions to display. By default, all transactions are displayed")
        val result = this.handleParserError(parser, args)

        val wallet = Wallet.get(dbHandler, result.getString("identifier"))
        if (wallet != null) {
            println("${wallet.toString(dbHandler)}\n")

            val transactions = wallet.getTransactions(dbHandler).sortedByDescending { it.date }
            var limit = result.getInt("transactions")
            if (limit == -1 || limit > transactions.size) {
                limit = transactions.size
            }
            for (i in 0 until limit) {
                println(transactions[i])
            }
        } else {
            throw AbortException("Wallet ${result.getString("identifier")} does not exist")
        }
    }
}