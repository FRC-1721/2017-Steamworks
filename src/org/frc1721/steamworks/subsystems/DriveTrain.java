package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.*;
import org.frc1721.steamworks.commands.DriveInTeleop;
import org.frc1721.steamworks.subsystems.NavxController;
import org.frc1721.steamworks.CustomRobotDrive.GyroMode;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;
/**
 *
 */
public class DriveTrain extends Subsystem {
	
	
	protected CustomRobotDrive m_robotDrive;
	protected NavxController m_navController;
	protected GyroMode gyroMode = GyroMode.off;
	
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	
		setDefaultCommand(new DriveInTeleop());
    }
    
    
	public DriveTrain(CustomRobotDrive robotDrive, NavxController navController) {
		super("DriveTrain");
		m_robotDrive = robotDrive;
		m_navController = navController;
	}

	public void setGyroMode (GyroMode gMode) {
		if (gyroMode != gMode) {
			gyroMode = gMode;
			if (gyroMode == GyroMode.off) {
				m_navController.disable();
			} else if (gyroMode == GyroMode.heading) {
				m_navController.reset();
				m_navController.enable();
				m_navController.setPIDSourceType(PIDSourceType.kDisplacement);
				CustomPIDController gyroController = m_navController.getPIDController();
				gyroController.setPID(RobotMap.navP, RobotMap.navI, RobotMap.navD, RobotMap.navF);

			} else {
				m_navController.reset();
				m_navController.enable();
				m_navController.setPIDSourceType(PIDSourceType.kRate);
				CustomPIDController gyroController = m_navController.getPIDController();
				gyroController.setPID(RobotMap.navRateP, RobotMap.navRateI, RobotMap.navRateD, RobotMap.navRateF);
			}
		}
		m_robotDrive.setGyroMode(gMode); 
	}	
	
	public void jInput(Joystick stick) {
		// I don't think we need this `-Math.abs(stick.getY())*stick.getY()` anymore?
		m_robotDrive.arcadeDrive(stick.getY(), stick.getTwist(), true, false);
	}
	
	public void stop() {
		m_robotDrive.drive(0, 0);
	}
	
    
}

