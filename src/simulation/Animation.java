package simulation;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JTextPane;

public class Animation extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Simulator sim;

	private int border = 80;

	private float xmin = 497000;
	private float xmax = 684000;

	private float ymin = 120000;
	private float ymax = 287000;

	// Liste mit allen Flugzeigen die geziechnet werden sollen
	HashMap<Aircraft, Object> querySet = new HashMap<Aircraft, Object>();

	public Animation(Simulator sim) throws HeadlessException {
		super();

		this.sim = sim;

		setBounds(0, 0, 600, 600);
		setLayout(null);
	}

	// Zeichnen
	public void paint(Graphics g) {
		super.paint(g);

		removeAll();
		
		JTextPane stat = new JTextPane();
		stat.setText("Hallo world");
		stat.setBounds(100, 100, 100, 100);
		add(stat);

		// System.out.print("einheitx " + einheitX + " einheity " + einheitY);
		System.out.println("height: " + getHeight() + " width:" + getWidth());

		SimWorld world = sim.getSimWorld();
		HashMap<String, Airport> aps = world.getAirports();
		for (String s : aps.keySet()) {
			Airport a = aps.get(s);

			double flughafenX = getXonMap(a.getX1());
			double flughafenY = getYonMap(a.getY2());

			JTextPane pane = new JTextPane();
			pane.setBounds((int) flughafenX, (int) flughafenY, 100, 20);
			pane.setText(s);
			add(pane);
			System.out.println(s + " flughafenX: " + flughafenX
					+ " flughafenY: " + flughafenY);

		}
		
		HashMap<String, Aircraft> acs = world.getAircrafts();
		for (String s : acs.keySet()) {
			Aircraft ac = acs.get(s);
			ac.getLastX();
			ac.getLastY();
		}

	}

	public double getXonMap(double CoordinateX) {
		float width = getWidth() - border; // kartenrand

		float einheitX = width / (xmax - xmin);

		return width - (einheitX * (xmax - CoordinateX)) + border / 2;

	}

	public double getYonMap(double CoordinateY) {
		float height = getHeight() - border; // kartenrand
		float einheitY = height / (ymax - ymin);

		return einheitY * (ymax - CoordinateY) + border / 2;

	}

}
