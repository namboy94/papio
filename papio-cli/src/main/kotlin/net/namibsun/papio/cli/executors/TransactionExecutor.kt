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

import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.MoneyValue
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments

/**
 * Executor for the Transaction root action
 * Handles the management of transactions in the database
 */
open class TransactionExecutor : FullExecutor {

    /**
     * Executes the 'create' option for a normal transaction
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeCreate(args: Array<String>, dbHandler: DbHandler) {
        this.createTransaction(args, dbHandler, false)
    }

    /**
     * Executes the 'create' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     * @param expense: Flag that switches to and from 'expense' mode, which negates the value of
     *                 the transaction beforehand
     */
    protected fun createTransaction(args: Array<String>, dbHandler: DbHandler, expense: Boolean) {

        val action = if (expense) {
            "expense"
        } else {
            "transaction"
        }

        val parser = ArgumentParsers.newFor("papio-cli $action create").build().defaultHelp(true)
        parser.addArgument("wallet")
                .help("The name or ID of the wallet this $action takes place in." +
                        "The wallet must already exist if the --create-wallet flag is not set.")
        parser.addArgument("category")
                .help("The name or ID of the category of this $action." +
                        "The category must already exist if the --create-wallet flag is not set.")
        parser.addArgument("transactionpartner")
                .help("The name or ID of the transaction partner this $action was done with." +
                        "The transaction partner must already exist if the --create-wallet flag is not set.")
        parser.addArgument("description")
                .help("A description of the transaction")
        parser.addArgument("amount")
                .type(Int::class.java)
                .help("The amount of money used in the $action in the same currency as the wallet.")
        parser.addArgument("-d", "--date")
                .setDefault("today")
                .help("The date of the $action in the format YYYY-MM-DD. Default to the current day.")
        parser.addArgument("--create-category").action(Arguments.storeTrue())
                .help("Creates the category if it does not exist yet")
        parser.addArgument("--create-transactionpartner").action(Arguments.storeTrue())
                .help("Creates the transaction partner if he doesn't exist yet")
        parser.addArgument("--create-wallet").action(Arguments.storeTrue())
                .help("Creates the wallet if it doesn't exist yet. Defaults to an initial value of EUR 0.00. " +
                        "This may be changed with the --create-wallet-initial-value and " +
                        "--create-wallet-currency options")
        parser.addArgument("--create-wallet-initial-value").type(Int::class.java).setDefault(0)
                .help("Specifies the initial value of a newly created wallet")
        parser.addArgument("--create-wallet-currency")
                .choices(Currency.values().map { it.name }).setDefault("EUR")
                .help("Specifies the currency of a newly created wallet")
        val results = this.handleParserError(parser, args)

        var wallet = WalletExecutor().getWallet(dbHandler, results.getString("wallet"))
        var category = CategoryExecutor().getCategory(dbHandler, results.getString("category"))
        var partner = TransactionPartnerExecutor()
                .getTransactionPartner(dbHandler, results.getString("transactionpartner"))

        if (wallet == null) {
            if (results.getBoolean("create_wallet")) {
                wallet = dbHandler.createWallet(
                        results.getString("wallet"),
                        MoneyValue(
                                results.getInt("create_wallet_initial_value"),
                                Currency.valueOf(results.getString("create_wallet_currency"))
                        )
                )
            } else {
                println("Wallet ${results.getString("wallet")} does not exist")
                System.exit(1)
            }
        }

        if (category == null) {
            if (results.getBoolean("create_category")) {
                category = dbHandler.createCategory(results.getString("category"))
            } else {
                println("Category ${results.getString("category")} does not exist")
                System.exit(1)
            }
        }
        if (partner == null) {
            if (results.getBoolean("create_transactionpartner")) {
                partner = dbHandler.createTransactionPartner(results.getString("transactionpartner"))
            } else {
                println("Transaction Partner ${results.getString("transactionpartner")} does not exist")
                System.exit(1)
            }
        }

        val amount = if (expense) {
            results.getInt("amount") * -1
        } else {
            results.getInt("amount")
        }
        val value = MoneyValue(amount, wallet!!.getCurrency())

        try {
            val transaction = dbHandler.createTransaction(
                    wallet,
                    category!!,
                    partner!!,
                    results.getString("description"),
                    value,
                    IsoDate(results.getString("date"))
            )

            println("Transaction Created:\n$transaction")
        } catch (e: IllegalArgumentException) {
            println("Date value '${results.getString("date")} is invalid")
        }
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
        for (transaction in dbHandler.getTransactions()) {
            println(transaction)
        }
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