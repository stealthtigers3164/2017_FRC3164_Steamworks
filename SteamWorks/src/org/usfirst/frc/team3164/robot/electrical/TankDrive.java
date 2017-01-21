package org.usfirst.frc.team3164.robot.electrical;

import org.usfirst.frc.team3164.robot.input.Gamepad;
import org.usfirst.frc.team3164.robot.motor.SparkMotor;

public class TankDrive {
	private DriveTrain<SparkMotor> m_driveTrain;
	
	public TankDrive(int frontLeftLocation, int backLeftLocation,
					 int frontRightLocation, int backRightLocation) {
		m_driveTrain = new DriveTrain<SparkMotor>(new SparkMotor(frontLeftLocation), 
									  new SparkMotor(backLeftLocation), 
									  new SparkMotor(frontRightLocation),
									  new SparkMotor(backRightLocation));
	}
	
	public void update(Gamepad Controller) {
		m_driveTrain.setLeftPower(Controller.sticks.LEFT_Y.getRaw());
		m_driveTrain.setRightPower(Controller.sticks.RIGHT_Y.getRaw());
		m_driveTrain.update();
	}
}
