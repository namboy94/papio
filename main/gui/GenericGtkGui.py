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
from gi.repository import Gtk


class GenericGtkGui(Gtk.Window):
    """
    Class that models a generic grid-based GTK Gui. This should be used like an
    abstract class from which other classes can inherit from.
    """

    def __init__(self, title="Finance Manager", parent=None):
        """
        Constructor which initializes the GUI
        :param title: The window title. Defaults to "Finance Manager"
        :param parent: The parent GUI window, provided one wants this Window
                        to have a parent, otherwise it defaults to None
        :return: void
        """
        # object variables
        self.grid = None
        self.window = None
        self.parent = None

        self.parent = parent

        # initialize GTK
        Gtk.Window.__init__(self, title=title)
        self.set_border_width(10)

        # Set up Grid Layout
        self.grid = Gtk.Grid()
        self.grid.set_column_homogeneous(True)
        self.grid.set_row_homogeneous(True)
        self.add(self.grid)

        # Delegated to the child classes
        self.lay_out()

    def lay_out(self):
        """
        Abstract method that adds objects to the GUI and places them into the layout.
        :raise NotImplementedError to indicate that this class is abstract
        :return: void
        """
        raise NotImplementedError("lay_out method not implemented")

    def start(self):
        """
        Starts the GUI and enters the gui mainloop. If this window has a parent,
        it will be hidden during the mainloop and reappear at the end
        :return: void
        """
        if self.parent:
            self.parent.window.hide()
        self.window = self
        self.window.connect("delete-event", Gtk.main_quit)
        self.window.show_all()
        Gtk.main()
        if self.parent:
            self.parent.window.show_all()
