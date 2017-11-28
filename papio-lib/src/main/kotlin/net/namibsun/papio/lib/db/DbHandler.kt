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

import net.namibsun.papio.lib.core.*
import net.namibsun.papio.lib.db.models.Category
import net.namibsun.papio.lib.db.models.Transaction
import net.namibsun.papio.lib.db.models.TransactionPartner
import net.namibsun.papio.lib.db.models.Wallet

/**
 * Interface that defines which methods a database handler should
 * implement
 *
 * Schema:
 *
 * wallets:
 * id | name | currency | starting_value]
 *
 * transactions:
 * id | wallet_id | transaction_partner_id | category_id | value | utc_unix_timestamp
 *
 * transaction_partners:
 * id | name
 *
 * categories:
 * id | name
 */
interface DbHandler {

    fun enableForeignKeys() // PRAGMA foreign_keys = ON

    // ----------------------------------------------------------------------------------------------------------------
    // Creators

    /**
     * Creates a new Wallet in the database and returns the Wallet object.
     * If the wallet already exists, the database is left as is and the existing
     * wallet is returned.
     * @param name: The wallet name. Must be unique.
     * @param startingValue: The starting value of the wallet.
     * @return The wallet object.
     */
    fun createWallet(name: String, startingValue: MoneyValue): Wallet

    /**
     * Creates a new Transaction Partner in the database and return the corresponding
     * TransactionPartner object. If the transaction partner already exists, the database
     * is not modified and the existing TransactionPartner is returned.
     * @param name: The name of the transaction partner. Must be unique.
     * @return The TransactionPartner object
     */
    fun createTransactionPartner(name: String): TransactionPartner

    /**
     * Creates a new category in the database. If the category already exists,
     * the database is not modified.
     * @param name: The name of the category
     * @return The corresponding Category object
     */
    fun createCategory(name: String) : Category

    /**
     * Stores a transaction for a wallet in the database
     * If the wallet does not exist, an exception is thrown
     * @param wallet: The wallet for which to store the transaction
     *
     */
    fun createTransaction(wallet: Wallet,
                          transactionPartner: TransactionPartner,
                          category: Category,
                          amount: MoneyValue,
                          unixUtcTimestamp: Int = (System.currentTimeMillis() / 1000).toInt()) : Transaction

    // ----------------------------------------------------------------------------------------------------------------
    // Global Getters

    /**
     * Retrieves all wallets in the database
     * @return All wallets in the database
     */
    fun getWallets(): List<Wallet>

    // ----------------------------------------------------------------------------------------------------------------
    // Transactions

    /**
     * Deletes a transaction. If the transaction does not exist, nothing is done.
     * @param transactionId: The ID of the transaction to delete
     */
    fun deleteTransaction(transactionId: Int)

    /**
     * Adjusts the monetary amount of a transaction
     * @param transactionId: The ID of the transaction to modify
     * @param amount: The new amount of the transaction
     */
    fun adjustTransactionAmount(transactionId: Int, amount: Int)

    // ----------------------------------------------------------------------------------------------------------------
    // Categories

    /**
     * Deletes a category from the database
     * @param categoryId: The ID of the category to delete
     */
    fun deleteCategory(categoryId: Int)

    /**
     * Retrieves all transactions for a category
     * @param categoryId: The ID of the category for which to retrieve the transactions
     * @return A list of transactions with the selected category
     */
    fun getTransactionsByCategory(categoryId: Int): List<Transaction>

    // ----------------------------------------------------------------------------------------------------------------
    // Transaction Partners

    /**
     * Deletes a transaction partner from the database
     * @param transactionPartnerId: The ID of the transaction partner to delete
     */
    fun deleteTransactionPartner(transactionPartnerId: Int)

    /**
     * Retrieves all transactions for a transaction partner
     * @param transactionPartnerId: The ID of the transaction partner for which to retrieve the transactions
     * @return A list of transactions with the selected transaction partner
     */
    fun getTransactionsByTransactionPartner(transactionPartnerId: Int): List<Transaction>

    // ----------------------------------------------------------------------------------------------------------------
    // Wallets

    /**
     * Deletes a wallet from the database
     * @param walletId: The ID of the wallet to deletes
     */
    fun deleteWallet(walletId: Int)

    /**
     * Retrieves all transactions in a wallet
     * @param walletId: The ID of the wallet from which to retrieve the transactions
     * @return A list of transactions in the wallet
     */
    fun getTransactionsByWallet(walletId: Int): List<Transaction>

}