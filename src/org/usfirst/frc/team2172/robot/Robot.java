package org.usfirst.frc.team2172.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;


public class Robot extends IterativeRobot {
	//Motor Declarations 
	CANTalon drive1 = new CANTalon(1);
	CANTalon drive2 = new CANTalon(2);
	CANTalon drive3 = new CANTalon(3);
	CANTalon drive4 = new CANTalon(4);
	CANTalon shoot = new CANTalon(8);
	CANTalon positioner =  new CANTalon(9);
	CANTalon intake = new CANTalon(6);
	CANTalon auger = new CANTalon(10);
	CANTalon feeder = new CANTalon(5);
	CANTalon climber = new CANTalon(7);
	PWM feedServo = new PWM(0);
	Timer fieldTimer = new Timer();
	REVDigitBoard digit = new REVDigitBoard();
	int autoMode = 1;
	
	//Robot Driver
	Driver driver = new Driver(drive1, drive2, drive3, drive4);
	Navigator navigator = new Navigator(drive1, drive2, new Point(0, 0, Math.PI/2));
	Joystick xBox = new Joystick(1);
	Joystick gamepad = new Joystick(2);
	
	//Robot Shooter
	Shooter shooter = new Shooter(shoot, positioner);
	//USBArduinoCamera pixy = new USBArduinoCamera(new SerialPort(115200, Port.kUSB1));
	
	@Override
	public void robotInit() {
		shooter.callibrate();
		feedServo.setBounds(3, 3, 2, 1, 1);
		CameraServer.getInstance().startAutomaticCapture();
		drive1.setPosition(0);
		drive2.setPosition(0);
		drive3.setPosition(0);
		drive4.setPosition(0);
		digit.clear();
		Thread autoChoose = new Thread(() -> {
			/*int tAutoMode = autoMode;
			double aMax = 214;
			double aMin = 198;
			while(!Thread.interrupted()){
				digit.display("" + tAutoMode + "A");
				int pot = digit.getPot();
				tAutoMode = (int)Math.round(((pot - aMin)/(aMax - aMin) * 2));
				SmartDashboard.putNumber("Auto Mode", autoMode);
				if (!digit.getButtonA()) {
					autoMode = tAutoMode;
					for (int i = 0; i < 5; i++) {
						digit.clear();
						Timer.delay(0.3);
						digit.display("" + tAutoMode + "A");
						Timer.delay(0.3);
					}
				}
				if(!digit.getButtonB()){
					while(!digit.getButtonB()){
						tAutoMode++;
						autoMode = tAutoMode;
					}
				}
			}*/
			/*while (!Thread.interrupted()) {
				if (!digit.getButtonB()) {
					Timer.delay(0.03);
					if (!digit.getButtonA()) {
						autoMode = tAutoMode;
						for (int i = 0; i < 5; i++) {
							digit.clear();
							Timer.delay(0.3);
							digit.display("--" + tAutoMode + "A");
							Timer.delay(0.3);
						}
					}
					else {
						if (tAutoMode == 3) {
							tAutoMode = 1;
						}
						else {
							tAutoMode++;
						}
						String selected = tAutoMode == autoMode ? "--" : "";
						digit.display(selected + tAutoMode + "A");
						Timer.delay(0.1);
					}
				}
				else if (!digit.getButtonA()) {
					Timer.delay(0.03);
					if (!digit.getButtonB()) {
						autoMode = tAutoMode;
						for (int i = 0; i < 5; i++) {
							digit.clear();
							Timer.delay(0.3);
							digit.display("--" + tAutoMode + "A");
							Timer.delay(0.3);
						}
					}
					else {
						if (tAutoMode == 1) {
							tAutoMode = 3;
						}
						else {
							tAutoMode--;
						}
						String selected = tAutoMode == autoMode ? "--" : "";
						digit.display(selected + tAutoMode + "A");
						Timer.delay(0.1);
					}
				}
			}*/
			int tAutoMode = autoMode;
			digit.display("--" + tAutoMode + "A");
			while (!Thread.interrupted()) {
				if (!digit.getButtonA()) {
					Timer.delay(0.05);
					if (!digit.getButtonB()) {
						autoMode = tAutoMode;
						for (int i = 0; i < 5; i++) {
							digit.clear();
							Timer.delay(0.3);
							digit.display("--" + tAutoMode + "A");
							Timer.delay(0.3);
						}
					}
					else {
						while (!digit.getButtonA()) {
							Timer.delay(0.05);
						}
						if (tAutoMode == 1) {
							tAutoMode = 3;
						}
						else {
							tAutoMode--;
						}
						String selected = tAutoMode == autoMode ? "--" : "";
						digit.display(selected + tAutoMode + "A");
						Timer.delay(0.1);
					}
				}
				else if (!digit.getButtonB()) {
					Timer.delay(0.05);
					if (!digit.getButtonA()) {
						autoMode = tAutoMode;
						for (int i = 0; i < 5; i++) {
							digit.clear();
							Timer.delay(0.3);
							digit.display("--" + tAutoMode + "A");
							Timer.delay(0.3);
						}
					}
					else {
						while (!digit.getButtonB()) {
							Timer.delay(0.05);
						}
						if (tAutoMode == 3) {
							tAutoMode = 1;
						}
						else {
							tAutoMode++;
						}
						String selected = tAutoMode == autoMode ? "--" : "";
						digit.display(selected + tAutoMode + "A");
						Timer.delay(0.1);
					}
				}
			}
		});
		autoChoose.start();
	}

	@Override
	public void autonomousInit() {
		driver.arcadeDrive(-1.0 ,0.0);
		fieldTimer.start();
	}

	@Override
	public void autonomousPeriodic() {
		switch(autoMode){
			case 1:
				//Foreward and Center Gear
				if(fieldTimer.get() < .7){
					driver.arcadeDrive(-1.0 ,0.0);
				}else if(fieldTimer.get() < 2.3){
					driver.arcadeDrive(-0.5 ,0.0);
				}else{
					driver.drive(0, 0, 0, 0);
				}
				break;
			case 2:
				
				break;
			case 3:
				//Gear Left
				break;
			default:
				if(fieldTimer.get() < .7){
					driver.arcadeDrive(-1.0 ,0.0);
				}else if(fieldTimer.get() < 2.3){
					driver.arcadeDrive(-0.5 ,0.0);
				}else{
					driver.drive(0, 0, 0, 0);
				}
				break;
		}
	}
	
	@Override
	public void teleopInit() {
		
	}

	@Override
	public void teleopPeriodic() {
		//DRIVE
		driver.arcadeDrive(xBox.getRawAxis(5), xBox.getRawAxis(4));
		
		if(gamepad.getRawButton(3) || xBox.getRawButton(6)){ //CLIMB
			climber.set(-1.0);
			intake.set(0.0);
			auger.set(0.0);
			shooter.disable();
			feeder.set(0.0);
		} else if(gamepad.getRawButton(2) || xBox.getRawButton(10)){ //INTAKE
			intake.set(1.0);
			auger.set(-1.0);
		//}else if(gamepad.getRawButton(1) || xBox.getRawButton(5)){ //SHOOT
		}else if(xBox.getRawButton(2)){ //SHOOT
			//feeder.set(1.0);
			//auger.set(-1.0);
			//feedServo.setSpeed(1);
			shooter.shoot(1.09);
			shooter.enable();
		} else { //NONE
			intake.set(0.0);
			auger.set(0.0);
			climber.set(0.0);
			shooter.disable();
			feeder.set(0.0);
			feedServo.setSpeed(-0.98);
		}
		
		//DOOZ THE DATA LOG
		//Joystick Values
		SmartDashboard.putNumber("Speed Axis", xBox.getRawAxis(5));
		SmartDashboard.putNumber("Rotation Axis", xBox.getRawAxis(4));
		
		//Drive & Climber Voltage and Current
		SmartDashboard.putNumber("Drive 1 Output Current", drive1.getOutputCurrent());
		SmartDashboard.putNumber("Drive 1 Output Voltage", drive1.getOutputVoltage());
		SmartDashboard.putNumber("Drive 2 Output Current", drive2.getOutputCurrent());
		SmartDashboard.putNumber("Drive 2 Output Voltage", drive2.getOutputVoltage());
		SmartDashboard.putNumber("Drive 3 Output Current", drive3.getOutputCurrent());
		SmartDashboard.putNumber("Drive 3 Output Voltage", drive3.getOutputVoltage());
		SmartDashboard.putNumber("Drive 4 Output Current", drive4.getOutputCurrent());
		SmartDashboard.putNumber("Drive 4 Output Voltage", drive4.getOutputVoltage());
		SmartDashboard.putNumber("Climber Output Current", climber.getOutputCurrent());
		SmartDashboard.putNumber("Climber Output Voltage", climber.getOutputVoltage());
		
		//Match Data
		SmartDashboard.putNumber("Match Time", Timer.getMatchTime());
		
		//Driver Station Data
		SmartDashboard.putNumber("Driver Station Time", Timer.getMatchTime());
	}

	@Override
	public void testPeriodic() {
		
	}
}

