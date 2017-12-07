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

import net.namibsun.papio.lib.db.*
import java.sql.ResultSet

@Suppress("EqualsOrHashCode")
/**
 * Models a Category in the database
 * @param id: The ID of the category in the database
 * @param name: The name of the category
 */
class Category(id: Int, name: String): TransactionHolderModel(Table.CATEGORIES, id, name) {

    /**
     * Checks for equality with another object
     * @param other: The other object
     * @return true if the objects are equal, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (other is Category) {
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
         * Generates a Category object from a ResultSet
         * @param resultSet: The ResultSet to use to generate the Category object
         * @return The generated Category object
         */
        @JvmStatic
        fun fromResultSet(resultSet: ResultSet): Category {
            return Category(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
            )
        }

        /**
         * Retrieves a Category from the database by its ID
         * @param dbHandler: The database handler to use
         * @param id: The ID of the category
         * @return The generated Category object or null if no applicable category was found
         */
        @JvmStatic
        fun get(dbHandler: DbHandler, id: Int): Category? {
            return dbHandler.getModel(Table.CATEGORIES, id) as Category?
        }

        /**
         * Retrieves a Category from the database by its name or ID
         * @param dbHandler: The database handler to use
         * @param nameOrId: The name or ID of the category
         * @return The generated Category object or null if no applicable category was found
         */
        @JvmStatic
        fun get(dbHandler: DbHandler, nameOrId: String): Category? {
            return dbHandler.getModel(Table.CATEGORIES, nameOrId) as Category?
        }

        /**
         * Retrieves a list of all Category objects in the database
         * @param dbHandler: The database handler to use
         * @return The list of Category objects
         */
        fun getAll(dbHandler: DbHandler): List<Category> {
            return dbHandler.getModels(Table.CATEGORIES).map { it as Category }
        }

        /**
         * Creates a new Category in the database and returns the corresponding Category object.
         * If a category with the same name already exists, no new category will be created
         * and the existing one will be returned instead
         * @param dbHandler: The database handler to use for database calls
         * @param name: The name of the category
         * @return The Category object
         */
        fun create(dbHandler: DbHandler, name: String) : Category {
            val stmt = dbHandler.connection.prepareStatement(
                    "INSERT INTO ${Table.CATEGORIES.tableName} (name) VALUES (?)"
            )
            stmt.setString(1, name)
            return NamedDbModel.createHelper(dbHandler, Table.CATEGORIES, name, stmt) as Category
        }
    }
}