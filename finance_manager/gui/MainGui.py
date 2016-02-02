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
from finance_manager.gui.GenericGtkGui import GenericGtkGui
import sys


class MainGui(GenericGtkGui):
    """
    Class that implements the Main GUI of the finance program
    """

    def __init__(self, account_name, account, parent):
        """
        Constructor that requires account details to function
        :param account_name:
        :param account:
        :param parent:
        :return: void
        """
        super.__init__(account_name, parent)
        self.account = account

    def lay_out(self):
        """
        Lays out all needed objects of the GUI
        :return: void
        """
        # Todo implement
        print(self)

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        In this case, the program ends once the window is closed
        :return: void
        """
        super(self).start()
        sys.exit(0)
