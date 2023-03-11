package com.team766.robot.mechanisms;

import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.EncoderReader;
//This is for the motor that controls the pulley
public class Arms extends Mechanism {
    
    private MotorController firstJoint = RobotProvider.instance.getMotor("arms.firstJoint");
    private CANSparkMax firstJointCANSparkMax = (CANSparkMax)firstJoint;
    private SparkMaxPIDController firstJointPIDController  = firstJointCANSparkMax.getPIDController();

    private MotorController secondJoint = RobotProvider.instance.getMotor("arms.secondJoint");
    private CANSparkMax secondJointTest = (CANSparkMax)secondJoint;
    private SparkMaxPIDController secondJointPID = secondJointTest.getPIDController();


    

    public Arms(){
        /*
        Please dont actually use these pid values rn bc they havent been tested!!!!
        */
        
        firstJointPIDController.setP(0);
        firstJointPIDController.setI(0);
        firstJointPIDController.setD(0);
        firstJointPIDController.setFF(0);
        firstJointPIDController.setSmartMotionMaxVelocity(1250, 0);
        firstJointPIDController.setSmartMotionMinOutputVelocity(0, 0);
        firstJointPIDController.setSmartMotionMaxVelocity(750, 0);
        firstJointPIDController.setOutputRange(-0.75, 0.75);


        secondJointPID.setP(0.00009999998292187229);
        secondJointPID.setI(0);
        secondJointPID.setD(0);
        secondJointPID.setFF(0.00029999998514540493);
        secondJointPID.setSmartMotionMaxVelocity(2500, 0);
        secondJointPID.setSmartMotionMinOutputVelocity(0, 0);
        secondJointPID.setSmartMotionMaxAccel(1500, 0);
        secondJointPID.setOutputRange(-1, 1);



        
        
    }


    //This allows the pulley motor power to be changed, usually manually
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction

    public void addArms(MotorController motor1, MotorController motor2){
        firstJoint = motor1;
        secondJoint = motor2;

        firstJointCANSparkMax = (CANSparkMax)firstJoint;
        secondJointTest = (CANSparkMax)secondJoint;
    }

    public void manuallySetArmOnePower(double power) {
        checkContextOwnership();
        firstJoint.set(power);
    }
    // Getter method for getting the first arms encoder distance
    public double getEncoderDistanceOfArmOne() {
        return firstJoint.getSensorPosition();
    }
    // resetting the encoder distance to zero for use without absolutes
    public void resetEncoders(){
        checkContextOwnership();
        firstJoint.setSensorPosition(0);
        secondJoint.setSensorPosition(0);
    }

	// PID for first arm
    public void pidForArmOne(double value){
        firstJointPIDController.setReference(value, CANSparkMax.ControlType.kSmartMotion);
        log(" he" + value );
        
    }
	// PID for second arm
    public void pidForArmTwo(double height_encoderUnits){
        
        secondJointPID.setReference(height_encoderUnits, CANSparkMax.ControlType.kSmartMotion);
        
    }
	
	// resetting time for use with the I in PID.
	
	// getter method for getting the encoder position of arm 2
    public double getEncoderDistanceOfArmTwo(){
        return secondJoint.getSensorPosition();
    }
	// antigrav
    public void holdArms(){ // Use Encoder Units to Radians in the sine
        firstJoint.set((-Math.sin((Math.PI / 88) * firstJoint.getSensorPosition())) * .021);
        secondJoint.set((-Math.sin((Math.PI / 88)* secondJoint.getSensorPosition())) * .011);
        

    }

	//changing degrees to encoder units for the non absolute encoder
    public double degreesToEU(double angle) {
        return angle * (44.0 / 90);
    }
	
	// manual changing of arm 2.
    public void manuallySetArmTwoPower(double power){
        checkContextOwnership();
        secondJoint.set(power);
    }

    
        
    

    
    }


/* ~~ Code Review ~~
    Use Voltage Control Mode when setting power (refer to CANSparkMaxMotorController.java)

    Maybe use Nicholas's formula for degrees to EU
    "Use break mode" - Ronald te not programmer

 */
