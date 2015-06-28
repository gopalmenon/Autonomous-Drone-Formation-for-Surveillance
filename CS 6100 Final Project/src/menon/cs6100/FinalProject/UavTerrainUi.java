package menon.cs6100.FinalProject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class UavTerrainUi {

	static final int TERRAIN_WIDTH = 640;
	static final int TERRAIN_HEIGHT = 480;

	private UavTerrainPanel uavTerrainPanel;

	public UavTerrainUi() {
        
        //Create the UAV terrain
        this.uavTerrainPanel = new UavTerrainPanel();
        uavTerrainPanel.setPreferredSize(new Dimension(TERRAIN_WIDTH, TERRAIN_HEIGHT));

	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
    	
        //Create and set up the window.
        JFrame frame = new JFrame("Top View of UAVs Flying over terrain");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);        
        menuBar.setPreferredSize(new Dimension(TERRAIN_WIDTH, 20));
        
        //Create user menus
        JMenu fileMenu, uavsMenu, visualizationMenu;
        JMenuItem exitMenuItem, placeUavsMenuItem, removeUavsMenuItem, goIntoFormationMenuItem;
        JCheckBoxMenuItem showVoronoiCellsMenuItem, showUavPathMenuItem;
        
        //File menu
        fileMenu = new JMenu("File");
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {System.exit(0);} });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);        
        
        //UAVs menu
        uavsMenu = new JMenu("UAVs");
        placeUavsMenuItem = new JMenuItem("Place UAVs");
        placeUavsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) { UavTerrainUi.this.uavTerrainPanel.setPlaceUavsMode(true); } });
        uavsMenu.add(placeUavsMenuItem);
        
        removeUavsMenuItem = new JMenuItem("Remove UAVs");
        removeUavsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) { UavTerrainUi.this.uavTerrainPanel.setPlaceUavsMode(false); } });
        uavsMenu.add(removeUavsMenuItem);
        menuBar.add(uavsMenu);    
        
        goIntoFormationMenuItem = new JMenuItem("Go Into Formation");
        goIntoFormationMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) { UavTerrainUi.this.uavTerrainPanel.deployUavs();; } });
        uavsMenu.add(goIntoFormationMenuItem);
        
        //Visualization menu
        visualizationMenu = new JMenu("Visualization");
        showVoronoiCellsMenuItem = new JCheckBoxMenuItem("Show Voronoi Cells", true);
        showVoronoiCellsMenuItem.addItemListener(new ItemListener() {public void itemStateChanged(ItemEvent e) {UavTerrainUi.this.uavTerrainPanel.setShowVoronoiCells(e.getStateChange() == ItemEvent.SELECTED ? true : false);}});
        visualizationMenu.add(showVoronoiCellsMenuItem);        
        showUavPathMenuItem = new JCheckBoxMenuItem("Show UAV Paths", true);
        showUavPathMenuItem.addItemListener(new ItemListener() {public void itemStateChanged(ItemEvent e) {UavTerrainUi.this.uavTerrainPanel.setShowUavPaths(e.getStateChange() == ItemEvent.SELECTED ? true : false);}});
        visualizationMenu.add(showUavPathMenuItem);
        menuBar.add(visualizationMenu);     
        
        //Set the menu bar and add the label to the content pane.
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(this.uavTerrainPanel, BorderLayout.CENTER);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	UavTerrainUi uavTerrainUi = new UavTerrainUi();
            	uavTerrainUi.createAndShowGUI();
            }
        });
    }

}
