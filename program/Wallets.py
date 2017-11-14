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