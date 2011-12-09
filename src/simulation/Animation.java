package simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

public class Animation extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Simulator sim;

	private int border = 160;

	private float xmin = 497000;
	private float xmax = 684000;

	private float ymin = 120000;
	private float ymax = 287000;

	private long currentTime;

	// Liste mit allen Flugzeigen die geziechnet werden sollen
	HashMap<Aircraft, Object> querySet = new HashMap<Aircraft, Object>();

	private Set<Aircraft> aircraftList;

	public Animation(Simulator sim) throws HeadlessException {
		super();

		this.sim = sim;
		this.aircraftList = new HashSet<Aircraft>();

		setBounds(0, 0, 1024, 768);
		setLayout(null);
		
	}

	private void printAirports(Graphics g){
		SimWorld world = sim.getSimWorld();
		HashMap<String, Airport> aps = world.getAirports();
		for (String s : aps.keySet()) {
			Airport a = aps.get(s);

			int flughafenX = getXonMap(a.getX1());
			int flughafenY = getYonMap(a.getY1());

			g.drawString(s, flughafenX + 10, flughafenY - 10);

			int[] x = new int[] { flughafenX, getXonMap(a.getX2()) };
			int[] y = new int[] { flughafenY, getYonMap(a.getY2()) };

			g.drawPolygon(x, y, x.length);
		
		}

	}
	
	// Zeichnen
	public void paint(Graphics g) {
		super.paint(g); // this causes the flackering
		
		printAirports(g);

		for (Aircraft ac : aircraftList) {

			ac.calcPosition(currentTime);

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
		float width = getWidth() - border; // kartenrand

		float einheitX = width / (xmax - xmin);

		return (int) (width - (einheitX * (xmax - CoordinateX)) + border / 2);

	}

	/**
	 * Scaling coordinate point to panel size
	 * 
	 * @param CoordinateY
	 * @return Coordinate scaled
	 */
	public int getYonMap(double CoordinateY) {
		float height = getHeight() - border; // kartenrand
		float einheitY = height / (ymax - ymin);

		return (int) (einheitY * (ymax - CoordinateY) + border / 2);

	}

	public void addToQuery(Aircraft aircraft) {
		System.out.println("Aircraft added: " + aircraft + " size:"
				+ aircraftList.size());
		aircraftList.add(aircraft);
	}

	public void removeFromQuery(Aircraft aircraft) {
		System.out.println("Aircraft removed: " + aircraft + " size:"
				+ aircraftList.size());
		aircraftList.remove(aircraft);
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

}
