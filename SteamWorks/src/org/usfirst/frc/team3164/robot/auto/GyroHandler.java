package org.usfirst.frc.team3164.robot.auto;

import org.usfirst.frc.team3164.robot.electrical.GyroscopeSensor;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GyroHandler {
	private GyroscopeSensor m_sensor;


	
	public GyroHandler(int channel) {
		m_sensor = new GyroscopeSensor(channel);
		
	}

	public double getRobotAngle() {
		return m_sensor.getAngle();
	}
}
