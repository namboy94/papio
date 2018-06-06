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

package net.namibsun.papio.lib.db.models

import net.namibsun.papio.lib.date.IsoDate
import net.namibsun.papio.lib.db.DbHandler
import net.namibsun.papio.lib.db.DbModel
import net.namibsun.papio.lib.db.Table
import net.namibsun.papio.lib.money.Currency
import net.namibsun.papio.lib.money.Value
import java.sql.ResultSet

@Suppress("EqualsOrHashCode")
/**
 * Models a transaction in the database
 * A transaction always references a wallet, a category and a transaction partner
 * Transactions are immutable and have to be deleted and created anew to be changed.
 * An exception to this is the amount, which may be changed when the associated wallet
 * changes its currency.
 * @param id: The transaction's database ID
 * @param wallet: The wallet associated with this transaction
 * @param category: The category associated with this transaction
 * @param partner: The partner of this transaction
 * @param description: A description of the transaction
 * @param amount: The amount of money that makes up the transaction. Always the same currency as the wallet
 * @param date: The ISO-8601 date indicating when the transaction took place
 */
class Transaction(
    id: Int,
    val wallet: Wallet,
    val category: Category,
    val partner: TransactionPartner,
    val description: String,
    private var amount: Value,
    val date: IsoDate
) : DbModel(Table.TRANSACTIONS, id) {

    /**
     * Retrieves the amount of money in this transaction
     * @return The amount of money in this transaction
     */
    fun getAmount(): Value {
        return this.amount
    }

    /**
     * Converts the currency of a transaction
     * @param dbHandler: The Database Handler to use
     * @param currency: The currency to use
     */
    fun convertCurrency(dbHandler: DbHandler, currency: Currency) {
        this.amount = this.amount.convert(currency)

        val stmt = dbHandler.connection.prepareStatement("UPDATE transactions SET amount=? WHERE id=?")
        stmt.setString(1, amount.serialize())
        stmt.setInt(2, this.id)
        stmt.execute()
        stmt.close()
    }

    /**
     * Generates a String that represents the Transaction object
     * @return The String representation of the transaction
     */
    override fun toString(): String {
        return "${super.toString()} Wallet: ${this.wallet.name}; Category: ${this.category.name}; " +
                "Transaction Partner: ${this.partner.name}; Description: ${this.description}; " +
                "Amount: ${this.amount}; Date: ${this.date}"
    }

    /**
     * Checks for equality with another object
     * @param other: The other object
     * @return true if the objects are equal, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (other is Transaction) {
            other.id == this.id &&
                    other.description == this.description &&
                    other.amount == this.amount &&
                    other.date == this.date &&
                    other.wallet == this.wallet &&
                    other.category == this.category &&
                    other.partner == this.partner
        } else {
            false
        }
    }

    /**
     * Static Methods
     */
    companion object {

        /**
         * Generates a Transaction object from a ResultSet
         * @param resultSet: The ResultSet to use to generate the Transaction object
         * @return The generated Transaction object
         */
        @JvmStatic
        fun fromResultSet(resultSet: ResultSet, dbHandler: DbHandler): Transaction {
            val wallet = dbHandler.getModel(Table.WALLETS, resultSet.getInt(Table.WALLETS.transactionClassifier)) as Wallet
            val category = dbHandler.getModel(Table.CATEGORIES, resultSet.getInt(Table.CATEGORIES.transactionClassifier)) as Category
            val partner = dbHandler.getModel(Table.TRANSACTION_PARTNERS, resultSet.getInt(Table.TRANSACTION_PARTNERS.transactionClassifier)) as TransactionPartner
            return Transaction(
                    resultSet.getInt("id"),
                    wallet,
                    category,
                    partner,
                    resultSet.getString("description"),
                    Value.deserialize(resultSet.getString("amount")),
                    IsoDate(resultSet.getString("date"))
            )
        }

        /**
         * Retrieves a Transaction from the database by its ID
         * @param dbHandler: The database handler to use
         * @param id: The ID of the Transaction
         * @return The generated Transaction object or null if no applicable Transaction was found
         */
        @JvmStatic
        fun get(dbHandler: DbHandler, id: Int): Transaction? {
            return dbHandler.getModel(Table.TRANSACTIONS, id) as Transaction?
        }

        /**
         * Retrieves a list of all Transaction objects in the database
         * @param dbHandler: The database handler to use
         * @return The list of Transaction objects
         */
        fun getAll(dbHandler: DbHandler): List<Transaction> {
            return dbHandler.getModels(Table.TRANSACTIONS).map { it as Transaction }
        }

        /**
         * Creates a new Transaction in the database and returns the corresponding Transaction object.
         * @param dbHandler: The database handler to use for database calls
         * @return The Tranaction object
         */
        @JvmStatic
        fun create(
            dbHandler: DbHandler,
            wallet: Wallet,
            category: Category,
            partner: TransactionPartner,
            description: String,
            amount: Value,
            date: IsoDate = IsoDate()
        ): Transaction {

            val stmt = dbHandler.connection.prepareStatement("" +
                    "INSERT INTO ${Table.TRANSACTIONS.tableName} (" +
                    "${Table.WALLETS.transactionClassifier}," +
                    "${Table.CATEGORIES.transactionClassifier}," +
                    "${Table.TRANSACTION_PARTNERS.transactionClassifier}," +
                    "description, amount, date) VALUES (?, ?, ?, ?, ?, ?)"
            )
            stmt.setInt(1, wallet.id)
            stmt.setInt(2, category.id)
            stmt.setInt(3, partner.id)
            stmt.setString(4, description)
            stmt.setString(5, amount.convert(wallet.getCurrency()).serialize())
            stmt.setString(6, date.toString())
            stmt.execute()
            stmt.close()

            val idStatement = dbHandler.connection.prepareStatement("SELECT last_insert_rowid()")
            idStatement.execute()
            val results = idStatement.resultSet
            val id = results.getInt(1)
            idStatement.close()
            results.close()

            return Transaction.get(dbHandler, id)!!
        }
    }
}