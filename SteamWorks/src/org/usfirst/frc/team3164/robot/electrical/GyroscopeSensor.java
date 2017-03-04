package org.usfirst.frc.team3164.robot.electrical;

import edu.wpi.first.wpilibj.AnalogGyro;;

public class GyroscopeSensor {
	private AnalogGyro m_gryo;

	public GyroscopeSensor(int channel) {
		m_gryo = new AnalogGyro(channel);
		m_gryo.initGyro();
		m_gryo.reset();
		
		m_gryo.calibrate();
		//m_gryo.setSensitivity(360);Defaults to what we need
	}

	public double getAngle() {
		return m_gryo.getAngle() % 360;
	}
	
	public void resetGyro() {
		this.m_gryo.reset();
	}
}