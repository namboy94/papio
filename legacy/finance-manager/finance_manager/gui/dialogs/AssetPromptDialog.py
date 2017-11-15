# coding=utf-8
"""
Copyright 2016-2017 Hermann Krumrey <hermann@krumreyh.com>

This file is part of finance-manager.

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
    from finance_manager.utils.MoneyMath import MoneyMath
    from finance_manager.utils.DateManager import DateManager
except ImportError:
    from gui.dialogs.GenericGtkDialog import GenericGtkDialog
    from utils.MoneyMath import MoneyMath
    from utils.DateManager import DateManager
from gi.repository import Gtk


class AssetPromptDialog(GenericGtkDialog):
    """
    A class that models an Asset Prompt Dialog
    """

    def __init__(self, parent):
        self.asset_name_text_field = None
        self.asset_value_text_field = None
        self.asset_date_widget = None
        super().__init__(parent)

    def lay_out(self):
        """
        Lays out the window
        :return: void
        """
        self.asset_name_text_field = self.add_label_and_text("Asset Name:", 0, 0, 20, 10)
        self.asset_value_text_field = self.add_label_and_text("Value:", 0, 10, 20, 10)
        self.asset_date_widget = self.add_date_widget(0, 20, 20, 10)

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        :return: void
        """
        return_value = None
        while return_value is None:
            result = super(AssetPromptDialog, self).start()
            if result == Gtk.ResponseType.OK:
                return_value = self.__ok_button__()
            elif result == Gtk.ResponseType.CANCEL:
                break
        self.destroy()
        return return_value

    def __ok_button__(self):
        """
        Generates a new asset dictionary from the user's input.
        If an error is encountered, the user is notified
        :return: the asset if no errors were encountered, otherwise None
        """
        valid_input, error_main, error_sec = self.__check_input__()
        if valid_input:
            date = self.asset_date_widget.get_date_string()
            value = self.asset_value_text_field.get_text()
            name = self.asset_name_text_field.get_text()
            asset = {"description": name,
                     "value": value,
                     "date": date}
            return asset
        else:
            self.parent.show_message_dialog(error_main, error_sec)
            return None

    def __check_input__(self):
        """
        Checks the input for errors and returns information about them if there are any.
        :return: False, the error description if an error is found, otherwise True, True, True
        """
        name = self.asset_name_text_field.get_text()
        value = self.asset_value_text_field.get_text()
        if len(name) == 0:
            return False, "Invalid Input", "Please enter an asset name"
        else:
            try:
                d, c = MoneyMath.parse_money_string(value)
                if d < 0:
                    raise ValueError()
            except ValueError:
                return False, "Invalid Input", "Please enter a valid, positive money value"

            try:
                self.asset_date_widget.get_date_string()
            except ValueError:
                return False, "Invalid Input", "Sorry, this date is invalid"

        return True, True, True
