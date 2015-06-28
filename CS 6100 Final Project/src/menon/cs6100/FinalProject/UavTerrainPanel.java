package menon.cs6100.FinalProject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;

import be.humphreys.simplevoronoi.GraphEdge;

@SuppressWarnings("serial")
public class UavTerrainPanel extends JPanel implements MouseListener {
	
	private static final int UAV_DIMENSION = 10;
	private static final int UAV_PATH_DIMENSION = 3;
	
	private boolean placeUavsMode;
	private boolean showVoronoiCells;
	private boolean showUavPaths;
	private boolean uavsDeployed;
	private int uavNumber;
	
	private Set<Uav> allUavs;
	private Set<Future<Void>> uavResults;
	private Set<UavPathPoint> uavPathPoints;
	private List<GraphEdge> voronoiCellEdges;
	
	public UavTerrainPanel() {
		
		this.placeUavsMode = true;
		this.showVoronoiCells = true;
		this.showUavPaths = true;
		this.uavsDeployed = false;
		this.uavNumber = 0;
		allUavs = new HashSet<Uav>();
		uavResults = new HashSet<Future<Void>>();
		uavPathPoints = new HashSet<UavPathPoint>();
		this.voronoiCellEdges = null;
		this.addMouseListener(this);
	}
	
	
	public void paintComponent(Graphics g) {
    	
        super.paintComponent(g);
        
        //Show all the UAVs
        for (Uav uav : this.allUavs) {
        	
            g.setColor(Color.GREEN);
            if (uav != null) {
            	g.fillOval(uav.getxCoordinate() - UAV_DIMENSION / 2, uav.getyCoordinate() - UAV_DIMENSION / 2, UAV_DIMENSION, UAV_DIMENSION);
            }

        }
        
        //Show all UAV flight paths
        if (this.showUavPaths) {
	        for (UavPathPoint uavPathPoint : this.uavPathPoints) {
	        	
	        	if (uavPathPoint != null) {
	        		g.setColor(Color.ORANGE);
	        		g.fillOval(uavPathPoint.getxCoordinate() - 1, uavPathPoint.getyCoordinate() - 1, UAV_PATH_DIMENSION, UAV_PATH_DIMENSION);
	        	}
	        }
        }
        
        //Show Voronoi cell edges
        if (this.showVoronoiCells) {
        	if (this.voronoiCellEdges != null) {
        		
        		for (GraphEdge graphEdge : voronoiCellEdges) {
        		
        			g.setColor(Color.BLUE);
        			g.drawLine(Double.valueOf(graphEdge.x1).intValue(), Double.valueOf(graphEdge.y1).intValue(), Double.valueOf(graphEdge.x2).intValue(), Double.valueOf(graphEdge.y2).intValue());
        		
        		}
        	}
        }
        
	}
	
	public void deployUavs() {
		
		if (!this.uavsDeployed) {
			
    		//Set up an agent pool
	    	ExecutorService uavPool = Executors.newFixedThreadPool(allUavs.size());
	    	
	    	for (Uav uav : allUavs) {
	    		
	    		if (uav != null) {
	    			
	    			//Start the UAVs so that they move into formation
	    			Future<Void> result = uavPool.submit(uav);
	    			
	    			uavResults.add(result);
	    			
	    		}
	    	}
	    	
	    	this.uavsDeployed = true;
	    	
		}
		
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		
		if (!this.uavsDeployed) {
			if (this.placeUavsMode) {
	
				//Place a UAV at the point where the mouse was clicked
				allUavs.add(new Uav(e.getX(), e.getY(), uavNumber++, this, this.allUavs));
				
			} else {
				
				//Remove UAV that was clicked on
				removeUavAt(e.getX(), e.getY());
				
			}
		}
		
		//Update the display
		repaint();

	}
	
	@Override
	public synchronized void repaint() {
		super.repaint();
	}
	
	private boolean removeUavAt(int xCoordinate, int yCoordinate) {
		
		double distanceToUavCenter = 0;
		//Loop through all UAVs and remove the one that was clicked on
		for (Uav uav : allUavs) {
			
			distanceToUavCenter = Math.pow(Math.pow(uav.getxCoordinate() - xCoordinate, 2.0) + Math.pow(uav.getyCoordinate() - yCoordinate, 2.0), 0.5);
			//If the mouse was clicked within the UAV boundary, then remove it
			if (distanceToUavCenter <= UAV_DIMENSION / 2) {
				allUavs.remove(uav);
				break;
			}
		
		}
		
		return false;
		
	}
	
	synchronized void updateUavPath(int xCoordinate, int yCoordinate, Color pathColor) {
		
		this.uavPathPoints.add(new UavPathPoint(xCoordinate, yCoordinate, pathColor));
		repaint();
	}
	
	synchronized void updateVoronoiCellEdges(List<GraphEdge> voronoiCellEdges) {
		
		this.voronoiCellEdges = voronoiCellEdges;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}


	@Override
	public void mouseReleased(MouseEvent e) {
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	}


	@Override
	public void mouseExited(MouseEvent e) {
	}


	public boolean isPlaceUavsMode() {
		return placeUavsMode;
	}


	public void setPlaceUavsMode(boolean placeUavsMode) {
		this.placeUavsMode = placeUavsMode;
	}


	public boolean isShowVoronoiCells() {
		return showVoronoiCells;
	}


	public void setShowVoronoiCells(boolean showVoronoiCells) {
		this.showVoronoiCells = showVoronoiCells;
	}


	public boolean isShowUavPaths() {
		return showUavPaths;
	}


	public void setShowUavPaths(boolean showUavPaths) {
		this.showUavPaths = showUavPaths;
	}
}
