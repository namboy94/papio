'''
main
The main program tying all classes and methods together

The program takes 4 command line parameters:
    1: wallets.txt
    2: assets.txt
    3: expenses.txt
    4: income.txt
    
Created on Apr 15, 2015
Modified on Apr 15, 2015

@author Hermann Krumrey
@version 1.0
'''

#imports
import sys
splitPath = sys.argv[0].split("/")
lengthToCut = len(splitPath[len(splitPath) - 1]) + len(splitPath[len(splitPath) - 2]) + 2
upperDirectory = sys.argv[0][:-lengthToCut]
sys.path.append(upperDirectory)
from program.objects.Wallet import Wallet
from program.parsers.FileParsers import walletParse, assetFileParser, incomeExpenseParser
from program.parsers.UserinputParser import UserInputParser

#Input files from sys.argv as easily readeable variables
walletFile = upperDirectory + "/program/data/wallets.txt"
assetFile = upperDirectory + "/program/data/assets.txt"
expenseFile = upperDirectory + "/program/data/expenses.txt"
incomeFile = upperDirectory + "/program/data/income.txt"
dataFiles = [walletFile,assetFile,expenseFile,incomeFile]

#List containing the wallets, and the unique assets wallet
wallets = []
assets = Wallet("assets")

#parsing inputs
walletParse(walletFile, wallets)
assetFileParser(assetFile, assets)
incomeExpenseParser(expenseFile, "expense", wallets)
incomeExpenseParser(incomeFile, "income", wallets)

inputParser = UserInputParser(wallets)
inputParser.parseUserInput(assets, wallets,dataFiles)