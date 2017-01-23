
package org.frc1721.steamworks;

import org.frc1721.steamworks.subsystems.*;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.command.*;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;

import com.kauailabs.navx.frc.AHRS;

public class Robot extends IterativeRobot {

	public static OI oi;
	
	/** 
	 * Subsystems List
	 * In order to create a new subsystem this list must be appended.
	 * Also note that you must initialize your subsystem in robotInit() 
	 */
	public static Climber climber;
	public static Shooter shooter;
	public static DriveTrain driveTrain;
	public static CustomRobotDrive robotDrive;
	public static NavxController navController;
	@Override
	public void robotInit() {
		/* Initialize the Drive Train systems */
		
		// Motor Controllers
		RobotMap.dtLeft = new Victor(RobotMap.dtlPWM);
		RobotMap.dtRight = new Victor(RobotMap.dtrPWM);
		//RobotMap.dtRight.setInverted(true);
		// Encoders
		RobotMap.dtlEnc = new Encoder(RobotMap.dtlEncPA, RobotMap.dtlEncPB);
		RobotMap.dtrEnc = new Encoder(RobotMap.dtrEncPA, RobotMap.dtrEncPB);
		RobotMap.dtlEnc.setDistancePerPulse(0.00727);
		RobotMap.dtrEnc.setDistancePerPulse(0.00727); // ToDo, move to RobotMap
		
		// PID Controllers
		RobotMap.dtLeftController = new CustomPIDController(RobotMap.dtP, RobotMap.dtI, 
				RobotMap.dtD, RobotMap.dtF, RobotMap.dtlEnc, RobotMap.dtLeft, 0.01);
		RobotMap.dtRightController = new CustomPIDController(RobotMap.dtP, RobotMap.dtI, 
				RobotMap.dtD, RobotMap.dtF, RobotMap.dtrEnc, RobotMap.dtRight, 0.01);
		RobotMap.dtLeftController.setPIDSourceType(PIDSourceType.kRate);
		RobotMap.dtRightController.setPIDSourceType(PIDSourceType.kRate);
		

		//robotDrive.setInvertedMotor(robotDrive.MotorType.kFrontRight, true);
		
		// Gyro and controller
        RobotMap.navx = new AHRS(SPI.Port.kMXP, RobotMap.navUpdateHz); 
        navController = new NavxController("HeadingController", RobotMap.navP, RobotMap.navI, RobotMap.navD,
        		RobotMap.navF, RobotMap.navx, PIDSourceType.kDisplacement);
		
        // Add the drive train last since it depends on robotDrive and navController
		//Drive System
		robotDrive = new CustomRobotDrive(RobotMap.dtLeft, RobotMap.dtRight,
						RobotMap.dtLeftController, RobotMap.dtRightController,
						navController);
		//robotDrive.stopMotors();
        
        driveTrain = new DriveTrain(robotDrive, navController);
        driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
        
		/* Add items to live windows */
        LiveWindow.addSensor("Gyro", "navx", RobotMap.navx);
		LiveWindow.addActuator("LeftRobotDrive", "Victor", RobotMap.dtLeft);
		LiveWindow.addActuator("RightRobotDrive", "Victor", RobotMap.dtRight);
		LiveWindow.addSensor("LeftRobotDrive", "Encoder", RobotMap.dtlEnc);
	    LiveWindow.addSensor("RightRobotDrive", "Encoder", RobotMap.dtrEnc);
	    LiveWindow.addActuator("LeftRobotDrive", "Controller", RobotMap.dtLeftController);
	    LiveWindow.addActuator("RightRobotDrive", "Controller", RobotMap.dtRightController);
	    
	    // Create the OI
	    oi = new OI();
	}

	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		robotDrive.enablePID();
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.rate);
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		LiveWindow.run();
		SmartDashboard.putNumber("Yaw",RobotMap.navx.getYaw());
//		SmartDashboard.putNumber("JoystickXAxis", OI.jstick.getX());
//		SmartDashboard.putNumber("JoystickYAxis", OI.jstick.getTwist());
	}

	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
