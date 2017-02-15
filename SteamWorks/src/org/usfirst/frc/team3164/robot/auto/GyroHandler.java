package org.usfirst.frc.team3164.robot.auto;

import org.usfirst.frc.team3164.robot.electrical.GyroscopeSensor;

public class GyroHandler {
	private GyroscopeSensor m_sensor;

	//NOTE: This is not the value that it should be
	private final double GYRO_SENSITIVITY = 1;

	private double lastTime = 0;
	
	public GyroHandler(int channel) {
		m_sensor = new GyroscopeSensor(channel, GYRO_SENSITIVITY);
		lastTime = System.currentTimeMillis();
	}

	public double getDegrees() {
		double nowTime = System.currentTimeMillis();
		
		double secondsPassed = (nowTime - lastTime) / 1000;
		
		return m_sensor.getRate() * secondsPassed;
	}
}
