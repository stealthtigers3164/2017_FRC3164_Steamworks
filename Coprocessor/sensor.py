#pip3 install pyserial
#pip3 install pynetworktables

import sys
import time
from networktables import NetworkTables
import serial

# configure the serial connections (the parameters differs on the device you are connecting to)
ser = serial.Serial(
	#port='/dev/ttyUSB1',
	port='/dev/tty.usbmodem1411',
	baudrate=115200,
	parity=serial.PARITY_ODD,
	stopbits=serial.STOPBITS_TWO,
	bytesize=serial.SEVENBITS
)

ser.open()
ser.isOpen()

print("Enter your commands below.\r\nInsert \"exit\" to leave the application.")

NetworkTables.initialize("roborio-3164-frc.local")
#NetworkTables.initialize("jrue.local")


class SomeClient(object):
    '''Demonstrates an object with magic networktables properties'''
    
    robotTime = ntproperty('/SmartDashboard/robotTime', 0, writeDefault=False)
    
    dsUltra = ntproperty('/SmartDashboard/Distance_Ultrasonic', 0)

c = SomeClient()

input=1
while 1 :
	# get keyboard input
	input = input(">> ")
        # Python 2 users
        # input = raw_input(">> ")
	if input == 'exit':
		ser.close()
		exit()
	else:
		# send the character to the device
		# (note that I happend a \r\n carriage return and line feed to the characters - this is requested by my device)
		# ser.write(input + '\r\n')
		out = ''
		# let's wait one second before reading output (let's give device time to answer)
		time.sleep(1)
		while ser.inWaiting() > 0:
			out += ser.read(1)
			
		if out != '':
			print(">>" + out)
			c.dsUltra = out