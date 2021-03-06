package org.usfirst.frc.team3164.robot.auto;

public class Turn {
	private RobotPosition m_position;
	private RobotPosition m_positionAfterTurn;

	private boolean m_isActive;
	private boolean m_isDone;

	public Turn(RobotPosition position, RobotPosition positionAfterTurn) {
		m_position = position;
		m_positionAfterTurn = positionAfterTurn;
		m_isActive = false;
		m_isDone = false;
	}

	public RobotPosition getPositon() {
		return m_position;
	}

	public RobotPosition getPositonAfterTurn() {
		return m_positionAfterTurn;
	}

	public void start() {
		m_isActive = true;
	}

	public void turnComplete() {
		m_isDone = true;
		m_isActive = false;
	}

	public boolean isActive() {
		return m_isActive;
	}

	public boolean isDone() {
		return m_isDone;
	}
}