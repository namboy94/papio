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

package net.namibsun.papio.cli

import java.io.File

/**
 * Config Singleton that stores paths to the database and the papio directory
 */
object Config {

    /**
     * The path to the .papio directory
     */
    var papioPath = File(System.getProperty("user.home"), ".papio")

    /**
     * The path to the SQLite database
     */
    var dbPath = File(this.papioPath.toString(), "data.db")

    /**
     * If set to true, certain CLI actions will require a manual (y/n) prompt to continue-
     */
    var cliConfirm = true

    /**
     * The default response to CLI (y/n) prompts in case the cliConfirm variable is set to false
     */
    var autoResponse = "y"
}
