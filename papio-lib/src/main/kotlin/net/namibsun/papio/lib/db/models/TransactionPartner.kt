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

package net.namibsun.papio.lib.db.models

import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.db.NamedDbModel
import net.namibsun.papio.lib.db.Table
import net.namibsun.papio.lib.db.TransactionHolderModel
import java.sql.ResultSet

@Suppress("EqualsOrHashCode")
/**
 * Class that models a transaction partner in the database
 * @param id: The ID of the transaction partner
 * @param name: The name of the transaction partner
 */
class TransactionPartner(id: Int, name: String) : TransactionHolderModel(Table.TRANSACTION_PARTNERS, id, name) {

    /**
     * Checks for equality with another object
     * @param other: The other object
     * @return true if the objects are equal, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (other is TransactionPartner) {
            other.id == this.id && other.name == this.name
        } else {
            false
        }
    }

    /**
     * Static Methods
     */
    companion object {

        /**
         * Generates a TransactionPartner object from a TransactionPartner
         * @param resultSet: The ResultSet to use to generate the Wallet object
         * @return The generated TransactionPartner object
         */
        @JvmStatic
        fun fromResultSet(resultSet: ResultSet): TransactionPartner {
            return TransactionPartner(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
            )
        }

        /**
         * Retrieves a Transaction Partner from the database by its ID
         * @param dbHandler: The database handler to use
         * @param id: The ID of the Transaction Partner
         * @return The generated Transaction Partner object or null if no applicable partner was found
         */
        @JvmStatic
        fun get(dbHandler: DbHandler, id: Int): TransactionPartner? {
            return dbHandler.getModel(Table.TRANSACTION_PARTNERS, id) as TransactionPartner?
        }

        /**
         * Retrieves a Transaction Partner from the database by its name or ID
         * @param dbHandler: The database handler to use
         * @param nameOrId: The name or ID of the Transaction Partner
         * @return The generated Transaction Partner object or null if no applicable partner was found
         */
        @JvmStatic
        fun get(dbHandler: DbHandler, nameOrId: String): TransactionPartner? {
            return dbHandler.getModel(Table.TRANSACTION_PARTNERS, nameOrId) as TransactionPartner?
        }

        /**
         * Retrieves a list of all TransactionPartner objects in the database
         * @param dbHandler: The database handler to use
         * @return The list of TransactionPartner objects
         */
        fun getAll(dbHandler: DbHandler): List<TransactionPartner> {
            return dbHandler.getModels(Table.TRANSACTION_PARTNERS).map { it as TransactionPartner }
        }

        /**
         * Creates a new Transaction Partner in the database and returns the corresponding TransactionPartner object.
         * If a transaction partner with the same name already exists, no new partner will be created
         * and the existing one will be returned instead
         * @param dbHandler: The database handler to use for database calls
         * @param name: The name of the Transaction Partner
         * @return The TransactionPartner object
         */
        fun create(dbHandler: DbHandler, name: String) : TransactionPartner {
            val stmt = dbHandler.connection.prepareStatement(
                    "INSERT INTO ${Table.TRANSACTION_PARTNERS.tableName} (name) VALUES (?)"
            )
            stmt.setString(1, name)
            return NamedDbModel.createHelper(dbHandler, Table.TRANSACTION_PARTNERS, name, stmt) as TransactionPartner
        }
    }
}