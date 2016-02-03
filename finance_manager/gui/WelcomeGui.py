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
from finance_manager.gui.MainGui import MainGui
from finance_manager.GlobalVariables import GlobalVariables
from finance_manager.objects.Account import Account
from gi.repository import Gtk
import os


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
        self.account_combo_box = None
        self.start_button = None
        self.load_button = None
        self.new_button = None
        super().__init__(title)

    def lay_out(self):
        """
        Lays out all needed objects of the GUI
        :return: void
        """
        # Generate GTK objects
        available_accounts = os.listdir(GlobalVariables.account_dir)
        self.account_combo_box = GenericGtkGui.generate_combo_box(available_accounts)
        self.start_button = GenericGtkGui.generate_simple_button("Start", self.load_existing)
        self.load_button = GenericGtkGui.generate_simple_button("Load", self.load_external)
        self.new_button = GenericGtkGui.generate_simple_button("New", self.load_new)

        # Lay out objects
        self.grid.attach(self.account_combo_box["combo_box"], 0, 0, 20, 10)
        self.grid.attach_next_to(self.start_button, self.account_combo_box["combo_box"], Gtk.PositionType.RIGHT, 20, 10)
        self.grid.attach_next_to(self.load_button, self.account_combo_box["combo_box"], Gtk.PositionType.BOTTOM, 20, 10)
        self.grid.attach_next_to(self.new_button, self.load_button, Gtk.PositionType.RIGHT, 20, 10)

    def start(self):
        """
        Extends the functionality of GenericGtkGui's start method if needed
        :return: void
        """
        super(WelcomeGui, self).start()

    def load_existing(self, widget):
        """
        Loads an existing account and starts the main program
        :param widget: the Button/Text Field which activated this command
        :return: void
        """
        if widget is not None:
            account_name = GenericGtkGui.get_current_selected_combo_box_option(self.account_combo_box)
            account_file = os.path.join(GlobalVariables.account_dir, account_name)
            account = Account(account_file)
            MainGui(account_name, account, self).start()

    def load_external(self, widget):
        """
        Loads an external account from a file and starts the main program
        :param widget: the Button/Text Field which activated this command
        :return: void
        """
        if widget is not None:
            account_file = str(self.show_file_chooser_dialog())
            if account_file:
                account_name = os.path.basename(account_file)
                account = Account(account_file)
                MainGui(account_name, account, self).start()
            else:
                self.show_message_dialog("No file selected", "No file was selected, please select a file, "
                                                             "open an existing account or create a new account.")

    def load_new(self, widget):
        """
        Creates a new account from user input and starts the main program.
        If the account already exists however, an error message is shown
        :param widget: the Button/Text Field which activated this command
        :return: void
        """
        if widget is not None:
            account_name = "from text box"
            account_file = os.path.join(GlobalVariables.account_dir, account_name)
            if os.path.isfile(account_file):
                self.open_message_box("File exists", "This account already exists. Make sure to enter a name for the"
                                                     "account that has not been used yet")
            else:
                account = Account(account_file)
                MainGui(account_name, account, self).start()
