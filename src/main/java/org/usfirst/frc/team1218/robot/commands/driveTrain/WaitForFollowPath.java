package org.usfirst.frc.team1218.robot.commands.driveTrain;

import org.usfirst.frc.team1218.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class WaitForFollowPath extends Command {

    public WaitForFollowPath() {
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    		System.out.print("WaitForFollowPath: Wating for follow Path");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !Robot.driveTrain.isPathFollowing();
    }

    // Called once after isFinished returns true
    protected void end() {
    		System.out.print("WaitForFollowPath: Follow Path Completed");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	System.out.print("WaitForFollowPath: Interrupted");
    }
}
