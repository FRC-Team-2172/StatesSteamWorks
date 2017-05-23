package org.usfirst.frc.team2172.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

public class Driver extends RobotDrive{
	private CANTalon drive1;
	private CANTalon drive2;
	private CANTalon drive3;
	private CANTalon drive4;
	
	public Driver(CANTalon canTalon1, CANTalon canTalon2, CANTalon canTalon3, CANTalon canTalon4) {
		super(canTalon1, canTalon4, canTalon2, canTalon3);
		this.drive1 = canTalon1;
		this.drive2 = canTalon2;
		this.drive3 = canTalon3;
		this.drive4 = canTalon4;
		
		this.drive1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.drive2.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.drive3.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.drive4.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		this.drive1.reverseSensor(false);
		this.drive2.reverseSensor(false);
		this.drive3.reverseSensor(false);
		this.drive4.reverseSensor(false);
		
		this.drive1.configEncoderCodesPerRev(1440);
		this.drive2.configEncoderCodesPerRev(1440);
		this.drive3.configEncoderCodesPerRev(1440);
		this.drive4.configEncoderCodesPerRev(1440);
	}
	
	@Override
	public void arcadeDrive(double speed, double rotation){
		this.drive1.set(speed - rotation);
		this.drive2.set(speed + rotation);
		this.drive3.set(-speed);
		this.drive4.set(-speed);
	}
	
	public void drive(double speed1, double speed2, double speed3, double speed4){
		this.drive1.set(speed1);
		this.drive2.set(speed2);
		this.drive3.set(speed3);
		this.drive4.set(speed4);
	}
	
	public void align(USBArduinoCamera camera){
		boolean aligned = false;
		int protection = 0;
		do {
			if((camera.getX() + 1) > 168){
				//Turn Right
				this.drive(-0.6, 0, 0, 0.2);
			} else if((camera.getX() + 1) < 152) {
				//Turn Left
				this.drive(0.6, 0, 0, -0.2);
			} else {
				this.drive(0, 0, 0, 0);
			}
			aligned = (((camera.getX() + 1) > 168) && ((camera.getX() + 1) < 152) || protection > 600);
			protection++;
		} while(!aligned);
		
		this.drive(0, 0, 0, 0);
	}
	
	public void goTo(Point point, Navigator navigator){
		Point[] path = navigator.generatePath(point, Math.PI/4);
	}
}
