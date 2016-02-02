# coding=utf-8
"""
Copyright 2016 Hermann Krumrey

This file is part of finance-manager.

    finance-manager is a program that offers simple basic finance management
    to keep track of expenses and income.

    finance-manager is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    finance-manager is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with finance-manager.  If not, see <http://www.gnu.org/licenses/>.
"""

# imports
from gui.GenericGtkGui import GenericGtkGui
# from gi.repository import Gtk


class WelcomeGui(GenericGtkGui):
    """
    Class that models the Welcome Screen GUI. It offers the choice of either
    using one of the already existing accounts in the file system, import an
    external file or creating a new account.
    """

    def __init__(self, title="Finance Manager"):
        """
        Extends the functionality of GenericGtkGui's constructor if needed
        :param title: the title of the GUI. Defaults to "Finance Manager"
        :return: void
        """
        super.__init__(self, title)

    def lay_out(self):
        """
        Lays out all needed objects of the GUI
        :return: void
        """
        # TODO Implement
        print(self)

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        :return: void
        """
        super(self).start()
