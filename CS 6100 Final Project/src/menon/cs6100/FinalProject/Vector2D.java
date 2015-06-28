package menon.cs6100.FinalProject;

public class Vector2D {
	
	private int xCoordinate;
	private int yCoordinate;
	
	public Vector2D(int xCoordinate, int yCoordinate) {
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
	}
	
	public Vector2D(int fromXCoordinate, int fromYCoordinate, int toXCoordinate, int toYCoordinate) {
	
		this.xCoordinate = toXCoordinate - fromXCoordinate;
		this.yCoordinate = toYCoordinate - fromYCoordinate;
	
	}
	
	public Vector2D subtract(Vector2D vectorToBeSubtracted) {
		
		return new Vector2D(this.xCoordinate - vectorToBeSubtracted.xCoordinate, this.yCoordinate - vectorToBeSubtracted.yCoordinate);		
		
	}
	
	public int crossProduct(Vector2D crossProductWith) {
		return this.xCoordinate * crossProductWith.yCoordinate - crossProductWith.xCoordinate * this.yCoordinate;
				
	}
	
	public double getMagnitude() {
		
		return Math.pow(Math.pow(this.xCoordinate, 2.0) + Math.pow(this.yCoordinate, 2.0), 0.5);
		
	}
}
