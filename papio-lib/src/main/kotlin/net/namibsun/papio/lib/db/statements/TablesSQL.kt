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

package net.namibsun.papio.lib.db.statements

/**
 * Class containing SQL statements for table management
 */
class TablesSQL {

    companion object {

        /**
         * Creates the wallets table
         */
        val createWalletTable = "CREATE TABLE IF NOT EXISTS wallets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE NOT NULL," +
                "initial_value INTEGER NOT NULL," +
                "currency TEXT NOT NULL)"

        /**
         * Creates the categories table
         */
        val createCategoryTable = "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE NOT NULL)"

        /**
         * Creates the transaction_partners table
         */
        val createTransactionPartnerTable = "CREATE TABLE IF NOT EXISTS transaction_partners (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE NOT NULL)"

        /**
         * Creates the transaction table
         */
        val createTransactionTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "wallet_id INTEGER NOT NULL," +
                "category_id INTEGER NOT NULL," +
                "transaction_partner_id INTEGER NOT NULL," +
                "description TEXT NOT NULL," +
                "amount: INTEGER NOT NULL," +
                "unix_utc_timestamp: INTEGER NOT NULL," +
                "FOREIGN KEY(wallet_id) REFERENCES wallets(id)," +
                "FOREIGN KEY(category_id) REFERENCES categories(id)," +
                "FOREIGN KEY(transaction_partner_id) REFERENCES transaction_partners(id))"

    }
}