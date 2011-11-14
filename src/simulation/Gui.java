package simulation;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.Document;


/**
 * @author ps
 * A frame with a JTextArea in a JScrollPane which serves as a log - window
 */
public class Gui extends JFrame {
	private JTextArea jt;

	public void init(){
		setTitle("AirTraffic");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jt = new JTextArea();
		jt.setEditable(false);
		JScrollPane jsp = new JScrollPane(jt);
		JPanel contentPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.weightx = 1.0;
        c.weighty = 1.0;
		contentPane.add(jsp,c);
		setContentPane(contentPane);
		// now make it visible
		setLocation(100,100);
		setSize(600,600);
		setVisible(true);
	}

	/**
	 * prints a String to the window (with a added line feed)
	 * @param string
	 */
	public void println(String string){
		jt.append(string+"\n");
	}
}
