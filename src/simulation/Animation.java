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

		setBounds(0, 0, 600, 600);
		setLayout(null);
	}

	// Zeichnen
	public void paint(Graphics g) {
		super.paint(g);

		getContentPane().removeAll();

		
		//System.out.println("height: " + getHeight() + " width:" + getWidth());

		SimWorld world = sim.getSimWorld();
		HashMap<String, Airport> aps = world.getAirports();
		for (String s : aps.keySet()) {
			Airport a = aps.get(s);

			int flughafenX = getXonMap(a.getX1());
			int flughafenY = getYonMap(a.getY2());

			// JTextPane pane = new JTextPane();
			// pane.setBounds(flughafenX, flughafenY, 100, 20);
			// pane.setText(s);
			// add(pane);
			//System.out.println(s + " flughafenX: " + flughafenX
			//		+ " flughafenY: " + flughafenY);
			g.drawString(s, (int) flughafenX, (int) flughafenY);
			g.fillOval(flughafenX, flughafenY, 5, 5);

		}

//		HashMap<String, Aircraft> acs = world.getAircrafts();
		//for (String airCraftName : acs.keySet()) {
		for (Aircraft ac : aircraftList) {
	
//			Aircraft ac = acs.get(airCraftName);

			ac.calcPosition(currentTime);

			int x = getXonMap(ac.getLastX());
			int y = getYonMap(ac.getLastY());
			//System.out.print(airCraftName + " is at x " + x + " y " + y);

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
		System.out.println("Aircraft added: "  + aircraft + " size:" + aircraftList.size());
		aircraftList.add(aircraft);
	}

	public void removeFromQuery(Aircraft aircraft) {
		System.out.println("Aircraft removed: "  + aircraft + " size:" + aircraftList.size());
		aircraftList.remove(aircraft);
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	
	

}
