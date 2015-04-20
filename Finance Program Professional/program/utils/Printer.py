'''
Printer
collection of methods that print various data to the command line

Created on Apr 15, 2015
Modified on Apr 15, 2015

@author Hermann Krumrey
@version 1.0
'''

#imports
import time
import re

"""
helpPrinter
prints the output of the user's "help" command. It lists all possible commands.
"""
def helpPrinter():

    print "help                                                           Outputs all possible console commands"
    print "assets                                                         Outputs all current assets"
    print "(transactions|expenses|income) (<PERIOD>)                      Outputs all transaction during a given time period"
    print "add (expense|income) <VALUE> <NAME> (from|to) <WALLLET>        Adds a new transaction"
    print "add asset <VALUE> <NAME>                                       Adds a new asset"
    print "transfer <VALUE> from <SOURCE> to <DESTINATION>                Transfers an amount from one wallet type to another"
    print "add wallet <NAME> <STARTINGVALUE>                              Adds a new wallet type"
    print "total money                                                    Outputs the sum of all liquid assets"
    print "quit                                                           Ends the program"
    
"""
assetPrinter
prints all assets to the console sorted by value
@param assets - the assets to be printed. Must be a Wallet object
"""
def assetPrinter(assets):
    
    #sorts assets by value and saves as local variable
    sortedAssets = assets.transactions
    sortedAssets.sort(key=lambda x: x.value)
    
    #local variables that keep track of some stats, mostly for formating
    totalValue = 0.00
    longestNameLength = len("Grand Total:")
    longestValueLength = len("0.00")
    longestPrintLength = len("Grand Total: 0.00")
    
    #loop establishes which values and names have the longest string representations
    for asset in sortedAssets:
        if len(asset.name) > longestNameLength:
            longestNameLength = len(asset.name)
        if len("%.2f" % (asset.value)) > longestValueLength:
            longestValueLength = len("%.2f" % (asset.value))
            
    #loop prints the individual names and values of all assets
    for asset in sortedAssets:
        nameSpaces = ((longestNameLength - len(asset.name)) + 6) * " "
        valueSpaces = (longestValueLength - len("%.2f" % (asset.value))) * " "
        totalValue = totalValue + asset.value
        printOut = ("%s%s%s%.2f" % (asset.name,nameSpaces,valueSpaces,asset.value))
        print printOut
        if longestPrintLength < len(printOut):
            longestPrintLength = len(printOut)
    
    #divider
    print longestPrintLength * "-"
    
    #prints the total value of all assets
    spaces = (((longestNameLength - len("Grand Total:")) + 6) + (longestValueLength - len("%.2f" % (totalValue)))) * " "
    print "Grand Total:%s%.2f" % (spaces, totalValue)
    
"""
transactionPrinter
prints all transactions in a given period
@param wallets - list of all wallets
@param userInput - the user's unedited input
@param mode - the mode of the date format (year, month, day or all)
"""
def transactionPrinter(wallets,userInput,mode):
    
    #first, the userInput must be parsed
    spaceSplitInput = userInput.split(" ")
    
    if spaceSplitInput[0] == "all":
        transactionType = spaceSplitInput[1]
    else:
        transactionType = spaceSplitInput[0]
        
    if len(spaceSplitInput) == 1:
        date = time.strftime("%Y")
    else:
        date = spaceSplitInput[1]
    
    #excluded regex transactions
    walletRegexString = "("
    index = 1
    for wallet in wallets:
        walletRegexString = walletRegexString + wallet.name
        if index < len(wallets):
            walletRegexString = walletRegexString + "|"
        else:
            walletRegexString = walletRegexString + ")"
        index += 1
    regexExceptions = []    
    regexExceptions.append(re.compile("transfer_from_" + walletRegexString))
    regexExceptions.append(re.compile("transfer_to_" + walletRegexString))
    regexExceptions.append(re.compile("starting_value"))
    regexExceptions.append(re.compile("correction"))
    
    #Saves all accepted transactions to a local variable
    acceptedTransactions = []       
    if mode == "all":
        for wallet in wallets:
            for transaction in wallet.transactions:
                if transactionType == "expenses" and transaction.value > 0:
                    continue
                if transactionType == "income" and transaction.value < 0:
                    continue
                excepted = False
                for regexException in regexExceptions:
                    if regexException.match(transaction.name):
                        excepted = True
                if not excepted:
                    acceptedTransactions.append(transaction)
    else:
        for wallet in wallets:
            for transaction in wallet.transactions:
                if transactionType == "expenses" and transaction.value > 0:
                    continue
                if transactionType == "income" and transaction.value < 0:
                    continue
                excepted = False
                for regexException in regexExceptions:
                    if regexException.match(transaction.name):
                        excepted = True
                if not excepted and transaction.dateyear == int(date.split("-")[0]):
                    if mode == "year":
                        acceptedTransactions.append(transaction)
                        continue
                    elif transaction.datemonth == int(date.split("-")[1]):
                        if mode == "month":
                            acceptedTransactions.append(transaction)
                            continue
                        elif transaction.dateday == int(date.split("-")[2]):
                            acceptedTransactions.append(transaction)
    
    #sorting the transactions                    
    acceptedTransactions.sort(key=lambda x: x.value)
    acceptedTransactions.sort(key=lambda x: x.location)
    acceptedTransactions.sort(key=lambda x: x.date)
    
    #initializes the individual wallet's values
    walletValues = []
    for wallet in wallets:
        walletValues.append(0.0)
    totalvalue = 0.0
                            
    #formatting checks
    longestNameLength = 0
    longestValueLength = 0
    longestPrintLength = 0
    tab = 4 * " "
    for transaction in acceptedTransactions:
        if len(transaction.name) > longestNameLength:
            longestNameLength = len(transaction.name)
        if len("%.2f" % (transaction.value)) > longestValueLength:
            longestValueLength = len("%.2f" % (transaction.value))
                
    #print start
    for transaction in acceptedTransactions:
        nameSpaces = (longestNameLength - len(transaction.name) + 6) * " "
        valueSpaces = (longestValueLength - len("%.2f" % (transaction.value))) * " "
        printOut = "%s%s%s%.2f%s%s%s%s" % (transaction.name,nameSpaces,valueSpaces,transaction.value,tab,transaction.date,tab,transaction.location)
        print printOut
        
        if longestPrintLength < len(printOut):
            longestPrintLength = len(printOut)
        
        totalvalue = totalvalue + transaction.value
        index = 0
        for wallet in wallets:
            if transaction.location == wallet.name:
                walletValues[index] = walletValues[index] + transaction.value
                break 
            index += 1
        
    #more formatting
    walletSpaces = []
    index = 0
    for wallet in wallets:
        numberOfWalletSpaces = longestPrintLength - len("Total " + wallet.name + ":") - len("%.2f" % (walletValues[index]))
        if numberOfWalletSpaces < 0:
            longestPrintLength = numberOfWalletSpaces
            numberOfWalletSpaces = longestPrintLength - len("Total " + wallet.name + ":") - len("%.2f" % (walletValues[index]))
            innerIndex = 0
            while innerIndex < index:
                walletSpaces[innerIndex] = (longestPrintLength - len("Total " + wallets[innerIndex].name + ":") - len("%.2f" % (walletValues[innerIndex]))) * " "
                innerIndex += 1
        walletSpaces.append((numberOfWalletSpaces * " "))
        index += 1
    index = 0
    
    #printing summary
    print longestPrintLength * "-"
    for wallet in wallets:
        print "Total %s:%s%.2f" % (wallet.name,walletSpaces[index],walletValues[index])
        index += 1
    print longestPrintLength * "-"
    totalSpaces = (longestPrintLength - len("Grand Total:") - len("%.2f" % (totalvalue))) * " "
    print "Grand Total:%s%.2f" % (totalSpaces,totalvalue)
    
"""
walletPrinter
prints the current balance of an input wallet to the console
@param wallets - the list of wallets
@param userInput - the user's input
"""
def walletPrinter(wallets, userInput):
    
    #searches for the correct wallet
    for wallet in wallets:
        if wallet.name == userInput:
            printWallet = wallet
            break
    
    #prints the wallet info
    print "Current %s balance: %.2f" % (printWallet.name,printWallet.value)

"""
totalPrinter
prints a summary of all assets (liquid and otherwise)
@param wallets - the list of wallets
@param assets - the assets wallet
"""
def totalPrinter(wallets,assets, mode):
    
    #formatting
    if mode == "all":
        longestStringLength = len("Current asset total: %.2f" % (assets.value))
    else:
        longestStringLength = 0
    for wallet in wallets:
        if len("Current %s balance: %.2f" % (wallet.name,wallet.value)) > longestStringLength:
            longestStringLength = len("Current %s balance: %.2f" % (wallet.name,wallet.value))
    
    #variable that keeps track of the total
    totalValue = 0.0
    
    #print start
    for wallet in wallets:
        spaces = (longestStringLength - len("Current " + wallet.name + " balance:") - len("%.2f" % wallet.value) + 6) * " "
        print "Current %s balance:%s%.2f" % (wallet.name,spaces,wallet.value)
        totalValue = totalValue + wallet.value
    if mode == "all":
        assetSpaces = (longestStringLength - len("Current asset total:") - len("%.2f" % (assets.value)) + 6) * " "
        print "Current asset total:%s%.2f" % (assetSpaces,assets.value)
        totalValue = totalValue + assets.value
    
    #divider
    print (longestStringLength + 6) * "-"
    
    #print total
    totalSpaces = (longestStringLength - len("Grand Total:") - len("%.2f" % (totalValue)) + 6) * " "
    print "Grand Total:%s%.2f" % (totalSpaces,totalValue)
    
    