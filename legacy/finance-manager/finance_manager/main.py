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
    from finance_manager.gui.WelcomeGui import WelcomeGui
    from finance_manager.GlobalVariables import GlobalVariables
except ImportError:
    from gui.WelcomeGui import WelcomeGui
    from GlobalVariables import GlobalVariables
import os


def main():
    """
    The main method of the program
    :return: void
    """
    # Create working directories if they don't exist yet
    if not os.path.isdir(GlobalVariables.account_dir):
        os.makedirs(GlobalVariables.account_dir)

    WelcomeGui().start()

if __name__ == '__main__':
    main()
