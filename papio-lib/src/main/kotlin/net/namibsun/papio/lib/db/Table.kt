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

/**
 * Enum that models the various tables in the database
 * @param tableName: The name of the table
 * @param transactionClassifier: The classifier used for this table in the transactions table
 * @param creatorSql: An SQL statement that creates the table if it does not exist
 */
enum class Table(val tableName: String, val transactionClassifier: String, val creatorSql: String) {
    WALLETS(
            "wallets",
            "wallet_id",
            "CREATE TABLE IF NOT EXISTS wallets (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    name TEXT UNIQUE NOT NULL," +
            "    initial_value TEXT NOT NULL" +
            ")"
    ),
    TRANSACTIONS(
            "transactions",
            "id",
            "CREATE TABLE IF NOT EXISTS transactions (" +
            "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   wallet_id INTEGER NOT NULL," +
            "   category_id INTEGER NOT NULL," +
            "   transaction_partner_id INTEGER NOT NULL," +
            "   description TEXT NOT NULL," +
            "   amount TEXT NOT NULL," +
            "   date TEXT NOT NULL," +
            "   FOREIGN KEY(wallet_id) REFERENCES wallets(id) ON DELETE CASCADE," +
            "   FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE," +
            "   FOREIGN KEY(transaction_partner_id) REFERENCES transaction_partners(id) ON DELETE CASCADE" +
            ")"
    ),
    TRANSACTION_PARTNERS(
            "transaction_partners",
            "transaction_partner_id",
            "CREATE TABLE IF NOT EXISTS transaction_partners (" +
            "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   name TEXT UNIQUE NOT NULL" +
            ")"
    ),
    CATEGORIES(
            "categories",
            "category_id",
            "CREATE TABLE IF NOT EXISTS categories (" +
            "   id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "   name TEXT UNIQUE NOT NULL" +
            ")"
    )
}