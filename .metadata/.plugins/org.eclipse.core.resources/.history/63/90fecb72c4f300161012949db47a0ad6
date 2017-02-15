package org.usfirst.frc.team3164.robot.auto;

public class MiddleRobotTurns  implements RobotTurns {
	private Turn m_middleToSideTurn;
	private Turn m_sideToPegTurn;
	
	private SideRobotTurns m_finalTurn;

	public MiddleRobotTurns() {
		m_middleToSideTurn = new Turn(RobotPosition.MIDDLE, RobotPosition.RIGHT);
		m_sideToPegTurn = new Turn(RobotPosition.RIGHT, RobotPosition.PEG);
		m_finalTurn = new SideRobotTurns(RobotPosition.RIGHT);
	}	

	@Override
	public Turn getActiveTurn() {
		if (m_middleToSideTurn.isActive()) {
			return m_middleToSideTurn;
		}
		if (!m_middleToSideTurn.isActive()) {
			return null;
		}
		return m_sideToPegTurn;
	}

	@Override
	public boolean isComplete() {
		if (m_middleToSideTurn.isDone() &&
			m_sideToPegTurn.isDone()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isActive() {
		Turn activeTurn = getActiveTurn();

		if (activeTurn == null) {
			return false;
		}

		return true;
	}

	@Override
	public void completeActiveTurn() {
		Turn activeTurn = getActiveTurn();

		if (activeTurn == null) {
			return;
		}

		activeTurn.turnComplete();
	}
}