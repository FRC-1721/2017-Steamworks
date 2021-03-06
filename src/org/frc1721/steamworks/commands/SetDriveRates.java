package org.frc1721.steamworks.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.CustomRobotDrive.GyroMode;
/**
 *
 */
public class SetDriveRates extends Command {

	protected boolean complete = false;
	protected double m_driveRate;
	protected double m_turnRate;

	
    public SetDriveRates(double driveRate, double turnRate) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	m_driveRate = driveRate;
    	m_turnRate = turnRate;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	complete = false;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.driveTrain.setDriveScale(m_driveRate, m_turnRate);
    	complete = true;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return complete;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
