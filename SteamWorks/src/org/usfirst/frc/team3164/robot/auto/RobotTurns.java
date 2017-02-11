package org.usfirst.frc.team3164.robot.auto;

public interface RobotTurns {
	boolean isComplete();
	boolean isActive();
	Turn getActiveTurn();
	void completeActiveTurn();
}