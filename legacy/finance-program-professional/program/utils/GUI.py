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
A Graphical User Interface to streamline the user-input process

Created on June 6, 2015

@author Hermann Krumrey
@version 0.1
"""

#imports
from Tkinter import Tk
from Tkinter import StringVar
from Tkinter import IntVar
from Tkinter import Entry
from Tkinter import Button
from Tkinter import Checkbutton
from Tkinter import Label
from PIL import Image, ImageTk
import tkMessageBox
import platform
import os

"""
The main GUI class
"""
class GUI(object):
    
    """
    Constructor that saves all required variables locally
    """
    def __init__(self, assets, wallets, dataFiles):
        self.assets = assets
        self.wallets = wallets
        self.dataFile = dataFiles
        
    """
    starts the GUI
    """
    def guiStart(self):
        
        #Initialize GUI
        self.gui = Tk()
        self.gui.geometry("450x400+300+300")
        self.gui.title("Finance Program Professional")
        self.gui.wm_resizable(False, False)
        
        #Start GUI
        self.gui.mainloop()
        
    """
    adds a button to the GUI
    @param text - the text to be displayed
    @param xPos - the x position in the window
    @param yPos - the y position in the window
    @param xSize - the width of the button
    @param ySize - the height of the button
    @param command - the function to be invoked when this button is pressed
    """
    def addButton(self, text, xPos, yPos, xSize, ySize, command):
        button = Button(self.gui, command=command, text=text)
        button.pack()
        button.place(x=xPos, y=yPos, width=xSize, height=ySize)
        
    """
    adds a textBox to the gui, which saves its content to a predefined variable
    @param variable - the variable to be used and displayed
    @param xPos - the x position in the window
    @param yPos - the y position in the window
    @param xSize - the width of the textBox
    @param ySize - the height of the textBox
    @param command - the function to be invoked when Enter is pressed
    """
    def addTextBox(self, variable, xPos, yPos, xSize, ySize, command):
        textBox = Entry(self.gui, textvariable=variable)
        textBox.pack()
        textBox.bind('<Return>', command)
        textBox.place(x=xPos, y=yPos, width=xSize, height=ySize)
        
    """
    adds a checkbox to the gui with the given parameters
    @param variable - the variable to be used
    @param text - the text to be displayed next to the checkbox
    @param xPos - the x position in the window
    @param yPos - the y position in the window
    @param xSize - the width of the checkBox
    @param ySize - the height of the checkBox
    @param command - the function to be invoked when the checkBox is pressed
    """
    def addCheckBox(self, variable, text, xPos, yPos, xSize, ySize, command):
        checkBox = Checkbutton(self.gui, command=command, text=text, variable=variable)
        checkBox.pack()
        checkBox.place(x=xPos, y=yPos, width=xSize, height=ySize)
        
    """
    adds a label to the GUI
    @param text - the text to be displayed by the label
    @param xPos - the x position in the window
    @param yPos - the y position in the window
    @param xSize - the width of the label
    @param ySize - the height of the label
    """
    def addLabel(self, text, xPos, yPos, xSize, ySize):
        label = Label(self.gui, text=text)
        label.pack()
        label.place(x=xPos, y=yPos, width=xSize, height=ySize)
        
    """
    adds a picture to the GUI
    @param image - the PhotoImage object containing the picture to be added
    @param xPos - the picture's x-position on the GUI
    @param yPos - the picture's y-position on the GUI
    @param xSize - the width of the picture
    @param ySize - the height of the picture
    """
    def addPictureLabel(self, image, xPos, yPos, xSize, ySize):
        label = Label(self.gui, image=image)
        label.pack()
        label.place(x=xPos, y=yPos, width=xSize, height=ySize)
    
    """
    converts an image file to a usable PhotoImage object
    @param - imageFile - the file containing the image
    """
    def convertToPhotoImage(self, imageFile, x, y):
        size = x, y
        image = Image.open(imageFile)
        image.thumbnail(size, Image.ANTIALIAS)
        return ImageTk.PhotoImage(image)