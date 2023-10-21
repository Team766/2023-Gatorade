package com.team766.robot.constants;

/** Constants used for reading values from the config file. */
public final class ConfigConstants {
	// utility class
	private ConfigConstants() {}

	// drive config values
	public static final String DRIVE_DRIVE_FRONT_RIGHT = "drive.DriveFrontRight";
	public static final String DRIVE_DRIVE_FRONT_LEFT = "drive.DriveFrontLeft";
	public static final String DRIVE_DRIVE_BACK_RIGHT = "drive.DriveBackRight";
	public static final String DRIVE_DRIVE_BACK_LEFT = "drive.DriveBackLeft";

	public static final String DRIVE_STEER_FRONT_RIGHT = "drive.SteerFrontRight";
	public static final String DRIVE_STEER_FRONT_LEFT = "drive.SteerFrontLeft";
	public static final String DRIVE_STEER_BACK_RIGHT = "drive.SteerBackRight";
	public static final String DRIVE_STEER_BACK_LEFT = "drive.SteerBackLeft";

	// intake config values
	public static final String INTAKE_MOTOR = "intake.motor";

	// wrist config values
	public static final String WRIST_MOTOR = "wrist.motor";
	public static final String WRIST_PGAIN = "wrist.pGain";
	public static final String WRIST_IGAIN = "wrist.iGain";
	public static final String WRIST_DGAIN = "wrist.dGain";
	public static final String WRIST_FFGAIN = "wrist.ffGain";

	// elevator config values
	public static final String ELEVATOR_LEFT_MOTOR = "elevator.leftMotor";
	public static final String ELEVATOR_RIGHT_MOTOR = "elevator.rightMotor";
	public static final String ELEVATOR_PGAIN = "elevator.pGain";
	public static final String ELEVATOR_IGAIN = "elevator.iGain";
	public static final String ELEVATOR_DGAIN = "elevator.dGain";
	public static final String ELEVATOR_FFGAIN = "elevator.ffGain";
	public static final String ELEVATOR_MAX_VELOCITY = "elevator.maxVelocity";
	public static final String ELEVATOR_MIN_OUTPUT_VELOCITY = "elevator.minOutputVelocity";
	public static final String ELEVATOR_MAX_ACCEL = "elevator.maxAccel";
}

