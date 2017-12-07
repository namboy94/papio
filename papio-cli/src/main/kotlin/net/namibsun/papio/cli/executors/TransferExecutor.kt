package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.argparse.ActionMode
import net.namibsun.papio.cli.argparse.HelpPrinter
import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.DbHandler
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
     */
    override fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?) {

        if (mode != null) {
            HelpPrinter().printAndExit()
        }

        val parser = ArgumentParsers.newFor("papio-cli transfer").build().defaultHelp(true)
        parser.addArgument("source").help("The sender name|ID of the transferred amount")
        parser.addArgument("dest").help("The recipient name|ID of the transferred amount")
        parser.addArgument("amount").type(Int::class.java).help("The amount to transfer")
        parser.addArgument("--date").setDefault("today")
                .help("Specifies the date on which the transfer takes place.")
        val result = this.handleParserError(parser, args)

        val sender = WalletExecutor().getWallet(dbHandler, result.getString("source"))
        val receiver = WalletExecutor().getWallet(dbHandler, result.getString("dest"))

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
                val category = dbHandler.createCategory("Transfer")
                val senderTransactionPartner = dbHandler.createTransactionPartner(sender.name)
                val receiverTransactionPartner = dbHandler.createTransactionPartner(receiver.name)

                val amount = MoneyValue(result.getInt("amount"), sender.getCurrency())
                val converted = amount.convert(receiver.getCurrency())
                val date = IsoDate(result.getString("date"))

                val senderTransaction = dbHandler.createTransaction(
                        sender, category, receiverTransactionPartner,
                        "Transferred $amount to ${receiver.name}",
                        MoneyValue(0, sender.getCurrency()) - amount, // *(-1)
                        date
                )

                val receiverTransaction = dbHandler.createTransaction(
                        receiver, category, senderTransactionPartner,
                        "Transferred $converted from ${sender.name}",
                        converted, date
                )

                println("Created transactions:\n\n$senderTransaction\n$receiverTransaction")
            }
        }
    }
}