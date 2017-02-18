package org.usfirst.frc.team3164.robot.auto;

import org.usfirst.frc.team3164.robot.electrical.motor.BasicMotor;
import org.usfirst.frc.team3164.robot.movement.DriveTrain;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @notes 
 *
 * We need to keep track of time for testing puposes, and maybe even make it where
 * if the time is running out then it actually speeds up.  This will make it so that even if
 * we make it to slow, the robot will automatically speed up.  
 *
 *
 * 
 */
public class AutoDrive<T extends BasicMotor> {

	//Turn Variables
	
	//NOTE: This is the starting position of the robot, this is marked final so that nothing can
	//NOTE: accidentaly change the varible.
	private final RobotPosition m_startingPosition;
	
	//NOTE: This handles all turn handling.
	//NOTE: Meaning behind "turn handling": Whether or not there is another 
	//NOTE: turn or the robot is currently turning
	private TurnHandler m_turnHandler;
	
	//

	//Turn constants

	// - MIDDLE TURNING CONSTANTS

	//NOTE: This is when the robot should start turning to the side(The initial turn)
	private final double MIDDLE_START_TURN_DISTANCE = 1143;

	//NOTE: This is the distance from the middle to the right side, 
	//NOTE: this is after the turn to point to the right side is complete.
	//NOTE: This is the total length from the end of the turn to the side of the arena
	private final double MIDDLE_TO_RIGHT_SIDE_TOTAL_LENGTH = 6223;
	
	//NOTE: This is the amount of units(feet, inches, meters, ?) that should
	//NOTE: trigger the second turn 
	private final int MIDDLE_TURN_DEGREE = 90;

	// - 
	// - SIDE TURNING CONSTANTS

	//NOTE: This is when the only turn should start
	private final double SIDE_START_TURN_DISTANCE = 2504.7448; //in mm

	//NOTE: This is when the robot should go foward to the peg
	private final int SIDE_STOP_TURNING_DEGREES = 60;
	
	// - 
	//

	//Sensor Variables

	//NOTE: This is where the two distance sensors will come from
	private NetworkTable m_sensorNetworkTable;
	
	//NOTE: These are the two values that are used to get the front distance sensor and the lidar distance sensor
	private String m_lidarNetworkTableName;
	private String m_frontUltraNetworkTableName;
	
	//NOTE: This is the class that you can call to get the degress traveled in a set amount of time
	//NOTE: also you should be able to get the length traveled too
	private GyroHandler m_gyroHandler;
	
	//

	//Motor/Driving Variables

	//I have no idea what is being used to be able to use the motors
	//so replace this with whatever does and then replace the mock functions 
	//with the real function
	private DriveTrain<T> m_driveTrain;

	//

	//Constructor
	//This initializes all of the variables that it needs to 
	public AutoDrive(int distanceInputSensorPort, DriveTrain<T> driveTrain, int gryoPort, RobotPosition startingPosition,
					 NetworkTable sensorNetworkTable, String lidarNetworkTableName, String frontUltraNetworkTableName) {
		
		//NOTE: If the robot never gets out of a turn because the degree is actually the same as the wanted one
		//Make it so that it has to be within 1 degree of the wanted value

		
		//NOTE: Moving stuff initialization
		m_driveTrain = driveTrain;
		
		//NOTE: Distance sensor initialization
		m_sensorNetworkTable = sensorNetworkTable;
		m_lidarNetworkTableName = lidarNetworkTableName;
		m_frontUltraNetworkTableName = frontUltraNetworkTableName;
		
		//NOTE: Turning code initialization
		m_gyroHandler = new GyroHandler(gryoPort);
		m_startingPosition = startingPosition;
		m_turnHandler = new TurnHandler(m_startingPosition);
	}

	private boolean m_run = true;
	
	public void update() {
		if (!m_run) {
			return;
		}
		//NOTE: Checks whether there is or not a turn is happening at the moment
		if (m_turnHandler.isTurning()) {
			//Continuing the turn
			Turn currentTurn = m_turnHandler.getActiveTurn();
			continueTurning(currentTurn, (int)m_gyroHandler.getRobotAngle());
		}
		else {
			//NOTE: Checking to see if the robot should start to turn or keep moving forward
			Turn lastTurn = m_turnHandler.getLastTurn();
			RobotPosition currentRobotPosition = m_startingPosition;
			
			if (lastTurn != null) {
				currentRobotPosition = lastTurn.getPositonAfterTurn();
			}
			
			if (currentRobotPosition == RobotPosition.PEG) {
				double forwardsDistance = getForwardDistance();
				if (forwardsDistance < 100) { // This is crucial
					//TODO: Auto align code should be called here, however I do not know where that code should go because that is
					//      not specific to this code, so it should go in some AutoAlign class
					if (AutoAlign.needed()) {
						AutoAlign.align();
					}
				}
				
				if (forwardsDistance == 300) {
					m_run = false;
				}
				
				SmartDashboard.putNumber("Forward Distance when facing the peg", forwardsDistance);
				m_driveTrain.moveByLength(forwardsDistance);
			}
			
			double backwardsDistance = getBackDistance();
			if (currentRobotPosition == RobotPosition.MIDDLE) {
				
				if (lastTurn.getPositon() == RobotPosition.MIDDLE) {
					if (backwardsDistance < MIDDLE_TO_RIGHT_SIDE_TOTAL_LENGTH) {
						m_driveTrain.moveByLength((MIDDLE_TO_RIGHT_SIDE_TOTAL_LENGTH) - backwardsDistance);
					} else {
						m_turnHandler.startTurn(currentRobotPosition, RobotPosition.RIGHT);
					}
				}
				
				if (backwardsDistance < MIDDLE_START_TURN_DISTANCE) {
					m_driveTrain.moveByLength(MIDDLE_START_TURN_DISTANCE - backwardsDistance);
				} else {
					m_turnHandler.startTurn(currentRobotPosition, RobotPosition.RIGHT);
				}
			} else {
				if (backwardsDistance < SIDE_START_TURN_DISTANCE) {
					m_driveTrain.moveByLength(SIDE_START_TURN_DISTANCE - backwardsDistance);
				} else {
					m_turnHandler.startTurn(currentRobotPosition, RobotPosition.PEG);
				}
			}
		}
	}

	public void continueTurning(Turn currentTurn, int robotAngle) {
		if (currentTurn.getPositon() == RobotPosition.MIDDLE) {
			if (robotAngle < MIDDLE_TURN_DEGREE) {
				m_driveTrain.turnLeftByDegrees(MIDDLE_TURN_DEGREE - robotAngle);
			} else if (robotAngle > MIDDLE_TURN_DEGREE) {
				m_driveTrain.turnRightByDegrees(MIDDLE_TURN_DEGREE - robotAngle);
			} else {
				currentTurn.turnComplete();
			}
		}
		else {
			
			if (currentTurn.getPositon() == RobotPosition.RIGHT &&
				currentTurn.getPositonAfterTurn() == RobotPosition.RIGHT) {
				if (robotAngle < 90) {
					m_driveTrain.turnRightByDegrees(90 - robotAngle);
				} else if (robotAngle > 90) {
					m_driveTrain.turnLeftByDegrees(robotAngle - 90);					
				} else {
					currentTurn.turnComplete();
				}
			}
			
			if (robotAngle < SIDE_STOP_TURNING_DEGREES) {
				if (currentTurn.getPositon() == RobotPosition.RIGHT) {
					m_driveTrain.turnLeftByDegrees(SIDE_STOP_TURNING_DEGREES - robotAngle);
				}
				else if (currentTurn.getPositon() == RobotPosition.LEFT) {
					m_driveTrain.turnRightByDegrees(SIDE_STOP_TURNING_DEGREES - robotAngle);
				}
			} else if (robotAngle > SIDE_STOP_TURNING_DEGREES) {
				if (currentTurn.getPositon() == RobotPosition.RIGHT) {
					m_driveTrain.turnRightByDegrees(robotAngle - SIDE_STOP_TURNING_DEGREES);
				}
				else if (currentTurn.getPositon() == RobotPosition.LEFT) {
					m_driveTrain.turnLeftByDegrees(robotAngle - SIDE_STOP_TURNING_DEGREES);
				}
			} else {
				currentTurn.turnComplete();
			}
		}
	}
	
	public double getBackDistance() {
		return (double) m_sensorNetworkTable.getValue(m_lidarNetworkTableName, -1);
	}
	
	public double getForwardDistance() {
		return (double) m_sensorNetworkTable.getValue(m_frontUltraNetworkTableName, -1);
	}
}