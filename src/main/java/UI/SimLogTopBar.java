package UI;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import Moteur.SimLogCircuit;

//import m2.acdi.SimLogTTSelWin;

public class SimLogTopBar extends JToolBar implements ActionListener {
	
	private final String buttonLabels[] = { "formula", "karnaugh", "nandnor", "truthtable", "reorganise", };

	private final String tabTips[] = { "Enter a formula", "Generate a Karnaugh table", "Do...something?",
			"Generate the truth table", "Reorganise the elements", };
	
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
					System.out.println(i);
					switch (i) {
					case 0:
						enterFormula();
						break;
					case 1:
						karnaugh();
						break;
					case 2:
						appli.notImplemented();
						break;
					case 3:
						if (!mainPanel.validation()) {
							appli.windowWarning("Circuit is not valid	");
						} else {
							if ((circuit.getNbrSwitch() > 0) && (circuit.getNbrLED() > 0)) {
								SimLogTTSelWin win = new SimLogTTSelWin(appli);
								win.setVisible(true);
							}
						}
						break;
					case 4:
						circuit.reorganize();
						appli.repaint();
						break;
					}
						
					
					break;
				}
			}
			appli.mainPanel.unsetCanvasIntersectFlag();
//			
//			if (e.getSource() == menuToolsFormula) {
//				  enterFormula();
//			} else if (e.getSource() == menuToolsKarnaugh) {
//					karnaugh();
//			} else if (e.getSource() == menuToolsNandNor) {
//					notImplemented();
//			} else if (e.getSource() == menuToolsTruthTable) {
//					if (!mainPanel.validation()) {
//						windowWarning( "Circuit is not valid	" );
//					}  else {
//						if ((circuit.getNbrSwitch()>0) && (circuit.getNbrLED()>0)) {
//							SimLogTTSelWin win=new SimLogTTSelWin(this);
//							win.setVisible(true);
//						}
//					}
//					mainPanel.edition();
//			} else if (e.getSource() == menuToolsReorganize) {
//					circuit.reorganize();
//					repaint();
//			}
			

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

}
