
package org.frc1721.steamworks;

import org.frc1721.steamworks.subsystems.Climber;
import org.frc1721.steamworks.subsystems.DriveTrain;
import org.frc1721.steamworks.subsystems.NavxController;
import org.frc1721.steamworks.subsystems.Shooter;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	public static OI oi;
	
	public static DigitalInput topLimitSwitch;
	public static DigitalInput bottomLimitSwitch;
	public static DigitalInput gearLimitSwitch;
	
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
		RobotMap.dtLeft = new VictorSP(RobotMap.dtlPWM);
		RobotMap.dtRight = new VictorSP(RobotMap.dtrPWM);
		RobotMap.lLift = new VictorSP(RobotMap.liftPWM);
		//RobotMap.dtRight.setInverted(true);
		// Encoders
		RobotMap.dtlEnc = new Encoder(RobotMap.dtlEncPA, RobotMap.dtlEncPB, RobotMap.dtrEncL);
		RobotMap.dtrEnc = new Encoder(RobotMap.dtrEncPA, RobotMap.dtrEncPB, RobotMap.dtlEncR);
		RobotMap.dtlEnc.setDistancePerPulse(0.0074536447630841);
		RobotMap.dtrEnc.setDistancePerPulse(0.0074074074074074); // TODO, move to RobotMap
		
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
	    
	    topLimitSwitch = new DigitalInput(RobotMap.topLs);
	    bottomLimitSwitch = new DigitalInput(RobotMap.bottomLs);
	    gearLimitSwitch = new DigitalInput(RobotMap.gearLs);
	    
//	    new Thread(() -> {
//            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
//            camera.setResolution(640, 480);
//            
//            CvSink cvSink = CameraServer.getInstance().getVideo();
//            CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
//            
//            Mat source = new Mat();
//            Mat output = new Mat();
//            
//            while(!Thread.interrupted()) {
//                cvSink.grabFrame(source);
//                Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
//                outputStream.putFrame(output);
//            }
//        }).start();
	    
	    // Create the OI
	    oi = new OI();
	}

	@Override
	public void disabledInit() {
	}

	@Override
	public void disabledPeriodic() {
		RobotMap.lLift.set(0);
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
		// Gyro is only reset when the  mode changes, so shut the it off then back on in case teleop
		// is started multiple times.
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.rate);
	}

	@Override
	public void robotPeriodic() {

		if(topLimitSwitch.get())
		{
			RobotMap.lLift.set(0.1);
		}
		
		if(bottomLimitSwitch.get())
		{
			RobotMap.lLift.set(-0.1);
		}
		
		printSmartDashboard();
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		LiveWindow.run();
<<<<<<< HEAD

	}
	
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
	
	private void printSmartDashboard ()
	{
		// Limit switch stuff
		SmartDashboard.putBoolean("Gear Limit Switch", gearLimitSwitch.get());
		SmartDashboard.putBoolean("Top Limit Switch", topLimitSwitch.get());
		SmartDashboard.putBoolean("Bottom Limit Switch", bottomLimitSwitch.get());
		
		// Navx stuff
//		SmartDashboard.putNumber("Yaw",RobotMap.navx.getYaw());
//		SmartDashboard.putNumber("Angle",RobotMap.navx.getAngle());
//		SmartDashboard.putNumber("CompassHeading",RobotMap.navx.getCompassHeading());
//		SmartDashboard.putNumber("Altitude",RobotMap.navx.getAltitude());
//		SmartDashboard.putNumber("DisplacementX",RobotMap.navx.getDisplacementX());
//		SmartDashboard.putNumber("DisplacementY",RobotMap.navx.getDisplacementY());
//		SmartDashboard.putNumber("DisplacementZ",RobotMap.navx.getDisplacementZ());
//		SmartDashboard.putNumber("Roll",RobotMap.navx.getRoll());
=======
		SmartDashboard.putNumber("Yaw", RobotMap.navx.getYaw());
//		SmartDashboard.putNumber("Angle", RobotMap.navx.getAngle());
//		SmartDashboard.putNumber("CompassHeading", RobotMap.navx.getCompassHeading());
//		SmartDashboard.putNumber("Altitude", RobotMap.navx.getAltitude());
//		SmartDashboard.putNumber("DisplacementX", RobotMap.navx.getDisplacementX());
//		SmartDashboard.putNumber("DisplacementY", RobotMap.navx.getDisplacementY());
//		SmartDashboard.putNumber("DisplacementZ", RobotMap.navx.getDisplacementZ());
//		SmartDashboard.putNumber("Roll", RobotMap.navx.getRoll());
>>>>>>> branch 'pid' of https://github.com/FRC-1721/2017-Steamworks.git
				
<<<<<<< HEAD
		// Controller stuff
//		SmartDashboard.putNumber("Joystick One YAxis", OI.jsticks[0].getY());
//		SmartDashboard.putNumber("Joystick One Twist", OI.jsticks[0].getTwist());
//		SmartDashboard.putNumber("Joystick Two YAxis", OI.jsticks[1].getTwist());
		
		// PID stuff
=======
		
		SmartDashboard.putNumber("Joystick One YAxis", OI.jsticks[0].getY());
		SmartDashboard.putNumber("Joystick One Twist", OI.jsticks[0].getTwist());		
		
>>>>>>> branch 'pid' of https://github.com/FRC-1721/2017-Steamworks.git
		SmartDashboard.putBoolean("PID", Robot.robotDrive.getPIDStatus());
	}
}
