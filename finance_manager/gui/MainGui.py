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
        self.account = account
        self.expenses = None
        self.income = None
        self.wallet_selector = None
        super().__init__(account_name, parent)

    def lay_out(self):
        """
        Lays out all needed objects of the GUI
        :return: void
        """
        # TODO Really create the GUI.
        # Create objects
        self.expenses = GenericGtkGui.generate_multi_list_box({"Value": (str,),
                                                               "Description": (str,),
                                                               "Recipient": (str,),
                                                               "Date": (str,),
                                                               "Wallet": (str,)})
        self.income = GenericGtkGui.generate_multi_list_box({"Value": (str,),
                                                             "Description": (str,),
                                                             "Source": (str,),
                                                             "Date": (str,),
                                                             "Wallet": (str,)})
        self.wallet_selector = GenericGtkGui.generate_combo_box(["all"] + self.account.get_wallet_names_as_list())

        # Lay out objects
        self.grid.attach(self.expenses["scrollable"], 0, 0, 10, 10)
        self.grid.attach(self.income["scrollable"], 0, 10, 10, 10)

        self.__fill_data__()

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        In this case, the program ends once the window is closed
        :return: void
        """
        super(MainGui, self).start()
        sys.exit(0)

    def __fill_data__(self):
        """
        Fills the data widgets with information of the loaded account
        :return: void
        """
        for expense in self.account.get_all_expenses_as_list():
            self.expenses["list_store"].append(expense)
        for income in self.account.get_all_income_as_list():
            self.income["list_store"].append(income)
