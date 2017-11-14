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
