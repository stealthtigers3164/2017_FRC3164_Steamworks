package org.usfirst.frc.team3164.robot.movement;

import java.util.ArrayList;

import org.usfirst.frc.team3164.robot.electrical.motor.BasicMotor;
import org.usfirst.frc.team3164.robot.electrical.motor.MotorSet;
import org.usfirst.frc.team3164.robot.input.Gamepad;

public class Lift<T extends BasicMotor> extends MotorSet<T> {
	
		
	private ArrayList<T> motors;
	
	private int firstLiftMotorIndex;	
	private int secondLiftMotorIndex;


	private Gamepad gamePad;
	
	private boolean update;
	
	private float spinSpeed = 0;
	
	public Lift(T flMotor, T slMotor) {
		//NOTE if you change the order of which the motors are added then the indices will change as well
		motors = new ArrayList<T>();
		firstLiftMotorIndex = addMotor(flMotor);
		secondLiftMotorIndex = addMotor(slMotor);

	}
	

	
	/**
	 * Spins the winch motor
	 * @param speed [-1,1]
	 */
	public void spinWinch(double speed) {
		getMotorByIndex(firstLiftMotorIndex).setPower(speed);
		getMotorByIndex(secondLiftMotorIndex).setPower(speed);
		
	}
	@Override
	public ArrayList<T> getMotors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getMotorByIndex(int Index) {
		return motors.get(Index);
	}

	@Override
	public int addMotor(T Motor) {
		if (motors.add(Motor))
			return motors.size() - 1;
		else
			return -1;
	}

	@Override
	public void updateMotors() {
		spinWinch(spinSpeed);
		
	}

	@Override
	public boolean shouldUpdate() {
		return update;
	}
	
	public void setUpdate(boolean Update) {
		update = Update;
	}
	
	public void setSpinSpeed(float speed) {
		spinSpeed = speed;
	}
	
	
}