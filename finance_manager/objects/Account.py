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
from finance_manager.utils.MoneyMath import MoneyMath


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
        self.account_balance_dollars = 0
        self.account_balance_cents = 0
        self.account_balance_no_assets_dollars = 0
        self.account_balance_no_assets_cents = 0
        self.asset_value_dollars = 0
        self.asset_value_cents = 0
        self.account_assets = []
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
        self.wallets = json_data["wallets"]
        self.account_assets = json_data["assets"]
        self.__update_balance__()

    def __update_balance__(self):
        """
        Updates the locally stored balance variables
        :return: void
        """
        self.account_balance_no_assets_dollars = 0
        self.account_balance_no_assets_cents = 0
        self.account_balance_dollars = 0
        self.account_balance_cents = 0
        self.asset_value_dollars = 0
        self.asset_value_cents = 0

        for wallet in self.wallets:
            self.account_balance_dollars += MoneyMath.parse_money_string(wallet["balance"])[0]
            self.account_balance_cents += MoneyMath.parse_money_string(wallet["balance"])[1]
            self.account_balance_no_assets_dollars += MoneyMath.parse_money_string(wallet["balance"])[0]
            self.account_balance_no_assets_cents += MoneyMath.parse_money_string(wallet["balance"])[1]
        for asset in self.account_assets:
            self.account_balance_dollars += MoneyMath.parse_money_string(asset["value"])[0]
            self.account_balance_cents += MoneyMath.parse_money_string(asset["value"])[1]
        self.asset_value_dollars = self.account_balance_dollars - self.account_balance_no_assets_dollars
        self.asset_value_cents = self.account_balance_cents - self.account_balance_no_assets_cents

        self.account_balance_dollars, self.account_balance_cents\
            = MoneyMath.normalize_value(self.account_balance_dollars, self.account_balance_cents)
        self.account_balance_no_assets_dollars, self.account_balance_no_assets_cents\
            = MoneyMath.normalize_value(self.account_balance_no_assets_dollars, self.account_balance_no_assets_cents)
        self.asset_value_dollars, self.asset_value_cents\
            = MoneyMath.normalize_value(self.asset_value_dollars, self.asset_value_cents)

    def __add_asset_to_balance__(self, value):
        """
        Adds an asset to the locally stored variables
        :param value: the value of the asset
        :return: void
        """
        dollars, cents = MoneyMath.parse_money_string(value)
        self.account_balance_dollars += dollars
        self.account_balance_cents += cents
        self.asset_value_dollars += dollars
        self.asset_value_cents += cents

    def __add_expense_or_income_to_balance__(self, value):
        """
        Adds an expense or income to the locally stored variables
        :param value: the value of the transaction
        :return: void
        """
        dollars, cents = MoneyMath.parse_money_string(value)
        self.account_balance_dollars += dollars
        self.account_balance_cents += cents
        self.account_balance_no_assets_dollars += dollars
        self.account_balance_no_assets_cents += cents

    def save(self):
        """
        Saves the current state of the account to the account file
        :return: void
        """
        json_dict = {"assets": self.account_assets,
                     "wallets": self.wallets}
        json_string = json.dumps(json_dict, indent=4)
        file = open(self.account_file_path, 'w')
        file.write(json_string)
        file.close()

    def get_wallet_names_as_list(self):
        """
        Retrieves the names of all wallets
        :return: a list of wallet names
        """
        wallet_names = []
        for wallet in self.wallets:
            wallet_names.append(wallet["name"])
        return wallet_names

    def get_all_expenses_as_list(self):
        """
        Returns a list of tuples containing all expenses
        :return: the list of tuples
        """
        expenses = []
        for wallet in self.wallets:
            for expense in wallet["expenses"]:
                expense_tuple = (expense["value"], expense["description"], expense["recipient"], expense["date"],
                                 wallet["name"])
                expenses.append(expense_tuple)
        return expenses

    def get_all_income_as_list(self):
        """
        Returns a list of tuples containing all income
        :return: the list of tuples
        """
        income = []
        for wallet in self.wallets:
            for inc in wallet["income"]:
                income_tuple = (inc["value"], inc["description"], inc["donor"], inc["date"], wallet["name"])
                income.append(income_tuple)
        return income

    def add_expense(self, value, description, recipient, date, wallet_name):
        """
        Adds an expense to the account
        :param value: The value of the expense
        :param description: a description of the expense
        :param recipient: the receiver of the expense (or the guy taking your money)
        :param date: the date/time on which the transaction took place
        :param wallet_name: the name of the wallet to which the expense should be added
        :return: the new wallet balance, or -1 if the wallet was not found
        """
        for wallet in self.wallets:
            if wallet["name"] == wallet_name:
                expense_dict = {"value": value, "description": description, "recipient": recipient, "date": date}
                wallet["expenses"].append(expense_dict)
                new_dollars, new_cents = MoneyMath.add_values_from_strings(wallet["balance"], "-" + value)
                wallet["balance"] = MoneyMath.encode_money_string(new_dollars, new_cents)
                self.__add_expense_or_income_to_balance__("-" + value)
                return wallet["balance"]
        return -1

    def add_expense_from_dict(self, expense_dict, wallet_name):
        """
        Adds an expense to the account from pre-prepared dictionaries
        :param expense_dict: the JSON-ready
        :param wallet_name: the name of the wallet to which the expense should be added
        :return: the new wallet balance, or -1 if the wallet was not found
        """
        for wallet in self.wallets:
            if wallet_name == wallet["name"]:
                wallet["expenses"].append(expense_dict)
                new_dollars, new_cents = MoneyMath.add_values_from_strings(wallet["balance"],
                                                                           "-" + expense_dict["value"])
                wallet["balance"] = MoneyMath.encode_money_string(-new_dollars, -new_cents)
                self.__add_expense_or_income_to_balance__("-" + expense_dict["value"])
                return wallet["balance"]
        return -1

    def add_income(self, value, description, donor, date, wallet_name):
        """
        Adds an income to the account
        :param value: the value of the income
        :param description: a description of the income
        :param donor: the one paying you :D
        :param date: the date of the transaction
        :param wallet_name: the name of the wallet to store this transaction in
        :return: the new wallet balance, or -1 if the wallet was not found
        """
        for wallet in self.wallets:
            if wallet["name"] == wallet_name:
                income_dict = {"value": value, "description": description, "donor": donor, "date": date}
                wallet["income"].append(income_dict)
                new_dollars, new_cents = MoneyMath.add_values_from_strings(wallet["balance"], value)
                wallet["balance"] = MoneyMath.encode_money_string(new_dollars, new_cents)
                self.__add_expense_or_income_to_balance__(value)
                return wallet["balance"]
        return -1

    def add_income_from_dict(self, income_dict, wallet_name):
        """
        Adds an income to the account from a JSON-ready dictionary
        :param income_dict: the income dictionary
        :param wallet_name: the name of the wallet involved in this transaction
        :return: the new wallet balance, or -1 if the wallet was not found
        """
        for wallet in self.wallets:
            if wallet_name == wallet["name"]:
                wallet["income"].append(income_dict)
                new_dollars, new_cents = MoneyMath.add_values_from_strings(wallet["balance"], income_dict["value"])
                wallet["balance"] = MoneyMath.encode_money_string(new_dollars, new_cents)
                self.__add_expense_or_income_to_balance__(income_dict["value"])
                return wallet["balance"]
        return -1

    def add_asset(self, value, description, date):
        """
        Adds an asset to the account
        :param value: the value of the asset
        :param description: a description of the asset
        :param date: the date of the asset (definition tbd)
        :return: void
        """
        self.account_assets.append({"value": value, "description": description, "date": date})
        self.__add_asset_to_balance__(value)

    def add_asset_from_dict(self, asset):
        """
        Adds an asset to the account from a JSON-ready dictionary
        :param asset: the asset to be added
        :return: void
        """
        self.account_assets.append(asset)
        self.__add_asset_to_balance__(asset["value"])
