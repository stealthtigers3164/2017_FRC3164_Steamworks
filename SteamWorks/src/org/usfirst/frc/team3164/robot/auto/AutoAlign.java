package org.usfirst.frc.team3164.robot.auto;

import org.usfirst.frc.team3164.robot.electrical.motor.SparkMotor;
import org.usfirst.frc.team3164.robot.movement.DriveTrain;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoAlign {
	
	public static void align(NetworkTable networkTable, DriveTrain<SparkMotor> driveTrain) {		

		if ((boolean) networkTable.getValue("boxVisible", false)) {
			int DistanceFromLeft = (int) networkTable.getValue("distLeft", -1);			
			int DistanceFromRight = (int) networkTable.getValue("distRight", -1);			
			
			boolean isCentered = false;
			
			for (int i = DistanceFromLeft - 10; i < DistanceFromLeft + 10; i++) {
				for (int j = DistanceFromRight - 10; i < DistanceFromRight + 10; j++) {
					if (i == j) {
						isCentered = true;
					}
				}
			}
			
			if (isCentered) {
				SmartDashboard.putBoolean("IsCentered", true);				
				return;
			} else {
				SmartDashboard.putBoolean("IsCentered", false);				
			}
				
			if (DistanceFromLeft > DistanceFromRight) {
				//The robot needs to turn left
				driveTrain.moveLeftByCameraPixels(DistanceFromLeft - DistanceFromRight);
			}
			
			if (DistanceFromRight > DistanceFromLeft) {
				//The robot needs to turn right
				driveTrain.moveRightByCameraPixels(DistanceFromRight - DistanceFromLeft);
			}
		}
	}
}
 