/* ---------------------------------------------------------------------------
 *  SimLog v 2.2
 *  Copyright (C) 2002-2003 Jean-Michel RICHER
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 *
 *  For any  comment please write to Jean-Michel RICHER at
 *  Jean-Michel.Richer@univ-angers.fr
 * ------------------------------------------------------------------------ */

/* //////////////////////////////////////////////////////////////////////// */
/* // ------------------------------------------------------------------ // */
/* // | class   :  SimLogFormulaWin                                    | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2003                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This window helps the user define the number of switch and LED
 *   that will be used for the circuit
 *
 *   @version 2.2, 14 October 2003
 *   @author Jean-Michel Richer
 */

package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Gate.SimLogGate;
import Moteur.SimLogAnaSynt;
import Moteur.SimLogCircuit;
import Moteur.SimLogFormula;
import Moteur.SimLogFormulaNode;
import Moteur.SimLogLink;
import Moteur.SimLogTruthTable;


import java.util.Vector;

public class SimLogFormulaWin extends JDialog implements ActionListener {

	//
	// Modes
	//

	public final static int ENTER_FORMULA = 1;
	public final static int BUILD_FORMULA = 2;
	int mode = 0;

	//
	// variables
	//

	private SimLogWin appli;
	private SimLogCircuit circuit;
	private JTextArea tFormula;
	private JButton bOk;
	private JButton bCancel;
	private JButton bSimplify = null;
	private boolean state = true;
	private SimLogFormula formula = null;
	private SimLogGate initialGate = null;

	/**
	 * create a panel with Ok and Cancel buttons
	 * 
	 * @return JPanel
	 */

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		if (mode == ENTER_FORMULA) {
			bOk = new JButton(" Draw ");
			bOk.addActionListener(this);
			bSimplify = new JButton("Simplify");
			bSimplify.addActionListener(this);
			panel.add(bSimplify);
		} else {
			bOk = new JButton("  Ok  ");
			bOk.addActionListener(this);
		}
		bCancel = new JButton(" Cancel ");
		bCancel.addActionListener(this);
		panel.add(bOk);
		panel.add(bCancel);
		return panel;
	}

	/**
	 * create a message panel
	 * 
	 * @return JPanel
	 */

	private JPanel createMessagePanel() {
		int i;
		JPanel panel = new JPanel();

		tFormula = new JTextArea();
		tFormula.setRows(3);
		tFormula.setColumns(40);
		JScrollPane scrollpane = new JScrollPane(tFormula);

		panel.setLayout(new BorderLayout());
		panel.add(BorderLayout.NORTH, new JLabel("Enter formula :"));
		panel.add(BorderLayout.CENTER, scrollpane);
		panel.add(BorderLayout.EAST, createHelpList());
		panel.add(BorderLayout.SOUTH, createLightsPanel());
		return panel;
	}
	
	/**
	 * create a JRadio panel
	 * 
	 * @return JPanel
	 */

	private JPanel createLightsPanel() {
		
		JPanel panel = new JPanel();
		circuit = appli.getCircuit();
		Vector<SimLogGate> gates = circuit.getListOfGates();		
		ButtonGroup group = new ButtonGroup();
		
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel("Show : "));
		JRadioButton none = new JRadioButton("None",true);
		none.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				tFormula.setText("");
				
			}
		});
		panel.add(none);
		group.add(none);
		JRadioButton radio;
		for (SimLogGate gate : gates) {
			if (gate.getType() == SimLogGate.LED_GATE) {
				radio = new JRadioButton(gate.getName());
				group.add(radio);
				radio.setActionCommand(gate.getName());
				radio.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String name = e.getActionCommand();
						SimLogGate g = circuit.findGateByName(name);
						showFormula(g);
						
					}
					
				});
				panel.add(radio);
			}
		}
		return panel;
	}
	
	/**
	 * Show the formula of the selected light
	 */
	private void showFormula(SimLogGate g) {
		String f = buildFormulaFromGate(g);
		tFormula.setText(f);
	}

	/**
	 * method used to center window on screen
	 */

	public void centerComponent() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenDim = tk.getScreenSize();
		screenDim.width = (screenDim.width - this.getWidth()) / 2;
		screenDim.height = (screenDim.height - this.getHeight()) / 2;
		this.setLocation(screenDim.width, screenDim.height);
	}

	/**
	 * constructor 1 we create a textarea for the user to enter a formula that
	 * will be implemented as a circuit
	 * 
	 * @param parent
	 *            parent Frame
	 */

	public SimLogFormulaWin(SimLogWin parent) {
		super(parent, true);
		setTitle("Formula");
		appli = parent;
		mode = ENTER_FORMULA;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center", createMessagePanel());
		getContentPane().add("South", createButtonPanel());
		pack();
	}

	/**
	 * constructor 2 we create an arithmatic representation of a part of a
	 * circuit starting at the gate given as a parameter
	 * 
	 * @param parent
	 *            parent Frame
	 * @param gate
	 *            SimLogGate
	 */

	public SimLogFormulaWin(SimLogWin parent, SimLogGate gate) {
		super(parent, true);
		setTitle("Enter formula");
		appli = parent;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center", createMessagePanel());
		tFormula.setEditable(false);
		getContentPane().add("South", createButtonPanel());
		initialGate = gate;
		buildFormula();
		mode = BUILD_FORMULA;
		pack();
	}

	/**
	 * Create the help panel
	 * 
	 * @return Component
	 */
	private Component createHelpList() {
		JTextPane helpList = new JTextPane();
		helpList.setBackground(getBackground());
		helpList.setText("HELP:\n\n +   : OR\n  .   : AND\n  -   : NOT\n ^   : XOR");
		helpList.setEditable(false);
		return helpList;
	}

	public boolean isOk(String s) {
		SimLogAnaSynt anasynt = new SimLogAnaSynt(s);
		anasynt.expr();
		if (anasynt.isCorrect()) {
			// anasynt.printStack();
			formula = new SimLogFormula(anasynt.getStack());
			if (formula.isCorrect())
				return true;
		}
		return false;
	}

	/**
	 * action manager
	 */

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bOk) {
			String s = tFormula.getText().trim();
			if (mode == ENTER_FORMULA) {
				if (isOk(s)) {
					state = true;
					dispose();
				} else
					appli.windowWarning("Formula is not correct");
			} else {
				dispose();
			}
		} else if (e.getSource() == bCancel) {
			state = false;
			dispose();
		} else if (e.getSource() == bSimplify) {
			tryToSimplify();
		}
	}

	private void tryToSimplify() {
		int i;
		SimLogCircuit cc;
		Vector vIn, vOut;

		String s = tFormula.getText().trim();
		if (!isOk(s)) {
			appli.windowWarning("Formula is not correct");
			return;
		}
		cc = new SimLogCircuit();
		vIn = new Vector();
		vOut = new Vector();
		cc.buildCircuit(getRoot());
		for (i = 0; i < cc.getNbrSwitch(); i++) {
			vIn.add(cc.getSwitch(i));
		}
		vOut.add(cc.getLED(0));
		SimLogTruthTable tt = new SimLogTruthTable(cc, vIn, vOut);
		tt.generateTable();
		tt.print();
		// SimLogTruthTableWin win=new SimLogTruthTableWin(appli,tt);
		// win.show();
		// SimLogListMask sllm = new
		// SimLogListMask(canvas.getTablePla(),NbVar,NbMonome);
	}

	/**
	 * return user confirmation
	 * 
	 * @return <code>true</code> if user clicked on the Ok button,
	 *         <code>false</code> if user clicked on the Cancel button
	 */

	public boolean getState() {
		return state;
	}

	/**
	 * return formula
	 * 
	 * @return String
	 */

	private String getFormula() {
		return tFormula.getText().trim();
	}

	public SimLogFormulaNode getRoot() {
		return formula.getRoot();
	}

	private void buildFormula() {
		String s;
		s = buildFormulaFromGate(initialGate);
		tFormula.setText(s);
	}

	private String buildFormulaFromGate(SimLogGate gate) {
		SimLogLink link;
		String s, s1, s2;

		s = s1 = s2 = null;
		switch (gate.getType()) {
		case SimLogGate.SWITCH_GATE:
			s = new String(gate.getName());
			break;
		case SimLogGate.AND_GATE:
		case SimLogGate.OR_GATE:
		case SimLogGate.NAND_GATE:
		case SimLogGate.NOR_GATE:
		case SimLogGate.XOR_GATE:
			link = gate.getInputLink(0);
			if (link != null)
				s1 = buildFormulaFromGate(link.getOutputGate());
			else
				s1 = "?";
			link = gate.getInputLink(1);
			if (link != null)
				s2 = buildFormulaFromGate(link.getOutputGate());
			else
				s2 = "?";
			break;
		case SimLogGate.NOT_GATE:
			link = gate.getInputLink(0);
			if (link != null) {
				s1 = buildFormulaFromGate(link.getOutputGate());
				if (link.getOutputGate().getType() == SimLogGate.SWITCH_GATE)
					s = new String("-" + s1);
				else
					s = new String("-(" + s1 + ")");
			} else
				s = new String("-(?)");
			break;
		case SimLogGate.LED_GATE:
			link = gate.getInputLink(0);
			if (link != null)
				return buildFormulaFromGate(link.getOutputGate());
		}
		switch (gate.getType()) {
		case SimLogGate.AND_GATE:
			s = new String("(" + s1 + "." + s2 + ")");
			break;
		case SimLogGate.OR_GATE:
			s = new String("(" + s1 + "+" + s2 + ")");
			break;
		case SimLogGate.NAND_GATE:
			s = new String("-(" + s1 + "." + s2 + ")");
			break;
		case SimLogGate.NOR_GATE:
			s = new String("-(" + s1 + "+" + s2 + ")");
			break;
		case SimLogGate.XOR_GATE:
			s = new String("(" + s1 + "^" + s2 + ")");
			break;
		}
		return s;
	}
}