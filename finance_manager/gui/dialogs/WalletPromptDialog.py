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
    from finance_manager.gui.dialogs.GenericGtkDialog import GenericGtkDialog
    from finance_manager.gui.GenericGtkGui import GenericGtkGui
    from finance_manager.utils.MoneyMath import MoneyMath
    from finance_manager.utils.DateManager import DateManager
except ImportError:
    from gui.dialogs.GenericGtkDialog import GenericGtkDialog
    from gui.GenericGtkGui import GenericGtkGui
    from utils.MoneyMath import MoneyMath
    from utils.DateManager import DateManager
from gi.repository import Gtk


class WalletPromptDialog(GenericGtkDialog):
    """
    Class that models a WalletPrompt Window
    """

    def __init__(self, parent):
        """
        Extends the functionality of GenericGtkGui's constructor
        :param parent: the prompt's parent
        :return: void
        """
        self.wallet_name_text_field = None
        self.wallet_starting_value_text_field = None
        super().__init__(parent)

    def lay_out(self):
        """
        Lays out the window
        :return: void
        """
        self.wallet_name_text_field = self.add_label_and_text("Wallet Name:", 0, 0, 20, 10)
        self.wallet_starting_value_text_field = self.add_label_and_text("Starting Value:", 0, 10, 20, 10)

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        :return: void
        """
        return_value = None
        while return_value is None:
            result = super(WalletPromptDialog, self).start()
            if result == Gtk.ResponseType.OK:
                return_value = self.__ok_button__()
            elif result == Gtk.ResponseType.CANCEL:
                break
        self.destroy()
        return return_value

    def __ok_button__(self):
        """
        Generates a new wallet dictionary from the user's input.
        If an error is encountered, the user is notified
        :return: the wallet if no errors were encountered, otherwise None
        """
        valid_input, error = self.__check_input__()
        if valid_input:
            start_dollars, start_cents = MoneyMath.parse_money_string(self.wallet_starting_value_text_field.get_text())
            income = []
            expenses = []
            date = DateManager.get_current_date_time_as_string()

            if start_dollars < 0:
                start_value = MoneyMath.encode_money_string(-start_dollars, -start_cents)
                expenses.append({"value": start_value,
                                 "description": "starting_value",
                                 "recipient": "starting",
                                 "date": date})
            else:
                start_value = MoneyMath.encode_money_string(start_dollars, start_cents)
                income.append({"value": start_value,
                               "description": "starting_value",
                               "donor": "starting",
                               "date": date})

            wallet = {"name": self.wallet_name_text_field.get_text(),
                      "balance": self.wallet_starting_value_text_field.get_text(),
                      "income": income,
                      "expenses": expenses}
            return wallet
        else:
            if error == "wallet_exists":
                self.parent.show_message_dialog("Invalid Input", "Sorry, this wallet already exists.")
            elif error == "invalid_value":
                self.parent.show_message_dialog("Invalid Input", "Sorry, this is not a valid money value.")
            else:
                self.parent.show_message_dialog("Invalid Input", "Sorry, your input is incorrect")
            return None

    def __check_input__(self):
        """
        Checks the input for errors and returns information about them if there are any.
        :return: False, the error description if an error is found, otherwise True, True
        """
        name = self.wallet_name_text_field.get_text()
        value = self.wallet_starting_value_text_field.get_text()
        if name in self.parent.account.get_wallet_names_as_list():
            return False, "wallet_exists"
        else:
            try:
                MoneyMath.parse_money_string(value)
            except ValueError:
                return False, "invalid_value"
        return True, True
