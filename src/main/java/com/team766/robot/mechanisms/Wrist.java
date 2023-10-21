package com.team766.robot.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import static com.team766.robot.constants.ConfigConstants.*;

/**
 * Basic wrist mechanism.  Used in conjunction with the {@link Intake} and {@link Elevator}.
 * Can be moved up and down as part of teleop or autonomous control to move the {@link Intake}
 * (attached to the end of the Wrist) closer to a game piece or game element (eg node in the 
 * field, human player station), at which point the {@link Intake} can grab or release the game
 * piece as appropriate.
 */
public class Wrist extends Mechanism {

	/**
	 * Pre-set positions for the wrist.
	 */
	public enum Position {

		// TODO: adjust these values.

		/** Wrist is fully up. */
		UP(0.0),
		/** Wrist is level with ground. */ 
		LEVEL(90.0),
		/** Wrist is fully down. */ 
		DOWN(180.0);

		private final double angle;
		
		Position(double angle) {
			this.angle = angle;
		}

		private double getAngle() {
			return angle;
		}
	}

	private final CANSparkMax motor;
	private final SparkMaxPIDController pidController;
	private final ValueProvider<Double> pGain;
	private final ValueProvider<Double> iGain;
	private final ValueProvider<Double> dGain;
	private final ValueProvider<Double> ffGain;
	private final RateLimiter rateLimiter = new RateLimiter(1.0 /* seconds */);

	/**
	 * Contructs a new Wrist.
	 */
	public Wrist() {
		MotorController halMotor = RobotProvider.instance.getMotor(WRIST_MOTOR);
		if (!(halMotor instanceof CANSparkMax)) {
			log(Severity.ERROR, "Motor is not a CANSparkMax!");
			throw new IllegalStateException("Motor is not a CANSparkMax!");
		}
		motor = (CANSparkMax) halMotor;

		// stash the PIDController for convenience.  will update the PID values to the latest from the config
		// file each time we use the motor.
		pidController = motor.getPIDController();
		pidController.setFeedbackDevice(motor.getEncoder());

		// grab config values for PID.
		pGain = ConfigFileReader.getInstance().getDouble(WRIST_PGAIN);
		iGain = ConfigFileReader.getInstance().getDouble(WRIST_IGAIN);
		dGain = ConfigFileReader.getInstance().getDouble(WRIST_DGAIN);
		ffGain = ConfigFileReader.getInstance().getDouble(WRIST_FFGAIN);
	}

	/**
	 * Returns the current angle of the wrist.
	 */
	public double getAngle() {
		return EncoderUtils.wristEUTodegrees(motor.getEncoder().getPosition());
	}

	/** 
	 * Rotates the wrist to a pre-set {@link Position}.
	 */
	public void rotate(Position position) {
		rotate(position.getAngle());
	}

	/**
	 * Starts rotating the wrist to the specified angle.
	 * NOTE: this method returns immediately.  Check the current wrist position of the wrist
	 * with {@link #getAngle()}.
	 */
	public void rotate(double angle) {
		checkContextOwnership();

		// set the PID controller values with whatever the latest is in the config
		pidController.setP(pGain.get());
		pidController.setI(iGain.get());
		pidController.setD(dGain.get());
		pidController.setFF(ffGain.get());

		pidController.setOutputRange(-1, 1);

		// convert the desired target degrees to encoder units
		double encoderUnits = EncoderUtils.wristDegreesToEU(angle);

		// set the reference point for the wrist
		pidController.setReference(encoderUnits, ControlType.kPosition);
	}

	@Override
	public void run() {
		if (rateLimiter.next()) {
			SmartDashboard.putNumber("WristAngle", getAngle());
		}
	}
}