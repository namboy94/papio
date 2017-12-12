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
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.Value
import java.sql.ResultSet

@Suppress("EqualsOrHashCode")
/**
 * Models a Wallet in the database
 * @param id: The ID of the wallet in the database
 * @param name: The name of the wallet
 */
class Wallet(id: Int, name: String, private var startingValue: Value) :
        TransactionHolderModel(Table.WALLETS, id, name) {

    /**
     * Calculates the balance of all transactions
     * @param dbHandler: The database handler to use
     * @return The current balance of the wallet
     */
    fun getBalance(dbHandler: DbHandler): Value {
        var value = Value("0", this.startingValue.currency)
        for (transaction in this.getTransactions(dbHandler)) {
            value += transaction.getAmount()
        }
        return value + this.startingValue
    }

    /**
     * Retrieves the currency currently used within this wallet
     * @return The currently used currency of the wallet
     */
    fun getCurrency(): Currency {
        return this.startingValue.currency
    }

    /**
     * Converts the currency of the wallet. All transaction values in the wallet will also be converted.
     */
    fun convertCurrency(dbHandler: DbHandler, currency: Currency) {
        for (transaction in this.getTransactions(dbHandler)) {
            transaction.convertCurrency(dbHandler, currency)
        }
        // Change currency of transaction BEFORE the wallet itself!
        this.startingValue = this.startingValue.convert(currency)

        val stmt = dbHandler.connection.prepareStatement("UPDATE wallets SET initial_value=? WHERE id=?")
        stmt.setString(1, this.startingValue.serialize())
        stmt.setInt(2, this.id)
        stmt.execute()
        stmt.close()
    }

    /**
     * Represents the wallet as a String
     * @return The wallet representation as a String
     */
    override fun toString(): String {
        return "${super.toString()} Starting Value: ${this.startingValue};"
    }

    /**
     * Represents the wallet as String including the current balance
     * @param dbHandler: The database handler used to retrieve the transactions for calculating the balance
     * @return The wallet represented as a String
     */
    fun toString(dbHandler: DbHandler): String {
        return "$this Balance: ${this.getBalance(dbHandler)};"
    }

    /**
     * Checks for equality with another object
     * @param other: The other object
     * @return true if the objects are equal, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (other is Wallet) {
            other.id == this.id && other.name == this.name && other.startingValue == this.startingValue
        } else {
            false
        }
    }

    /**
     * Static Methods
     */
    companion object {

        /**
         * Generates a Wallet object from a ResultSet
         * @param resultSet: The ResultSet to use to generate the Wallet object
         * @return The generated Wallet object
         */
        @JvmStatic
        fun fromResultSet(resultSet: ResultSet): Wallet {
            return Wallet(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    Value.deserialize(resultSet.getString("initial_value"))
            )
        }

        /**
         * Retrieves a Wallet from the database by its ID
         * @param dbHandler: The database handler to use
         * @param id: The ID of the Wallet
         * @return The generated Wallet object or null if no applicable wallet was found
         */
        @JvmStatic
        fun get(dbHandler: DbHandler, id: Int): Wallet? {
            return dbHandler.getModel(Table.WALLETS, id) as Wallet?
        }

        /**
         * Retrieves a Wallet from the database by its name or ID
         * @param dbHandler: The database handler to use
         * @param nameOrId: The name or ID of the Wallet
         * @return The generated Wallet object or null if no applicable wallet was found
         */
        @JvmStatic
        fun get(dbHandler: DbHandler, nameOrId: String): Wallet? {
            return dbHandler.getModel(Table.WALLETS, nameOrId) as Wallet?
        }

        /**
         * Retrieves a list of all Wallet objects in the database
         * @param dbHandler: The database handler to use
         * @return The list of Wallet objects
         */
        fun getAll(dbHandler: DbHandler): List<Wallet> {
            return dbHandler.getModels(Table.WALLETS).map { it as Wallet }
        }

        /**
         * Creates a new Wallet in the database and returns the corresponding Wallet object.
         * If a Wallet with the same name already exists, no new partner will be created
         * and the existing one will be returned instead
         * @param dbHandler: The database handler to use for database calls
         * @param name: The name of the Wallet
         * @return The Wallet object
         */
        fun create(dbHandler: DbHandler, name: String, initialValue: Value): Wallet {
            val stmt = dbHandler.connection.prepareStatement(
                    "INSERT INTO ${Table.WALLETS.tableName} (name, initial_value) VALUES (?, ?)"
            )
            stmt.setString(1, name)
            stmt.setString(2, initialValue.serialize())
            return NamedDbModel.createHelper(dbHandler, Table.WALLETS, name, stmt) as Wallet
        }
    }
}