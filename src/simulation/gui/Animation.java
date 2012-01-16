package simulation.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
	private static Animation animation;
	private ImagePanel imagePanel;
	private AirportPanel airportPanel;

	public static Animation init(Simulator sim, Clock c) {
		animation = new Animation(sim, c);
		return animation;
	}

	public static Animation getInstance() {
		return animation;

	}

	private Animation(Simulator sim, Clock c) throws HeadlessException {
		super();

		this.sim = sim;
		this.clock = c;
		this.aircraftList = new HashSet<Aircraft>();

		this.setTitle("ProcessId: " + sim.getIdOfProcessor() + " isMaster: " + sim.isMaster());

		setBounds(0, 0, 1024, 768);
		setLayout(null);

		airportPanel = new AirportPanel();
		imagePanel = new ImagePanel("res/swiss.jpg");
		this.getContentPane().add(airportPanel);
		this.getContentPane().add(imagePanel);

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

		int ret = (int) (einheitY * (Y_MAX - CoordinateY) + BOARDER / 2);

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
		imagePanel.repaint();
	}

	public void repaintAircrafts() {
		airportPanel.repaint();
	}

	private class AirportPanel extends JPanel {

		public AirportPanel() {
			this.setSize(Animation.this.getSize());
			setLayout(null);
			setVisible(true);
			setOpaque(false);
		}

		public void paint(Graphics g) {
			try {
				super.paint(g); // this causes the flackering
				printAircrafts(g);
				// printAircrafts(g);
			} catch (Exception ex) {
				// catch the ConcurrentModificationException
			}
		}

		private void printAircrafts(Graphics g) {
			for (Aircraft ac : aircraftList) {

				ac.calcPosition(clock.currentSimulationTime());

				int x = getXonMap(ac.getLastX());
				int y = getYonMap(ac.getLastY());

				g.setColor(Color.RED);
				g.fillOval(x, y, 5, 5);

			}

		}

	}

	private class ImagePanel extends JPanel {

		private Image img;

		public ImagePanel(String path) {

			java.net.URL url = Animation.class.getResource(path);
			if (url == null) {
				System.out.println("Null");
			}
			img = new ImageIcon(url, "Switzerland").getImage();

			setSize(Animation.this.getSize());
			setLayout(null);

			setSize(Animation.this.getSize());
			setLayout(null);
		}

		public void paintComponent(Graphics g) {

			this.setSize(Animation.this.getWidth(), Animation.this.getHeight());
			g.drawImage(img, 0, 0, Animation.this.getWidth(), Animation.this.getHeight(), this);
			printAirports(g);
		}

		private void printAirports(Graphics g) {
			SimWorld world = sim.getSimWorld();
			HashMap<String, Airport> aps = world.getAirports();
			for (String s : aps.keySet()) {
				Airport a = aps.get(s);

				int flughafenX = getXonMap(a.getX1());
				int flughafenY = getYonMap(a.getY1());
				g.setColor(Color.BLUE);
				// g.drawString(s, flughafenX + 10, flughafenY - 10);

				int[] x = new int[] { flughafenX, getXonMap(a.getX2()) };
				int[] y = new int[] { flughafenY, getYonMap(a.getY2()) };
				g.drawPolygon(x, y, x.length);

				// g.setColor(a.getColor());

			}

		}

	}

}