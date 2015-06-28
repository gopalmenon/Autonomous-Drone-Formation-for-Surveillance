package menon.cs6100.FinalProject;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.humphreys.simplevoronoi.GraphEdge;

public class VoronoiCell {
	
	private List<VoronoiCellEdge> cellCoordinates;
	private List<Point> sortedCellCoordinates;
	private boolean cellOnTerrainBorder;
	
	public VoronoiCell(List<GraphEdge> voronoiCellEdges) {
		
		this.cellCoordinates = new ArrayList<VoronoiCellEdge>();
		this.sortedCellCoordinates = new ArrayList<Point>();
		this.cellOnTerrainBorder = false;
		
		for (GraphEdge graphEdge : voronoiCellEdges) {
			
			this.cellCoordinates.add(new VoronoiCellEdge(Double.valueOf(graphEdge.x1).intValue(), Double.valueOf(graphEdge.y1).intValue(),Double.valueOf(graphEdge.x2).intValue(),Double.valueOf(graphEdge.y2).intValue()));

		}
		
		this.sort();
		
	}
	
	/**
	 * Sort the coordinates in order
	 */
	private void sort() {
		
		//The cell can either be internal to the terrain or it can meet a terrain border.
		//If it meets the terrain border, either the x or y coordinate for an edge will be zero.
		//For a cell bordering the terrain edge, start with the coordinate on the border. 
		//For an internal cell, start with any coordinate.
		
		//Add the first edge
		
		VoronoiCellEdge voronoiCellEdge = getAnEdgeEndingOnTerrainBorder();
		if (voronoiCellEdge != null) {
			
			this.cellOnTerrainBorder = true;
			
			Point point1 = new Point(voronoiCellEdge.getxCoordinate1(), voronoiCellEdge.getyCoordinate1());
			Point point2 = new Point(voronoiCellEdge.getxCoordinate2(), voronoiCellEdge.getyCoordinate2());
			
			if (point1.x == 0 || point1.y == 0 || point1.x == UavTerrainUi.TERRAIN_WIDTH || point1.y == UavTerrainUi.TERRAIN_HEIGHT) {
				this.sortedCellCoordinates.add(point1);
				this.sortedCellCoordinates.add(point2);
			} else {
				this.sortedCellCoordinates.add(point2);
				this.sortedCellCoordinates.add(point1);
			}
			
		} else {
			if (this.cellCoordinates.size() != 0 ) {
				this.sortedCellCoordinates.add(new Point(this.cellCoordinates.get(0).getxCoordinate1(), this.cellCoordinates.get(0).getyCoordinate1()));
				this.sortedCellCoordinates.add(new Point(this.cellCoordinates.get(0).getxCoordinate2(), this.cellCoordinates.get(0).getyCoordinate2()));
			}
		}
		
		Point lastPointAdded = this.sortedCellCoordinates.get(1), candidatePoint1 = null, candidatePoint2 = null;
		//Add the rest of the edges, starting with the one that shares a common point with the last one added
		int numberOfSortedEdges = 0;
		while (true) {
			
			//Terminate the loop if all the points have been inserted into the sorted list
			if (this.cellOnTerrainBorder) {
				numberOfSortedEdges = this.sortedCellCoordinates.size() - 1;
			} else {
				numberOfSortedEdges = this.sortedCellCoordinates.size();
			}
			
			if (numberOfSortedEdges == this.cellCoordinates.size()) {
				break;
			}
			
			for (VoronoiCellEdge candidateVoronoiCellEdge : this.cellCoordinates) {
				
				candidatePoint1 = new Point(candidateVoronoiCellEdge.getxCoordinate1(), candidateVoronoiCellEdge.getyCoordinate1());
				candidatePoint2 = new Point(candidateVoronoiCellEdge.getxCoordinate2(), candidateVoronoiCellEdge.getyCoordinate2());
				
				//If the edge contains the last point added, then add the other point on the edge
				if (lastPointAdded.x == candidatePoint1.x && lastPointAdded.y == candidatePoint1.y && !this.sortedCellCoordinates.contains(candidatePoint2)) {
					this.sortedCellCoordinates.add(candidatePoint2);
					lastPointAdded = candidatePoint2;
					break;
				} else if (lastPointAdded.x == candidatePoint2.x && lastPointAdded.y == candidatePoint2.y && !this.sortedCellCoordinates.contains(candidatePoint1)) {
					this.sortedCellCoordinates.add(candidatePoint1);
					lastPointAdded = candidatePoint1;
					break;
				}
				
			}
		
		}
		
		//If the Voronoi cell is on the border, then it will be open. Add the remaining 
		//points on the border so that it makes a convex polygon.
		if (this.cellOnTerrainBorder) {
			
			int numberOfCornersInCell = this.sortedCellCoordinates.size();
			//Get cross products of last two edges
			int crossProduct1 = 0;
			Vector2D vector1 = null, vector2 = null;
			if (numberOfCornersInCell > 2) {
				vector1 = new Vector2D(this.sortedCellCoordinates.get(numberOfCornersInCell - 1).x, this.sortedCellCoordinates.get(numberOfCornersInCell - 1).y, this.sortedCellCoordinates.get(numberOfCornersInCell - 2).x, this.sortedCellCoordinates.get(numberOfCornersInCell - 2).y);
				vector2 = new Vector2D(this.sortedCellCoordinates.get(numberOfCornersInCell - 2).x, this.sortedCellCoordinates.get(numberOfCornersInCell - 2).y, this.sortedCellCoordinates.get(numberOfCornersInCell - 3).x, this.sortedCellCoordinates.get(numberOfCornersInCell - 3).y);
				crossProduct1 = vector1.crossProduct(vector2);
			}
			
			if (crossProduct1 != 0) {
				
				Point topLeftCorner = new Point(0, 0);
				Point topRightCorner = new Point(UavTerrainUi.TERRAIN_WIDTH, 0);
				Point bottomLeftCorner = new Point(0, UavTerrainUi.TERRAIN_HEIGHT);
				Point bottomRightCorner = new Point(UavTerrainUi.TERRAIN_WIDTH, UavTerrainUi.TERRAIN_HEIGHT);
				
				//Choose the line along the border which will result in the same cross product sign
				Point firstPointOnBorder = this.sortedCellCoordinates.get(0), lastPointOnBorder = this.sortedCellCoordinates.get(this.sortedCellCoordinates.size() - 1);
				Vector2D vectorComingOutAtFirstPoint = new Vector2D(this.sortedCellCoordinates.get(1).x, this.sortedCellCoordinates.get(1).y, this.sortedCellCoordinates.get(0).x, this.sortedCellCoordinates.get(0).y);
				
				if (firstPointOnBorder.x == 0) {
					
					int crossProductGoingUp = vectorComingOutAtFirstPoint.crossProduct(new Vector2D(firstPointOnBorder.x, firstPointOnBorder.y, 0, 0));
					
					if (lastPointOnBorder.x == 0) {

						//Do nothing as the cell is implicitly closed
						
					} else if (lastPointOnBorder.x == UavTerrainUi.TERRAIN_WIDTH) {
													
						if ((crossProductGoingUp > 0 && crossProduct1 > 0) || (crossProductGoingUp < 0 && crossProduct1 < 0)) {
							this.sortedCellCoordinates.add(topRightCorner);
							this.sortedCellCoordinates.add(topLeftCorner);
						} else {
							this.sortedCellCoordinates.add(bottomRightCorner);
							this.sortedCellCoordinates.add(bottomLeftCorner);
						}
						
					} else if (lastPointOnBorder.y == 0) {
						
						if ((crossProductGoingUp > 0 && crossProduct1 > 0) || (crossProductGoingUp < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(topLeftCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(topRightCorner);
							this.sortedCellCoordinates.add(bottomRightCorner);
							this.sortedCellCoordinates.add(bottomLeftCorner);
							
						}
						
					} else if (lastPointOnBorder.y == UavTerrainUi.TERRAIN_HEIGHT) {
						
						if ((crossProductGoingUp > 0 && crossProduct1 > 0) || (crossProductGoingUp < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(bottomRightCorner);
							this.sortedCellCoordinates.add(topRightCorner);
							this.sortedCellCoordinates.add(topLeftCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(bottomLeftCorner);
							
						}
						
						
					} else {
						System.err.println("Could not complete open Veronoi cell at (" + lastPointOnBorder.x + ", " + lastPointOnBorder.y + ". Program needs to terminate.");
						System.exit(0);
					}
					
				} else if (firstPointOnBorder.x == UavTerrainUi.TERRAIN_WIDTH) {
					
					int crossProductGoingUp = vectorComingOutAtFirstPoint.crossProduct(new Vector2D(firstPointOnBorder.x, firstPointOnBorder.y, UavTerrainUi.TERRAIN_WIDTH, 0));
					
					if (lastPointOnBorder.x == 0) {
						
						if ((crossProductGoingUp > 0 && crossProduct1 > 0) || (crossProductGoingUp < 0 && crossProduct1 < 0)) {
						
							this.sortedCellCoordinates.add(topLeftCorner);
							this.sortedCellCoordinates.add(topRightCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(bottomLeftCorner);
							this.sortedCellCoordinates.add(bottomRightCorner);

						}
						
					} else if (lastPointOnBorder.x == UavTerrainUi.TERRAIN_WIDTH) {

						//Do nothing as the cell is implicitly closed
						
					} else if (lastPointOnBorder.y == 0) {
						
						if ((crossProductGoingUp > 0 && crossProduct1 > 0) || (crossProductGoingUp < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(topRightCorner);

						} else {
							
							this.sortedCellCoordinates.add(topLeftCorner);
							this.sortedCellCoordinates.add(bottomLeftCorner);
							this.sortedCellCoordinates.add(bottomRightCorner);
							
						}
						
					} else if (lastPointOnBorder.y == UavTerrainUi.TERRAIN_HEIGHT) {

						if ((crossProductGoingUp > 0 && crossProduct1 > 0) || (crossProductGoingUp < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(bottomLeftCorner);
							this.sortedCellCoordinates.add(topLeftCorner);
							this.sortedCellCoordinates.add(topRightCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(bottomRightCorner);
							
						}
					} else {
						System.err.println("Could not complete open Veronoi cell at (" + lastPointOnBorder.x + ", " + lastPointOnBorder.y + ". Program needs to terminate.");
						System.exit(0);
					}
					
				} else if (firstPointOnBorder.y == 0) {
					
					int crossProductGoingLeft = vectorComingOutAtFirstPoint.crossProduct(new Vector2D(firstPointOnBorder.x, firstPointOnBorder.y, 0, 0));

					if (lastPointOnBorder.x == 0) {
						
						if ((crossProductGoingLeft > 0 && crossProduct1 > 0) || (crossProductGoingLeft < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(topLeftCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(bottomLeftCorner);
							this.sortedCellCoordinates.add(bottomRightCorner);
							this.sortedCellCoordinates.add(topRightCorner);
							
						}
						
						
					} else if (lastPointOnBorder.x == UavTerrainUi.TERRAIN_WIDTH) {
						
						if ((crossProductGoingLeft > 0 && crossProduct1 > 0) || (crossProductGoingLeft < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(bottomRightCorner);
							this.sortedCellCoordinates.add(bottomLeftCorner);
							this.sortedCellCoordinates.add(topLeftCorner);
							
							
						} else {
							
							this.sortedCellCoordinates.add(topRightCorner);
							
						}
						
					} else if (lastPointOnBorder.y == 0) {
						
						//Do nothing as the cell is implicitly closed
						
					} else if (lastPointOnBorder.y == UavTerrainUi.TERRAIN_HEIGHT) {
						
						if ((crossProductGoingLeft > 0 && crossProduct1 > 0) || (crossProductGoingLeft < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(bottomLeftCorner);
							this.sortedCellCoordinates.add(topLeftCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(bottomRightCorner);
							this.sortedCellCoordinates.add(topRightCorner);
							
						}

					} else {
						System.err.println("Could not complete open Veronoi cell at (" + lastPointOnBorder.x + ", " + lastPointOnBorder.y + ". Program needs to terminate.");
						System.exit(0);
					}
					
				} else if (firstPointOnBorder.y == UavTerrainUi.TERRAIN_HEIGHT) {
					
					int crossProductGoingLeft = vectorComingOutAtFirstPoint.crossProduct(new Vector2D(firstPointOnBorder.x, firstPointOnBorder.y, 0, UavTerrainUi.TERRAIN_HEIGHT));
					
					if (lastPointOnBorder.x == 0) {
						
						if ((crossProductGoingLeft > 0 && crossProduct1 > 0) || (crossProductGoingLeft < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(bottomLeftCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(topLeftCorner);
							this.sortedCellCoordinates.add(topRightCorner);
							this.sortedCellCoordinates.add(bottomRightCorner);
							
						}
						
					} else if (lastPointOnBorder.x == UavTerrainUi.TERRAIN_WIDTH) {
						
						if ((crossProductGoingLeft > 0 && crossProduct1 > 0) || (crossProductGoingLeft < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(bottomLeftCorner);
							this.sortedCellCoordinates.add(topLeftCorner);
							this.sortedCellCoordinates.add(topRightCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(bottomRightCorner);
							
						}
						
					} else if (lastPointOnBorder.y == 0) {
						
						if ((crossProductGoingLeft > 0 && crossProduct1 > 0) || (crossProductGoingLeft < 0 && crossProduct1 < 0)) {
							
							this.sortedCellCoordinates.add(topLeftCorner);
							this.sortedCellCoordinates.add(bottomLeftCorner);
							
						} else {
							
							this.sortedCellCoordinates.add(topRightCorner);
							this.sortedCellCoordinates.add(bottomRightCorner);
							
						}
						
					} else if (lastPointOnBorder.y == UavTerrainUi.TERRAIN_HEIGHT) {
						
						//Do nothing as the cell is implicitly closed
						
					} else {
						System.err.println("Could not complete open Veronoi cell at (" + lastPointOnBorder.x + ", " + lastPointOnBorder.y + ". Program needs to terminate.");
						System.exit(0);
					}
					
				} else {
					System.err.println("Could not complete open Veronoi cell at (" + firstPointOnBorder.x + ", " + firstPointOnBorder.y + ". Program needs to terminate.");
					System.exit(0);
				}
			
			}
			
		}
		
	}
	
	public List<Point> getSortedCoordinates() {
		
		return Collections.unmodifiableList(this.sortedCellCoordinates);
	}
	
	private VoronoiCellEdge getAnEdgeEndingOnTerrainBorder() {
		
		for (VoronoiCellEdge voronoiCellEdge : this.cellCoordinates) {
			
			if (voronoiCellEdge.getxCoordinate1() == 0 || voronoiCellEdge.getyCoordinate1() == 0 || voronoiCellEdge.getxCoordinate2() == 0 || voronoiCellEdge.getyCoordinate2() == 0 ||
				voronoiCellEdge.getxCoordinate1() == UavTerrainUi.TERRAIN_WIDTH || voronoiCellEdge.getyCoordinate1() == UavTerrainUi.TERRAIN_HEIGHT || voronoiCellEdge.getxCoordinate2() == UavTerrainUi.TERRAIN_WIDTH || voronoiCellEdge.getyCoordinate2() == UavTerrainUi.TERRAIN_HEIGHT) {
				return voronoiCellEdge;
			}
			
		}
		
		return null;
	}

	/**
	 * @return the centroid of the cell. See http://en.wikipedia.org/wiki/Centroid
	 */
	public Point getCellCentroid() {
		
		double cellCenterOfGravityXCoordinate = 0, cellCenterOfGravityYCoordinate = 0, cellSignedArea = 0;
		int numberOfCornersInCell = this.sortedCellCoordinates.size();
		
	    for (int cornerCounter = 0; cornerCounter < numberOfCornersInCell - 1; ++cornerCounter) {
	    	
	    	cellCenterOfGravityXCoordinate += (this.sortedCellCoordinates.get(cornerCounter).x + this.sortedCellCoordinates.get(cornerCounter + 1).x) *
	    			                          (this.sortedCellCoordinates.get(cornerCounter).x * this.sortedCellCoordinates.get(cornerCounter + 1).y -
	    			                           this.sortedCellCoordinates.get(cornerCounter + 1).x * this.sortedCellCoordinates.get(cornerCounter).y);
	    	
	    	cellCenterOfGravityYCoordinate += (this.sortedCellCoordinates.get(cornerCounter).y + this.sortedCellCoordinates.get(cornerCounter + 1).y) *
	    			                          (this.sortedCellCoordinates.get(cornerCounter).x * this.sortedCellCoordinates.get(cornerCounter + 1).y -
	    			                           this.sortedCellCoordinates.get(cornerCounter + 1).x * this.sortedCellCoordinates.get(cornerCounter).y);
	    	
	    	cellSignedArea += this.sortedCellCoordinates.get(cornerCounter).x * this.sortedCellCoordinates.get(cornerCounter + 1).y -
	    			          this.sortedCellCoordinates.get(cornerCounter + 1).x * this.sortedCellCoordinates.get(cornerCounter).y;
	    	
	    }
	    
	    cellSignedArea /= 2.0;
	    cellCenterOfGravityXCoordinate /= 6 * cellSignedArea;
	    cellCenterOfGravityYCoordinate /= 6 * cellSignedArea;
	    
	    return new Point((int) cellCenterOfGravityXCoordinate, (int) cellCenterOfGravityYCoordinate);
	    
	}
	
	public boolean isCellOnTerrainBorder() {
		return cellOnTerrainBorder;
	}

}
