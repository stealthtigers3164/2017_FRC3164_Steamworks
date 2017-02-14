package org.usfirst.frc.team3164.robot.electrical;


import edu.wpi.first.wpilibj.PWM;

public class PWMDistance {

	private PWM distSensor;

	/**
	 * PWM distance sensor class, setup for MaxBotix HRLV 1013 Ultrasonic Sensor
	 * @param port analog port
	 */
	PWMDistance(int port) {
		this.distSensor = new PWM(port);

		//this.distSensor.setBounds(5000, deadbandMax, center, deadbandMin, 300);
		
	}

	/**
	 * Returns distance in mm (NEEDS TESTING)
	 * @return
	 */
	public double getDistance() {
		return this.distSensor.getRaw();
	}
}
