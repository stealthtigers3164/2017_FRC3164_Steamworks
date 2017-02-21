
package org.usfirst.frc.team3164.robot;

import org.usfirst.frc.team3164.robot.auto.AutoDrive;
import org.usfirst.frc.team3164.robot.auto.RobotPosition;
import org.usfirst.frc.team3164.robot.comms.Watchcat;
import org.usfirst.frc.team3164.robot.electrical.ElectricalConfig;
import org.usfirst.frc.team3164.robot.electrical.motor.BasicMotor;
import org.usfirst.frc.team3164.robot.electrical.motor.SparkMotor;
import org.usfirst.frc.team3164.robot.input.Gamepad;
import org.usfirst.frc.team3164.robot.movement.DriveTrain;
import org.usfirst.frc.team3164.robot.movement.Lift;
import org.usfirst.frc.team3164.robot.thread.ThreadQueue;
import org.usfirst.frc.team3164.robot.thread.WorkerThread;
import org.usfirst.frc.team3164.robot.vision.Camera;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	private final String driveTank = "Tank Drive";
	private final String driveForza = "Forza Drive";
	private final String driveNone = "No Drive";
	private String driveSelected;
	private SendableChooser chooserDT;

	private DriveTrain<SparkMotor> drive;
	private Gamepad gamePad1;
	private Camera camera;

	private ThreadQueue<WorkerThread> queue;
	private final String rightRobotPosition = "Right";
	private final String leftRobotPosition = "Left";
	private final String middleRobotPosition = "Middle";

	private /* final */ NetworkTable grip = NetworkTable.getTable("grip");
	
	private AutoDrive<SparkMotor> m_autonomous;
	private SendableChooser m_startPositionChooser;

	private SparkMotor m_winch;
	
	public void robotInit() {
		// TEMP
		/*
		 * chooser = new SendableChooser(); chooser.addDefault("Default Auto",
		 * defaultAuto); chooser.addObject("My Auto", customAuto);
		 * SmartDashboard.putData("Auto choices", chooser);
		 */
		// END Temp

		Watchcat.init();// Not sure if should go here

		////////////// Gamepad //////////////
		gamePad1 = new Gamepad(0);

		////////////// Drivetrain //////////////
		drive = new DriveTrain<SparkMotor>(
				new SparkMotor(ElectricalConfig.wheel_frontLeft_motor, ElectricalConfig.wheel_frontLeft_rev),
				new SparkMotor(ElectricalConfig.wheel_frontRight_motor, ElectricalConfig.wheel_frontRight_rev),
				new SparkMotor(ElectricalConfig.wheel_backLeft_motor, ElectricalConfig.wheel_backLeft_rev),
				new SparkMotor(ElectricalConfig.wheel_backRight_motor, ElectricalConfig.wheel_backRight_rev), gamePad1);
		drive.setScaleFactor(0.7);// Overridden by smart dashboard

		m_winch = new SparkMotor(ElectricalConfig.winch_motor, true);
		
		gamePad1.sticks.setDeadzones();

		////////////// Driving //////////////

		chooserDT = new SendableChooser();
		chooserDT.addDefault("Forza Drive", driveForza);
		chooserDT.addObject("Tank Drive", driveTank);
		chooserDT.addObject("No Driving", driveNone);
		SmartDashboard.putData("Drivetrain", chooserDT);
		
		m_startPositionChooser = new SendableChooser();
		m_startPositionChooser.addDefault("RIGHT", rightRobotPosition);
		m_startPositionChooser.addObject("LEFT", leftRobotPosition);
		m_startPositionChooser.addObject("MIDDLE", middleRobotPosition);
		SmartDashboard.putData("StartPosition", m_startPositionChooser);

		drive.useForzaDrive();
		SmartDashboard.putNumber("Driving Scale Factor", 1);
		SmartDashboard.putNumber("Turning Scale Factor", 1);

		queue = new ThreadQueue<WorkerThread>();
		

		
		String startPositionSelected = (String) chooserDT.getSelected();
		
		RobotPosition robotStartingPosition = RobotPosition.RIGHT;
		
		switch(startPositionSelected) {
		case rightRobotPosition:
			robotStartingPosition = RobotPosition.RIGHT;
			break;
		case leftRobotPosition:
			robotStartingPosition = RobotPosition.LEFT;
			break;
		case middleRobotPosition:
			robotStartingPosition = RobotPosition.MIDDLE;
			break;
		}
		
		NetworkTable distanceTable = NetworkTable.getTable("distance");
		String nameOfLidarInNetworkTable = "lidar";
		String nameOfFrontUltra = "ultrasonic";
		
		m_autonomous = new AutoDrive<SparkMotor>(drive, robotStartingPosition, 
				distanceTable, nameOfLidarInNetworkTable, nameOfFrontUltra);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	public void autonomousInit() {
	}

	public void autonomousPeriodic() {
		m_autonomous.update();
	}

	public void teleopInit() {
		SmartDashboard.putString("Mode", "Teleop");
		SmartDashboard.putNumber("buttonPort", 1);
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {

		drive.setScaleFactor(SmartDashboard.getNumber("Driving Scale Factor"));
		drive.setScaleFactor(SmartDashboard.getNumber("Turning Scale Factor"), true);
		SmartDashboard.putNumber("rightY", gamePad1.sticks.RIGHT_Y.getScaled());

		if (gamePad1.trigger.getRightPressed(true)) {
			m_winch.setPower(1);
		}
		if (gamePad1.buttons.BUTTON_B.isOn()) {
			m_winch.setPower(0);
		}
		
		driveSelected = (String) chooserDT.getSelected();
		switch (driveSelected) {
		case driveNone:
			drive.setUpdate(false);
			break;
		case driveForza:
			if (drive.getDriveType() != DriveTrain.FORZA_DRIVE)
				drive.useForzaDrive();
			if (!drive.shouldUpdate())
				drive.setUpdate(true);
			break;
		case driveTank:
			if (drive.getDriveType() != DriveTrain.TANK_DRIVE)
				drive.useTankDrive();
			if (!drive.shouldUpdate())
				drive.setUpdate(true);
		default:

			break;
		}

		drive.updateMotors();
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testInit() {
		SmartDashboard.putString("Mode", "Test");

	}

	public void testPeriodic() {

	}

}
