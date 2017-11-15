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