package menon.cs6100.FinalProject;

public class VoronoiCellEdge {
	
	public static final VoronoiCellEdge terrainLeftEdge = new VoronoiCellEdge(0, 0, 0, UavTerrainUi.TERRAIN_HEIGHT);
	public static final VoronoiCellEdge terrainRightEdge = new VoronoiCellEdge(UavTerrainUi.TERRAIN_WIDTH, 0, UavTerrainUi.TERRAIN_WIDTH, UavTerrainUi.TERRAIN_HEIGHT);
	
	private int xCoordinate1;
	private int yCoordinate1;
	private int xCoordinate2;
	private int yCoordinate2;
	
	public VoronoiCellEdge(int xCoordinate1, int yCoordinate1, int xCoordinate2, int yCoordinate2) {
		this.xCoordinate1 = xCoordinate1;
		this.yCoordinate1 = yCoordinate1;
		this.xCoordinate2 = xCoordinate2;
		this.yCoordinate2 = yCoordinate2;
	}

	public int getxCoordinate1() {
		return xCoordinate1;
	}

	public int getyCoordinate1() {
		return yCoordinate1;
	}

	public int getxCoordinate2() {
		return xCoordinate2;
	}

	public int getyCoordinate2() {
		return yCoordinate2;
	}
	
	/**
	 * @param yCoordinate
	 * @return true if horizontal line passing through yCoordinate intersects the edge
	 */
	public boolean intersectsWithHorizontalLineThrough(int rayYCoordinate, boolean travellingRight, int uavXCoordinate) {
		
		//if the cell edge is horizontal, the lines will never intersect
		if(this.yCoordinate2 == this.yCoordinate1) {
			return false;
		}
		
		//Find the x coordinate of the point of intersection
		double pointOfIntersection = ((double) (rayYCoordinate - this.yCoordinate1) * (this.xCoordinate2 - this.xCoordinate1) / (this.yCoordinate2 - this.yCoordinate1)) + this.xCoordinate1; 
		
		//See if the point of intersection is between the two end points of the line
		if (this.xCoordinate1 < this.xCoordinate2) {
			if (this.xCoordinate1 <= pointOfIntersection && pointOfIntersection <= this.xCoordinate2) {
				if (travellingRight) {
					if (pointOfIntersection >= uavXCoordinate) {
						return true;
					}
				} else {
					if (pointOfIntersection <= uavXCoordinate) {
						return true;
					}
				}
			}
		} else if (this.xCoordinate1 > this.xCoordinate2) {
			if (this.xCoordinate2 <= pointOfIntersection && pointOfIntersection <= this.xCoordinate1) {
				if (travellingRight) {
					if (pointOfIntersection >= uavXCoordinate) {
						return true;
					}
				} else {
					if (pointOfIntersection <= uavXCoordinate) {
						return true;
					}
				}
			}
		} else {
			if (this.yCoordinate1 < this.yCoordinate2) {
				if (this.yCoordinate1 <= rayYCoordinate && rayYCoordinate <= this.yCoordinate2) {
					if (travellingRight) {
						if (pointOfIntersection >= uavXCoordinate) {
							return true;
						}
					} else {
						if (pointOfIntersection <= uavXCoordinate) {
							return true;
						}
					}
				}
			} else if (this.yCoordinate1 > this.yCoordinate2) {
				if (this.yCoordinate1 >= rayYCoordinate && rayYCoordinate >= this.yCoordinate2) {
					if (travellingRight) {
						if (pointOfIntersection >= uavXCoordinate) {
							return true;
						}
					} else {
						if (pointOfIntersection <= uavXCoordinate) {
							return true;
						}
					}
				}
			}
			
		}
		
		return false;
		
	}
	
}
