package org.usfirst.frc.team2172.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.SerialPort;

public class USBArduinoCamera {
	private Thread t;
	private SerialPort usb;
	private ArrayList<Integer> values;
	
	public USBArduinoCamera(SerialPort usb) {
		this.usb = usb;
		this.values = new ArrayList<Integer>();
		this.t = new Thread(() -> {
	        while (!Thread.interrupted()) {
	        	if (this.usb.getBytesReceived() > 0) {
	    			byte[] buffer = usb.read(6);
	    			final byte actualStartByte = 0xffffffA5;
	    			final byte endByte = 0x25;
	    			int startByte = 0;
	    			for (int i = 0; buffer[i] != actualStartByte; i++) {
	  	    			if (buffer[i] == actualStartByte) {
	    					startByte = i;
	    				}
	    			}
	    			ArrayList<Integer> values = new ArrayList<Integer>();
	    			int index = startByte;
	    			while (buffer[index] != endByte) {
	    				if (index + 1 > buffer.length) {
	    					index = 0;
	    				}
	    				else {
	    					index++;
	    				}
	    				values.add((int) buffer[index]);
	    			}
	    			this.values = values;
	        	}
	        }
	    });
	}
	
	public int getX() {
		return values.get(0) & 0xFF;
	}
	
	public int getY() {
		return values.get(1);
	}
	
	public int getWidth() {
		return values.get(2);
	}
	
	public int getHeight() {
		return values.get(3);
	}
	
	public void start() {
		this.t.start();
	}
	
	public void restart() {
		this.usb.reset();
	}
	
	public boolean isStarted() {
		return this.t.isAlive();
	}
	
	public boolean hasValues() {
		return values.size() > 0;
	}
	
	public static USBArduinoCamera instantiateAndStart(SerialPort usb) {
		USBArduinoCamera arduino = new USBArduinoCamera(usb);
		arduino.start();
		return arduino;
	}
}
