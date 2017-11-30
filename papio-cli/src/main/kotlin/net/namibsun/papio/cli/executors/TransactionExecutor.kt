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
import net.namibsun.papio.lib.money.MoneyValue
import net.sourceforge.argparse4j.ArgumentParsers
import java.time.ZonedDateTime

/**
 * Executor for the Transaction root action
 * Handles the management of transactions in the database
 */
class TransactionExecutor : Executor {

    /**
     * Executes the 'create' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeCreate(args: Array<String>, dbHandler: DbHandler) {
        val now = ZonedDateTime.now()
        val parser = ArgumentParsers.newFor("papio-cli transaction create").build().defaultHelp(true)
        parser.addArgument("wallet")
                .help("The name or ID of the wallet this transaction takes place in." +
                        "The wallet must already exist.")
        parser.addArgument("category")
                .help("The name or ID of the category of this transaction." +
                        "If the category does not exist, it will be created")
        parser.addArgument("transactionpartner")
                .help("The name or ID of the transaction partner this transaction was done with." +
                        "If the transaction partner does not exist, the partner will be created")
        parser.addArgument("description")
                .help("A description of the transaction")
        parser.addArgument("amount")
                .type(Int::class.java)
                .help("The amount of money used in the transaction in the same currency as the wallet.")
        parser.addArgument("-d", "--date")
                .setDefault("${now.year}-${now.month}-${now.dayOfMonth}")
                .help("The date of the transaction in the format YYYY-MM-DD. Default to the current day.")
        val results = this.handleParserError(parser, args)

        val wallet = WalletExecutor().getWallet(dbHandler, results.getString("wallet"))
        var category = CategoryExecutor().getCategory(dbHandler, results.getString("category"))
        var partner = TransactionPartnerExecutor().getTransactionPartner(
                dbHandler, results.getString("transactionpartner"))

        if (wallet == null) {
            println("Wallet ${results.getString("wallet")} does not exist")
            System.exit(1)
        }
        if (category == null) {
            category = dbHandler.createCategory(results.getString("category"))
        }
        if (partner == null) {
            partner = dbHandler.createTransactionPartner(results.getString("transactionpartner"))
        }

        val transaction = dbHandler.createTransaction(
                wallet!!,
                category,
                partner,
                results.getString("description"),
                MoneyValue(
                        results.getInt("amount"),
                        wallet.getCurrency()
                )
        )

        println("Transaction Created:\n$transaction")
    }

    /**
     * Executes the 'delete' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDelete(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli transaction delete").build().defaultHelp(true)
        parser.addArgument("identifier").type(Int::class.java).help("The ID of the transaction")
        val result = this.handleParserError(parser, args)

        val transaction = dbHandler.getTransaction(result.getInt("identifier"))
        if (transaction != null) {
            val confirm = this.getUserConfirmation("Delete transaction\n$transaction\n?")
            if (confirm) {
                transaction.delete(dbHandler)
                println("Transaction has been deleted")
            } else {
                println("Transaction wallet cancelled")
            }
        } else {
            println("Transaction not found.")
        }
    }

    /**
     * Executes the 'list' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeList(args: Array<String>, dbHandler: DbHandler) {
        println("Listing transactions is not supported. Use the display option with a wallet, category or " +
                "transaction partner instead.")
    }

    /**
     * Executes the 'display' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDisplay(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli transaction display").build().defaultHelp(true)
        parser.addArgument("id").type(Int::class.java).help("The ID of the transaction")
        val result = this.handleParserError(parser, args)

        val transaction = dbHandler.getTransaction(result.getInt("id"))
        if (transaction != null) {
            println(transaction)
        } else {
            println("Transaction not found")
        }
    }
}