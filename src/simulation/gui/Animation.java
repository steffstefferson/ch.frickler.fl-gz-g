package simulation.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
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

/**
 * Class for providing a GUI to the simulation. The Background of the JFrame is
 * a Map of Switzerland and all airports and airplanes are painted on this
 * background. The simulation gets repainted when a RepaintEvent gets processed.
 * The intervall is determined by global variable REPAINT_GAP.
 * 
 * This class is a singleton because there is only one GUI for each LP.
 * 
 */
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
	private BackgroundPanel imagePanel;
	private AircraftPanel airportPanel;

	/**
	 * Initiates the animation
	 * 
	 * @param sim
	 *            Simulator
	 * @param c
	 *            Clock
	 * @return instance of the animation gui
	 */
	public static Animation init(Simulator sim, Clock c) {
		animation = new Animation(sim, c);
		return animation;
	}

	/**
	 * 
	 * @return singleton of the animation gui
	 */
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

		airportPanel = new AircraftPanel();
		imagePanel = new BackgroundPanel("res/swiss.jpg");
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

	/**
	 * Adds an aircraft to the local aircraftList. Each aircraft in this list
	 * gets painted on the map
	 * 
	 * @param aircraft
	 *            Aircraft to paint on gui
	 */
	public void addToQuery(Aircraft aircraft) {
		aircraftList.add(aircraft);
	}

	/**
	 * 
	 * @param aircraft
	 *            Aircraft to remove
	 */
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

	/**
	 * 
	 * Panel which paints all aircrafts. This panel has the higest Z-Index and
	 * gets repainted with each REPAINT_EVENT
	 * 
	 */
	private class AircraftPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6487012083573542984L;

		public AircraftPanel() {
			this.setSize(Animation.this.getSize());
			setLayout(null);
			setVisible(true);
			setOpaque(false);
		}

		public void paint(Graphics g) {
			try {
				super.paint(g);
				printAircrafts(g);
			} catch (Exception ex) {
				// catch the ConcurrentModificationException
			}
		}

		private void printAircrafts(Graphics g) {
			for (Aircraft ac : aircraftList) {

				ac.calcPosition(clock.currentSimulationTime());
				Point currentPosition = new Point(getXonMap(ac.getLastX()), getYonMap(ac.getLastY()));

				double destX = getXonMap(ac.getDestination().getX1());
				double destY = getYonMap(ac.getDestination().getY1());

				// den richtungsvektor aus dem ziel und der aktuelle position
				// berechnen

				double vecX = 0;
				double vecY = 0;

				// calculate the vector pointing in the flight direction
				if (ac.getState() == Aircraft.ON_HOLDING_LOOP) {
					
					Airport ap = ac.getDestination();
					double vecToAirportX = getXonMap(ap.getX1()) - currentPosition.x;
					double vecToAirportY = getYonMap(ap.getY1()) - currentPosition.y;

					// the vector of the flight direction is orthogonal to the
					// direction between the aircraft and the airport
					vecX = -1 * vecToAirportY;
					vecY = vecToAirportX;

				} else {
					vecX = currentPosition.x - destX;
					vecY = currentPosition.y - destY;
				}

				// calculate the triangle representing our aircraft
				Point[] triangle = getTriangleByVector(currentPosition, vecX, vecY);

				int[] xs = { triangle[0].x, triangle[1].x, triangle[2].x };
				int[] ys = { triangle[0].y, triangle[1].y, triangle[2].y };

				// // Polygon triangle = new Polygon();
				g.setColor(Color.RED);
				g.fillPolygon(xs, ys, 3);

			}

		}

		/**
		 * calculates the points for drawing an aircraft. the aircraft is
		 * calculated by the vector of its flight direction
		 * 
		 * @param currentPosition
		 *            the current position on the map
		 * @param vecX
		 *            the X component of the flight direction vector
		 * @param vecY
		 *            the Y component of the flight direction vector
		 * @return an array of points to draw the aircraft (fillPolygon)
		 */
		private Point[] getTriangleByVector(Point currentPosition, double vecX, double vecY) {
			Point[] triangle = new Point[3];
			// normalize the vector
			double betrag = Math.sqrt(vecX * vecX + vecY * vecY);
			double normVecX = vecX / betrag;
			double normVecY = vecY / betrag;

			// the nose of the aircraft is displayed 15px ahead
			vecX = normVecX * 12;
			vecY = normVecY * 12;

			// calculate the nose
			triangle[0] = new Point((int) (currentPosition.x - vecX), (int) (currentPosition.y - vecY));

			// we use 5 px for the wings
			vecX = normVecX * 5;
			vecY = normVecY * 5;

			// calculate the wings using a +90 and -90 degree rotation of the
			// vector
			triangle[1] = new Point((int) (currentPosition.x + vecY), (int) (currentPosition.y - vecX));
			triangle[2] = new Point((int) (currentPosition.x - vecY), (int) (currentPosition.y + vecX));

			return triangle;
		}

	}

	/**
	 * 
	 * This panel paints the background and all airports on it. It only gets
	 * repainted when the window gets resized
	 * 
	 */
	private class BackgroundPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4122626147951344358L;
		private Image img;

		public BackgroundPanel(String path) {

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
			// Paint each airport runway
			for (String s : aps.keySet()) {
				Airport a = aps.get(s);

				int flughafenX = getXonMap(a.getX1());
				int flughafenY = getYonMap(a.getY1());
				g.setColor(Color.BLUE);

				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(new BasicStroke(8.0f));

				Line2D l = new Line2D.Double(flughafenX, flughafenY, getXonMap(a.getX2()), getYonMap(a.getY2()));

				g2d.draw(l);

			}

		}

	}

}