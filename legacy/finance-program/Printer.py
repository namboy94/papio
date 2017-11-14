import re

def assetPrinter(assets):
    
    grandTotal = 0.0
    longestString = 0
    
    longestNameLength = 0
    for asset in assets.transactions:
        if len(asset.name) > longestNameLength:
            longestNameLength = len(asset.name)
    
    longestValueLength = 0
    for asset in assets.transactions:
        valueString = "%.2f" % (asset.value)
        if len(valueString) > longestValueLength:
            longestValueLength = len(valueString)
    if len("Grand Total:") > longestNameLength:
        longestNameLength = len("Grand Total:")
    
    assets.transactions.sort(key=lambda x: x.value)

    for asset in assets.transactions:
        nameSpaces = ((longestNameLength - len(asset.name)) + 6) * " "
        valuePrint = "%.2f" % (asset.value)
        valueSpaces = (longestValueLength - len(valuePrint)) * " "
        grandTotal = grandTotal + asset.value
        printOut = asset.name + nameSpaces + valueSpaces + valuePrint
        print asset.name + nameSpaces + valueSpaces + valuePrint
        if longestString < len(printOut):
            longestString = len(printOut)
    
    print longestString * "-"
    grandTotalString = "%.2f" % (grandTotal)
    spaces = (((longestNameLength - len("Grand Total:")) + 6) + (longestValueLength - len(grandTotalString))) * " "
    print "Grand Total:%s%.2f" % (spaces, grandTotal)
        
        
def transactionPrinter(transactions, date, mode, transactiontype):
    
    acceptedTransactions = []
    
    for transaction in transactions:
        regexcheck1 = re.compile("transfer_from_[wallet|cash|credit|bank]")
        regexcheck2 = re.compile("transfer_to_[wallet|cash|credit|bank]")
        if transactiontype == "income" and transaction.value < 0:
            continue
        if transactiontype == "expense" and transaction.value >= 0:
            continue
        if regexcheck1.match(transaction.name) or regexcheck2.match(transaction.name) or transaction.name == "starting_value" or transaction.name == "correction":
            continue
        
        if transaction.dateyear == int(date.split("-")[0]):
            if mode == "year":
                acceptedTransactions.append(transaction)
                continue
            if transaction.datemonth == int(date.split("-")[1]):
                if mode == "month":
                    acceptedTransactions.append(transaction)
                    continue
                if transaction.dateday == int(date.split("-")[2]):
                    acceptedTransactions.append(transaction)
                    
    acceptedTransactions.sort(key=lambda x: x.value)
    acceptedTransactions.sort(key=lambda x: x.location)
    acceptedTransactions.sort(key=lambda x: x.date)
    
    walletTotal = 0.0
    cashTotal = 0.0
    creditTotal = 0.0
    bankTotal = 0.0
    grandTotal = 0.0
    longestString = 0
    
    longestNameLength = 0
    longestValueLength = 0
    for transaction in acceptedTransactions:
        if len(transaction.name) > longestNameLength:
            longestNameLength = len(transaction.name)
        valueString = "%.2f" % (transaction.value)
        if len(valueString) > longestValueLength:
            longestValueLength = len(valueString)
        
    
    
    for transaction in acceptedTransactions:
        tab = 4 * " "
        nameSpaces = (longestNameLength - len(transaction.name) + 6) * " "
        valueString = "%.2f" % (transaction.value)
        valueSpaces = (longestValueLength - len(valueString)) * " "
        printOut = transaction.name + nameSpaces + valueSpaces + str(transaction.value) + tab + transaction.date + tab + transaction.location
        print transaction.name + nameSpaces + valueSpaces + valueString + tab + transaction.date + tab + transaction.location
        
        if longestString < len(printOut):
            longestString = len(printOut)
        
        if transaction.location == "wallet":
            walletTotal = walletTotal + transaction.value
        elif transaction.location == "cash":
            cashTotal = cashTotal + transaction.value
        elif transaction.location == "credit":
            creditTotal = creditTotal + transaction.value
        elif transaction.location == "bank":
            bankTotal = bankTotal + transaction.value
        grandTotal = grandTotal + transaction.value
    
    spacesBank = (longestString - len("Total Bank:") - len("%.2f" % (bankTotal))) * " "
    spacesCash = (longestString - len("Total Cash:") - len("%.2f" % (cashTotal))) * " "
    spacesCredit = (longestString - len("Total Credit:") - len("%.2f" % (creditTotal))) * " "
    spacesWallet = (longestString - len("Total Wallet:") - len("%.2f" % (walletTotal))) * " "
    spacesTotal = (longestString - len("Grand Total:") - len("%.2f" % (grandTotal))) * " "
    
    print longestString * "-"
    print "Total Bank:%s%.2f" % (spacesBank, bankTotal)
    print "Total Cash:%s%.2f" % (spacesCash, cashTotal)
    print "Total Credit:%s%.2f" % (spacesCredit, creditTotal)
    print "Total Wallet:%s%.2f" % (spacesWallet, walletTotal)
    print longestString * "-"
    print "Grand Total:%s%.2f" % (spacesTotal, grandTotal)
        
def walletPrint(walletType, wallet, cash, credit, bank):
    if walletType == "wallet":
        print "Current Wallet Balance: %.2f" % (wallet.value)
    elif walletType == "cash":
        print "Current Cash Balance: %.2f" % (cash.value)      
    elif walletType == "credit":
        print "Current Credit Card Balance: %.2f" % (credit.value)
    elif walletType == "bank":
        print "Current Bank Balance: %.2f" % (bank.value)
