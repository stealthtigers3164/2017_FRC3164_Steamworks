import os
import time

from networktables import NetworkTables

import cv2
from grip import GripPipeline

#Open Network Tables
ntAddress = "jrue.local"
#ntAddress = "roborio-3164-frc.local"

print("Connecting to roboRIO\n")
NetworkTables.initialize(ntAddress)
nt = NetworkTables.getTable("grip")


#Init Camera
capture = cv2.VideoCapture(0)
if capture.isOpened() == False:
	print("not opened")

capture.set(3, 640) #width
capture.set(4, 480) #height

capture.set(21, 1) #autoexposure

capture.set(15, 0.8) #exposure
#capture.set(10, 20 ) #brightness

 





#Init Grip
grip = GripPipeline()




while True:
	os.system("uvcdynctrl --device=video0 -s 'Exposure, Auto' 1")
	os.system("uvcdynctrl --device=video0 -s 'Exposure (Absolute)' 9")
	capture.grab()
	ret, img = capture.retrieve()


	bk = False

	#img = cv2.imread('./examples/LED Peg/1ftH2ftD1Angle0Brightness.jpg')
	
	grip.process(img)
	contours = grip.filter_contours_output;
	for i in range(len(contours)):
		x, y, w, h = cv2.boundingRect(contours[i])
		#Box Testing to Make Sure Correct Boxes are Chosen
		for k in range(len(contours)):
			if i != k:
				x2, y2, w2, h2 = cv2.boundingRect(contours[k])
				if y2 + y == 0:
					break
					#prevent division by zero
				#Get Ratios of Heights and Widths of the Two Boxes
				widthRatioScore = 100 - (100 * abs(1 - (w/w2)))
				heightRatioScore = 100 - (100 * abs(1 - (h/h2)))
				
				#Get Difference in X/Y, its a percent difference on a 100 scale
				yDiff = abs(y2 - y) / (y2 + y) * 200
				xDiff = abs(x2 - x) / (x2 + x) * 200
				#If the ratios of the widths and the heights abocve 60%, and 
				#Difference in y value is under twenty percent
				#Not sure about xDiff
				if (widthRatioScore > 60 and heightRatioScore > 60 and yDiff < 35 and xDiff < 100):
					bk = True
					break
		if bk == True:
			break

	if bk == True:
		print("Placing numbers in NT")
		if x > x2:
			boxLeftX = x2
			boxLeftY = y2
			boxLeftW = w2
			boxLeftH = h2
			boxRightX = x
			boxRightY = y
			boxRightW = w
			boxRightH = h
		else:
			boxLeftX = x
			boxLeftY = y
			boxLeftW = w
			boxLeftH = h
			boxRightX = x2
			boxRightY = y2
			boxRightW = w2
			boxRightH = h2	
		nt.putBoolean("boxVisible", True)
		nt.putNumber("distLeft", boxLeftX)
		nt.putNumber("distRight", (640 - boxRightX - boxRightW))
		bk = False
	else:	
		print("no box found")
		nt.putBoolean("boxVisible", False)



	time.sleep(0.1)
	print("\nCYCLE\n")
	cv2.imshow('image',img)
	cv2.waitKey(0)
	cv2.destroyAllWindows()
capture.release()
