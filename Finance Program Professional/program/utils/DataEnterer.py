'''
DataEnterer
collection of methods that enter data into the data files

Created on Apr 15, 2015
Modified on Apr 16, 2015

@author Hermann Krumrey
@version 1.0
'''

#imports
import time
from program.objects.Asset import Asset
from program.objects.Transaction import Transaction
from program.objects.Wallet import Wallet

"""
addNewTransaction
adds a new transaction to a wallet (takes multiple types of wallets)
"""
def addNewTransaction(userInput,dataFiles,wallets,assets):
    
    #establishes the transaction type
    transactionType = userInput.split(" ")[1]
    
    #saves the current date to a string
    date = time.strftime("%Y-%m-%d")
    
    #Saves user input to variables:
    value = float(userInput.split(" ")[2])
    valueAsString = "%.2f" % (value)
    name = userInput.split(" ")[3]
    if transactionType != "asset":
        walletType = userInput.split(" ")[5]
    
    #change value to negative if handling expenses
    if transactionType == "expense":
        value = value * (-1)
        
    if transactionType == "asset":
        assets.addTransaction(Asset(name,value))
    else:
        newTransaction = Transaction(name,value,date,walletType)
        for wallet in wallets:
            if wallet.name == walletType:
                wallet.addTransaction(newTransaction)
                break
    
    #Write to files
    newLine = ""
    if transactionType == "expense":
        f = open(dataFiles[2], "r")
        if not f.readline() == "":
            newLine = "\n"
        f.close()
        f = open(dataFiles[2], "a")
        f.write(newLine + valueAsString + " " + date + " " + name + " " + walletType)
        f.close()
    elif transactionType == "income":
        f = open(dataFiles[3], "r")
        if not f.readline() == "":
            newLine = "\n"
        f.close()
        f = open(dataFiles[3], "a")
        f.write(newLine + valueAsString + " " + date + " " + name + " " + walletType)
        f.close()
    elif transactionType == "asset":
        f = open(dataFiles[1], "r")
        if not f.readline() == "":
            newLine = "\n"
        f.close()
        f = open(dataFiles[1], "a")
        f.write(newLine + valueAsString + " " + name)
        f.close()
        
"""
transfer
transfer a set amount from one wallet type to another and edit the data files accordingly
@param userInput - the user's input from the inputparser
@param dataFiles - the files containing the data
@param wallets - the list of all wallets
"""
def transfer(userInput, dataFiles, wallets):
    
    #parsing the user's input
    value = float(userInput.split()[1])
    source = userInput.split()[3]
    destination = userInput.split()[5]
    
    #establishes which wallets are affected
    index = 0
    for wallet in wallets:
        if wallet.name == source:
            sourceWalletIndex = index
        if wallet.name == destination:
            destinationWalletIndex = index
        index += 1
    index = 0
    
    #adds the transaction to the wallets
    date = time.strftime("%Y-%m-%d")
    valueAsString = "%.2f" % (value)
    negativeValue = value * (-1)
    sourceExpense = Transaction("transfer_to_" + destination,negativeValue,date,source)
    destinationIncome = Transaction("transfer_from_" + source,value,date,destination)
    wallets[sourceWalletIndex].addTransaction(sourceExpense)
    wallets[destinationWalletIndex].addTransaction(destinationIncome)
    
    #saves the changes to the data files
    expenseNewLine = ""
    incomeNewLine = ""
    expenseFile = open(dataFiles[2],"r")
    incomeFile = open(dataFiles[3],"r")
    if not expenseFile.readline() == "": expenseNewLine = "\n"
    if not incomeFile.readline() == "": incomeNewLine = "\n"
    expenseFile.close()
    incomeFile.close()
    expenseFile = open(dataFiles[2],"a")
    incomeFile = open(dataFiles[3],"a")
    expenseFile.write(expenseNewLine + valueAsString + " " + date + " transfer_to_" + destination + " " + source)
    incomeFile.write(incomeNewLine + valueAsString + " " + date + " transfer_from_" + source + " " + destination)
    expenseFile.close()
    incomeFile.close()

"""
walletAdder
Creates a new wallet with a starting value and saves the wallet to the data files
Furthermore, the new wallet object is appended to the existing list of wallets
"""
def walletAdder(dataFiles,wallets,userInput):
    
    #getting current date
    date = time.strftime("%Y-%m-%d")
    
    #Parsing user input
    walletName = userInput.split(" ")[2]
    startingValue = float(userInput.split(" ")[3])
    startingValueString = "%.2f" % (startingValue)
    
    #Create new wallet and append to wallet list
    newWallet = Wallet(walletName)
    startingTransaction = Transaction("starting_value",startingValue,date,walletName)
    newWallet.addTransaction(startingTransaction)
    wallets.append(newWallet)
    
    #Write the new data to file.
    walletNewLine = ""
    incomeNewLine = ""
    walletFile = open(dataFiles[0], "r")
    incomeFile = open(dataFiles[3], "r")
    if not walletFile.readline() == "": walletNewLine = "\n"
    if not incomeFile.readline() == "": incomeNewLine = "\n"
    walletFile.close()
    incomeFile.close()
    walletFile = open(dataFiles[0], "a")
    incomeFile = open(dataFiles[3], "a")
    walletFile.write(walletNewLine + walletName)
    incomeFile.write(incomeNewLine + startingValueString + " " + date + " starting_value " + walletName)
    walletFile.close()
    incomeFile.close()