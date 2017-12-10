package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.ActionMode
import net.namibsun.papio.cli.BaseExecutor
import net.namibsun.papio.cli.HelpException
import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.money.Value
import net.sourceforge.argparse4j.ArgumentParsers

/**
 * Executor that handles a transfer of money from one wallet to another
 */
class TransferExecutor : BaseExecutor {

    /**
     * Executes a transfer action
     * @param args: The command line arguments to parse
     * @param dbHandler: The database handler to use
     * @param mode: The mode for which to execute
     * @throws HelpException: If the user input is invalid and the root help message should be printed
     */
    override fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?) {

        if (mode != null) {
            throw HelpException()
        }

        val parser = ArgumentParsers.newFor("papio-cli transfer").build().defaultHelp(true)
        parser.addArgument("source").help("The sender name|ID of the transferred amount")
        parser.addArgument("dest").help("The recipient name|ID of the transferred amount")
        parser.addArgument("amount").help("The amount to transfer")
        parser.addArgument("--date").setDefault("today")
                .help("Specifies the date on which the transfer takes place.")
        val result = this.handleParserError(parser, args)

        val sender = Wallet.get(dbHandler, result.getString("source"))
        val receiver = Wallet.get(dbHandler, result.getString("dest"))

        when {
            sender == null -> {
                println("Wallet ${result.getString("source")} does not exist")
                System.exit(1)
            }
            receiver == null -> {
                println("Wallet ${result.getString("dest")} does not exist")
                System.exit(1)
            }
            else -> {
                val category = Category.create(dbHandler, "Transfer")
                val senderTransactionPartner = TransactionPartner.create(dbHandler, sender.name)
                val receiverTransactionPartner = TransactionPartner.create(dbHandler, receiver.name)

                val amount = try {
                    Value(result.getString("amount"), sender.getCurrency())
                } catch (e: NumberFormatException) {
                    println("${result.getString("amount")} is not a valid monetary amount")
                    System.exit(1)
                    null!! // Won't be reached
                }
                val converted = amount.convert(receiver.getCurrency())
                val date = IsoDate(result.getString("date"))

                val senderTransaction = Transaction.create(
                        dbHandler, sender, category, receiverTransactionPartner,
                        "Transferred $amount to ${receiver.name}",
                        Value(0.toString(), sender.getCurrency()) - amount, // *(-1)
                        date
                )

                val receiverTransaction = Transaction.create(
                        dbHandler, receiver, category, senderTransactionPartner,
                        "Transferred $converted from ${sender.name}",
                        converted, date
                )

                println("Created transactions:\n\n$senderTransaction\n$receiverTransaction")
            }
        }
    }
}