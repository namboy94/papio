"""
Copyright 2015-2017 Hermann Krumrey <hermann@krumreyh.com>

This file is part of finance-program.

finance-program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

finance-program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with finance-program.  If not, see <http://www.gnu.org/licenses/>.
"""

from Wallets import Wallets
from IncomeExpenseParser import incomeExpenseFileParser
from AssetParser import assetFileParser
from Printer import transactionPrinter, walletPrint, assetPrinter
from DataEnterer import addNewTransaction, transfer
import sys
import re

wallet = Wallets("wallet")
cash = Wallets("cash")
bank = Wallets("bank")
credit = Wallets("credit")
asset = Wallets("asset")

expenseFile = sys.argv[1]
incomeFile = sys.argv[2]
assetFile = sys.argv[3]

incomeExpenseFileParser(expenseFile, "expense", wallet, bank, credit, cash)
incomeExpenseFileParser(incomeFile, "income", wallet, bank, credit, cash)
assetFileParser(assetFile, asset)

allTransactions = wallet.transactions + cash.transactions + bank.transactions + credit.transactions

print "Welcome to the HK Finance Manager"
running = True

while running:
    
    userInput = raw_input("\n")
    
    helpRegex = re.compile("help")
    assetsRegex = re.compile("assets")
    incomeRegexJMD = re.compile("income [0-9]{4}-[0-9]{2}-[0-9]{2}")
    incomeRegexJM = re.compile("income [0-9]{4}-[0-9]{2}")
    incomeRegexJ = re.compile("income [0-9]{4}")
    expensesRegexJMD = re.compile("expenses [0-9]{4}-[0-9]{2}-[0-9]{2}")
    expensesRegexJM = re.compile("expenses [0-9]{4}-[0-9]{2}")
    expensesRegexJ = re.compile("expenses [0-9]{4}")
    transactionsRegexJMD = re.compile("transactions [0-9]{4}-[0-9]{2}-[0-9]{2}")
    transactionsRegexJM = re.compile("transactions [0-9]{4}-[0-9]{2}")
    transactionsRegexJ = re.compile("transactions [0-9]{4}")
    addExpenseRegex = re.compile("add expense [0-9]+.[0-9]{2} [^ ]+ from (cash|credit|bank|wallet)")
    addIncomeRegex = re.compile("add income [0-9]+.[0-9]{2} [^ ]+ to (cash|credit|bank|wallet)")
    addAssetRegex = re.compile("add asset [0-9]+.[0-9]{2} [^ ]")
    transferRegex = re.compile("transfer [0-9]+.[0-9]{2} from (wallet|bank|credit|cash) to (wallet|bank|credit|cash)")
    walletTypeValueRegex = re.compile("(wallet|cash|credit|bank)")
    totalRegex = re.compile("total")
    quitRegex = re.compile("quit")
    
    if helpRegex.match(userInput):
        print "List of Commands:\n"
        print "help                                                                              Prints a list of possible commands to the console"
        print "assets                                                                            Prints the value and the name of all assets to the console"
        print "income <DATE>                                                                     Prints the income during a given time period to the console (Year-Month-Day)"
        print "expenses <DATE>                                                                   Prints the expenses during a given time period to the console (Year-Month-Day)"
        print "transactions <DATE>                                                               Prints all transactions during a given time period to the console (Year-Month-Day)"
        print "add income <VALUE> <NAME> to (wallet|bank|cash|credit)                            Adds a new income to the database."
        print "add expense <VALUE> <NAME> from (wallet|bank|cash|credit)                         Adds a new expense to the database."
        print "add asset <VALUE> <NAME>                                                          Adds a new asset to the database."
        print "transfer <VALUE> from (wallet|bank|cash|credit) to (wallet|bank|cash|credit)      Transfers an amount from one wallet type to another"
        print "(wallet|cash|credit|bank)                                                         Prints the current amount in the selected wallet type"
    elif assetsRegex.match(userInput):
        assetPrinter(asset)
    elif incomeRegexJMD.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "day", "income")
    elif incomeRegexJM.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "month", "income")
    elif incomeRegexJ.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "year", "income")
    elif expensesRegexJMD.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "day", "expense")
    elif expensesRegexJM.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "month", "expense")
    elif expensesRegexJ.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "year", "expense")
    elif transactionsRegexJMD.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "day", "all")
    elif transactionsRegexJM.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "month", "all")
    elif transactionsRegexJ.match(userInput):
        transactionPrinter(allTransactions, userInput.split(" ")[1], "year", "all")
    elif addExpenseRegex.match(userInput):
        addNewTransaction(float(userInput.split(" ")[2]), userInput.split(" ")[3], userInput.split(" ")[5], "expense", wallet, cash, credit, bank, asset, sys.argv)
        allTransactions = wallet.transactions + cash.transactions + bank.transactions + credit.transactions
    elif addIncomeRegex.match(userInput):
        addNewTransaction(float(userInput.split(" ")[2]), userInput.split(" ")[3], userInput.split(" ")[5], "income", wallet, cash, credit, bank, asset, sys.argv)
        allTransactions = wallet.transactions + cash.transactions + bank.transactions + credit.transactions
    elif addAssetRegex.match(userInput):
        addNewTransaction(float(userInput.split(" ")[2]), userInput.split(" ")[3], "asset", "asset", wallet, cash, credit, bank, asset, sys.argv)
    elif transferRegex.match(userInput):
        transfer(userInput.split(" ")[3], userInput.split(" ")[5], float(userInput.split(" ")[1]), sys.argv, wallet, cash, credit, bank)
        allTransactions = wallet.transactions + cash.transactions + bank.transactions + credit.transactions
    elif walletTypeValueRegex.match(userInput):
        walletPrint(userInput, wallet, cash, credit, bank)
    elif quitRegex.match(userInput):
        running = False
    elif totalRegex.match(userInput):
        totalVal = wallet.value + bank.value + credit.value + cash.value
        print "%.2f" % (totalVal)
    else:
        print "wrong input"