"""
Copyright 2015-2017 Hermann Krumrey <hermann@krumreyh.com>

This file is part of finance-program.

finance-program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

finance-program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with finance-program.  If not, see <http://www.gnu.org/licenses/>.
"""

class Wallets(object):

    value = 0.0
    transactions = []
    name = ""


    def __init__(self, name):
        
        self.value = 0
        self.transactions = []
        self.name = name
        
    def addTransaction(self,transaction):
        self.value = self.value + transaction.value
        self.transactions.append(transaction)