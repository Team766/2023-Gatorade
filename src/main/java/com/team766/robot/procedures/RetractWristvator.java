package com.team766.robot.procedures;

import com.team766.robot.mechanisms.Elevator;
import com.team766.robot.mechanisms.Shoulder;
import com.team766.robot.mechanisms.Wrist;

public class RetractWristvator extends MoveWristvator {

	public RetractWristvator() {
		super(Shoulder.Position.BOTTOM, Elevator.Position.RETRACTED, Wrist.Position.RETRACTED);
	}
}
