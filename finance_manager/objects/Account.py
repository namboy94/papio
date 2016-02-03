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
import os
import json


class Account(object):
    """
    The Account class handles the information stored in an account file and serves
    it via publicly accessible methods.
    """

    def __init__(self, account_file_path):
        """
        Constructor method. It reads the information given in the stored account file.
        If the file does not exist, a new account will be created at the specified location
        @:param account_file_path - the path to the account file.
        @:return: void
        """
        # object variables
        self.account_file_path = ""
        self.account_balance = 0
        self.account_balance_no_assets = 0
        self.account_assets = []
        self.account_expenses = []
        self.account_income = []
        self.wallets = []

        self.account_file_path = account_file_path
        self.__read_file__()

    def __read_file__(self):
        """
        Parses the account file and stores the information to the local variables.
        If the account file does not exist, a new account will be created
        :return: void
        """
        if os.path.isfile(self.account_file_path):
            file = open(self.account_file_path, 'r')
            data = json.load(file)
            file.close()
            self.__parse_json__(data)
        else:
            self.save()

    def __parse_json__(self, json_data):
        """
        Parses the json data
        :param json_data: the json data to be parsed
        :return: void
        """
        self.account_assets = json_data["assets"]
        self.account_expenses = json_data["expenses"]
        self.account_income = json_data["income"]
        self.wallets = json_data["wallets"]
        for wallet in self.wallets:
            self.account_balance += int(wallet["balance"])
            self.account_balance_no_assets += int(wallet["balance"])
        for asset in self.account_assets:
            self.account_balance += int(asset["value"])

    def save(self):
        """
        Saves the current state of the account to the account file
        :return: void
        """
        json_dict = {"assets": self.account_assets,
                     "income": self.account_income,
                     "expenses": self.account_expenses,
                     "wallets": self.wallets}
        json_string = json.dumps(json_dict, indent=4)
        file = open(self.account_file_path, 'w')
        file.write(json_string)
        file.close()
