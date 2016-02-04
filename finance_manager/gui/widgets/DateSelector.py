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
    from finance_manager.utils.DateManager import DateManager
except ImportError:
    from gui.GenericGtkGui import GenericGtkGui
    from utils.DateManager import DateManager

from gi.repository import Gtk


class DateSelector(Gtk.Grid):
    """
    A class that models a Date selector
    """

    def __init__(self, time=False):
        """
        Overrides the constructor, adding date selector elements
        @:param time: Flag that can be set to show time additionally to the date
        :return: void
        """
        super().__init__(column_spacing=1, row_spacing=1)
        self.time = time

        self.year_label = GenericGtkGui.generate_label("Year")
        self.month_label = GenericGtkGui.generate_label("Month")
        self.day_label = GenericGtkGui.generate_label("Day")
        self.year_text = GenericGtkGui.generate_text_entry("")
        self.month_text = GenericGtkGui.generate_text_entry("")
        self.day_text = GenericGtkGui.generate_text_entry("")
        if self.time:
            self.hour_label = GenericGtkGui.generate_label("Hour")
            self.minute_label = GenericGtkGui.generate_label("Minute")
            self.second_label = GenericGtkGui.generate_label("Second")
            self.hour_text = GenericGtkGui.generate_text_entry("")
            self.minute_text = GenericGtkGui.generate_text_entry("")
            self.second_text = GenericGtkGui.generate_text_entry("")

        self.attach(self.year_label, 0, 0, 10, 10)
        self.attach_next_to(self.month_label, self.year_label, Gtk.PositionType.RIGHT, 10, 10)
        self.attach_next_to(self.day_label, self.month_label, Gtk.PositionType.RIGHT, 10, 10)
        self.attach_next_to(self.year_text, self.year_label, Gtk.PositionType.BOTTOM, 10, 10)
        self.attach_next_to(self.month_text, self.month_label, Gtk.PositionType.BOTTOM, 10, 10)
        self.attach_next_to(self.day_text, self.day_label, Gtk.PositionType.BOTTOM, 10, 10)
        if self.time:
            self.attach_next_to(self.hour_label, self.day_label, Gtk.PositionType.RIGHT, 10, 10)
            self.attach_next_to(self.minute_label, self.hour_label, Gtk.PositionType.RIGHT, 10, 10)
            self.attach_next_to(self.second_label, self.minute_label, Gtk.PositionType.RIGHT, 10, 10)
            self.attach_next_to(self.hour_text, self.hour_label, Gtk.PositionType.BOTTOM, 10, 10)
            self.attach_next_to(self.minute_text, self.minute_label, Gtk.PositionType.BOTTOM, 10, 10)
            self.attach_next_to(self.second_text, self.second_label, Gtk.PositionType.BOTTOM, 10, 10)
        self.set_current_time()
        self.current_time_check = GenericGtkGui.generate_check_box("Current Time")
        self.attach_next_to(self.current_time_check, self.year_text, Gtk.PositionType.BOTTOM, 30, 5)

    def set_current_time(self):
        """
        Sets the entries to the current time
        :return:
        """
        self.year_text.set_text(DateManager.get_current_year_string())
        self.month_text.set_text(DateManager.get_current_month_string())
        self.day_text.set_text(DateManager.get_current_day_string())
        if self.time:
            self.hour_text.set_text(DateManager.get_current_hour_string())
            self.minute_text.set_text(DateManager.get_current_minute_string())
            self.second_text.set_text(DateManager.get_current_second_string())

    def get_date_string(self):
        """
        Returns the input as a date string
        :return: the date string
        """
        year = self.year_text.get_text()
        month = self.month_text.get_text()
        day = self.day_text.get_text()
        if self.time:
            hour = self.hour_text.get_text()
            minute = self.minute_text.get_text()
            second = self.second_text.get_text()
        else:
            hour = "0"
            minute = "0"
            second = "0"

        return DateManager.get_time_as_date_string(year, month, day, hour, minute, second)
