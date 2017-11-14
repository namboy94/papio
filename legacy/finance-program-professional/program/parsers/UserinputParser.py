'''
UserInputParser
class that contains a method that parses a user's input continually until the program is quit.

Created on Apr 15, 2015
Modified on Apr 15, 2015

@author Hermann Krumrey
@version 1.0
'''

#imports
import re
from program.utils.Printer import helpPrinter, assetPrinter, transactionPrinter, walletPrinter, totalPrinter
from program.utils.DataEnterer import addNewTransaction, transfer, walletAdder

class UserInputParser(object):
    
    """
    Constructor
    constructs a new inputParser and initializes all required regex objects.
    @param - 
    """
    def __init__(self,wallets):
        
        walletString = "("
        index = 1
        for wallet in wallets:
            walletString = walletString + wallet.name
            if index < len(wallets):
                walletString = walletString + "|"
            else:
                walletString = walletString + ")"
            index += 1

        #REGEX expressions used
        self.helpRegex = re.compile("help")
        self.assetsRegex = re.compile("assets")
        self.transactionsRegexJMD = re.compile("(transactions|expenses|income) [0-9]{4}-[0-9]{2}-[0-9]{2}")
        self.transactionsRegexJM = re.compile("(transactions|expenses|income) [0-9]{4}-[0-9]{2}")
        self.transactionsRegexJ = re.compile("(transactions|expenses|income) [0-9]{4}")
        self.transactionsRegex = re.compile("(transactions|expenses|income)")
        self.allTransactionsRegex = re.compile("all (transactions|expenses|income)")
        self.addTransactionRegex = re.compile("add (expense|income) [0-9]+.[0-9]{2} [^ ]+ (from|to) " + walletString)
        self.addAssetRegex = re.compile("add asset [0-9]+.[0-9]{2} [^ ]")
        self.transferRegex = re.compile("transfer [0-9]+.[0-9]{2} from "+ walletString + " to " + walletString)
        self.walletTypeValueRegex = re.compile(walletString)
        self.addWalletRegex = re.compile("add wallet [^ ]+ [0-9]+.[0-9]{2}")
        self.totalMoneyRegex = re.compile("(total money){1}")
        self.totalRegex = re.compile("(total){1}")
        self.quitRegex = re.compile("quit")

    """
    parseUserInput
    parses user input continuously and acts accordingly. Makes use of regex.
    @param assets - The unique assets wallet containing all assets
    @param wallets - The list of unique wallets
    """
    def parseUserInput(self,assets,wallets,dataFiles):
        
        running = True
        while running:
        
            userInput= raw_input("Please enter your action:\n")
            print ""
            
            if self.helpRegex.match(userInput):
                helpPrinter()
            elif self.assetsRegex.match(userInput):
                assetPrinter(assets)
            elif self.transactionsRegexJMD.match(userInput):
                transactionPrinter(wallets, userInput, "day")
            elif self.transactionsRegexJM.match(userInput):
                transactionPrinter(wallets, userInput, "month")
            elif self.transactionsRegexJ.match(userInput):
                transactionPrinter(wallets, userInput, "year")
            elif self.transactionsRegex.match(userInput):
                transactionPrinter(wallets, userInput, "year")
            elif self.allTransactionsRegex.match(userInput):
                transactionPrinter(wallets, userInput, "all")
            elif self.addTransactionRegex.match(userInput):
                addNewTransaction(userInput, dataFiles, wallets, assets)
            elif self.addAssetRegex.match(userInput):
                addNewTransaction(userInput, dataFiles, wallets, assets)
            elif self.transferRegex.match(userInput):
                transfer(userInput, dataFiles, wallets)
            elif self.walletTypeValueRegex.match(userInput):
                walletPrinter(wallets, userInput)
            elif self.addWalletRegex.match(userInput):
                walletAdder(dataFiles, wallets, userInput)
                self.refreshRegex(wallets)
            elif self.totalMoneyRegex.match(userInput):
                totalPrinter(wallets, assets, "money")
            elif self.totalRegex.match(userInput):
                totalPrinter(wallets, assets, "all")
            elif self.quitRegex.match(userInput):
                running = False
            else:
                print "wrong input"
                
            print ""
    
    """
    refreshRegex
    reloads all regex objects that are subject to change
    """
    def refreshRegex(self,wallets):
        
        walletString = "("
        index = 1
        for wallet in wallets:
            walletString = walletString + wallet.name
            if index < len(wallets):
                walletString = walletString + "|"
            else:
                walletString = walletString + ")"
            index += 1
        
        self.addTransactionRegex = re.compile("add (expense|income) [0-9]+.[0-9]{2} [^ ]+ (from|to) " + walletString)
        self.transferRegex = re.compile("transfer [0-9]+.[0-9]{2} from "+ walletString + " to " + walletString)
        self.walletTypeValueRegex = re.compile(walletString)