package org.usfirst.frc.team3164.robot.auto;

import java.util.ArrayList;

public class TurnHandler {
	private ArrayList<Turn> m_turns;
	
	public TurnHandler(RobotPosition startingPosition) {
		m_turns = new ArrayList<Turn>();
	}

	public void completeActiveTurn() {
		for (Turn turn : m_turns) {
			if (turn.isActive()) {
				turn.turnComplete();
			}
		}
	}

	public boolean isTurning() {
		for (Turn turn : m_turns) {
			if (turn.isActive()) {
				return true;
			}
		}
		return false;
	}

	public Turn getActiveTurn() {
		for (Turn turn : m_turns) {
			if (turn.isActive()) {
				return turn;
			}
		}
		return null;
	}
	
	public Turn getLastTurn() {
		Turn lastDoneTurn = null;
		for (Turn turn : m_turns) {
			if (lastDoneTurn != null &&
				!turn.isDone()) {
				return lastDoneTurn;
			} else if (turn.isDone()) {
				lastDoneTurn = turn;
			}
		}
		return null;
	}
	
	public void startTurn(RobotPosition startingPosition, RobotPosition endGoal) {
		Turn newTurn = new Turn(startingPosition, endGoal);
		newTurn.start();
		m_turns.add(newTurn);
	}
}