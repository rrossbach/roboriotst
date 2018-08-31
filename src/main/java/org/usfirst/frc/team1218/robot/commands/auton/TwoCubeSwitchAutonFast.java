	package org.usfirst.frc.team1218.robot.commands.auton;

import org.usfirst.frc.team1218.robot.Robot;
import org.usfirst.frc.team1218.robot.Robot.Plate;
import org.usfirst.frc.team1218.robot.RobotMap;
import org.usfirst.frc.team1218.robot.commands.arm.ActuateArm;
import org.usfirst.frc.team1218.robot.commands.arm.ActuateIntakeArm;
import org.usfirst.frc.team1218.robot.commands.arm.ActuateIntakeWheels;
import org.usfirst.frc.team1218.robot.commands.arm.DropPowerCube;
import org.usfirst.frc.team1218.robot.commands.arm.ShootPowerCube;
import org.usfirst.frc.team1218.robot.commands.driveTrain.FollowPath;
import org.usfirst.frc.team1218.robot.commands.driveTrain.TalonFollowPath;
import org.usfirst.frc.team1218.robot.commands.driveTrain.WaitForProfilePointsRemaining;
import org.usfirst.frc.team1218.robot.commands.elevator.ElevatorMotionMagicMove;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *
 */
public class TwoCubeSwitchAutonFast extends CommandGroup {

    public TwoCubeSwitchAutonFast(Plate plate) {
    		TalonFollowPath pathCmd;
    		addSequential(new SwitchAutonFast(plate));
    		if(plate == Plate.RIGHT) {
    			pathCmd = new TalonFollowPath(RobotMap.centerStartRightSwitchReversePath,true);
    		}else {
    			pathCmd = new TalonFollowPath(RobotMap.centerStartLeftSwitchReversePath,true);
    		}
    		addParallel(pathCmd);
    		addSequential(new WaitForProfilePointsRemaining(5));
    		addParallel(new ActuateArm(false));
    		addParallel(new ActuateIntakeArm(true));
    		addParallel(new ActuateIntakeWheels(1.0)); // First Cube is now "hopefully" in the switch
    		addParallel(new ElevatorMotionMagicMove(RobotMap.elevatorReverseLimit + 3));
    		addParallel(new TalonFollowPath(RobotMap.twoCubeSwichPickupPath,false));
    		addSequential(new WaitForProfilePointsRemaining(9));
    		addParallel(new ActuateIntakeArm(false));
    		addSequential(new TimedCommand(0.25));
    		addParallel(new ActuateIntakeWheels(0));
    		addParallel(new ActuateArm(true));
    		addParallel(new ElevatorMotionMagicMove(RobotMap.elevatorReverseLimit + 10));
    		addParallel(new TalonFollowPath(RobotMap.twoCubeSwichPickupPath,true));
    		addSequential(new WaitForProfilePointsRemaining(5));
    		addSequential(new SwitchAutonFast(plate));
    		
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    }
}
