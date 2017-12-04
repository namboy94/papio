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

package net.namibsun.papio.cli.argparse

/**
 * Enum that models which part of the database are to be changed
 */
enum class RootMode {
    WALLET,
    CATEGORY,
    TRANSACTIONPARTNER,
    TRANSACTION,
    BACKUP,
    TRANSFER,
    EXPENSE
}

/**
 * Enum that models the actions that are applicable on a root category
 */
enum class ActionMode {
    LIST, DISPLAY, CREATE, DELETE
}

/**
 * Hashmap that maps which action modes each root mode is able to use.
 */
val modeMap: Map<RootMode, List<ActionMode>> = mapOf(
        RootMode.WALLET to ActionMode.values().toList(),
        RootMode.CATEGORY to ActionMode.values().toList(),
        RootMode.TRANSACTIONPARTNER to ActionMode.values().toList(),
        RootMode.TRANSACTION to ActionMode.values().toList(),
        RootMode.BACKUP to listOf(),
        RootMode.TRANSFER to listOf(),
        RootMode.EXPENSE to listOf(ActionMode.CREATE)
)