package org.usfirst.frc.team3164.robot.auto;

public class SideRobotTurns implements RobotTurns {
	private Turn m_turn;

	public SideRobotTurns(RobotPosition currentPosition) {
		m_turn = new Turn(currentPosition, RobotPosition.MIDDLE);
	}

	@Override
	public Turn getActiveTurn() {
		return m_turn;
	}

	@Override
	public boolean isComplete() {
		return getActiveTurn().isDone();
	}

	@Override
	public boolean isActive() {
		return getActiveTurn().isActive();
	}

	@Override
	public void completeActiveTurn() {
		getActiveTurn().turnComplete();
	}

	@Override
	public Turn getLastTurn() {
		if (m_turn.isDone()) {
			return m_turn;
		}
		return null;
	}
}