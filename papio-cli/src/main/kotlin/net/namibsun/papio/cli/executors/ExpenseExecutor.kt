package net.namibsun.papio.cli.executors

import net.namibsun.papio.cli.argparse.ActionMode
import net.namibsun.papio.cli.argparse.HelpPrinter
import net.namibsun.papio.lib.db.DbHandler

/**
 * Class that extends the Transaction Executor with the ability to automatically create
 * expenses without having to manually set the amount to store as negative
 */
class ExpenseExecutor : TransactionExecutor() {

    /**
     * Makes sure that expenses can only be created
     * @param args: The command line arguments to parse
     * @param dbHandler: The database handler to use
     * @param mode: The mode for which to execute
     */
    override fun execute(args: Array<String>, dbHandler: DbHandler, mode: ActionMode?) {
        when (mode) {
            ActionMode.CREATE -> { this.executeCreateExpense(args, dbHandler) }
            else -> { HelpPrinter().printAndExit() }
        }
    }

    /**
     * Executes the 'create' option for an expense transaction
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    private fun executeCreateExpense(args: Array<String>, dbHandler: DbHandler) {
        this.createTransaction(args, dbHandler, true)
    }
}