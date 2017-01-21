package org.usfirst.frc.team3164.robot.motor;

import edu.wpi.first.wpilibj.Spark;

public class SparkMotor implements MotorController {
	private Spark m_spark;
	private int m_location;
	private double m_power;
	
	public SparkMotor(int location) {
		m_location = location;
		m_spark = new Spark(m_location);
	}
	
	@Override
	public void update() {
		m_spark.set(m_power);
	}
	
	@Override
	public double getPower() {
		return m_power;
	}

	@Override
	public void setPower(double power) {
		m_power = power;
	}

	@Override
	public int getLocation() {
		return m_location;
	}
}