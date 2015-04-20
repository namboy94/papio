"""
Wallet
class that models a wallet as an object

Created on Apr 15, 2015
Modified on Apr 15, 2015

@author Hermann Krumrey
@version 1.0
"""

class Wallet(object):
    
    """
    Constructor
    constructs a new wallet object
    Initializes the wallet with a balance of 0.0 and no transactions
    @param name - the name of the wallet
    """
    def __init__(self,name):
        self.value = 0.0
        self.transactions = []
        self.name = name
        
    """
    addTransaction
    adds a new transaction to the wallet and adjusts the balance accordingly
    @param transaction - the Transaction object to be added to the wallet (Can also be an asset)
    """
    def addTransaction(self,transaction):
        self.value = self.value + transaction.value
        self.transactions.append(transaction)