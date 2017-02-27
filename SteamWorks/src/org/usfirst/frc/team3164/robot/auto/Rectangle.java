package org.usfirst.frc.team3164.robot.auto;

public class Rectangle {
	private int m_x;
	private int m_y;
	private int m_width;
	private int m_height;
	
	public Rectangle(int x, int y, int width, int height) {
		m_x = x;
		m_y = y;
		m_width = width;
		m_height = height;
	}
	
	public int getX() {
		return m_x;
	}
	
	public int getY() {
		return m_y;
	}
	
	public int getWidth() {
		return m_width;
	}
	
	public int getHeight() {
		return m_height;
	}
}
