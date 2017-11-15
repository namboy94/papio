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
    from finance_manager.gui.GenericGtkGui import GenericGtkGui
    from finance_manager.gui.widgets.DateSelector import DateSelector
except ImportError:
    from gui.GenericGtkGui import GenericGtkGui
    from gui.widgets.DateSelector import DateSelector

from gi.repository import Gtk


class GenericGtkDialog(Gtk.Dialog):
    """
    A generic GTK Dialog wrapper
    """

    def __init__(self, parent):
        """
        Initializes the dialog
        :param parent: the parent window of this dialog
        :return: void
        """
        self.parent = parent
        Gtk.Dialog.__init__(self, "My Dialog", parent, 0,
                            (Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL,
                             Gtk.STOCK_OK, Gtk.ResponseType.OK))
        self.set_default_size(300, 200)
        self.box = self.get_content_area()
        self.grid = Gtk.Grid(column_homogeneous=True,
                             column_spacing=1,
                             row_spacing=1)
        self.box.pack_start(self.grid, False, False, 1)
        self.lay_out()

    def lay_out(self):
        """
        Lays out the arrangement of the objects contained in this dialog
        :return: void
        """
        raise NotImplementedError("lay_out method not implemented")

    def start(self):
        """
        Starts the dialog
        :return: the result type of the dialog
        """
        self.box.show_all()
        return super(GenericGtkDialog, self).run()

    def add_label_and_text(self, text, x_pos, y_pos, x_dim, y_dim):
        """
        Adds a label and a text to the bottom of the dialog
        :param text: The text to be displayed on the label
        :param x_pos: the x position
        :param y_pos: the y position
        :param x_dim: the x size
        :param y_dim: the y size
        :return: the text field object
        """
        label_object = GenericGtkGui.generate_label(text)
        text_object = GenericGtkGui.generate_text_entry("")
        self.grid.attach(label_object, x_pos, y_pos, int(x_dim / 2), y_dim)
        self.grid.attach(text_object, x_pos + int(x_dim / 2), y_pos, int(x_dim / 2), y_dim)
        return text_object

    def add_label_and_combo_box(self, text, options, x_pos, y_pos, x_dim, y_dim):
        """
        Adds a label and a combo box to the bottom of the dialog
        :param text: The text to be displayed on the label
        :param options: the x position
        :param x_pos: the x position
        :param y_pos: the y position
        :param x_dim: the x size
        :param y_dim: the y size
        :return: the combo box list store object
        """
        label_object = GenericGtkGui.generate_label(text)
        combo_box = GenericGtkGui.generate_combo_box(options)
        self.grid.attach(label_object, x_pos, y_pos, int(x_dim / 2), y_dim)
        self.grid.attach(combo_box["combo_box"], x_pos + int(x_dim / 2), y_pos, int(x_dim / 2), y_dim)
        return combo_box

    def add_date_widget(self, x_pos, y_pos, x_dim, y_dim):
        """
        Adds a date selector widget to the dialog
        :param x_pos: the x position
        :param y_pos: the y position
        :param x_dim: the x size
        :param y_dim: the y size
        :return: the widget
        """
        widget = DateSelector(True)
        self.grid.attach(widget, x_pos, y_pos, x_dim, y_dim)
        return widget
