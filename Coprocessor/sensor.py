#pip3 install pyserial
#pip3 install pynetworktables

import sys
import time
from networktables import NetworkTables
from networktables.util import ntproperty
import serial
from time import sleep



# configure the serial connections (the parameters differs on the device you are connecting to)
ser = serial.Serial(
	#port='/dev/ttyUSB1',
	port='/dev/tty.usbmodem1411',
	baudrate=115200,
	parity=serial.PARITY_ODD,
	stopbits=serial.STOPBITS_TWO,
	bytesize=serial.SEVENBITS
)

#ser.open()
ser.isOpen()

print("Connecting to roboRIO and Ardino\n")

#NetworkTables.initialize("roborio-3164-frc.local")
NetworkTables.initialize("jrue.local")


def isint(value):
  try:
    int(value)
    return True
  except:
    return False

def isstr(value):
  try:
    str(value)
    return True
  except:
    return False

def readLastLine(ser):
    last_data=''
    while True:
        data=ser.readline()
        if data!='':
            last_data=data
        else:
            return last_data

class Distance(object):
    dLidar = ntproperty('/distance/lidar', 0)
    dUltra = ntproperty('/distance/ultrasonic', 0)

d = Distance()


while 1 :
	#output should be |u:333|l:333|
	try:
		out = ""
		out = ser.readline().decode("utf-8") 
		#out = readLastLine(ser)

		outSplit = out.split("|")
		for split in outSplit:
			num = -1
			typ = ""
			for twiceSplit in split.split(":"):
				if isint(twiceSplit):
					if int(twiceSplit) > 0:
						num = int(twiceSplit)
				if (str(twiceSplit) == "u") or (str(twiceSplit) == "l"):
					typ = twiceSplit
			#print("twiceSplit: " + split + " |num:" + str(num) + "|typ:" + typ)
			if str(typ) == "u":
				d.dUltra = num
			if typ == "l":
				d.dLidar = num
		ser.flush()
	except:
		sleep(.1)
		print ("exception")
	sleep(0.025)
	

