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
try:
    from finance_manager.gui.GenericGtkGui import GenericGtkGui
except ImportError:
    from gui.GenericGtkGui import GenericGtkGui


class IncomePrompt(GenericGtkGui):
    """
    Class that models a IncomePrompt Window
    """

    def __init__(self, parent):
        """
        Extends the functionality of GenericGtkGui's constructor
        :param parent: the prompt's parent
        :return: void
        """
        super().__init__("New Income", parent, False)

    def lay_out(self):
        """
        Lays out the window
        :return: void
        """
        print()

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        :return: void
        """
        print()