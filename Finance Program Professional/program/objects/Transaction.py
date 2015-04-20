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