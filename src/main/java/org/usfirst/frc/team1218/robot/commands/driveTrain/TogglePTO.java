package org.usfirst.frc.team1218.robot.commands.driveTrain;

import org.usfirst.frc.team1218.robot.Robot;
import org.usfirst.frc.team1218.robot.commands.elevator.ElevatorDefault;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class TogglePTO extends InstantCommand{
	
	public TogglePTO() {
		super();
	}
	@Override
	public void execute() {
		boolean enablePto = Robot.driveTrain.isPtoEngaged();
		if(enablePto) {
			Robot.driveTrain.setBrake(NeutralMode.Coast);
			Robot.driveTrain.engagePto(false);
		}else {
			Robot.driveTrain.setBrake(NeutralMode.Brake);
			Robot.driveTrain.engagePto(true);
			new ElevatorDefault().start();
		}
		System.out.println("PTO Enable set to:" + !enablePto);
	}
}
