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
import datetime


class DateManager(object):
    """
    Class that handles date-related functions
    """

    @staticmethod
    def get_current_date_time_as_string(time=True):
        """
        Determines the current time and turns it into a string
        :param time: flag to include the current time
        :return: the date string
        """
        year = DateManager.get_current_year_string()
        month = DateManager.get_current_month_string()
        day = DateManager.get_current_day_string()
        if time:
            hour = DateManager.get_current_hour_string()
            minute = DateManager.get_current_minute_string()
            second = DateManager.get_current_second_string()
            return year + "/" + month + "/" + day + ":" + hour + "-" + minute + "-" + second
        else:
            return year + "/" + month + "/" + day

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
        valid = DateManager.validate_date(year, month, day, hour, minute, second)
        if valid:
            return year + "/" + month.zfill(2) + "/" + day.zfill(2) + ":" + \
                   hour.zfill(2) + "-" + minute.zfill(2) + "-" + second.zfill(2)
        else:
            raise ValueError("Invalid Date")

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

    @staticmethod
    def validate_date(year, month, day, hour, minute, second):
        """
        Checks if a valid date was entered
        :param year: the year to check
        :param month: the month to check
        :param day: the day to check
        :param hour: the hour to check
        :param minute: the minute to scheck
        :param second: the second to check
        :return: True, if the date is valid, False otherwise
        """
        try:
            int(year)
            int(month)
            int(day)
            int(hour)
            int(minute)
            int(second)
        except ValueError:
            return False

        if int(year) < 1:
            return False
        if int(month) < 1 or int(month) > 12:
            return False
        if int(day) < 1:
            if int(day) > 28 and int(month) == 2:
                if int(year) % 4 != 0:
                    return False
                elif int(day) > 29:
                    return False
            elif int(day) > 30 and int(month) in [4, 6, 9, 11]:
                return False
            elif int(day) > 31 and int(month) in [1, 3, 5, 7, 8, 10, 12]:
                return False
        if int(hour) < 1 or int(hour) > 23:
            return False
        if int(minute) < 1 or int(minute) > 59:
            return False
        if int(second) < 1 or int(second) > 59:
            return False
        return True
