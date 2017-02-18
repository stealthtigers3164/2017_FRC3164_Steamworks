package org.usfirst.frc.team3164.robot.electrical;

import edu.wpi.first.wpilibj.AnalogGyro;

public class GyroscopeSensor {
	private AnalogGyro m_gryo;

	public GyroscopeSensor(int channel, double sensitivity) {
		m_gryo = new AnalogGyro(channel);
		m_gryo.initGyro();
		m_gryo.reset();
		m_gryo.calibrate();
		m_gryo.setSensitivity(sensitivity);
	}

	public double getAngle() {
		return m_gryo.getAngle();
	}

	public int getCenter() {
		return m_gryo.getCenter();
	}

	public double getOffset() {
		return m_gryo.getOffset();
	}

	public double getRate() {
		return m_gryo.getRate();
	}
}