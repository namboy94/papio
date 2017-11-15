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

from nose.tools import with_setup
from nose.tools import assert_true

try:
    from objects.Account import Account
except ImportError:
    from finance_manager.objects.Account import Account


class TestFootballScores(object):
    """
    Unit Test Class that tests the Account class
    """

    def __init__(self):
        """
        Constructor
        """
        str(self)

    @classmethod
    def setup_class(cls):
        """
        Sets up the test class
        """
        print()

    @classmethod
    def teardown_class(cls):
        """
        Tears down the test class
        """
        print()

    def setup(self):
        """
        Sets up a test
        """
        str(self)

    def teardown(self):
        """
        Tears down a test
        """
        str(self)

    @with_setup(setup, teardown)
    def test_dummy(self):
        """
        Dummy Test
        """
        assert_true(True)
