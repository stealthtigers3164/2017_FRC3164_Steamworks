
package org.usfirst.frc.team3164.robot;

import org.usfirst.frc.team3164.robot.comms.Watchcat;
import org.usfirst.frc.team3164.robot.electrical.ElectricalConfig;
import org.usfirst.frc.team3164.robot.electrical.motor.SparkMotor;
import org.usfirst.frc.team3164.robot.input.Gamepad;
import org.usfirst.frc.team3164.robot.movement.DriveTrain;
import org.usfirst.frc.team3164.robot.movement.Lift;
import org.usfirst.frc.team3164.robot.thread.ThreadQueue;
import org.usfirst.frc.team3164.robot.thread.WorkerThread;
import org.usfirst.frc.team3164.robot.vision.Camera;

import edu.wpi.first.wpilibj.IterativeRobot;
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
    private Lift<SparkMotor> lift;
    private Gamepad gamePad1;
    private Gamepad gamePad2;
    private Camera camera;
    
    private ThreadQueue<WorkerThread> queue;

    
    //private /*final*/ NetworkTable grip = NetworkTable.getTable("grip");

    public void robotInit() {
    	//TEMP
    	/*chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);*/
        //END Temp
        
        
        Watchcat.init();//Not sure if should go here
        
        //////////////		Gamepad		//////////////
        gamePad1 = new Gamepad(0);
        gamePad2 = new Gamepad(1);
        
        //////////////		Drivetrain		//////////////
        drive = new DriveTrain<SparkMotor>(
        			new SparkMotor(ElectricalConfig.wheel_frontLeft_motor, ElectricalConfig.wheel_frontLeft_rev),
        			new SparkMotor(ElectricalConfig.wheel_frontRight_motor, ElectricalConfig.wheel_frontRight_rev),
        			new SparkMotor(ElectricalConfig.wheel_backLeft_motor, ElectricalConfig.wheel_backLeft_rev),
        			new SparkMotor(ElectricalConfig.wheel_backRight_motor, ElectricalConfig.wheel_backRight_rev),
        			gamePad1);
        drive.setScaleFactor(0.7);//Overridden by smart dashboard
        
        
        
        //////////////		Winch		//////////////
        lift = new Lift<SparkMotor>(
        		new SparkMotor(ElectricalConfig.lift_first_motor, ElectricalConfig.lift_first_rev),
        		new SparkMotor(ElectricalConfig.lift_second_motor, ElectricalConfig.lift_second_rev));
        
        
        
        gamePad1.sticks.setDeadzones();
        gamePad2.sticks.setDeadzones();

        //////////////		Driving		//////////////
        
        chooserDT = new SendableChooser();
        chooserDT.addDefault("Forza Drive", driveForza);
        chooserDT.addObject("Tank Drive", driveTank);
        chooserDT.addObject("No Driving", driveNone);
        SmartDashboard.putData("Drivetrain", chooserDT);
		
        drive.useForzaDrive();
        SmartDashboard.putNumber("Driving Scale Factor", 1);
        SmartDashboard.putNumber("Turning Scale Factor", 1);


        
        queue = new ThreadQueue<WorkerThread>();

    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	
    }

    public void autonomousPeriodic() {

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

    	
    	 driveSelected = (String) chooserDT.getSelected();
    	 	switch(driveSelected) {
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
    	 	
    	/*
    	 * Winch Motor
    	 */
    	     	 	lift.spinWinch(gamePad2.sticks.RIGHT_Y.getScaled());

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
