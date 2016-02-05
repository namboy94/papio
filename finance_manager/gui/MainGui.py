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
    from finance_manager.gui.dialogs.WalletPromptDialog import WalletPromptDialog
    from finance_manager.gui.dialogs.AssetPromptDialog import AssetPromptDialog
    from finance_manager.gui.dialogs.IncomePromptDialog import IncomePromptDialog
    from finance_manager.gui.dialogs.ExpensePromptDialog import ExpensePromptDialog
except ImportError:
    from gui.GenericGtkGui import GenericGtkGui
    from gui.dialogs.WalletPromptDialog import WalletPromptDialog
    from gui.dialogs.AssetPromptDialog import AssetPromptDialog
    from gui.dialogs.IncomePromptDialog import IncomePromptDialog
    from gui.dialogs.ExpensePromptDialog import ExpensePromptDialog

import sys

from gi.repository import Gtk


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
        self.assets = None
        self.wallet_selector = None
        self.income_button = None
        self.expense_button = None
        self.asset_button = None
        self.new_expense_button = None
        self.new_income_button = None
        self.new_asset_button = None
        self.new_wallet_button = None
        self.selected_wallet = "all"
        self.selected_wallet_index = 0
        super().__init__(account_name, parent)

    def lay_out(self):
        """
        Lays out all needed objects of the GUI
        :return: void
        """
        # Create objects
        self.income = GenericGtkGui.generate_multi_list_box({"Value": (str,),
                                                             "Description": (str,),
                                                             "Source": (str,),
                                                             "Date": (str,),
                                                             "Wallet": (str,)})
        self.expenses = GenericGtkGui.generate_multi_list_box({"Value": (str,),
                                                               "Description": (str,),
                                                               "Recipient": (str,),
                                                               "Date": (str,),
                                                               "Wallet": (str,)})
        self.assets = GenericGtkGui.generate_multi_list_box({"Value": (str,),
                                                             "Description": (str,),
                                                             "Date": (str,)})

        self.income_button = GenericGtkGui.generate_simple_button("Income", self.__set_mode__, self.income)
        self.expense_button = GenericGtkGui.generate_simple_button("Expenses", self.__set_mode__, self.expenses)
        self.asset_button = GenericGtkGui.generate_simple_button("Assets", self.__set_mode__, self.assets)
        self.wallet_selector = GenericGtkGui.generate_combo_box(["all"] + self.account.get_wallet_names_as_list())
        self.wallet_selector["combo_box"].connect("changed", self.__on_wallet_change__)

        self.new_expense_button = GenericGtkGui.generate_simple_button("New Expense", self.__open_new_expense_prompt__)
        self.new_income_button = GenericGtkGui.generate_simple_button("New Income", self.__open_new_income_prompt__)
        self.new_asset_button = GenericGtkGui.generate_simple_button("New Asset", self.__open_new_asset_prompt__)
        self.new_wallet_button = GenericGtkGui.generate_simple_button("New Wallet", self.__open_new_wallet_prompt__)

        # Lay out objects
        self.grid.attach(self.income["scrollable"], 0, 5, 20, 10)
        self.grid.attach(self.expenses["scrollable"], 0, 5, 20, 10)
        self.grid.attach(self.assets["scrollable"], 0, 5, 20, 10)
        self.grid.attach_next_to(self.income_button, self.assets["scrollable"], Gtk.PositionType.TOP, 5, 4)
        self.grid.attach_next_to(self.expense_button, self.income_button, Gtk.PositionType.RIGHT, 5, 4)
        self.grid.attach_next_to(self.asset_button, self.expense_button, Gtk.PositionType.RIGHT, 5, 4)
        self.grid.attach_next_to(self.wallet_selector["combo_box"], self.asset_button, Gtk.PositionType.RIGHT, 5, 4)
        self.grid.attach_next_to(self.new_income_button, self.assets["scrollable"], Gtk.PositionType.BOTTOM, 10, 7)
        self.grid.attach_next_to(self.new_expense_button, self.new_income_button, Gtk.PositionType.RIGHT, 10, 7)
        self.grid.attach_next_to(self.new_asset_button, self.new_income_button, Gtk.PositionType.BOTTOM, 10, 7)
        self.grid.attach_next_to(self.new_wallet_button, self.new_asset_button, Gtk.PositionType.RIGHT, 10, 7)

        self.__fill_data__()

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        In this case, the program ends once the window is closed
        :return: void
        """
        super(MainGui, self).start()
        self.account.save()
        sys.exit(0)

    def __fill_data__(self):
        """
        Fills the data widgets with information of the loaded account
        :return: void
        """
        self.assets["list_store"].clear()
        self.wallet_selector["list_store"].clear()
        self.wallet_selector["list_store"].append(("All",))
        for wallet in self.account.get_wallet_names_as_list():
                self.wallet_selector["list_store"].append([wallet])
        for asset in self.account.get_all_assets_as_list():
                self.assets["list_store"].append(asset)
        self.__change_wallet__()

    def __change_wallet__(self):
        """
        Changes the displayed expenses and income to only show the data for one wallet
        :return:
        """
        self.expenses["list_store"].clear()
        self.income["list_store"].clear()
        if self.selected_wallet == "All":
            for expense in self.account.get_all_expenses_as_list():
                self.expenses["list_store"].append(expense)
            for income in self.account.get_all_income_as_list():
                self.income["list_store"].append(income)
        else:
            for expense in self.account.get_all_expenses_as_list(self.selected_wallet):
                self.expenses["list_store"].append(expense)
            for income in self.account.get_all_income_as_list(self.selected_wallet):
                self.income["list_store"].append(income)
        self.wallet_selector["combo_box"].set_active(self.selected_wallet_index)

    def __set_mode__(self, widget, widget_to_display):
        """
        Switches to a different view
        :param widget: the button or text entry that initialized this command
        :param widget_to_display: the widget to be displayed
        :return:
        """
        if widget is not None or widget is None:
            self.income["scrollable"].set_visible(False)
            self.expenses["scrollable"].set_visible(False)
            self.assets["scrollable"].set_visible(False)
            widget_to_display[0]["scrollable"].set_visible(True)

    def __open_new_expense_prompt__(self, widget):
        """
        opens a new expense prompt window
        :param widget: the button that called this method
        :return: void
        """
        if len(self.account.get_wallet_names_as_list()) == 0:
            GenericGtkGui.show_message_dialog(self, "No wallets found", "Please add a wallet before adding expenses")
            return
        if widget is not None:
            try:
                expense, wallet_name = ExpensePromptDialog(self).start()
                self.account.add_expense_from_dict(expense, wallet_name)
                self.account.save()
                self.__fill_data__()
            except TypeError:
                return

    def __open_new_income_prompt__(self, widget):
        """
        opens a new income prompt window
        :param widget: the button that called this method
        :return: void
        """
        if len(self.account.get_wallet_names_as_list()) == 0:
            GenericGtkGui.show_message_dialog(self, "No wallets found", "Please add a wallet before adding income")
            return
        if widget is not None:
            try:
                income, wallet_name = IncomePromptDialog(self).start()
                self.account.add_income_from_dict(income, wallet_name)
                self.account.save()
                self.__fill_data__()
            except TypeError:
                return

    def __open_new_asset_prompt__(self, widget):
        """
        opens a new asset prompt window
        :param widget: the button that called this method
        :return: void
        """
        if widget is not None:
            asset = AssetPromptDialog(self).start()
            if asset is not None:
                self.account.add_asset_from_dict(asset)
                self.account.save()
                self.__fill_data__()

    def __open_new_wallet_prompt__(self, widget):
        """
        opens a new wallet prompt window
        :param widget: the button that called this method
        :return: void
        """
        if widget is not None:
            wallet = WalletPromptDialog(self).start()
            if wallet is not None:
                self.account.add_wallet_from_dict(wallet)
                self.account.save()
                self.__fill_data__()

    def __on_wallet_change__(self, widget):
        """
        Runs whenever the selected wallet combo box element is selected
        :param widget: the combo box affected
        :return: void
        """
        if widget is not None:
            this_selected_wallet = GenericGtkGui.get_current_selected_combo_box_option(self.wallet_selector)
            if this_selected_wallet:
                self.selected_wallet = this_selected_wallet
                self.selected_wallet_index = self.wallet_selector["combo_box"].get_active()
                self.__change_wallet__()
