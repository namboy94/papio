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
import datetime


class DateManager(object):
    """
    Class that handles date-related functions
    """

    @staticmethod
    def get_current_date_time_as_string():
        """
        Determines the current time and turns it into a string
        :return: the date string
        """
        year = DateManager.get_current_year_string()
        month = DateManager.get_current_month_string()
        day = DateManager.get_current_day_string()
        hour = DateManager.get_current_hour_string()
        minute = DateManager.get_current_minute_string()
        second = DateManager.get_current_second_string()

        return year + "/" + month + "/" + day + ":" + hour + "-" + minute + "-" + second

    @staticmethod
    def get_time_as_date_string(year, month, day, hour, minute, second):
        """
        Turns Date values into a date string
        :param year: the year
        :param month: the month
        :param day: the day
        :param hour: the hour
        :param minute: the minute
        :param second: the second
        :return: the date string
        """
        return year + "/" + month.zfill(2) + "/" + day.zfill(2) + ":" + \
               hour.zfill(2) + "-" + minute.zfill(2) + "-" + second.zfill(2)

    @staticmethod
    def get_current_year_string():
        """
        Returns the current year as a string
        :return: the year string
        """
        return str(datetime.datetime.now().year)

    @staticmethod
    def get_current_month_string():
        """
        Returns the current month as a string
        :return: the month string
        """
        return str(datetime.datetime.now().month).zfill(2)

    @staticmethod
    def get_current_day_string():
        """
        Returns the current day as a string
        :return: the day string
        """
        return str(datetime.datetime.now().day).zfill(2)

    @staticmethod
    def get_current_hour_string():
        """
        Returns the current hour as a string
        :return: the hour string
        """
        return str(datetime.datetime.now().hour).zfill(2)

    @staticmethod
    def get_current_minute_string():
        """
        Returns the current minute as a string
        :return: the minute string
        """
        return str(datetime.datetime.now().minute).zfill(2)

    @staticmethod
    def get_current_second_string():
        """
        Returns the current second as a string
        :return: the second string
        """
        return str(datetime.datetime.now().second).zfill(2)
