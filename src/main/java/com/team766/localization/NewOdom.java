package com.team766.localization;

import com.team766.framework.LoggingBase;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import com.ctre.phoenix.sensors.CANCoder;
import com.team766.logging.Category;
import com.team766.robot.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Rotation2d;
 
/**
 * Method which calculates the position of the robot based on wheel positions.
 */
public class NewOdom {

	// probably good at every 100th of a second but test
	private RateLimiter odometryLimiter;

	private MotorController[] motorList;
	// The order of CANCoders should be the same as in motorList
	private CANCoder[] CANCoderList;
	private	int motorCount; 

	private Pose2d[] prevPositions;
	private Pose2d[] currPositions;
	private double[] prevEncoderValues;
	private double[] currEncoderValues;
	private double gyroPosition;
	private Rotation2d rotation2d;

	private Pose2d currentPosition;

	// In Meters
	private static double WHEEL_CIRCUMFERENCE;
	public static double GEAR_RATIO;
	public static int ENCODER_TO_REVOLUTION_CONSTANT;

	// In the same order as motorList, relative to the center of the robot
	private Transform2d[] wheelPositions;
	
	/**
	 * Constructor for Odometry, taking in several defines for the robot.
	 * @param motors A list of every wheel-controlling motor on the robot.
	 * @param CANCoders A list of the CANCoders corresponding to each wheel, in the same order as motors.
	 * @param wheelLocations A list of the locations of each wheel, in the same order as motors.
	 * @param wheelCircumference The circumfrence of the wheels, including treads.
	 * @param gearRatio The gear ratio of the wheels.
	 * @param encoderToRevolutionConstant The encoder to revolution constant of the wheels.
	 * @param rateLimiterTime How often odometry should run.
	 */
	public NewOdom(MotorController[] motors, CANCoder[] CANCoders, Transform2d[] wheelLocations, double wheelCircumference, double gearRatio, int encoderToRevolutionConstant, double rateLimiterTime) {

		odometryLimiter = new RateLimiter(rateLimiterTime);
		motorList = motors;
		CANCoderList = CANCoders;
		motorCount = motorList.length;
		// log("Motor count " + motorCount);
		prevPositions = new Pose2d[motorCount];
		currPositions = new Pose2d[motorCount];
		prevEncoderValues = new double[motorCount];
		currEncoderValues = new double[motorCount];

		wheelPositions = wheelLocations;
		WHEEL_CIRCUMFERENCE = wheelCircumference;
		GEAR_RATIO = gearRatio;
		ENCODER_TO_REVOLUTION_CONSTANT = encoderToRevolutionConstant;

		currentPosition = new Pose2d(0, 0, rotation2d);
		for (int i = 0; i < motorCount; i++) {
			prevPositions[i] = new Pose2d(0, 0, rotation2d);
			currPositions[i] = new Pose2d(0, 0, rotation2d);
			prevEncoderValues[i] = 0;
			currEncoderValues[i] = 0;
		}
	}

	public String getName() {
		return "Odometry";
	}

	/**
	 * Sets the current position of the robot to Point P
	 * @param P The point to set the current robot position to
	 */
	public void setCurrentPosition(Pose2d P) {
		currentPosition = P;
		// log("Set Current Position to: " + P.toString());
		for (int i = 0; i < motorCount; i++) {
			prevPositions[i] = currentPosition.plus(wheelPositions[i]);
			currPositions[i] = currentPosition.plus(wheelPositions[i]);
		}
		// log("Current Position: " + currentPosition.toString());
	}
	

	/**
	 * Updates the odometry encoder values to the robot encoder values.
	 */
	private void setCurrentEncoderValues() {
		for (int i = 0; i < motorCount; i++) {
			prevEncoderValues[i] = currEncoderValues[i];
			currEncoderValues[i] = motorList[i].getSensorPosition();
			currEncoderValues[i] *= (DriverStation.getAlliance() == Alliance.Blue ? 1 : -1);
		}
	}

	private static Vector2D rotate(Vector2D v, double angle) {
		return new Vector2D(
			v.getX() * Math.cos(angle) - Math.sin(angle) * v.getY(),
			v.getY() * Math.cos(angle) + v.getX() * Math.sin(angle));
	}

	/**
	 * Updates the position of each wheel of the robot by assuming each wheel moved in an arc.
	 */
	private void updateCurrentPositions() {
		Rotation2d rotationChange;
		double radius;
		double deltaX;
		double deltaY;
		gyroPosition = -Robot.gyro.getGyroYaw();
		
		/*
		Point slopeFactor = new Point(Math.sqrt(Math.cos(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.cos(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.cos(Math.toRadians(Robot.gyro.getGyroPitch())) * Math.cos(Math.toRadians(Robot.gyro.getGyroPitch())) + Math.sin(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.sin(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.cos(Math.toRadians(Robot.gyro.getGyroRoll())) * Math.cos(Math.toRadians(Robot.gyro.getGyroRoll()))),
									  Math.sqrt(Math.sin(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.sin(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.cos(Math.toRadians(Robot.gyro.getGyroPitch())) * Math.cos(Math.toRadians(Robot.gyro.getGyroPitch())) + Math.cos(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.cos(Math.toRadians(Robot.gyro.getGyroYaw())) * Math.cos(Math.toRadians(Robot.gyro.getGyroRoll())) * Math.cos(Math.toRadians(Robot.gyro.getGyroRoll()))));
		*/

		for (int i = 0; i < motorCount; i++) {
			// prevPositions[i] = new PointDir(currentPosition.getX() + 0.5 * DISTANCE_BETWEEN_WHEELS / Math.sin(Math.PI / motorCount) * Math.cos(currentPosition.getHeading() + ((Math.PI + 2 * Math.PI * i) / motorCount)), currentPosition.getY() + 0.5 * DISTANCE_BETWEEN_WHEELS / Math.sin(Math.PI / motorCount) * Math.sin(currentPosition.getHeading() + ((Math.PI + 2 * Math.PI * i) / motorCount)), currPositions[i].getHeading());
			// This following line only works if the average of wheel positions is (0,0)

			// need 2 set it to a pose instead 4 angle
			prevPositions[i].set(currentPosition.add(wheelPositions[i]), currPositions[i].getHeading());

			// transformBy a transform that only has a diff angle?
			currPositions[i].setHeading(-CANCoderList[i].getAbsolutePosition() + gyroPosition);
			
			rotationChange = currPositions[i].getRotation().minus(prevPositions[i].getRotation());

			double yaw = -Math.toRadians(Robot.gyro.getGyroYaw());
			double roll = Math.toRadians(Robot.gyro.getGyroRoll());
			double pitch = Math.toRadians(Robot.gyro.getGyroPitch());

			// converts from robot oriented to field oriented
			double w = Math.toRadians(CANCoderList[i].getAbsolutePosition());
			Vector2D u = new Vector2D(Math.cos(yaw) * Math.cos(pitch), Math.sin(yaw) * Math.cos(pitch));
			Vector2D v = new Vector2D(Math.cos(yaw) * Math.sin(pitch) * Math.sin(roll) - Math.sin(yaw) * Math.cos(roll), 
								Math.sin(yaw) * Math.sin(pitch) * Math.sin(roll) + Math.cos(yaw) * Math.cos(roll));
			Vector2D a = u.scalarMultiply(Math.cos(w)).add(v.scalarMultiply(Math.sin(w)));
			Vector2D b = u.scalarMultiply(-Math.sin(w)).add(v.scalarMultiply(Math.cos(w)));
			Vector2D wheelMotion;

			// log("u: " + u + " v: " + v + " a: " + a + " b: " + b);

			// double oldWheelX;
			// double oldWheelY;

			// estimates the bot moved in a circle to calculate new position
			if (rotationChange.getDegrees() != 0) {
				radius = 180 * (currEncoderValues[i] - prevEncoderValues[i]) / (Math.PI * rotationChange.getDegrees());
				// could def make this math cleaner w/rotation functions
				deltaX = radius * Math.sin(Math.toRadians(rotationChange.getDegrees()));
				deltaY = radius * (1 - Math.cos(Math.toRadians(rotationChange.getDegrees())));

				wheelMotion = a.scalarMultiply(deltaX).add(b.scalarMultiply(-deltaY));

				// oldWheelX = ((Math.cos(Math.toRadians(prevPositions[i].getHeading())) * deltaX - Math.sin(Math.toRadians(prevPositions[i].getHeading())) * deltaY) * slopeFactor.getX() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
				// oldWheelY = ((Math.sin(Math.toRadians(prevPositions[i].getHeading())) * deltaX + Math.cos(Math.toRadians(prevPositions[i].getHeading())) * deltaY) * slopeFactor.getY() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
			
			} else {
				wheelMotion = a.scalarMultiply((currEncoderValues[i] - prevEncoderValues[i]));

				// oldWheelX = ((currEncoderValues[i] - prevEncoderValues[i]) * Math.cos(Math.toRadians(prevPositions[i].getHeading())) * slopeFactor.getX() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
				// oldWheelY = ((currEncoderValues[i] - prevEncoderValues[i]) * Math.sin(Math.toRadians(prevPositions[i].getHeading())) * slopeFactor.getY() * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
			}
			wheelMotion = wheelMotion.scalarMultiply(WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
			// wheelMotion = rotate(wheelMotion, Math.toRadians(gyroPosition));
			// log("Difference: " + (oldWheelX - wheelMotion.getX()) + ", " + (oldWheelY - wheelMotion.getY()) + "Old Method: " + oldWheelX + ", " + oldWheelY + "Current Method: " + wheelMotion.getX() + ", " + wheelMotion.getY());
			// log("Current: " + currPositions[i] + " Motion: " + wheelMotion + " New: " + currPositions[i].add(wheelMotion));

			// need to make wheelMotion a pose
			currPositions[i].set(currPositions[i].subtract(wheelMotion));
		}
	}

	/**
	 * Calculates the position of the robot by finding the average of the wheel positions.
	 */
	private void findRobotPosition() {
		double sumX = 0;
		double sumY = 0;
		for (int i = 0; i < motorCount; i++) {
			sumX += currPositions[i].getX();
			sumY += currPositions[i].getY();
			// log("sumX: " + sumX + " Motor Count: " + motorCount + " CurrentPosition: " + currPositions[i]);
		}
		// need to get gyroPosition figured out w/rotation2d
		currentPosition.set(sumX / motorCount, sumY / motorCount, gyroPosition);
	}

	// Intended to be placed inside Robot.drive.run()
	// so localization can prob use 
	public Pose2d run() {
		if (odometryLimiter.next()) {
			setCurrentEncoderValues();
			updateCurrentPositions();
			findRobotPosition();
			// log(currentPosition.toString());
		}
		return currentPosition;
	}
}
