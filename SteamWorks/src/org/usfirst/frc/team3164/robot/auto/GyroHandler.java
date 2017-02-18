package org.usfirst.frc.team3164.robot.auto;

import org.usfirst.frc.team3164.robot.electrical.GyroscopeSensor;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GyroHandler {
	private GyroscopeSensor m_sensor;

	//NOTE: This is not the value that it should be
	private final double GYRO_SENSITIVITY = 1;
	
	public GyroHandler(int channel) {
		m_sensor = new GyroscopeSensor(channel, GYRO_SENSITIVITY);
	}

	public double getRobotAngle() {
		double angle = m_sensor.getAngle() % 360;
		SmartDashboard.putNumber("Robot Angle", angle);
		return angle;
	}
}
