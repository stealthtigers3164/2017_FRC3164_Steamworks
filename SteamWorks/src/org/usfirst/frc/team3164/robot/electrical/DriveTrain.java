package org.usfirst.frc.team3164.robot.electrical;

import org.usfirst.frc.team3164.robot.motor.MotorController;

public class DriveTrain<T extends MotorController>  {
	//Left Motors
	private T m_frontLeft;
	private T m_backLeft;
	
	//Right Motors
	private T m_frontRight;
	private T m_backRight;
	
	public DriveTrain(T frontLeft, T backLeft,
					  T frontRight, T backRight) {
		m_frontLeft = frontLeft;
		m_backLeft = backLeft;
		m_frontRight = frontRight;
		m_backRight = frontRight;
	}
	
	public void update() {
		m_frontLeft.update();
		m_backLeft.update();
		m_frontRight.update();
		m_backRight.update();
	}
	
	public void setLeftPower(double power) {
		setFrontLeftPower(power);
		setBackLeftPower(power);
	}
	
	public void setRightPower(double power) {
		setFrontRightPower(power);
		setBackRightPower(power);
	}
	
	public void setFrontLeftPower(double power) {
		m_frontLeft.setPower(power);
	}

	public void setBackLeftPower(double power) {
		m_backLeft.setPower(power);
	}
	
	public void setFrontRightPower(double power) {
		m_frontRight.setPower(power);
	}

	public void setBackRightPower(double power) {
		m_backRight.setPower(power);
	}
	
	public MotorController getFrontLeft() {
		return m_frontLeft;
	}
	
	public MotorController getBackLeft() {
		return m_backLeft;
	}
	
	public MotorController getFrontRight() {
		return m_frontRight;
	}
	
	public MotorController getBackRight() {
		return m_backRight;
	}
}