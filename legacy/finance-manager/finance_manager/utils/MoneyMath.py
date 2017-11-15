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


class MoneyMath(object):
    """
    Class containing some abstract methods to handle monetary data
    """

    @staticmethod
    def parse_money_string(money_string):
        """
        Parses a money string and returns it as a tuple of dollars and cents
        :param money_string: the string to be parsed
        :return: a tuple of dollars and cents
        :raise: an Exception if an illegal string is entered (e.g. 123.456)
        :raise: a NumberFormatException if non-number values are given
        """
        money_string = money_string.replace(",", ".")
        dollars = int(money_string.split(".")[0])
        if len(money_string.split(".")) == 1:
            cents = 0
        elif len(money_string.split(".")) == 2:
            cents = int(money_string.split(".")[1])
        else:
            raise Exception("Illegal Money String: " + money_string)
        if dollars < 0:
            cents *= -1
        return dollars, cents

    @staticmethod
    def encode_money_string(dollars, cents):
        """
        Encodes a pair of dollar and cent values to a money string
        :param dollars: the amount of dollars
        :param cents: the amount of cents
        :return: the created string
        """
        dollar_string = str(dollars)
        if cents < 0:
            cent_string = str(-cents)
        else:
            cent_string = str(cents)
        return dollar_string + "." + cent_string

    @staticmethod
    def add_values_from_strings(money_string_1, money_string_2):
        """
        Adds two monetary values together
        :param money_string_1: the first value
        :param money_string_2: the second value
        :return: the sum of the values as tuple of dollars and cents
        """
        value_1 = MoneyMath.parse_money_string(money_string_1)
        value_2 = MoneyMath.parse_money_string(money_string_2)
        dollars = value_1[0] + value_2[0]
        cents = value_1[1] + value_2[1]
        return MoneyMath.normalize_value(dollars, cents)

    @staticmethod
    def add_values_from_tuple(dollars_1, cents_1, dollars_2, cents_2):
        """
        Adds two monetary values together
        :param dollars_1: the first dollar value
        :param cents_1: the first cent value
        :param dollars_2: the second dollar value
        :param cents_2: the second cent value
        :return: the sum of the values as a tuple of dollars and cents
        """
        dollars = dollars_1 + dollars_2
        cents = cents_1 + cents_2
        return MoneyMath.normalize_value(dollars, cents)

    @staticmethod
    def normalize_value(dollars, cents):
        """
        Checks if the cents value is above 100 and if it is, reduces it until it isn't anymore
        while always adding 1 to the dollar value
        :param dollars: the dollar value
        :param cents: the cent value
        :return: the normalized dollar and cent value
        """
        normalized_dollars = dollars
        normalized_cents = cents
        while normalized_cents >= 100:
            normalized_cents -= 100
            normalized_dollars += 1
        return normalized_dollars, normalized_cents
