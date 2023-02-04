package com.team766.robot.mechanisms;

import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.EncoderReader;
//This is for the motor that controls the pulley
public class Pulley extends Mechanism {
    //This enables the code to interact with the motor that controls the pulley
    private MotorController elevator;
    private EncoderReader elevatorReader;
    private PIDController pid;

    // private EncoderReader pulleyEncoder;

    public Pulley() {
        elevator = RobotProvider.instance.getMotor("elevator");
        pid = new PIDController(.00019493,.000018494,0, 0, -.2, .2, 110 );


    }
    //This allows the pulley motor power to be changed
    //The magnitude ranges from 0.0-1.0, and sign (positive/negative) determines the direction
    public void setPulleyPower(double power) {
        checkContextOwnership();
        elevator.set(power);
    }

    public double getEncoderDistance() {
        return elevator.getSensorPosition();
    }
    public void resetEncoder(){
        checkContextOwnership();
        elevator.setSensorPosition(0);
    }
	public void setPosition(double position){
        //checkContextOwnership();
		while(elevator.getSensorPosition() != position){
			if(elevator.getSensorPosition() < position){
				elevator.set(.17);
			} else if(elevator.getSensorPosition()> position){
				elevator.set(-.17);
			}
			elevator.set(0);
		}
	}
    public void pidtest(){
        pid.setSetpoint(0);
        pid.calculate(elevator.getSensorPosition());
        elevator.set(pid.getOutput());
        log("" + elevator.getSensorPosition());
    }
    public void reset(){
        pid.reset();
    }

}