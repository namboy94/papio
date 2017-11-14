"""
Asset
class that models an asset as an object

Created on Apr 15, 2015
Modified on Apr 15, 2015

@author Hermann Krumrey
@version 1.0
"""
class Asset(object):
    
    """
    Constructor
    constructs a new Asset object
    @param name - The name of the asset
    @param value - The numerical value of the asset
    """
    def __init__(self,name,value):
        
        self.name = name
        self.value = value