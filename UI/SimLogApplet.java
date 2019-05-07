package UI;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;


public class SimLogApplet extends Applet implements ActionListener {

	//
	// variables
	//

	private Button bOk;
	private SimLogWin win = null;

	/**
	 * main method for applets
	 */

	public void init() {
		Font font = new Font("TimesRoman", Font.PLAIN, 30);

		bOk = new Button("Run");
		bOk.addActionListener(this);
		bOk.setFont(font);
		setLayout(new BorderLayout());
		add("North", new Label("SimLog v2.1"));
		add("Center", bOk);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bOk) {
			if (win == null) {
				System.err.println("NULL");
			} else {
				System.err.println("NOT NULL");
			}
			if (win == null) {
				win = new SimLogWin(this);
				win.setVisible(true);
			}
		}
	}

	public void destroyFrame() {
		if (win != null) {
			win.dispose();
			win = null;
		}
	}
}