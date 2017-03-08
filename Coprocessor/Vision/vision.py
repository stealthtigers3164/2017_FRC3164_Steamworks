

import math
import os
import threading
import time
from http.server import BaseHTTPRequestHandler, HTTPServer
from io import StringIO
from random import randint
from socketserver import ThreadingMixIn

from networktables import NetworkTables
from PIL import Image

import cv2
from grip import GripPipeline


class CamHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path.endswith('.mjpg'):
            self.send_response(200)
            self.send_header(
                'Content-type', 'multipart/x-mixed-replace; boundary=--jpgboundary')
            self.end_headers()
            while True:
                try:
                    rc, img = capture.read()
                    if not rc:
                        continue
                
                    imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                    if self.path.endswith('hsv.mjpg'):
                        #imgRGB = cv2.bitwise_and(img, img, mask=grip.hsv_threshold_output)
                        #imgRGB = grip.hsv_threshold_output
                        #Need to convert image to imgRGB
                        _, tmpHSV = cv2.imencode(".jpeg", grip.hsv_threshold_output, [int(cv2.IMWRITE_JPEG_QUALITY), 75])
                        imgRGB = cv2.imdecode(tmpHSV, cv2.IMREAD_COLOR)
                    elif self.path.endswith('blur.mjpg'):
                        _, tmpBLUR = cv2.imencode(".jpeg", grip.blur_output, [int(cv2.IMWRITE_JPEG_QUALITY), 75])
                        imgRGB = cv2.imdecode(tmpBLUR, cv2.IMREAD_COLOR)
                    else:
                        imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                    
                    if "contours_" in self.path:
                        contours = grip.find_contours_output
                        if "filter_contours_" in self.path:
                            contours = grip.filter_contours_output
                        for i in range(len(contours)):
                            x, y, w, h = cv2.boundingRect(contours[i])
                            cv2.rectangle(imgRGB, (x, y), (x + w, y + h), color=(0, 0, 255), thickness=1)
                        
                    ret, tmpFile = cv2.imencode(".jpeg", imgRGB, [int(cv2.IMWRITE_JPEG_QUALITY), 50])
                    self.wfile.write(b'--jpgboundary')
                    self.send_header('Content-type', 'image/jpeg')
                    self.send_header('Content-length', str(tmpFile.size))
                    self.end_headers()
                    self.wfile.write( tmpFile.data )
                
                    
                    time.sleep(0.05)
                except KeyboardInterrupt:
                    break
            return
        if self.path.endswith('.html'):
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write('<html><head></head><body>')
            self.wfile.write('<img src="http://127.0.0.1:8080/cam.mjpg"/>')
            self.wfile.write('</body></html>')
            return


class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    """Handle requests in a separate thread."""

def server_thread(arg):
    try:
        server = ThreadedHTTPServer(('localhost', 8080), CamHandler)
        print("Web Server Started")
        server.serve_forever()
    except KeyboardInterrupt:
        capture.release()
        server.socket.close()



def main():
    global capture, grip


    #Open Network Tables
    ntAddress = "jrue.local"
    #ntAddress = "roborio-3164-frc.local"

    print("Connecting to roboRIO\n")
    NetworkTables.initialize(ntAddress)
    nt = NetworkTables.getTable("grip")
    ntDistance = NetworkTables.getTable("distance")

    #Init Camera
    capture = cv2.VideoCapture(0)
    #Change to localhost when running on Pi
    #Going to :8080 website, the brightness can be changed
    #capture = cv2.VideoCapture("http://coprocessor.local:8080/?action=stream")
    if capture.isOpened() == False:
        print("not opened")
    capture.set(3, 640) #width
    capture.set(4, 480) #height
    #capture.set(21, 1) #autoexposure
    #capture.set(15, 0.8) #exposure
    #capture.set(10, 20 ) #brightness




    #Init Grip
    grip = GripPipeline()

    #init server thread
    thread = threading.Thread(target = server_thread, args = (10, ))
    thread.start()
    #thread.join()

    while True:
        #os.system("uvcdynctrl --device=video0 -s 'Exposure, Auto' 1")
        #os.system("uvcdynctrl --device=video0 -s 'Exposure (Absolute)' 9")
        #os.system("uvcdynctrl --device=video0 -s 'Brightness' 9")
        #capture.grab()
        ret, img = capture.retrieve()

        time.sleep(0.1)

        bk = False

        ''' Testing With Sample Images'' '
        dist = 4
        forwardDist = ntDistance.putNumber("lidar", dist * 304.8)
        img = cv2.imread('./examples/LED Peg/1ftH' + str(dist) + 'ftD0Angle0Brightness.jpg')


        ' '' End Testing '''
        
        grip.process(img)
        contours = grip.filter_contours_output;
        for i in range(len(contours)):
            x, y, w, h = cv2.boundingRect(contours[i])
            #Box Testing to Make Sure Correct Boxes are Chosen
            for k in range(len(contours)):
                if i != k:
                    x2, y2, w2, h2 = cv2.boundingRect(contours[k])
                    if y2 + y == 0 or x + x2 == 0:
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
            print("_found box, putting data in nt")
            boxLeftX = x
            boxLeftY = y
            boxLeftW = w
            boxLeftH = h
            boxRightX = x2
            boxRightY = y2
            boxRightW = w2
            boxRightH = h2	
            if x > x2:
                boxLeftX = x2
                boxLeftY = y2
                boxLeftW = w2
                boxLeftH = h2
                boxRightX = x
                boxRightY = y
                boxRightW = w
                boxRightH = h
            
            ''' Calculating Distance '''
            leftEdgesRatio = 209.55 / (boxRightX - boxLeftX + 0.001) #mm per pixel
            realDistFromCenter = (320.0 - 0.5 * (boxLeftX + (boxRightX + boxRightW))) * leftEdgesRatio
                #Image Center Point (320) - Box Center Point (average of left and right bound)
            forwardDist = ntDistance.getNumber("lidar")
            turnAngle = math.degrees(math.atan(realDistFromCenter/forwardDist))


            turnDirection = "center"
            deadzone = 5 #deadzone to determine if center, in degrees
            if turnAngle > deadzone:
                turnDirection = "left"
            elif turnAngle < (0-deadzone):
                turnDirection = "right"

        

            nt.putBoolean("boxVisible", True)
            nt.putNumber("distLeft", boxLeftX)
            nt.putNumber("distRight", (640 - boxRightX - boxRightW))
            nt.putNumber("distFromCenterPoint", realDistFromCenter) #convert back to inches with: * 0.03937008
            nt.putNumber("turnAngle", turnAngle)
            nt.putString("turnDirection", turnDirection)
            #nt.putNumber("testing", leftEdgesRatio * boxLeftH * 0.03937008) #height of tape in inches, usually too big
            bk = False
        else:	
            print("_no box found")
            nt.putBoolean("boxVisible", False)



        time.sleep(0.1)
        print("\nCYCLE")

        ''' To Draw a Bounding Rectangle'''
        #cv2.rectangle(img, (boxLeftX, boxLeftY), (boxLeftX + boxLeftW, boxLeftY + boxLeftH), color=(0, 255, 0), thickness=2)
        #cv2.rectangle(img, (boxRightX, boxRightY), (boxRightX + boxRightW, boxRightY + boxRightH), color=(0, 0, 255), thickness=2)

        
        #cv2.line(img, (int((boxRightX + boxRightW + boxLeftX)/2), int(((boxLeftY + (0.5 * boxLeftH)) + (boxRightY + (0.5 * boxRightH)))/2)), (320, 240), color=(0, 255, 0), thickness=2)
            #Draws line from center of box to center of picture

        #cv2.imshow('image',img)
        #cv2.waitKey(0)
        #cv2.destroyAllWindows()
    capture.release()


    '''
    Peg Measurements


    From left edge to right edge:	10.25	inches	|	260.35	mm
    From bottom to top				5		inches	|	127		mm
    Ground to bottom 				10.75	inches	|	273.05	mm
    Tape width 						2		inches	|	50.8	mm
    Dist between tape				6.25	inches	|	158.75	mm
    Dist from left edges			8.25	inches	|	209.55	mm

    For scaling, were using distance from top left edge of left peg to top left edge of right peg

    '''

if __name__ == '__main__':
    main()
