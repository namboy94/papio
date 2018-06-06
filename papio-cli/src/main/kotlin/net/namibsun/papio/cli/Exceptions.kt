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

package net.namibsun.papio.cli

/**
 * Exception that is thrown whenever the root help message should be printed and the program should terminate
 */
class HelpException : Exception() {

    /**
     * Prints a help message to the console and exits with exit code 1
     */
    fun printHelpAndExit() {

        println("papio-cli options:\n")
        for (rootMode in RootMode.values()) {
            val options = "${modeMap[rootMode]!!.map { it.name }}"
            println("${rootMode.name} $options")
        }
        System.exit(1)
    }
}

/**
 * Exception that gets thrown whenever the program should abort
 */
class AbortException(message: String) : Exception(message)