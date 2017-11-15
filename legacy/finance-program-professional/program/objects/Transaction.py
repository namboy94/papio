"""
Copyright 2015-2017 Hermann Krumrey <hermann@krumreyh.com>

This file is part of finance-program-professional.

finance-program-professional is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

finance-program-professional is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with finance-program-professional.  If not, see <http://www.gnu.org/licenses/>.
"""

"""
Transaction
class that models a transaction as an object

Created on Apr 15, 2015
Modified on Apr 15, 2015

@author Hermann Krumrey
@version 1.0
"""

class Transaction(object):
    
    """
    Constructor
    constructs a new Transaction object
    @param name - the name of the transaction
    @param value - the numerical value of the transaction
    @param date - the date as string in the form YYYY-MM-DD
    @param location - the 'location' or wallet type from which this transaction originates
    """
    def __init__(self,name,value,date,location):
        self.name = name
        self.value = value
        self.dateday = int(date.split("-")[2])
        self.datemonth = int(date.split("-")[1])
        self.dateyear = int(date.split("-")[0])
        self.date = date
        self.location = location