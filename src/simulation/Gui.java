package simulation;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author ps A frame with a JTextArea in a JScrollPane which serves as a log -
 *         window
 */
public class Gui extends JFrame {

	private JTextArea jtaDebug;

	public void init(Simulator sim) {

		setTitle("AirTraffic");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jtaDebug = new JTextArea();
		jtaDebug.setEditable(false);
		JScrollPane jsp = new JScrollPane(jtaDebug);

		add(jsp);

		setLocation(100, 100);
		setSize(1200, 600);
		setVisible(true);
	}

	/**
	 * prints a String to the window (with a added line feed)
	 * 
	 * @param string
	 */
	public void println(String string) {
		jtaDebug.append(string + "\n");

	}

}
