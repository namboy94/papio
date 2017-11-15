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

import sys
import re
from Transaction import Transaction

def incomeExpenseFileParser(inputFile, transactionType, wallet, bank, credit, cash):
    lines = [line.rstrip('\n') for line in open(inputFile)]
    for line in lines:
        regex = re.compile("[0-9]+\.[0-9]{2} [0-9]{4}\-[0-9]{2}\-[0-9]{2} [^\s]+ (wallet|cash|credit|bank)")
        if regex.match(line):
            transactionValue = line.split(" ")[0]
            transactionDate = line.split(" ")[1]
            transactionName = line.split(" ")[2]
            if transactionType == "expense":
                transactionValue = (float(transactionValue) * (-1))
            else:
                transactionValue = float(transactionValue)
            walletType = line.split(" ")[3]
            transaction = Transaction(transactionName, transactionValue, transactionDate, walletType)
            if walletType == "wallet":
                wallet.addTransaction(transaction)
            elif walletType == "cash":
                cash.addTransaction(transaction)
            elif walletType == "credit":
                credit.addTransaction(transaction)
            elif walletType == "bank":
                bank.addTransaction(transaction)           
        else:
            sys.exit(1)
