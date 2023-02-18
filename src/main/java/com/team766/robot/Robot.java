package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static CANdleMech candle;

	public static void robotInit() {
		// Initialize mechanisms here
		candle = new CANdleMech(8, 16, 16);
	}
}
