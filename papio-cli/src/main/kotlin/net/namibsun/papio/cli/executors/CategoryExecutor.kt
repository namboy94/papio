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
import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.db.models.Category
import net.sourceforge.argparse4j.ArgumentParsers

/**
 * Executor for the Category Root action
 * Manages categories in the database
 */
class CategoryExecutor : FullExecutor {

    /**
     * Executes the 'create' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeCreate(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli category create").build().defaultHelp(true)
        parser.addArgument("name").help("The name of the new category")
        val results = this.handleParserError(parser, args)
        val original = Category.get(dbHandler, results.getString("name"))
        val category = Category.create(dbHandler, results.getString("name"))
        if (original == null) {
            println("Category created:\n$category")
        } else {
            throw AbortException("Category already exists:\n$category")
        }
    }

    /**
     * Executes the 'delete' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDelete(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli category delete").build().defaultHelp(true)
        parser.addArgument("identifier").help("The name or ID of the category")
        val result = this.handleParserError(parser, args)

        val category = Category.get(dbHandler, result.getString("identifier"))
        if (category != null) {
            val confirm = this.getUserConfirmation("Delete category\n$category\nand all transactions using it?")
            if (confirm) {
                category.delete(dbHandler)
                println("Category has been deleted")
            } else {
                println("Deleting category cancelled")
            }
        } else {
            throw AbortException("Category ${result.getString("identifier")} does not exist")
        }
    }

    /**
     * Executes the 'list' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeList(args: Array<String>, dbHandler: DbHandler) {
        for (category in Category.getAll(dbHandler)) {
            println(category)
        }
    }

    /**
     * Executes the 'display' option
     * @param args: The command line arguments without root and action mode arguments
     * @param dbHandler: The database handler to use
     */
    override fun executeDisplay(args: Array<String>, dbHandler: DbHandler) {
        val parser = ArgumentParsers.newFor("papio-cli category display").build().defaultHelp(true)
        parser.addArgument("identifier").help("The name or ID of the category")
        parser.addArgument("-t", "--transactions")
                .type(Int::class.java).setDefault(-1)
                .help("Sets the amount of transactions to display. By default, all transactions are displayed")
        val result = this.handleParserError(parser, args)

        val category = Category.get(dbHandler, result.getString("identifier"))
        if (category != null) {
            println("$category\n")

            val transactions = category.getTransactions(dbHandler).sortedByDescending { it.date }
            var limit = result.getInt("transactions")
            if (limit == -1 || limit > transactions.size) {
                limit = transactions.size
            }
            for (i in 0 until limit) {
                println(transactions[i])
            }
        } else {
            throw AbortException("Category ${result.getString("identifier")} does not exist")
        }
    }
}
