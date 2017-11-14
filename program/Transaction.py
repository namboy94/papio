class Transaction(object):

    name = ""
    value = 0.0
    dateday = 0
    datemonth = 0
    dateyear = 0
    date = ""
    location = ""
    


    def __init__(self, name, value, date, location):
        
        self.name = name
        self.value = value
        self.dateday = int(date.split("-")[2])
        self.datemonth = int(date.split("-")[1])
        self.dateyear = int(date.split("-")[0])
        self.date = date
        self.location = location