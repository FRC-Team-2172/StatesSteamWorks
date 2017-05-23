package org.usfirst.frc.team2172.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	private CANTalon shooter;
	private CANTalon positioner;
	private double cAngle;
	
	public Shooter(CANTalon shooter, CANTalon positioner){
		this.shooter = shooter;
		this.positioner = positioner;
		
		this.shooter.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.shooter.reverseSensor(false);
		this.shooter.configEncoderCodesPerRev(1440);
		this.shooter.configNominalOutputVoltage(+0.0f, -0.0f);
		this.shooter.configPeakOutputVoltage(+12.0f, -12.0f);
		this.shooter.setProfile(0);
		this.shooter.setF(0.1097);
		this.shooter.setP(0.22);
		this.shooter.setI(0); 
		this.shooter.setD(0);
		this.shooter.changeControlMode(TalonControlMode.Speed);
		
		this.positioner.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.positioner.reverseSensor(false);
		this.positioner.configEncoderCodesPerRev(1440);
		this.positioner.configNominalOutputVoltage(+0.0f, -0.0f);
		this.positioner.configPeakOutputVoltage(+12.0f, -12.0f);
		this.positioner.setProfile(0);
	}
	
	public void enable(){
		//this.shooter.set(312);
		this.shooter.set(2000);
	}
	
	public void disable(){
		this.shooter.set(0);
	}
	
	public void shoot(USBArduinoCamera camera){
		double p = 0.2;
		double d = 0.0254*79.5/Math.tan(camera.getY()*0.325 * (Math.PI/180));
		double tAngle = Math.atan((Math.pow(p*9.44, 2)-Math.sqrt(Math.pow(p*9.44, 4)-Math.pow(9.8*d, 2)-(2*9.8*(2.743-0.45)*Math.pow(p*9.44, 2))))/(9.8*d));
		
		if(this.cAngle > tAngle){
			this.positioner.set(0.5);
			this.cAngle = (Math.PI/2) - (0.454 - ((2*Math.PI)*(0.25*this.positioner.getEncPosition()/90)/16.0/(9.4 * Math.PI))); //There is a small chance I fucked up Q
		} else if(this.cAngle < tAngle){
			this.positioner.set(-0.5);
			this.cAngle = (Math.PI/2) - (0.454 - ((2*Math.PI)*(0.25*this.positioner.getEncPosition()/90)/16.0/(9.4 * Math.PI))); //There is a small chance I fucked up Q
		} else {
			this.positioner.set(0);
		}
	}
	
	public void shoot(double tAngle){
		SmartDashboard.putNumber("Positioner Target Angle", tAngle);
		SmartDashboard.putNumber("Positioner Current Angle", cAngle);
		if(this.cAngle > tAngle){
			this.positioner.set(0.5);
			this.cAngle = (Math.PI/2) - (0.454 - ((2*Math.PI)*((0.25*this.positioner.getEncPosition()/90)/16.0/(9.4 * Math.PI)))); //There is a small chance I fucked up Q
		} else if(this.cAngle < tAngle){
			this.positioner.set(-0.5);
			this.cAngle = (Math.PI/2) - (0.454 - ((2*Math.PI)*((0.25*this.positioner.getEncPosition()/90)/16.0/(9.4 * Math.PI)))); //There is a small chance I fucked up Q
		} else {
			this.positioner.set(0);
		}
	}
	
	public void callibrate(){
		/*
		boolean callibrated = false;
		this.positioner.set(0.5);
		Timer.delay(0.05);
		double amp = this.positioner.getOutputCurrent();
		while(!callibrated){
			this.positioner.set(0.5);
			SmartDashboard.putNumber(Math.abs(this.positioner.getOutputCurrent() - amp)/amp);
			
			if(Math.abs(this.positioner.getOutputCurrent() - amp)/amp > 1.2){
				callibrated = true;
				this.positioner.setEncPosition(0);
				this.positioner.set(0);
				this.cAngle = (Math.PI/2) - 0.454;
			} else {
				amp = (amp + this.positioner.getOutputCurrent())/2;
			}
		}
		*/
		this.positioner.setEncPosition(0);
		this.cAngle = (Math.PI/2) - 0.454; 
	}
	
	public void test(){
		this.enable();
		this.shooter.set(10000);
		DriverStation.reportWarning("" + this.shooter.getSpeed(), false);
	}
}
