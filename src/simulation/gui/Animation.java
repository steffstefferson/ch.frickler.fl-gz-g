package simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import simulation.Simulator;
import simulation.logic.Clock;
import simulation.model.Aircraft;
import simulation.model.Airport;
import simulation.model.SimWorld;

public class Animation extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int BOARDER = 160;

	private static final float X_MIN = 497000;
	private static final float X_MAX = 684000;
	private static final float Y_MIN = 120000;
	private static final float Y_MAX = 287000;

	private Set<Aircraft> aircraftList;
	private Simulator sim;
	private Clock clock;

	public Animation(Simulator sim, Clock c) throws HeadlessException {
		super();

		this.sim = sim;
		this.clock = c;
		this.aircraftList = new HashSet<Aircraft>();

		this.setTitle("ProcessId: "+sim.getIdofProcessor()+" isMaster: "+sim.isMaster());
		
		setBounds(0, 0, 1024, 768);
		setLayout(null);

	}

	public void paint(Graphics g) {
		try{
		super.paint(g); // this causes the flackering
		printAirports(g);
		printAircrafts(g);
		}catch(Exception ex){
			// catch the ConcurrentModificationException
		}
	}
	
	private void printAirports(Graphics g) {
		SimWorld world = sim.getSimWorld();
		HashMap<String, Airport> aps = world.getAirports();
		for (String s : aps.keySet()) {
			Airport a = aps.get(s);

			int flughafenX = getXonMap(a.getX1());
			int flughafenY = getYonMap(a.getY1());
			g.setColor(Color.BLACK);
			g.drawString(s, flughafenX + 10, flughafenY - 10);

			int[] x = new int[] { flughafenX, getXonMap(a.getX2()) };
			int[] y = new int[] { flughafenY, getYonMap(a.getY2()) };
			g.setColor(a.getColor());
			g.drawPolygon(x, y, x.length);
			
			Dimension dim = a.getControlarea();
			int xArea = getXonMap(a.getX1()-dim.getWidth());
			int yArea = getYonMap(a.getY1()-dim.getHeight());
			
			int xArea1 = getXonMap(a.getX1()+dim.getWidth());
			int yArea1 = getYonMap(a.getY1()+dim.getHeight());
			
			g.drawRect(xArea,yArea1,xArea1-xArea,yArea-yArea1);

		}

	}

	private void printAircrafts(Graphics g) {
		// TODO: Fredsave because of ConcurrentModificationException?
		for (Aircraft ac : aircraftList) {

			ac.calcPosition(clock.currentSimulationTime());

			int x = getXonMap(ac.getLastX());
			int y = getYonMap(ac.getLastY());

			g.setColor(Color.RED);
			g.fillOval(x, y, 5, 5);

		}
	}

	/**
	 * Scaling coordinate point to panel size
	 * 
	 * @param CoordinateX
	 * @return Coordinate scaled
	 */
	public int getXonMap(double CoordinateX) {
		float width = getWidth() - BOARDER; // map boarder

		float einheitX = width / (X_MAX - X_MIN);
		int ret = (int) (width - (einheitX * (X_MAX - CoordinateX)) + BOARDER / 2);

		return ret < 0 ? 0 : ret;
	}

	/**
	 * Scaling coordinate point to panel size
	 * 
	 * @param CoordinateY
	 * @return Coordinate scaled
	 */
	public int getYonMap(double CoordinateY) {
		float height = getHeight() - BOARDER; // map boarder
		float einheitY = height / (Y_MAX - Y_MIN);

		int ret =  (int) (einheitY * (Y_MAX - CoordinateY) + BOARDER / 2);
		
		return ret < 0 ? 0 : ret;
	}

	public void addToQuery(Aircraft aircraft) {
		aircraftList.add(aircraft);
	}

	public void removeFromQuery(Aircraft aircraft) {
		aircraftList.remove(aircraft);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.repaint();
	}

}
