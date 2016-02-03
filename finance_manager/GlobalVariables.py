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
import platform


class GlobalVariables(object):
    """
    Class that provides easy access to some system-wide default variables
    """

    # class variables

    # OS independent
    home_dir = os.path.expanduser('~')
    operating_system = ""
    document_dir = ""

    # Linux Specific
    if platform.system() == "Linux":
        operating_system = "linux"
        document_dir = os.path.join(home_dir, ".finance-manager")

    # Windows Specific
    elif platform.system() == "Windows":
        operating_system = "windows"
        document_dir = os.path.join(home_dir, "Documents", "finance-manager")

    # OS independent once more
    account_dir = os.path.join(document_dir, "accounts")