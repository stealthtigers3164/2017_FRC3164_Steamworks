#!/bin/sh
rm -rf bin
mkdir bin
javac -d bin/ -cp src:/home/pi/wpilib/java/NetworkTables_Mod.jar:/home/pi/wpilib/java/current/lib/WPILib.jar:/home/pi/wpilib/java/current/lib/WPILib-sources.jar:/home/pi/wpilib/java/current/lib/opencv.jar:/home/pi/wpilib/java/current/lib/opencv-sources.jar:/home/pi/wpilib/java/current/lib/cscore.jar:/home/pi/wpilib/java/current/lib/cscore-sources.jar:/usr/share/java/RXTXcomm.jar src/com/team3164/vision/Vision.java

cd ./bin
jar cvfm coproc.jar ../manifest.txt ./com/team3164/vision/*.class
