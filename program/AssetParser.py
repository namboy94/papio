import sys
import re
from Asset import Asset

def assetFileParser(inputFile, assets):
    lines = [line.rstrip('\n') for line in open(inputFile)]
    for line in lines:
        regex = re.compile("[0-9]+\.[0-9]{2} [^\s]+")
        if regex.match(line):
            assetValue = float(line.split(" ")[0])
            assetName = line.split(" ")[1]
            
            asset = Asset(assetName, assetValue)
            
            assets.addTransaction(asset)
                    
        else:
            print "Error in the Asset File"
            sys.exit(1)
