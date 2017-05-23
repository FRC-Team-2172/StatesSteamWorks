package org.usfirst.frc.team2172.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

public class Driver extends RobotDrive{
	private CANTalon drive1;
	private CANTalon drive2;
	private CANTalon drive3;
	private CANTalon drive4;
	private ADXRS450_Gyro gyro;
	private Timer timer;
	private static double x = 0;
	private static double y = 0;
	private static double displacement = 0;
	public static double displacement2;
	private static boolean gearAligned;
	public static boolean hasAutoRun = false;
	private double leftSpeed;
	private double rightSpeed;
	
	
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
	public void alignGearPeg(){
		boolean gearAligned = false;
		
		
	}
	public double distance(double x){
		double velocity = (drive1.getSpeed()-drive2.getSpeed())/2.0;
	    x = x + velocity * 0.01;
		return velocity;
	}
	public static void encoderThread(CANTalon driv1, CANTalon driv2){
		displacement2 = 0.0;
		Thread t1 = new Thread(() -> {
			//gyro.reset();
	        while (!Thread.interrupted()) {
	        	//timer.reset();
	    		//double speed = (driv1.getEncVelocity() - driv2.getEncVelocity())/2.0*0.82*3.1415926/210.0;
	    		//x = x + speed * Math.cos(gyro.getAngle()%360 * 3.14 / 180.0) * 0.01;//timez.get();
	    		//y = y + speed * Math.sin(gyro.getAngle()%360 * 3.14 / 180.0) * 0.01;//times.get();
	    		//displacement = Math.sqrt(Math.pow(y,2) +Math.pow(x,2));
	    		displacement2 = displacement2 + (((Math.abs(driv1.getEncVelocity()) + Math.abs(driv2.getEncVelocity())/2.0)*0.01)/210);
	    		Timer.delay(0.01);
	        }
	    });
	    t1.start();
	    
	}
	public double displacement(){
		return displacement;
	}
	public boolean gearAligned(){
		return gearAligned;
	}
	public void straightDrive(double speed){
		if (hasAutoRun == false){
			leftSpeed = speed * 0.9;
			rightSpeed = speed * 0.9;
			speed = speed *0.9;
			this.drive1.set(leftSpeed);
			this.drive2.set(rightSpeed);
			this.drive3.set(-speed);
			this.drive4.set(-speed);
		}
		hasAutoRun = true;
		/*if (leftSpeed + rightSpeed > (2.0 * speed)){
			if (drive1.getEncVelocity() - 10.0 > drive2.getEncVelocity()){
				leftSpeed = leftSpeed - 0.005;
				this.drive1.set(leftSpeed);
			}else if (drive1.getEncVelocity() + 10.0 < drive2.getEncVelocity()){
				rightSpeed = rightSpeed - 0.005;
				this.drive2.set(rightSpeed);
			}
		}else{*/
			if (drive1.getEncVelocity() - 5.0 > drive2.getEncVelocity()){
				rightSpeed = rightSpeed + 0.005;
				this.drive2.set(rightSpeed);
			}else if (drive1.getEncVelocity() + 5.0 < drive2.getEncVelocity()){
				leftSpeed = leftSpeed + 0.005;
				this.drive1.set(leftSpeed);
			}
		//}
		this.drive1.set(leftSpeed);
		this.drive2.set(rightSpeed);
	}
}
