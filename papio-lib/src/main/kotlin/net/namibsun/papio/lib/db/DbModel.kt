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

package net.namibsun.papio.lib.db

import net.namibsun.papio.lib.db.models.Transaction
import java.sql.PreparedStatement

/**
 * Abstract class that models a database model with an ID
 * @param table: The table in which the model is stored
 * @param id: The ID of the model in the table
 */
abstract class DbModel(val table: Table, val id: Int) {

    /**
     * Deletes the model from the database
     * @param dbHandler: The database handler to use
     */
    fun delete(dbHandler: DbHandler) {
        val stmt = dbHandler.connection.prepareStatement("DELETE FROM ${this.table.name} WHERE id=?")
        stmt.setInt(1, this.id)
        stmt.execute()
        stmt.close()
    }

    /**
     * Creates a semicolon-separated String representation of the Model
     * @return The model's String representation
     */
    override fun toString(): String {
        return "${this.table.name}; ID: ${this.id};"
    }
}

/**
 * Abstract class that models a database model with an ID and name
 * @param table: The table in which the model is stored
 * @param id: The ID of the model in the table
 * @param name: The unique name of the model in the database table
 */
abstract class NamedDbModel(table: Table, id: Int, val name: String) : DbModel(table, id) {

    /**
     * Creates a semicolon-separated String representation of the Model
     * @return The model's String representation
     */
    override fun toString(): String {
        return "${super.toString()} Name: ${this.name}"
    }

    /**
     * Static helper methods
     */
    companion object {

        /**
         * Helper method that combines share aspects of a database row creation for named models.
         * If the model already exists in the database, the existing one will be returned
         * @param dbHandler: The database handler to use
         * @param table: The database table type
         * @param name: The name of the named database model
         * @param stmt: A prepared statement that is executed to insert a new object into the database
         * @return The generated database model object
         * @throws IllegalArgumentException If the name of the model is a valid Integer to avoid ID/name conflicts
         */
        @JvmStatic
        protected fun createHelper(dbHandler: DbHandler, table: Table, name: String, stmt: PreparedStatement):
                NamedDbModel {

            val existing = dbHandler.getModel(table, name)
            if (existing != null) {
                stmt.close()
                return existing
            }

            stmt.execute()
            stmt.close()

            return dbHandler.getModel(table, name)!!
        }
    }
}

/**
 * Abstract class that models a database model that has associated transactions
 * @param table: The table in which the model is stored
 * @param id: The ID of the model in the table
 * @param name: The unique name of the model in the database table
 */
abstract class TransactionHolderModel(table: Table, id: Int, name: String) : NamedDbModel(table, id, name) {

    /**
     * Retrieves all transactions for the model from the database
     * @param dbHandler: The database handler to use
     * @return A list of transactions associated with this model
     */
    fun getTransactions(dbHandler: DbHandler): List<Transaction> {
        val stmt = dbHandler.connection.prepareStatement(
                "SELECT * FROM ${Table.TRANSACTIONS.tableName} WHERE ${this.table.transactionClassifier}=?"
        )
        stmt.setInt(1, this.id)
        stmt.execute()
        val results = stmt.resultSet

        val transactions = mutableListOf<Transaction>()
        while (results.next()) {
            transactions.add(Transaction.fromResultSet(results, dbHandler))
        }

        stmt.close()
        results.close()
        return transactions
    }
}