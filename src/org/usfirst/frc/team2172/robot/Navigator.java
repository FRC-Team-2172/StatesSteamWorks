package org.usfirst.frc.team2172.robot;

import java.util.ArrayList;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

import edu.wpi.first.wpilibj.Timer;

public class Navigator {
	private CANTalon leftTalon;
	private CANTalon rightTalon;
	
	private double x;
	private double y;
	private double theta;
	
	@SuppressWarnings("unused")
	private Thread t;
	
	public Navigator(CANTalon leftTalon, CANTalon rightTalon, Point startPoint){
		this.leftTalon = leftTalon;
		this.rightTalon = rightTalon;
		this.leftTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.rightTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.leftTalon.reverseSensor(false);
		this.rightTalon.reverseSensor(false);
		this.leftTalon.configEncoderCodesPerRev(1440);
		this.rightTalon.configEncoderCodesPerRev(1440);
		
		this.x = startPoint.X;
		this.y = startPoint.Y;
		this.theta = startPoint.THETA;
		
		this.t = new Thread(() -> {
			while(!Thread.interrupted()){
				leftTalon.setEncPosition(0);
				rightTalon.setEncPosition(0);
				Timer.delay(0.1);
				double xl = leftTalon.getEncPosition();
				double xr = rightTalon.getEncPosition();
				
				//Possibly fucked up theta
				if(xl > xr){ //Theta is getting bigger
					this.theta += (2*Math.PI)*((2/3 * Math.PI * (xl-xr))/(2*2.583*Math.PI));
					this.x += Math.cos(theta) * 2/3 * Math.PI * (((xr+xl)/2)/1440); 
					this.y += Math.sin(theta) * 2/3 * Math.PI * (((xr+xl)/2)/1440);
				} else if(xr > xl){ //Theta is getting smaller
					this.theta -= (2*Math.PI)*((2/3 * Math.PI * (xr-xl))/(2*2.583*Math.PI));
					this.x += Math.cos(theta) * 2/3 * Math.PI * (((xr+xl)/2)/1440); 
					this.y += Math.sin(theta) * 2/3 * Math.PI * (((xr+xl)/2)/1440);
				} else { //Theta has not changed
					this.x += Math.cos(theta) * 2/3 * Math.PI * (((xr+xl)/2)/1440); 
					this.y += Math.sin(theta) * 2/3 * Math.PI * (((xr+xl)/2)/1440); 
				}
			}
		});
	}
	
	public Point[] generatePath(Point target, double maxRadsPerStep){
		Obstacle[] problems = {
			//THESE ARE THE PLACES THAT WE DIE	
		};
		
		//THIS IS GONNA BE THE PLACE WHERE WE WANNA IMPLEMENT THE RADAR SHITZ
		
		Point current = new Point(this.x, this.y, this.theta);
		boolean pathComplete = false;
		ArrayList<Point> points = new ArrayList<Point>();
		
		Point c = current;
		Point t = target;
		
		for(int i = 0; i < 2; i++){
			points.add(new Point(0, 0, 0));
		}
		
		points.set(1, new Point(Math.cos(target.THETA + Math.PI) + target.X, Math.sin(target.THETA  + Math.PI) + target.Y, target.THETA));
		
		if(Math.abs(current.angleTo(target) - current.THETA) > maxRadsPerStep){
			if(current.angleTo(target) - current.THETA < 0){
				points.set(0, new Point(Math.cos(current.THETA - maxRadsPerStep) + current.X, Math.sin(current.THETA - maxRadsPerStep) + current.Y, current.THETA - maxRadsPerStep));
			} else {
				points.set(0, new Point(Math.cos(current.THETA + maxRadsPerStep) + current.X, Math.sin(current.THETA + maxRadsPerStep) + current.Y, current.THETA + maxRadsPerStep));
			}
		} else {
			points.set(0, new Point(Math.cos(current.angleTo(target)) + current.X, Math.sin(current.angleTo(target)) + current.Y, current.angleTo(target)));
		}
		
		for(int i = 0; !pathComplete; i++){
			current = points.get(i);
			target = points.get(points.size() - 1 - i);
			pathComplete = (current.distanceTo(target) <= 1.5);
			
			if(!pathComplete){
				if(Math.abs(target.angleTo(current) - target.getOppTheta()) > maxRadsPerStep){
					if((Math.abs(target.angleTo(current) - target.getOppTheta()) >= Math.PI && target.angleTo(current) - target.getOppTheta() > 0)||(Math.abs(target.angleTo(current) - target.getOppTheta()) < Math.PI && target.angleTo(current) - target.getOppTheta() < 0)){
						points.add(points.size() - 1 - i, new Point(Math.cos(target.getOppTheta() + maxRadsPerStep) + target.X, Math.sin(target.getOppTheta() + maxRadsPerStep) + target.Y, target.THETA - maxRadsPerStep));
					} else {
						points.add(points.size() - 1 - i, new Point(Math.cos(target.getOppTheta() - maxRadsPerStep) + target.X, Math.sin(target.getOppTheta() - maxRadsPerStep) + target.Y, target.THETA + maxRadsPerStep));
					}
			    } else {
			    	points.add(points.size() - 1 - i, new Point(Math.cos(target.angleTo(current)) + target.X, Math.sin(target.angleTo(current)) + target.Y, target.angleTo(current) + Math.PI));
			    }
				
				if(Math.abs(current.angleTo(target) - current.THETA) > maxRadsPerStep){
					if((Math.abs(current.angleTo(target) - current.THETA) >= Math.PI && current.angleTo(target) - current.THETA > 0)||(Math.abs(current.angleTo(target) - current.THETA) < Math.PI && current.angleTo(target) - current.THETA < 0)){
						points.add(i + 1, new Point(Math.cos(current.THETA - maxRadsPerStep) + current.X, Math.sin(current.THETA - maxRadsPerStep) + current.Y, current.THETA - maxRadsPerStep));
					} else {
						points.add(i + 1, new Point(Math.cos(current.THETA + maxRadsPerStep) + current.X, Math.sin(current.THETA + maxRadsPerStep) + current.Y, current.THETA + maxRadsPerStep));
					}
			    } else {
			    	points.add(i + 1, new Point(Math.cos(current.angleTo(target)) + current.X, Math.sin(current.angleTo(target)) + current.Y, current.angleTo(target)));
			    }
			}
		}
		
		for(int i = 0; i < points.size(); i++){
			for(int j = 0; j < problems.length; j++){
				if(points.get(i).distanceTo(new Point(problems[j].X, problems[j].Y, 0)) <= problems[j].RADIUS){
					Point dodge = new Point((problems[j].RADIUS + 4)*Math.cos(points.get(i).angleTo(new Point(problems[j].X, problems[j].Y, 0)) + Math.PI) + problems[j].X, (problems[j].RADIUS + 4)*Math.sin(points.get(i).angleTo(new Point(problems[j].X, problems[j].Y, 0)) + Math.PI) + problems[j].Y, (Math.PI/2) - points.get(i).angleTo(new Point(problems[j].X, problems[j].Y, 0)));
					
					points = this.generatePath(c, dodge, Math.PI/4);
					points.addAll(this.generatePath(dodge, t, Math.PI/4));
				} else {
					i += (int)points.get(i).distanceTo(new Point(problems[j].X, problems[j].Y, 0));
				}
			}
		}
		
		return (Point[]) points.toArray();
	}
	
	private ArrayList<Point> generatePath(Point current, Point target, double maxRadsPerStep){
		Obstacle[] problems = {
			//THESE ARE THE PLACES THAT WE DIE	
		};
		
		boolean pathComplete = false;
		ArrayList<Point> points = new ArrayList<Point>();
		
		Point c = current;
		Point t = target;
		
		for(int i = 0; i < 2; i++){
			points.add(new Point(0, 0, 0));
		}
		
		points.set(1, new Point(Math.cos(target.THETA + Math.PI) + target.X, Math.sin(target.THETA  + Math.PI) + target.Y, target.THETA));
		
		if(Math.abs(current.angleTo(target) - current.THETA) > maxRadsPerStep){
			if(current.angleTo(target) - current.THETA < 0){
				points.set(0, new Point(Math.cos(current.THETA - maxRadsPerStep) + current.X, Math.sin(current.THETA - maxRadsPerStep) + current.Y, current.THETA - maxRadsPerStep));
			} else {
				points.set(0, new Point(Math.cos(current.THETA + maxRadsPerStep) + current.X, Math.sin(current.THETA + maxRadsPerStep) + current.Y, current.THETA + maxRadsPerStep));
			}
		} else {
			points.set(0, new Point(Math.cos(current.angleTo(target)) + current.X, Math.sin(current.angleTo(target)) + current.Y, current.angleTo(target)));
		}
		
		for(int i = 0; !pathComplete; i++){
			current = points.get(i);
			target = points.get(points.size() - 1 - i);
			pathComplete = (current.distanceTo(target) <= 1.5);
			
			if(!pathComplete){
				if(Math.abs(target.angleTo(current) - target.getOppTheta()) > maxRadsPerStep){
					if((Math.abs(target.angleTo(current) - target.getOppTheta()) >= Math.PI && target.angleTo(current) - target.getOppTheta() > 0)||(Math.abs(target.angleTo(current) - target.getOppTheta()) < Math.PI && target.angleTo(current) - target.getOppTheta() < 0)){
						points.add(points.size() - 1 - i, new Point(Math.cos(target.getOppTheta() + maxRadsPerStep) + target.X, Math.sin(target.getOppTheta() + maxRadsPerStep) + target.Y, target.THETA - maxRadsPerStep));
					} else {
						points.add(points.size() - 1 - i, new Point(Math.cos(target.getOppTheta() - maxRadsPerStep) + target.X, Math.sin(target.getOppTheta() - maxRadsPerStep) + target.Y, target.THETA + maxRadsPerStep));
					}
			    } else {
			    	points.add(points.size() - 1 - i, new Point(Math.cos(target.angleTo(current)) + target.X, Math.sin(target.angleTo(current)) + target.Y, target.angleTo(current) + Math.PI));
			    }
				
				if(Math.abs(current.angleTo(target) - current.THETA) > maxRadsPerStep){
					if((Math.abs(current.angleTo(target) - current.THETA) >= Math.PI && current.angleTo(target) - current.THETA > 0)||(Math.abs(current.angleTo(target) - current.THETA) < Math.PI && current.angleTo(target) - current.THETA < 0)){
						points.add(i + 1, new Point(Math.cos(current.THETA - maxRadsPerStep) + current.X, Math.sin(current.THETA - maxRadsPerStep) + current.Y, current.THETA - maxRadsPerStep));
					} else {
						points.add(i + 1, new Point(Math.cos(current.THETA + maxRadsPerStep) + current.X, Math.sin(current.THETA + maxRadsPerStep) + current.Y, current.THETA + maxRadsPerStep));
					}
			    } else {
			    	points.add(i + 1, new Point(Math.cos(current.angleTo(target)) + current.X, Math.sin(current.angleTo(target)) + current.Y, current.angleTo(target)));
			    }
			}
		}
		
		for(int i = 0; i < points.size(); i++){
			for(int j = 0; j < problems.length; j++){
				if(points.get(i).distanceTo(new Point(problems[j].X, problems[j].Y, 0)) <= problems[j].RADIUS){
					Point dodge = new Point((problems[j].RADIUS + 4)*Math.cos(points.get(i).angleTo(new Point(problems[j].X, problems[j].Y, 0)) + Math.PI) + problems[j].X, (problems[j].RADIUS + 4)*Math.sin(points.get(i).angleTo(new Point(problems[j].X, problems[j].Y, 0)) + Math.PI) + problems[j].Y, (Math.PI/2) - points.get(i).angleTo(new Point(problems[j].X, problems[j].Y, 0)));
					
					points = this.generatePath(c, dodge, Math.PI/4);
					points.addAll(this.generatePath(dodge, t, Math.PI/4));
				} else {
					i += (int)points.get(i).distanceTo(new Point(problems[j].X, problems[j].Y, 0));
				}
			}
		}
		
		return points;
	}
	
	public double getX(){return this.x;}
	
	public double getY(){return this.y;}
	
	public double getTheta(){return this.theta;}
	
	public Point getLocation(){return new Point(this.x, this.y, this.theta);}

}
