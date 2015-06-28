package menon.cs6100.FinalProject;

import java.awt.Color;

public class UavPathPoint {

	private int xCoordinate;
	private int yCoordinate;
	private Color pathColor;
	
	public UavPathPoint(int xCoordinate, int yCoordinate, Color pathColor) {
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.pathColor = pathColor;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public Color getPathColor() {
		return pathColor;
	}

	public void setPathColor(Color pathColor) {
		this.pathColor = pathColor;
	}
}
