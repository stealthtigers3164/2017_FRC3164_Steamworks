#pip3 install pyserial
#pip3 install pynetworktables

import sys
import time
from networktables import NetworkTables
from networktables.util import ntproperty
import serial
from time import sleep


#serialPort = "/dev/cu.usbmodem1421"
serialPort = "/dev/ttyACM0"

#ntAddress = "jrue.local"
ntAddress = "roborio-3164-frc.local"

# configure the serial connections (the parameters differs on the device you are connecting to)


print("Opening Serial Port")
tries = 0

while tries <= 99:
	try:
		ser = serial.Serial(
			port=serialPort,
			baudrate=115200,
			parity=serial.PARITY_ODD,
			stopbits=serial.STOPBITS_TWO,
			bytesize=serial.SEVENBITS)
		if(ser.isOpen()):
			tries = 101
		
		#tries = 10
	except:
		print("Error Opening Serial Port | Try " + str(tries + 1) + "/100")
		tries = tries + 1
		sleep(3)
if tries > 99 and tries < 101:
	print("Couldn't Connect to Arduino over Serial, exiting")
	sys.exit()
print("Serial Port Opened\n")


print("Connecting to roboRIO and Ardino\n")

NetworkTables.initialize(ntAddress)


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

err = False
nack = False
while 1 :
	#output should be |u:333|l:333|
	try:
		if err:
			print("Closing Port")
			ser.close()
			sleep(.2)
			ser = serial.Serial(
				port=serialPort,
				baudrate=115200,
				parity=serial.PARITY_ODD,
				stopbits=serial.STOPBITS_TWO,
				bytesize=serial.SEVENBITS)
			print("Opening Port")
			ser.isOpen()
			sleep(.2)
			err = False
	except:
		print ("except reopen")


	try:
		out = ""
		out = ser.readline().decode("utf-8") 
		#out = readLastLine(ser)

		#print(out)
		if "nack" in out:
			ser.flushInput()
		if "failed" in out:
			ser.flushInput()
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
		ser.flushInput()
	except:
		sleep(.1)
		print ("exception norm")
		err = True	
	


	sleep(0.03)
	

