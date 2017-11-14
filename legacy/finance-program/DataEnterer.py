import time
from Transaction import Transaction
from Asset import Asset

def addNewTransaction(value, name, walletType, transactionType, wallet, cash, credit, bank, asset, files):
    date = time.strftime("%Y-%m-%d")
    valueString = "%.2f" % (value)
    if transactionType == "expense":
        value = value * (-1)
    if transactionType == "asset":
        newAsset = Asset(name, value)
    else:
        newTransaction = Transaction(name, value, date, walletType)
    if walletType == "wallet":
        wallet.addTransaction(newTransaction)
    elif walletType == "credit":
        credit.addTransaction(newTransaction)
    elif walletType == "cash":
        cash.addTransaction(newTransaction)    
    elif walletType == "bank":
        bank.addTransaction(newTransaction)
    elif walletType == "asset":
        asset.addTransaction(newAsset)
        
    newline = ""
        
    if transactionType == "expense":
        f = open(files[1], "r")
        if not f.readline() == "":
            newline = "\n"
        f.close()
        f = open(files[1], "a")
        f.write(newline + valueString + " " + date + " " + name + " " + walletType)
        f.close()
    elif transactionType == "income":
        f = open(files[2], "r")
        if not f.readline() == "":
            newline = "\n"
        f.close()
        f = open(files[2], "a")
        f.write(newline + valueString + " " + date + " " + name + " " + walletType)
        f.close()
    elif transactionType == "asset":
        f = open(files[3], "r")
        if not f.readline() == "":
            newline = "\n"
        f.close()
        f = open(files[3], "a")
        f.write(newline + valueString + " " + name)
        f.close()
    
def transfer(source, destination, value, files, wallet, cash, credit, bank):
    
    if source == "wallet":
        source = wallet
    elif source == "cash":
        source = cash
    elif source == "credit":
        source = credit
    elif source == "bank":
        source = bank
        
    if destination == "wallet":
        destination = wallet
    elif destination == "cash":
        destination = cash
    elif destination == "credit":
        destination = credit
    elif destination == "bank":
        destination = bank
    
    date = time.strftime("%Y-%m-%d")
    valueString = "%.2f" % (value)
    negativeValue = value * (-1)
    sourceExpense = Transaction("transfer_to_" + destination.name, negativeValue, date, source.name)
    destinationIncome = Transaction("transfer_from_" + source.name, value, date, destination.name)
    source.addTransaction(sourceExpense)
    destination.addTransaction(destinationIncome)
    
    expenseNewLine = ""
    incomeNewLine = ""
    
    expenseFile = open(files[1], "r")
    incomeFile = open(files[2], "r")
    if not expenseFile.readline() == "":    expenseNewLine = "\n"
    if not incomeFile.readline() == "":     incomeNewLine = "\n"
    expenseFile.close
    incomeFile.close
    
    expenseFile = open(files[1], "a")
    incomeFile = open(files[2], "a")
    expenseFile.write(expenseNewLine + valueString + " " + date + " transfer_to_" + destination.name + " " + source.name)
    incomeFile.write(incomeNewLine + valueString + " " + date + " transfer_from_" + source.name + " " + destination.name)
    expenseFile.close()
    incomeFile.close()