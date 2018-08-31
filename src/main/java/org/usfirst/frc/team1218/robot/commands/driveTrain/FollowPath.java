package org.usfirst.frc.team1218.robot.commands.driveTrain;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.usfirst.frc.team1218.robot.Robot;
import org.usfirst.frc.team1218.robot.RobotMap;
import org.usfirst.frc.team1218.robot.subsystems.DriveTrain;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Trajectory;
import com.team254.lib.trajectory.Trajectory.Segment;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Test command to have the drive train follow a generated motion profile
 */
public class FollowPath extends Command {

	private Notifier processThread;
	private boolean useTalonMPMode, runBACKWARDS;
	private enum FollowerState { Waiting, Starting, Running, Interrupting, Done };
	private AtomicReference<FollowerState> state = new AtomicReference<FollowerState>(FollowerState.Waiting);
	private ArrayList<Segment> leftVelPts, rightVelPts;
	private double dtSeconds;
	
	private class PointExecutor implements Runnable {
		private long startTime;
		private int step = 0;

		@SuppressWarnings("unused")
		private Segment invertSegment(Segment s) {
			return new Segment(-s.pos, -s.vel, -s.acc, -s.jerk, s.heading, s.dt, s.x, s.y);
		}
		
		public void run() {
	    	if (state.compareAndSet(FollowerState.Starting, FollowerState.Running)) {
	    		System.out.println("Notifier Initalized");
	    		Robot.driveTrain.startLogging();
	    		startTime = System.currentTimeMillis();
	    	}
	    	step = (int)((System.currentTimeMillis() - startTime) / (long)(dtSeconds * 1000));
	    	//System.out.print("step: " + step);
	    	try {
	    		//Robot.driveTrain.shift(true);
	    		if (state.get() == FollowerState.Interrupting) throw new Exception("Interrupting profile");
	    		if (DriverStation.getInstance().isDisabled()) throw new Exception("Robot Disabled");
	    		if (runBACKWARDS){
	    			Robot.driveTrain.setVelocity(DriveTrain.ftPerSecToEncVel(rightVelPts.get(step).vel), 
	    										DriveTrain.ftPerSecToEncVel(leftVelPts.get(step).vel));	
	    		} else {
	    			Robot.driveTrain.setVelocity(DriveTrain.ftPerSecToEncVel(leftVelPts.get(step).vel), 
												DriveTrain.ftPerSecToEncVel(rightVelPts.get(step).vel));	
	    		}
	    	} catch (Exception e) {
	    		System.out.println("PointExecutor caught exception " + e.getMessage() + ", stopping Notifier");
	    		processThread.stop();
	    		Robot.driveTrain.stopLogging();
	    		state.set(FollowerState.Done);
	    	}
		}
	}
	
	public FollowPath() {
    	requires(Robot.driveTrain);
    	runBACKWARDS = false;
		leftVelPts = new ArrayList<Segment>();
		rightVelPts = new ArrayList<Segment>();
    	processThread = new Notifier(new PointExecutor());
	}
 
	public void setPath(Path p, boolean useTalonMPMode) {
    	if (state.get() != FollowerState.Waiting) {
    		System.out.println("FollowPath:  setPath() called while already running, ignoring");
    		return;
    	}
		this.useTalonMPMode = useTalonMPMode;		
    	leftVelPts.clear();
    	rightVelPts.clear();
    	//store the velocity pts
		int numPoints = p.getLeftWheelTrajectory().getNumSegments();
		Trajectory lt = p.getLeftWheelTrajectory(),
				   rt = p.getRightWheelTrajectory();
		for (int i = 0; i < numPoints; i++) {
			leftVelPts.add(lt.getSegment(i));
			rightVelPts.add(rt.getSegment(i));
			if (i==0) dtSeconds = lt.getSegment(i).dt;
		}

	}
	
    // Called just before this Command runs the first time
    protected void initialize() {
    	//Robot.driveTrain.loadPIDFConstants(RobotMap.leftLowGearPIDF, RobotMap.rightLowGearPIDF);
    	if (state.compareAndSet(FollowerState.Waiting,FollowerState.Starting)) {
        	System.out.println("starting FollowPath command");
    		processThread.startPeriodic(dtSeconds / 2.0);
    	} else {
    		System.out.println("FollowPath.initialize() exepcted WAITING but found " + state.get());
    		System.out.println("\tcannot set STARTING");
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        if (state.compareAndSet(FollowerState.Done, FollowerState.Waiting)) {
        	System.out.println("finished FollowPath command");
        	return true;
        } else {
        	return false;
        }
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	if (state.compareAndSet(FollowerState.Running, FollowerState.Interrupting)) {
    		System.out.println("Interrupting FollowPath");
    	} else {
    		System.out.println("FollowPath.interrupted() expected RUNNING but found " + state.get());
    		System.out.println("\tcannot set INTERRUPTING");
    	}
    	Robot.driveTrain.stopLogging();
    }
}

