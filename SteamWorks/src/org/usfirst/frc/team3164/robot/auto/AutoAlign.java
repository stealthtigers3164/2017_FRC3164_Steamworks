package org.usfirst.frc.team3164.robot.auto;

import org.usfirst.frc.team3164.robot.electrical.motor.SparkMotor;
import org.usfirst.frc.team3164.robot.movement.DriveTrain;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class AutoAlign {
	private static final int width = 640;
	
	private static final String m_leftRectangleBaseName = null;
	private static final String m_rightRectangleBaseName = null;
	
	public static void align(NetworkTable networkTable, DriveTrain<SparkMotor> driveTrain) {
		Rectangle rightRectangle = getRightRectangle(networkTable);
		Position  rightPosition = new Position(rightRectangle.getX(), rightRectangle.getY());

		Rectangle leftRectangle = getLeftRectangle(networkTable);		
		Position  leftPosition = new Position(leftRectangle.getX(), leftRectangle.getY());
		
		int DistanceFromRightRectangleToRightSide = width - rightPosition.getX() - (rightRectangle.getWidth() / 2);
		int DistanceFromLeftRectangleToLeftSide = leftPosition.getX() - (rightRectangle.getWidth() / 2);
		
		if (DistanceFromLeftRectangleToLeftSide == DistanceFromRightRectangleToRightSide) {
			//No alignment is needed
			return;
		}
		
		if (DistanceFromLeftRectangleToLeftSide > DistanceFromRightRectangleToRightSide) {
			//The robot needs to turn left
			driveTrain.moveLeftByCameraPixels(DistanceFromLeftRectangleToLeftSide - DistanceFromRightRectangleToRightSide);
		}
		
		if (DistanceFromRightRectangleToRightSide > DistanceFromLeftRectangleToLeftSide) {
			//The robot needs to turn right
			driveTrain.moveRightByCameraPixels(DistanceFromRightRectangleToRightSide - DistanceFromLeftRectangleToLeftSide);
		}
	}
	
	private static Rectangle getLeftRectangle(NetworkTable grip) {
		return new Rectangle((int) grip.getValue(m_leftRectangleBaseName + "X", -1),
				 (int) grip.getValue(m_leftRectangleBaseName + "Y", -1),
				 (int) grip.getValue(m_leftRectangleBaseName + "Width", -1),
				 (int) grip.getValue(m_leftRectangleBaseName + "Height", -1));
	}
	
	private static Rectangle getRightRectangle(NetworkTable grip) {
		return new Rectangle((int) grip.getValue(m_rightRectangleBaseName + "X", -1),
							 (int) grip.getValue(m_rightRectangleBaseName + "Y", -1),
							 (int) grip.getValue(m_rightRectangleBaseName + "Width", -1),
							 (int) grip.getValue(m_rightRectangleBaseName + "Height", -1));
	}
}
 