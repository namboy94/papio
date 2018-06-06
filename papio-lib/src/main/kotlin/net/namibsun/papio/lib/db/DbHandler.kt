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

package net.namibsun.papio.lib.db

import net.namibsun.papio.lib.db.models.Wallet
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.Category
import java.sql.Connection

/**
 * Class that manages database calls
 * @param connection: A JDBC compatible database connection
 */
class DbHandler(val connection: Connection) {

    /**
     * Initializes the database tables
     */
    init {
        this.connection.autoCommit = true
        val stmt = this.connection.createStatement()
        stmt.execute("PRAGMA foreign_keys = ON")

        for (table in Table.values()) {
            stmt.execute(table.creatorSql)
        }
        stmt.close()
    }

    /**
     * Closes the database connection
     */
    fun close() {
        this.connection.close()
    }

    /**
     * Retrieves a model from the database based on its ID.
     * @param table: The table from which to fetch the model
     * @param id: The ID of the model to fetch
     * @return The retrieved model or null if none was found
     */
    fun getModel(table: Table, id: Int): DbModel? {
        val stmt = this.connection.prepareStatement("SELECT * FROM ${table.tableName} WHERE id=?")
        stmt.setInt(1, id)
        stmt.execute()
        val result = stmt.resultSet

        var model: DbModel? = null
        if (result.next()) {
            model = when (table) {
                Table.WALLETS -> Wallet.fromResultSet(result)
                Table.TRANSACTIONS -> Transaction.fromResultSet(result, this)
                Table.TRANSACTION_PARTNERS -> TransactionPartner.fromResultSet(result)
                Table.CATEGORIES -> Category.fromResultSet(result)
            }
        }

        stmt.close()
        result.close()
        return model
    }

    /**
     * Retrieves a named model from the database based on a string that either represents their ID or name
     * @param table: The table from which to fetch the model
     * @param nameOrId: The ID or name of the model to fetch
     * @return The retrieved model or null if none was found
     */
    fun getModel(table: Table, nameOrId: String): NamedDbModel? {

        // Check for ID first, since otherwise some rows could become unreachable using a CLI
        var model = try {
            this.getModel(table, nameOrId.toInt()) as NamedDbModel
        } catch (e: IndexOutOfBoundsException) {
            null
        } catch (e: NumberFormatException) {
            null
        }

        if (model == null) {
            val stmt = this.connection.prepareStatement("SELECT * FROM ${table.tableName} WHERE name=?")
            stmt.setString(1, nameOrId)
            stmt.execute()
            val result = stmt.resultSet

            if (result.next()) {
                model = when (table) {
                    Table.WALLETS -> Wallet.fromResultSet(result)
                    Table.TRANSACTION_PARTNERS -> TransactionPartner.fromResultSet(result)
                    Table.CATEGORIES -> Category.fromResultSet(result)
                    Table.TRANSACTIONS -> null // Transactions don't have names
                }
            }

            stmt.close()
            result.close()
        }
        return model
    }

    /**
     * Retrieves all models in a database table
     * @param table: The table for which to fetch all models
     * @return A List of Database model objects
     */
    fun getModels(table: Table): List<DbModel> {

        val stmt = this.connection.prepareStatement("SELECT * FROM ${table.tableName}")
        stmt.execute()
        val result = stmt.resultSet

        val models = mutableListOf<DbModel>()
        while (result.next()) {
            models.add( when (table) {
                Table.WALLETS -> Wallet.fromResultSet(result)
                Table.TRANSACTIONS -> Transaction.fromResultSet(result, this)
                Table.TRANSACTION_PARTNERS -> TransactionPartner.fromResultSet(result)
                Table.CATEGORIES -> Category.fromResultSet(result)
            })
        }

        stmt.close()
        result.close()
        return models
    }
}
