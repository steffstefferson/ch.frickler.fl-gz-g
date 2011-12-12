package simulation.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
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

		setBounds(0, 0, 1024, 768);
		setLayout(null);

	}

	private void printAirports(Graphics g) {
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

	public void paint(Graphics g) {
		super.paint(g); // this causes the flackering

		printAirports(g);

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

		return (int) (width - (einheitX * (X_MAX - CoordinateX)) + BOARDER / 2);

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

		return (int) (einheitY * (Y_MAX - CoordinateY) + BOARDER / 2);
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
