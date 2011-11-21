package simulation;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

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
		jtaDebug.setAutoscrolls(true);
		JScrollPane jsp = new JScrollPane(jtaDebug);
		
		// Scrolls as long as you don't klick inside the textarea
		DefaultCaret caret = (DefaultCaret)jtaDebug.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

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
