package org.usfirst.frc.team3164.robot.auto;

public class TurnHandler {
	private RobotTurns m_turns;

	public enum RobotTurnHandlerType {
		TURNHANDLER_MIDDLE,
		TURNHANDLER_SIDE
	}

	private RobotTurnHandlerType type;

	public TurnHandler(RobotPosition startingPosition) {
		if (startingPosition == RobotPosition.MIDDLE) {
			m_turns = new MiddleRobotTurns();
			type = RobotTurnHandlerType.TURNHANDLER_MIDDLE;
		}
		else if (startingPosition == RobotPosition.RIGHT ||
				startingPosition == RobotPosition.LEFT) {
			m_turns = new SideRobotTurns(startingPosition);
			type = RobotTurnHandlerType.TURNHANDLER_SIDE;
		}
	}

	public void completeActiveTurn() {
		m_turns.completeActiveTurn();
	}

	public boolean isTurning() {
		return m_turns.isActive();
	}

	public Turn getActiveTurn() {
		return m_turns.getActiveTurn();
	}

	public int getNumTurns() {
		if (type == RobotTurnHandlerType.TURNHANDLER_SIDE) {
			return 1;
		} 
		return 2;
	}
	
	public Turn getLastTurn() {
		return m_turns.getLastTurn();
	}
}