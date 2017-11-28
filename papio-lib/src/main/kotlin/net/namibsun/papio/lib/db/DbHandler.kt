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

import net.namibsun.papio.lib.core.MoneyValue
import net.namibsun.papio.lib.core.Currency
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet
import java.sql.Connection

/**
 * Class that manages database calls
 * @param connection: A JDBC compatible database connection
 */
class DbHandler(private val connection: Connection) {

    init {
        this.connection.autoCommit = true
        this.connection.createStatement().execute("PRAGMA foreign_keys = ON")
        this.connection.createStatement().execute("" +
                "CREATE TABLE IF NOT EXISTS wallets (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT UNIQUE NOT NULL," +
                "    initial_value INTEGER NOT NULL," +
                "    currency TEXT NOT NULL" +
                ")")
        this.connection.createStatement().execute("" +
                "CREATE TABLE IF NOT EXISTS categories (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   name TEXT UNIQUE NOT NULL" +
                ")")
        this.connection.createStatement().execute("" +
                "CREATE TABLE IF NOT EXISTS transaction_partners (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   name TEXT UNIQUE NOT NULL" +
                ")")
        this.connection.createStatement().execute("" +
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   wallet_id INTEGER NOT NULL," +
                "   category_id INTEGER NOT NULL," +
                "   transaction_partner_id INTEGER NOT NULL," +
                "   description TEXT NOT NULL," +
                "   amount INTEGER NOT NULL," +
                "   unix_utc_timestamp INTEGER NOT NULL," +
                "   FOREIGN KEY(wallet_id) REFERENCES wallets(id)," +
                "   FOREIGN KEY(category_id) REFERENCES categories(id)," +
                "   FOREIGN KEY(transaction_partner_id) REFERENCES transaction_partners(id)" +
                ")")
    }

    /**
     * Closes the database connection
     */
    fun close() {
        this.connection.close()
    }

    /**
     * Creates a new Wallet in the database and returns the Wallet object.
     * If the wallet already exists, the database is left as is and the existing
     * wallet is returned.
     * If the wallet already exists, the starting value will be ignored.
     * @param name: The wallet name. Must be unique.
     * @param startingValue: The starting value of the wallet.
     * @return The created wallet object or the existing one if it already existed
     */
    fun createWallet(name: String, startingValue: MoneyValue): Wallet {

        // Don't allow duplicates
        val existing = this.getWallet(name)
        if (existing != null) {
            return existing
        }

        val statement = this.connection.prepareStatement("" +
                "INSERT INTO wallets " +
                "(name, initial_value, currency) " +
                "VALUES (?, ?, ?)"
        )
        statement.setString(1, name)
        statement.setInt(2, startingValue.getValue())
        statement.setString(3, startingValue.getCurrency().name)
        statement.execute()
        statement.close()
        return this.getWallet(name)!!
    }

    /**
     * Retrieves a wallet from the database based on its name
     * @param name: The name of the wallet
     * @return The wallet object or null if the wallet was not found
     */
    fun getWallet(name: String): Wallet? {
        return this.generateWalletFromDatabase("name", name)
    }

    /**
     * Retrieves a wallet from the database based on its ID
     * @param id: The ID of the wallet
     * @return The wallet object or null if the wallet was not found
     */
    fun getWallet(id: Int): Wallet? {
        return this.generateWalletFromDatabase("id", id)
    }

    /**
     * Handles the nitty gritty of fetching a wallet from the database.
     * It accepts a type value that can be either 'name' or 'id'.
     * DO NOT USE ANYTHING ELSE!!!
     * If 'name' was used, value MUST BE A STRING!
     * If 'id' was used, value MUST BE AN INT!
     * @param type: The type to check against. Must be 'name' or 'id'
     * @param value: The value with which to identify the wallet. Must be String or Int.
     * @return The Wallet object or null if the wallet was not found
     */
    private fun generateWalletFromDatabase(type: String, value: Any): Wallet? {

        val statement = this.connection.prepareStatement("" +
                "SELECT id, name, initial_value, currency " +
                "FROM wallets WHERE $type=?"
        )
        if (type == "name") {
            statement.setString(1, value as String)
        } else if (type == "id") {
            statement.setInt(1, value as Int)
        }
        statement.execute()
        val results = statement.resultSet

        val wallet = if (!results.next()) {
            null
        } else {
            Wallet( results.getInt("id"),
                    results.getString("name"),
                    MoneyValue(
                            results.getInt("initial_value"),
                            Currency.valueOf(results.getString("currency"))
                    )
            )
        }
        statement.close()
        results.close()
        return wallet
    }

    /**
     * Creates a new category in the database. If the category already exists,
     * the database is not modified and the existing category is returned.
     * @param name: The name of the category
     * @return The corresponding Category object
     */
    fun createCategory(name: String): Category {

        // Don't allow duplicates
        val existing = this.getCategory(name)
        if (existing != null) {
            return existing
        }

        val statement = this.connection.prepareStatement("INSERT INTO categories (name) VALUES (?)")
        statement.setString(1, name)
        statement.execute()
        statement.close()
        return this.getCategory(name)!!
    }

    /**
     * Retrieves a category from the database using a name
     * @param name: The name of the category
     * @return The Category object or null if no category with the given name was found
     */
    fun getCategory(name: String): Category? {
        return this.generateCategoryFromDatabase("name", name)
    }

    /**
     * Retrieves a category from the database using an ID
     * @param id: The ID of the category
     * @return The Category object or null if no category with the given ID was found
     */
    fun getCategory(id: Int): Category? {
        return this.generateCategoryFromDatabase("id", id)
    }

    /**
     * Handles the nitty gritty of fetching a category from the database.
     * It accepts a type value that can be either 'name' or 'id'.
     * DO NOT USE ANYTHING ELSE!!!
     * If 'name' was used, value MUST BE A STRING!
     * If 'id' was used, value MUST BE AN INT!
     * @param type: The type to check against. Must be 'name' or 'id'
     * @param value: The value with which to identify the category. Must be String or Int.
     * @return The Category object or null if the category was not found
     */
    private fun generateCategoryFromDatabase(type: String, value: Any): Category? {
        val statement = this.connection.prepareStatement("SELECT id, name FROM categories WHERE $type=?")
        if (type == "name") {
            statement.setString(1, value as String)
        } else if (type == "id") {
            statement.setInt(1, value as Int)
        }
        statement.execute()
        val results = statement.resultSet

        val category = if (!results.next()) {
            null
        } else {
            Category(results.getInt("id"), results.getString("name"))
        }
        statement.close()
        results.close()
        return category
    }

    /**
     * Creates a new Transaction Partner in the database and return the corresponding
     * TransactionPartner object. If the transaction partner already exists, the database
     * is not modified and the existing TransactionPartner is returned.
     * @param name: The name of the transaction partner. Must be unique.
     * @return The corresponding TransactionPartner object
     */
    fun createTransactionPartner(name: String): TransactionPartner {

        // Don't allow duplicates
        val existing = this.getTransactionPartner(name)
        if (existing != null) {
            return existing
        }

        val statement = this.connection.prepareStatement("INSERT INTO transaction_partners (name) VALUES (?)")
        statement.setString(1, name)
        statement.execute()
        statement.close()
        return this.getTransactionPartner(name)!!
    }

    /**
     * Retrieves a Transaction Partner from the database for a name.
     * @param name: The name of the transaction partner to fetch
     * @return The TransactionPartner object or null if no corresponding transaction partner was found in the database
     */
    fun getTransactionPartner(name: String): TransactionPartner? {
        return this.generateTransactionPartnerFromDatabase("name", name)
    }

    /**
     * Retrieves a Transaction Partner from the database for an ID.
     * @param id: The ID of the transaction partner to fetch
     * @return The TransactionPartner object or null if no corresponding transaction partner was found in the database
     */
    fun getTransactionPartner(id: Int): TransactionPartner? {
        return this.generateTransactionPartnerFromDatabase("id", id)
    }

    /**
     * Handles the nitty gritty of fetching a transaction partner from the database.
     * It accepts a type value that can be either 'name' or 'id'.
     * DO NOT USE ANYTHING ELSE!!!
     * If 'name' was used, value MUST BE A STRING!
     * If 'id' was used, value MUST BE AN INT!
     * @param type: The type to check against. Must be 'name' or 'id'
     * @param value: The value with which to identify the category. Must be String or Int.
     * @return The TransactionPartner object or null if the transaction partner was not found
     */
    private fun generateTransactionPartnerFromDatabase(type: String, value: Any): TransactionPartner? {
        val statement = this.connection.prepareStatement("SELECT id, name FROM transaction_partners WHERE $type=?")
        if (type == "name") {
            statement.setString(1, value as String)
        } else if (type == "id") {
            statement.setInt(1, value as Int)
        }
        statement.execute()
        val results = statement.resultSet

        val transactionPartner = if (!results.next()) {
            null
        } else {
            TransactionPartner(results.getInt("id"), results.getString("name"))
        }
        statement.close()
        results.close()
        return transactionPartner
    }

    /**
     * Stores a transaction for a wallet, category and transaction partner in the database.
     * @param wallet: The wallet for which to store the transaction
     * @param category: The category of the transaction
     * @param transactionPartner: The transaction partner of the transaction
     * @param description: The description of the transaction
     * @param amount: The monetary amount of the transaction
     * @param unixUtcTimestamp: A UTC timestamp that denotes the time the transaction took place.
     *                          Defaults to the current time.
     * @return The created Transaction object
     */
    fun createTransaction(wallet: Wallet,
                          category: Category,
                          transactionPartner: TransactionPartner,
                          description: String,
                          amount: MoneyValue,
                          unixUtcTimestamp: Int = (System.currentTimeMillis() / 1000).toInt()): Transaction {
        val converted = amount.convert(wallet.getCurrency())
        val statement = this.connection.prepareStatement("" +
                "INSERT INTO transactions " +
                "(wallet_id, category_id, transaction_partner_id, description, amount, unix_utc_timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
        )
        statement.setInt(1, wallet.id)
        statement.setInt(2, category.id)
        statement.setInt(3, transactionPartner.id)
        statement.setString(4, description)
        statement.setInt(5, converted.getValue())
        statement.setInt(6, unixUtcTimestamp)
        statement.execute()

        val idStatement = this.connection.prepareStatement("SELECT last_insert_rowid()")
        idStatement.execute()
        val results = idStatement.resultSet
        val id = results.getInt(1)

        statement.close()
        idStatement.close()
        results.close()

        return Transaction(id, wallet, category, transactionPartner, description, converted, unixUtcTimestamp)
    }

    /**
     * Retrieves all wallets in the database
     * @return All wallets in the database
     */
    fun getWallets(): List<Wallet> {
        val statement = this.connection.prepareStatement("SELECT id, name, initial_value, currency FROM wallets")
        statement.execute()
        val results = statement.resultSet
        val wallets = mutableListOf<Wallet>()

        while (results.next()) {
            wallets.add(Wallet(
                    results.getInt("id"),
                    results.getString("name"),
                    MoneyValue(results.getInt("initial_value"), Currency.valueOf(results.getString("currency")))
            ))
        }
        statement.close()
        results.close()
        return wallets
    }

    /**
     * Adjusts the starting value of a wallet. Can also be used to change the currency of a wallet.
     * @param walletId: The ID of the wallet to adjust
     * @param initialValue: The new initial value of the wallet
     */
    fun adjustWalletStartingValue(walletId: Int, initialValue: MoneyValue) {
        val statement = this.connection.prepareStatement("UPDATE wallets SET initial_value=?,currency=? WHERE id=?")
        statement.setInt(1, initialValue.getValue())
        statement.setString(2, initialValue.getCurrency().name)
        statement.setInt(3, walletId)
        statement.execute()
        statement.close()
    }

    /**
     * Deletes a transaction. If the transaction does not exist, nothing is done.
     * @param transactionId: The ID of the transaction to delete
     */
    fun deleteTransaction(transactionId: Int) {
        this.deleteRowFromTableById("transactions", transactionId)
    }

    /**
     * Adjusts the monetary amount of a transaction
     * @param transactionId: The ID of the transaction to modify
     * @param amount: The new amount of the transaction
     */
    fun adjustTransactionAmount(transactionId: Int, amount: Int) {
        val statement = this.connection.prepareStatement("UPDATE transactions SET amount=? WHERE id=?")
        statement.setInt(1, amount)
        statement.setInt(2, transactionId)
        statement.execute()
        statement.close()
    }

    /**
     * Deletes a category from the database
     * @param categoryId: The ID of the category to delete
     */
    fun deleteCategory(categoryId: Int) {
        this.deleteRowFromTableById("categories", categoryId)
    }

    /**
     * Retrieves all transactions for a category
     * @param categoryId: The ID of the category for which to retrieve the transactions
     * @return A list of transactions with the selected category
     */
    fun getTransactionsByCategory(categoryId: Int): List<Transaction> {
        return this.getTransactionsByIdType("category", categoryId)
    }

    /**
     * Deletes a transaction partner from the database
     * @param transactionPartnerId: The ID of the transaction partner to delete
     */
    fun deleteTransactionPartner(transactionPartnerId: Int) {
        this.deleteRowFromTableById("transaction_partners", transactionPartnerId)
    }

    /**
     * Retrieves all transactions for a transaction partner
     * @param transactionPartnerId: The ID of the transaction partner for which to retrieve the transactions
     * @return A list of transactions with the selected transaction partner
     */
    fun getTransactionsByTransactionPartner(transactionPartnerId: Int): List<Transaction> {
        return this.getTransactionsByIdType("transaction_partner", transactionPartnerId)
    }

    /**
     * Deletes a wallet from the database
     * @param walletId: The ID of the wallet to deletes
     */
    fun deleteWallet(walletId: Int) {
        this.deleteRowFromTableById("wallets", walletId)
    }

    /**
     * Retrieves all transactions in a wallet
     * @param walletId: The ID of the wallet from which to retrieve the transactions
     * @return A list of transactions in the wallet
     */
    fun getTransactionsByWallet(walletId: Int): List<Transaction> {
        return this.getTransactionsByIdType("wallet", walletId)
    }

    /**
     * Deletes a row from a table based on an ID
     * @param tableName: The table from which to delete the entry
     * @param id: The ID of the row to delete
     */
    private fun deleteRowFromTableById(tableName: String, id: Int) {
        val statement = this.connection.prepareStatement("DELETE FROM $tableName WHERE id=?")
        statement.setInt(1, id)
        statement.execute()
        statement.close()
    }

    /**
     * Retrieves all transactions for either a category, wallet, transaction partner etc. based on
     * the idType parameter and the ID value that identifies it.
     * @param idType: The ID Type, for example 'category' or 'wallet'
     * @param id: The corresponding ID
     * @return A list of applicable transactions
     */
    private fun getTransactionsByIdType(idType: String, id: Int): List<Transaction> {
        val statement = this.connection.prepareStatement("" +
                "SELECT id, wallet_id, category_id, transaction_partner_id, description, amount, unix_utc_timestamp " +
                "FROM transactions WHERE ${idType}_id=?"
        )
        statement.setInt(1, id)
        statement.execute()
        val results = statement.resultSet

        val transactions = mutableListOf<Transaction>()
        while (results.next()) {
            val wallet = this.getWallet(results.getInt("wallet_id"))!!
            transactions.add(Transaction(
                    results.getInt("id"),
                    wallet,
                    this.getCategory(results.getInt("category_id"))!!,
                    this.getTransactionPartner(results.getInt("transaction_partner_id"))!!,
                    results.getString("description"),
                    MoneyValue(results.getInt("amount"), wallet.getCurrency()),
                    results.getInt("unix_utc_timestamp")
            ))
        }
        return transactions
    }
}