'''
FileParsers
collection of methods that parse the input files

Created on Apr 15, 2015
Modified on Apr 15, 2015

@author Hermann Krumrey
@version 1.0
'''

#imports
import sys
import re
from program.objects.Asset import Asset
from program.objects.Wallet import Wallet
from program.objects.Transaction import Transaction

"""
assetFileParser
parses an input asset file, creates Asset objects accordingly and saves them in the parameter assets
@param assets - A wallet object used to store all assets
"""
def assetFileParser(inputFile, assets):
    #traverses the entire file and saves each line as a seperate String value in a list
    lines = [line.rstrip('\n') for line in open(inputFile)]
    
    #loop that makes sure that no regex violations are contained in the file
    #Once checked by regex, they are added to assets
    for line in lines:
        
        regex = re.compile("[0-9]+\.[0-9]{2} [^\s]+")
        
        #matches
        if regex.match(line):
            assetValue = float(line.split(" ")[0])
            assetName = line.split(" ")[1]
            asset = Asset(assetName, assetValue)
            assets.addTransaction(asset)
        #does not match
        else:
            print "Error in asset file. The following line is not valid:"
            print line
            #Stops program
            sys.exit(1) 

"""
walletParse
parses the wallet.txt file
@param walletFile - the file to be parsed
@param wallett - the list of wallets to be populated by the parser
"""
def walletParse(walletFile,wallets):
    
    #traverses the entire file and saves each line as a seperate String value in a list
    lines = [line.rstrip('\n') for line in open(walletFile)]
    
    #appends the wallets to the wallets list
    for line in lines:
        wallets.append(Wallet(line))
        
"""
"""
def incomeExpenseParser(inputFile, transactionType, wallets):
    #traverses the entire file and saves each line as a seperate String value in a list
    lines = [line.rstrip('\n') for line in open(inputFile)]
    
    #initiates the line checking regex. If at any point this regex does not match a line, the program terminates
    walletRegexString = ""
    index = 1
    for wallet in wallets:
        walletRegexString = walletRegexString + wallet.name
        if index < len(wallets):
            walletRegexString = walletRegexString + "|"
        else:
            walletRegexString = walletRegexString + ")"       
    lineRegex = re.compile("[0-9]+\.[0-9]{2} [0-9]{4}\-[0-9]{2}\-[0-9]{2} [^\s]+ " + walletRegexString)
    
    #traverses through the lines and parses them
    for line in lines:
        if lineRegex.match(line):
            transactionValue = line.split(" ")[0]
            transactionDate = line.split(" ")[1]
            transactionName = line.split(" ")[2]
    
            if transactionType == "expense":
                transactionValue = (float(transactionValue) * (-1))
            else:
                transactionValue = float(transactionValue)
                
            walletType = line.split(" ")[3]
            transaction = Transaction(transactionName, transactionValue, transactionDate, walletType)
            for wallet in wallets:
                if wallet.name == walletType:
                    wallet.addTransaction(transaction)
        
        else:
            print "input error found in the %s file on line %s" % (transactionType,line)
    
    
    