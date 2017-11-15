"""
Copyright 2015-2017 Hermann Krumrey <hermann@krumreyh.com>

This file is part of finance-program-professional.

finance-program-professional is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

finance-program-professional is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with finance-program-professional.  If not, see <http://www.gnu.org/licenses/>.
"""

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
#from program.parsers.UserinputParser import UserInputParser
from program.utils.GUI import GUI

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

#inputParser = UserInputParser(wallets)
#inputParser.parseUserInput(assets, wallets,dataFiles)
gui = GUI(assets, wallets, dataFiles)
gui.guiStart()