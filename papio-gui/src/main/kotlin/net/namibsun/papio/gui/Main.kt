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

package net.namibsun.papio.gui

fun main(args: Array<String>) {
    println("Hello World")

    val x = mutableListOf(1, 2)
    val y = mutableListOf(2)
    println(x.zip(y))
    x.distinct().toMutableList()

    val z = mutableMapOf(1 to listOf(1))
    z.filter { it.value.isEmpty() }
}