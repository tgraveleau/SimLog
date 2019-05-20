package UI;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import Moteur.SimLogCircuit;
import Moteur.SimLogTruthTable;

//import m2.acdi.SimLogTTSelWin;

public class SimLogTopBar extends JToolBar implements ActionListener {
	
	private final String buttonLabels[] = { "formula", "karnaugh", "NandTool", "NorTool", "truthtable", "reorganise", "edit", "simulation"};

	private final String tabTips[] = { "Enter a formula", "Generate a Karnaugh table", "Turn gates to NAND gates", "Turn gates to NOR gates",
			"Generate the truth table", "Reorganise the elements","Edition mode", "Simulation mode",  };
	
	private JButton tabButtons[];
	//private int markedButton = 0;
	private SimLogWin appli = null;
	private SimLogCircuit circuit = null;
	public SimLogPanel mainPanel;
	
	/**
	 * default constructor
	 *
	 * @param win
	 *            parent application
	 */

	public SimLogTopBar(SimLogWin win, SimLogCircuit circuit, SimLogPanel mainPanel) {
		super("gates", SwingConstants.HORIZONTAL);

		int i;
		JButton b;
		Applet applet;

		appli = win;
		applet = win.getApplet();
		
		this.circuit=circuit;
		this.mainPanel=mainPanel;

		tabButtons = new JButton[buttonLabels.length];

		if (applet == null) {

			for (i = 0; i < buttonLabels.length; i++) {
				b = new JButton(new ImageIcon("." + appli.getFileSeparator()
						+ "img" + appli.getFileSeparator() + buttonLabels[i]
						+ ".gif"));
				b.setToolTipText(tabTips[i]);
				b.addActionListener(this);
				// b.setFont( font );
				b.setBackground(Color.white);
				b.setForeground(Color.black);
				tabButtons[i] = b;
				add(b);
			}
			tabButtons[5].setEnabled(false);
		} else {
			Image tabImg[] = new Image[buttonLabels.length];
			MediaTracker tracker = new MediaTracker(this);

			for (i = 0; i < buttonLabels.length; i++) {
				tabImg[i] = applet.getImage(applet.getDocumentBase(), "img/"
						+ buttonLabels[i] + ".gif");
				tracker.addImage(tabImg[i], i);
			}

			try {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				tracker.waitForAll();
			} catch (Exception e) {
				System.out.println(e.toString());
			}

			for (i = 0; i < buttonLabels.length; i++) {
				b = new JButton(new ImageIcon(tabImg[i]));
				b.setToolTipText(tabTips[i]);
				b.addActionListener(this);
				// b.setFont( font );
				b.setBackground(Color.white);
				b.setForeground(Color.black);
				tabButtons[i] = b;
				add(b);
			}

		}
		//tabButtons[0].setBackground(Color.red);
	}

	/**
	 * action manager
	 */

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			int i;
			JButton b = (JButton) e.getSource();
			for (i = 0; i < tabButtons.length; i++) {
				if (b == tabButtons[i]) {
					// unmark button
					switch (i) {
					case 0:
						enterFormula();
						break;
					case 1:
						karnaugh();
						break;
					case 2:
						//appli.notImplemented();
						if (!mainPanel.validation()) {
							appli.windowWarning("Circuit is not valid	");
						}else {
							circuit.toNand();
							appli.repaint();
						}
						break;
					case 3:
						appli.notImplemented();
						break;
						//JONATHAN
					case 4:
						if (!mainPanel.validation()) {
							appli.windowWarning("Circuit is not valid	");
						} else {
							if ((circuit.getNbrSwitch() > 0) && (circuit.getNbrLED() > 0)) {
								SimLogTTSelWin win = new SimLogTTSelWin(appli);
								win.setVisible(true);
							}
						}
						break;
					case 5:
						circuit.reorganize();
						appli.repaint();
						break;
					case 6:
						mainPanel.edition();
						//tabButtons[5].setEnabled(false);
						//tabButtons[6].setEnabled(true);
						break;
					case 7:
						if (!mainPanel.validation()) {
							appli.windowWarning("Circuit is not valid	");
						} else {
							mainPanel.simulation();
							//tabButtons[5].setEnabled(true);
							//tabButtons[6].setEnabled(false);
						}
						break;
					
					}					
					
				}
			}
			appli.mainPanel.unsetCanvasIntersectFlag();
		

		}
	}
	
	
	
	private void enterFormula() {
		SimLogFormulaWin win = new SimLogFormulaWin(appli);

		win.setVisible(true);
		if (win.getState() == true) {
			circuit.buildCircuit(win.getRoot());
			circuit.reorganize();
		}
		mainPanel.edition();
	}

	private void karnaugh() {	
		SimLogPlaParamWin win = new SimLogPlaParamWin(appli);
		win.centerComponent();
		win.setVisible(true);
	}
	
	/**
	 * chose simulation mode in which the Toolbar is not accessible
	 */

	public void simulation() {
		
		tabButtons[5].setEnabled(true);
		tabButtons[6].setEnabled(false);
	}

	/**
	 * chose edition mode in which the Toolbar is accessible
	 */

	public void edition() {
		tabButtons[5].setEnabled(false);
		tabButtons[6].setEnabled(true);
	}

}
