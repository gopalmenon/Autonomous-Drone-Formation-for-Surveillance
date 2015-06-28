package menon.cs6100.FinalProject;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import be.humphreys.simplevoronoi.GraphEdge;
import be.humphreys.simplevoronoi.Voronoi;

public class Uav implements Callable<Void> {
	
	private int xCoordinate;
	private int yCoordinate;
	private int uavNumber;
	private UavTerrainPanel parent;
	private Set<Uav> allUavs;
	private Color pathColor;
	
	private List<Point> uavCoordinates;
	
	public Uav(int xCoordinate, int yCoordinate, int uavNumber, UavTerrainPanel parent, Set<Uav> allUavs) {
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.uavNumber = uavNumber;
		this.parent = parent;
		this.allUavs = allUavs;
		this.pathColor = getPathColor();
	}
	
	private Color getPathColor() {
		return Color.ORANGE;
	}
	
	@Override
	public Void call() throws Exception {
		
		Voronoi voronoiCellGenerator = new Voronoi(0.00001f);
		
		List<GraphEdge> voronoiCellEdges = voronoiCellGenerator.generateVoronoi(getUavCoordinatesArray(true), getUavCoordinatesArray(false), 0.0, UavTerrainUi.TERRAIN_WIDTH, 0.0, UavTerrainUi.TERRAIN_HEIGHT);
		
		returnVoronoiCellEdges(voronoiCellEdges);
		
		VoronoiCell voronoiCellEnclosingUav = getEnclosingVoronoiCell(voronoiCellEdges);
		
		int counter = 0, extraSleepTime = 0;;
		Point nextLocation = null;
		while (true) {
		
			counter++;
			if (counter > UavTerrainUi.TERRAIN_HEIGHT) {
				break;
			}
			nextLocation = getCoordinatesOfOneStepTowardsCellCentroid(voronoiCellEnclosingUav);
			this.xCoordinate = nextLocation.x;
			this.yCoordinate = nextLocation.y;
			leaveUavTraceOnTerrain();
			extraSleepTime = this.uavNumber * 5;
			Thread.sleep(100 + extraSleepTime);
			voronoiCellEdges = voronoiCellGenerator.generateVoronoi(getUavCoordinatesArray(true), getUavCoordinatesArray(false), 0.0, UavTerrainUi.TERRAIN_WIDTH, 0.0, UavTerrainUi.TERRAIN_HEIGHT);
			
			returnVoronoiCellEdges(voronoiCellEdges);
			
			voronoiCellEnclosingUav = getEnclosingVoronoiCell(voronoiCellEdges);
			
		}
		
		return null;
	}
	
	private void leaveUavTraceOnTerrain() {

		this.parent.updateUavPath(this.xCoordinate, this.yCoordinate, this.pathColor);
		
	}
	
	private void returnVoronoiCellEdges(List<GraphEdge> voronoiCellEdges) {
		
		if (this.uavNumber == 0) {
			
			this.parent.updateVoronoiCellEdges(voronoiCellEdges);
			
		}
		
	}
	
	private double[] getUavCoordinatesArray(boolean returnXCoordinates) {
		
		if(returnXCoordinates) {
			this.uavCoordinates = getUavCoordinates();
		}
		
		double[] returnValue = new double[this.uavCoordinates.size()];
		
		int coordinatesArrayCounter = 0;
		for (Point point : this.uavCoordinates) {
			
			if (returnXCoordinates) {
				returnValue[coordinatesArrayCounter++] = point.getX();
			} else {
				returnValue[coordinatesArrayCounter++] = point.getY();
			}
		}
		
		return returnValue;
	}
	
	
	private List<Point> getUavCoordinates() {
		
		List<Point> returnValue = new ArrayList<Point>();
		
		for (Uav uav : this.allUavs) {
			
			if (uav != null) {
				
				returnValue.add(new Point(uav.getxCoordinate(), uav.getyCoordinate()));
				
				
			}
			
		}
		
		return returnValue;
	}
	
	/**
	 * @return the Voronoi cell enclosing the UAV
	 */
	private VoronoiCell getEnclosingVoronoiCell(List<GraphEdge> voronoiCellEdges) {
		
		List<List<GraphEdge>> allVoronoiCellEdges = new ArrayList<List<GraphEdge>>();
		int numberOfUavs = this.allUavs.size();
		
		//Create the list of Voronoi cell edges
		for (int uavCounter = 0; uavCounter < numberOfUavs; ++uavCounter) {
			allVoronoiCellEdges.add(new ArrayList<GraphEdge>());
		}
		
		//Separate out the edges according to the cells that they boder
		for (GraphEdge graphEdge : voronoiCellEdges) {
			allVoronoiCellEdges.get(graphEdge.site1).add(graphEdge);
			allVoronoiCellEdges.get(graphEdge.site2).add(graphEdge);
		}
		
		//Get the list of all Voronoi cells
		List<VoronoiCell> cellsInTerrain = new ArrayList<VoronoiCell>();
		for (List<GraphEdge> specificCellEdges : allVoronoiCellEdges) {
			
			cellsInTerrain.add(new VoronoiCell(specificCellEdges));
			
		}
		
		return getCellEnclosingUav(cellsInTerrain);
			
	}
		
	private VoronoiCell getCellEnclosingUav(List<VoronoiCell> cellsInTerrain) {
		
		//Loop through all the cells in the terrain
		for (VoronoiCell voronoiCell : cellsInTerrain) {
			
			if (isEnclosingUav(voronoiCell)) {
				return voronoiCell;
			}
			
		}
		
		return null;
		
	}
	
	private void printVeronoiCells(List<VoronoiCell> cellsInTerrain) {
		
		StringBuffer voronoiCells = new StringBuffer();
		voronoiCells.append("UAV# ").append(this.uavNumber).append(" at ").append(this.xCoordinate).append(", ").append(this.yCoordinate).append(" has the following cells: ");
		int cellCounter = 0;
		for (VoronoiCell voronoiCell : cellsInTerrain) {
			voronoiCells.append("\n").append(cellCounter++).append(" ").append(getVeronoiCellCoordinate(voronoiCell));
		}
		
		System.out.println(voronoiCells.toString());
		
	}
	
	private String getVeronoiCellCoordinate(VoronoiCell cell) {
		
		StringBuffer cellCoordinates = new StringBuffer();
		List<Point> cellCorners = cell.getSortedCoordinates();
		for (Point cellCorner : cellCorners) {
			cellCoordinates.append(cellCorner.x).append(",").append(cellCorner.y).append(" ");
		}
		return cellCoordinates.toString();
		
	}
	
	/**
	 * @param voronoiCell
	 * @return true if Voronoi cell encloses UAV
	 */
	private boolean isEnclosingUav(VoronoiCell voronoiCell) {
				
		//Check to see if the UAV lies within the cell. Send out a horizontal ray from the UAV in two directions. If the ray going   
		//in one direction crosses an edge of the cell or the terrain boundary twice, then the cell does not enclose the UAV.
		
		//Loop through each edge in Voronoi cell
		List<Point> voronoiCellPoints = voronoiCell.getSortedCoordinates();
		int numberOfVoronoiCellPoints = voronoiCellPoints.size(), numberOfRayIntersectionsToRight = 0, numberOfRayIntersectionsToLeft = 0;
		VoronoiCellEdge cellEdge = null;
		
		//Add up how many times a ray travelling from the UAV to the right and left intersects with the cell edges
		for (int cellPointCounter = 0; cellPointCounter < numberOfVoronoiCellPoints; ++cellPointCounter) {
			
			if (cellPointCounter == numberOfVoronoiCellPoints - 1) {
				cellEdge = new VoronoiCellEdge(voronoiCellPoints.get(cellPointCounter).x, voronoiCellPoints.get(cellPointCounter).y, voronoiCellPoints.get(0).x, voronoiCellPoints.get(0).y);
			} else {
				cellEdge = new VoronoiCellEdge(voronoiCellPoints.get(cellPointCounter).x, voronoiCellPoints.get(cellPointCounter).y, voronoiCellPoints.get(cellPointCounter + 1).x, voronoiCellPoints.get(cellPointCounter + 1).y);
			}
			
			if (cellEdge != null) {
				
				//Check if ray traveling to right intersects the cell edge
				if (cellEdge.intersectsWithHorizontalLineThrough(this.yCoordinate, true, this.xCoordinate)) {
					++numberOfRayIntersectionsToRight;
				} 
				
				//Check if ray traveling to left intersects the cell edge
				if (cellEdge.intersectsWithHorizontalLineThrough(this.yCoordinate, false, this.xCoordinate)) {
					++numberOfRayIntersectionsToLeft;
				} 
				
			}
			
		}
		
		//If the UAV is inside the cell, then the number of intersections will be non-zero in each direction
		if (numberOfRayIntersectionsToRight != 0 && numberOfRayIntersectionsToLeft != 0) {
			return true;
		}
		
		return false;
	}
	
	private Point getCoordinatesOfOneStepTowardsCellCentroid(VoronoiCell enclosingCell) {
		
		if (enclosingCell == null) {
			return null;
		}
		
		Point enclosingCellCentroid = enclosingCell.getCellCentroid();
		
		int xCoordinateToReturn = this.xCoordinate;
		int yCoordinateToReturn = this.yCoordinate;
		
		if (this.xCoordinate > enclosingCellCentroid.x) {
			--xCoordinateToReturn;
		} if (this.xCoordinate < enclosingCellCentroid.x) {
			++xCoordinateToReturn;
		}
		
		if(this.yCoordinate > enclosingCellCentroid.y) {
			--yCoordinateToReturn;
		} else if (this.yCoordinate < enclosingCellCentroid.y) {
			++yCoordinateToReturn;
		}
		return new Point(xCoordinateToReturn, yCoordinateToReturn);
		
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
}
