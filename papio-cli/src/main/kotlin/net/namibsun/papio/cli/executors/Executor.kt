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

package net.namibsun.papio.cli.executors

/**
 * Interface that acts as a common base class of all Executors
 */
interface Executor {

    /**
     * Executes the 'create' option
     */
    fun executeCreate()

    /**
     * Executes the 'delete' option
     */
    fun executeDelete()

    /**
     * Executes the 'list' option
     */
    fun executeList()

    /**
     * Executes the 'display' option
     */
    fun executeDisplay()
}