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
/* // | class   :  SimLogCanvas                                        | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This canvas is used to display the current circuit
 *
 *   @version 2.2, 14 October 2002
 *   @author Jean-Michel Richer
 */

package UI;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;


import Gate.SimLogGate;
import Gate.SimLogSwitchGate;
import Moteur.SimLogCircuit;
import Moteur.SimLogLink;
import Moteur.SimLogTruthTable;

// Listener for PopupMenu

class PopupListener extends MouseAdapter {
	public int mouseX, mouseY;
	private JPopupMenu popup;
	private SimLogCanvas canvas;

	public PopupListener(SimLogCanvas c, JPopupMenu p) {
		popup = p;
		canvas = c;
	}

	public void mousePressed(MouseEvent e) {
		maybeShow(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShow(e);
	}

	public void maybeShow(MouseEvent e) {
		if (e.isPopupTrigger()) {
			SimLogGate gate = null;

			mouseX = e.getX();
			mouseY = e.getY();
			gate = canvas.circuit.getGateAtPos(mouseX, mouseY);
			canvas.setPopupGate(gate);
			if (gate != null) {
				if (gate.getType() == SimLogGate.LED_GATE) {
					canvas.popupItems[1].setEnabled(false);
					canvas.popupItems[4].setEnabled(true);
				} else {
					canvas.popupItems[1].setEnabled(true);
					canvas.popupItems[4].setEnabled(false);
				}
				if (gate.getType() == SimLogGate.SWITCH_GATE) {
					canvas.popupItems[1].setEnabled(false);
				}
				popup.show(e.getComponent(), e.getX(), e.getY());

			}
		}
	}

}

// Main class

public class SimLogCanvas extends JPanel implements MouseListener,
		MouseMotionListener, ActionListener {

	//
	// variables
	//

	private int xMouse, yMouse; // mouse position

	private SimLogWin appli;
	private SimLogToolbar toolbar;

	public SimLogCircuit circuit;
	private Vector listOfGates;
	private SimLogGate selectedGate = null;

	// Parameters for popup menu
	JPopupMenu popup;
	JMenuItem popupItems[];

	private PopupListener popupListener;
	private SimLogGate popupGate = null;

	// parameters used when moving a gate

	private SimLogGate gateToMove = null;
	private int gateToMoveDX = 0;
	private int gateToMoveDY = 0;
	private int gateToMoveXOld;
	private int gateToMoveYOld;

	private boolean showGrid = false;

	// parameters for delete Area

	private int deleStartX;
	private int deleStartY;
	private int deleEndX;
	private int deleEndY;
	private boolean deleFlag = false;
	private int deleXPoly[] = { 0, 0, 0, 0, 0 };
	private int deleYPoly[] = { 0, 0, 0, 0, 0 };

	// parameters used when linking two gates

	private SimLogGate srcGateToLink = null;
	private SimLogGate dstGateToLink = null;
	private int xMouseLink;
	private int yMouseLink;

	//
	// display
	//

	private Font font;
	private boolean intersectFlag = false;
	private int intersectXPos;
	private int intersectYPos;

	/**
	 * default constructor
	 *
	 * @param win
	 *            parent frame or application
	 */

	public SimLogCanvas(SimLogWin win) {
		int i;

		appli = win;
		setCircuit(appli.getCircuit());
		toolbar = win.toolbar;
		font = new Font("Courrier", Font.PLAIN, 10);
		listOfGates = new Vector();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		createPopupMenu();
		popupListener = new PopupListener(this, popup);
		addMouseListener(popupListener);

		// setBackground( Color.black );
		setBackground(Color.white);
		setSize(new Dimension(200, 200));
		setPreferredSize(new Dimension(200, 200));

	}

	public void createPopupMenu() {
		popupItems = new JMenuItem[6];
		popupItems[0] = new JMenuItem("delete");
		popupItems[1] = new JMenuItem("exchange links");
		popupItems[2] = new JMenuItem("rename gate");
		popupItems[3] = new JMenuItem("replace gate");
		popupItems[4] = new JMenuItem("truth table");
		popupItems[5] = new JMenuItem("get formula");

		popup = new JPopupMenu();
		for (int i = 0; i < popupItems.length; i++) {
			popupItems[i].addActionListener(this);
			popup.add(popupItems[i]);
		}
	}

	/**
	 * define circuit to be displayed
	 *
	 * @param c
	 *            circuit
	 */

	public void setCircuit(SimLogCircuit c) {
		circuit = c;
		listOfGates = c.getListOfGates();
	}

	/**
	 * add a new gate to the circuit given its position
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */

	private void addGate(int x, int y) {
		String name = null;

		if (toolbar.getGateType() == SimLogGate.SWITCH_GATE) {
			SimLogNameSwitchWin win = new SimLogNameSwitchWin(appli);
			win.centerComponent();
			win.setVisible(true);
			if (win.getState() == true) {
				name = win.getName();
			} else
				return;
		}
		if (toolbar.getGateType() == SimLogGate.LED_GATE) {
			SimLogNameLEDWin win = new SimLogNameLEDWin(appli);
			win.centerComponent();
			win.setVisible(true);
			if (win.getState() == true) {
				name = win.getName();
			} else
				return;
		}
		circuit.addGate(x, y, toolbar.getGateType(), name);
		repaint();
	}

	private void paintGrid(Graphics g) {
		int i;

		g.setColor(Color.LIGHT_GRAY);
		for (i = 75; i < 1000; i += 80) {
			g.drawLine(0, i, 10000, i);
			g.drawLine(i, 0, i, 10000);
		}
	}

	/**
	 * paint circuit (replace method paint by paintComponent)
	 */

	public void paintComponent(Graphics g) {
		int i, size;
		SimLogGate gate;

		super.paintComponent(g); // paint background

		if (showGrid == true)
			paintGrid(g);

		listOfGates = circuit.getListOfGates();
		size = listOfGates.size();
		setFont(font);
		for (i = 0; i < size; i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			gate.paint(g);
		}

		if (deleFlag == true) {
			g.setColor(Color.red);
			deleXPoly[0] = deleStartX;
			deleYPoly[0] = deleStartY;
			deleXPoly[1] = deleEndX;
			deleYPoly[1] = deleStartY;
			deleXPoly[2] = deleEndX;
			deleYPoly[2] = deleEndY;
			deleXPoly[3] = deleStartX;
			deleYPoly[3] = deleEndY;
			deleXPoly[4] = deleStartX;
			deleYPoly[4] = deleStartY;
			g.drawPolygon(deleXPoly, deleYPoly, 5);
		}

		if (intersectFlag == true) {
			intersectFlag = false;
			g.setColor(Color.red);
			g.drawRect(intersectXPos, intersectYPos, SimLogGate.WIDTH,
					SimLogGate.HEIGHT);
		}

		// link
		if (srcGateToLink != null) {
			g.setColor(Color.magenta);
			g.drawLine(srcGateToLink.x + SimLogGate.WIDTH / 2, srcGateToLink.y
					+ SimLogGate.HEIGHT / 2, xMouseLink, yMouseLink);
		}
	}

	/**
	 * add a link to selected gates. The gates are selected using the mouse
	 * during a pressed, dragged and released event.
	 */

	private int addLink() {
		if (srcGateToLink == null)
			return -1;
		if (dstGateToLink == null)
			return -2;

		if (srcGateToLink == dstGateToLink)
			return -5;
		return circuit.addLink(srcGateToLink, dstGateToLink);
	}

	/**
	 * delete gates in a rectangle.
	 */

	private void deleteArea() {
		int i = 0;
		SimLogGate g;
		Rectangle r = new Rectangle(deleStartX, deleStartY, deleEndX
				- deleStartX, deleEndY - deleStartY);
		circuit.removeGatesIn(r);
		r = null;
	}

	/**
	 * rename gate. The gate is renamed following its type.
	 *
	 * @param g
	 *            gate to rename
	 */

	private void renameGate(SimLogGate g) {
		String name = null;

		switch (g.getType()) {

		case SimLogGate.SWITCH_GATE:
			SimLogRenSwitchWin win = new SimLogRenSwitchWin(appli, g, circuit);
			win.centerComponent();
			win.setVisible(true);
			if (win.getState() == true) {
				circuit.renameGate(g, win.getNewName());
				repaint();
			}
			win = null;
			break;

		case SimLogGate.LED_GATE:
			SimLogRenLEDWin winl = new SimLogRenLEDWin(appli, g, circuit);
			winl.centerComponent();
			winl.setVisible(true);
			if (winl.getState() == true) {
				circuit.renameGate(g, winl.getNewName());
				repaint();
			}
			winl = null;
			break;

		default:
			SimLogRenameWin wind = new SimLogRenameWin(appli, g.getName());
			wind.centerComponent();
			wind.setVisible(true);
			if (wind.getState() == true) {
				name = wind.getNewName();
				if (circuit.gateHasAlreadyNameExcept(g, name)) {
					appli.messageWarning("Gate with name " + name
							+ " already exists");
				} else {
					circuit.renameGate(g, name);
				}
				repaint();
			}
			wind = null;
			break;
		}
	}

	/**
	 * open a window to ask ths user if he wants to rename a gate
	 *
	 * @param g
	 *            gate to rename
	 */

	private void tryToRenameGate(SimLogGate g) {
		if (appli.yesno("Do you want to rename gate ?") == true) {
			renameGate(g);
		}
	}

	public void unsetIntersectFlag() {
		intersectFlag = false;
		intersectXPos = 0;
		intersectYPos = 0;
		repaint();
	}

	public boolean getIntersectFlag() {
		return intersectFlag;
	}

	// ============================================================
	// methods for MouseListener
	// ============================================================

	/**
	 * Mouse Clicked Used when in GATE state to deposit a gate or in DELEte
	 * state to remove gate under mouse
	 */

	public void mouseClicked(MouseEvent e) {
		int gateNbr;
		int x = e.getX();
		int y = e.getY();
		SimLogGate gate;

		switch (toolbar.getState()) {

		case SimLogToolbar.STATE_GATE: {
			// look for a gate that intersects with mouse position
			gate = circuit.getIntersectedGate(x, y);
			if (gate == null) {
				addGate(x, y);
				appli.noMessage();
			} else {
				if (gate.isInsideNameZone(x, y)) {
					tryToRenameGate(gate);
				} else {
					intersectFlag = true;
					intersectXPos = x;
					intersectYPos = y;
					appli.messageWarning("Gate position intersects with other gate");
				}
			}
			repaint();
		}
			break;

		//
		case SimLogToolbar.STATE_DELE: {
			gateNbr = circuit.getGateIndexAtPos(x, y);
			if (gateNbr != -1) {
				int n;
				gate = (SimLogGate) listOfGates.elementAt(gateNbr);
				if (gate.isInsideRemoveZone(x, y)) {
					circuit.removeGateAtIndex(gateNbr);
				} else {
					n = gate.isNearInput(x, y);
					if (n != -1) {
						SimLogLink link = gate.getInputLink(n);
						if (link != null) {
							link.remove();
							link = null;
						}
					}
				}
			} else {
				appli.messageWarning("not pointing a gate");
			}
			repaint();
		}
			break;

		//
		case SimLogToolbar.STATE_SIML: {
			gate = circuit.getGateAtPos(x, y);
			if (gate != null) {
				if (gate.getType() == SimLogGate.SWITCH_GATE) {
					((SimLogSwitchGate) gate).switchOnOff();
					simulation();
					repaint();
				}
			}
		}
			break;
		}
	}

	/**
	 * mouse pressed
	 */

	public void mousePressed(MouseEvent e) {
		SimLogGate gate;
		int x = e.getX(), y = e.getY();
		switch (toolbar.getState()) {

		case SimLogToolbar.STATE_SELECTED:
//		case SimLogToolbar.STATE_MOVE:
			gate = circuit.getGateAtPos(x, y);
			if (gate != null) {
				gateToMove = gate;
//				gateToMove.setMovingState();
				gateToMove.setSelectedState();
				gateToMoveXOld = gateToMove.x;
				gateToMoveYOld = gateToMove.y;
				gateToMoveDX = gateToMove.x - x;
				gateToMoveDY = gateToMove.y - y;
			} else {
				gateToMove = null;
			}
			break;

		case SimLogToolbar.STATE_LINK:
			gate = circuit.getGateAtPos(x, y);
			if (gate != null) {
				srcGateToLink = gate;
			} else {
				srcGateToLink = null;
			}
			break;

		case SimLogToolbar.STATE_DELE:
			deleStartX = x;
			deleStartY = y;
			deleFlag = true;
			break;

		}
	}

	/**
	 * mouse dragged
	 */

	public void mouseDragged(MouseEvent e) {
		int x = e.getX(), y = e.getY();

		switch (toolbar.getState()) {

		case SimLogToolbar.STATE_SELECTED:
//		case SimLogToolbar.STATE_MOVE:
			if (gateToMove != null) {
				gateToMove.moveTo(x + gateToMoveDX, y + gateToMoveDY);
//				gateToMove.setMovingState();
				repaint();
			}
			break;

		case SimLogToolbar.STATE_LINK:
			if (srcGateToLink != null) {
				xMouseLink = e.getX();
				yMouseLink = e.getY();
				repaint();
			}
			break;

		case SimLogToolbar.STATE_DELE:
			deleEndX = x;
			deleEndY = y;
			repaint();
			break;

		}
	}

	/**
	 * mouse released
	 */

	public void mouseReleased(MouseEvent e) {
		SimLogGate gate;
		int x = e.getX(), y = e.getY();

		switch (toolbar.getState()) {

		case SimLogToolbar.STATE_SELECTED:
//		case SimLogToolbar.STATE_MOVE:
			if (gateToMove != null) {
				if (circuit.getIntersectedGate(gateToMove) != null) {
					gateToMove.moveTo(gateToMoveXOld, gateToMoveYOld);
				}
				gateToMove.setSelectedState();
				if (selectedGate != null)
					selectedGate.setNormalState();
				selectedGate = gateToMove;
				gateToMove = null;
			} else if (selectedGate != null) {
				selectedGate.setNormalState();
				selectedGate = null;
			}
			repaint();
			break;

		case SimLogToolbar.STATE_LINK:
			gate = circuit.getGateAtPos(x, y);
			if (gate != null) {
				dstGateToLink = gate;
				addLink();
			}
			srcGateToLink = null;
			dstGateToLink = null;
			repaint();
			break;

		case SimLogToolbar.STATE_DELE:
			deleEndX = e.getX();
			deleEndY = e.getY();
			deleteArea();
			deleFlag = false;
			repaint();
			break;

		}
	}

	/**
	 * mouse entered
	 */

	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * mouse exited
	 */

	public void mouseExited(MouseEvent e) {
//
//		switch (toolbar.getState()) {
//		case SimLogToolbar.STATE_MOVE: {
//			if (gateToMove != null) {
//				gateToMove.setNormalState();
//				gateToMove.moveTo(gateToMoveXOld, gateToMoveYOld);
//				gateToMove = null;
//				repaint();
//			}
//		}
//			break;
//		}
	}

	/**
	 * mouse moved
	 */

	public void mouseMoved(MouseEvent e) {
	}

	/**
	 * check if circuit is valid, i.e. all input and output gates are connected
	 *
	 * @return <code>true</code> if circuit is valid , <code>false</code>
	 *         otherwise
	 */

	public boolean validation() {
		int i;
		SimLogGate g;
		boolean state = true;

		for (i = 0; i < listOfGates.size(); i++) {
			g = (SimLogGate) listOfGates.elementAt(i);
			if (g.getType() != SimLogGate.SWITCH_GATE) {
				g.setValue(SimLogGate.UNSET);
			}
			g.setNormalState();
		}

		// check if all inputs are set

		for (i = 0; i < listOfGates.size(); i++) {
			g = (SimLogGate) listOfGates.elementAt(i);
			if (g.getType() != SimLogGate.SWITCH_GATE) {
				if (g.checkInput() == false) {
					g.setFocusState();
					state = false;
				}
			}
		}

		// check if all outputs are connected

		for (i = 0; i < listOfGates.size(); i++) {
			g = (SimLogGate) listOfGates.elementAt(i);
			if (g.getType() != SimLogGate.LED_GATE) {
				if (g.checkOutput() == false) {
					state = false;
					g.setFocusState();
				}
			}
		}

		repaint();

		return state;
	}

	/**
	 * enter edition mode where you can move, remove or rename gates, add and
	 * remove links
	 */

	public void edition() {
		int i;
		SimLogGate g;

		for (i = 0; i < listOfGates.size(); i++) {
			g = (SimLogGate) listOfGates.elementAt(i);
			g.setNormalState();
		}
		repaint();
	}

	/**
	 * enter simulation mode during which you can only change the switches
	 */

	public void simulation() {
		int i;
		SimLogGate g;

		for (i = 0; i < listOfGates.size(); i++) {
			g = (SimLogGate) listOfGates.elementAt(i);
			if (g.getType() != SimLogGate.SWITCH_GATE)
				g.setValue(SimLogGate.UNSET);
			g.setActiveState();
		}

		for (i = 0; i < listOfGates.size(); i++) {
			g = (SimLogGate) listOfGates.elementAt(i);
			if (g.getType() == SimLogGate.LED_GATE) {
				g.compute();
			}
		}
		repaint();
	}

	/**
	 * remove all gates and links of the circuit
	 */

	public void emptyGates() {
		circuit.empty();
		repaint();
	}

	/**
	 * return preferred size
	 */

	public Dimension getPreferredSize() {
		return new Dimension(4000, 4000);
	}

	/**
	   *
		 */

	public Dimension getMaximumSize() {
		return new Dimension(4000, 4000);
	}

	/**
	   *
		 */

	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	public void actionPerformed(ActionEvent e) {
		SimLogGate gate;

		// delete
		if (e.getSource() == popupItems[0]) {
			/*int gateNbr = circuit.getGateIndexAtPos(popupListener.mouseX,
					popupListener.mouseY);
			System.out.println(gateNbr);*/
			gate = circuit.getGateAtPos(popupListener.mouseX,
					popupListener.mouseY);

			circuit.removeGateWithName(gate);
			repaint();
			/*if (gateNbr != -1) {
				int n;
				// gate = (SimLogGate) listOfGates.elementAt( gateNbr );
				circuit.removeGateAtIndex(gateNbr);
				//circuit.removeGate(gate);
				//circuit.removeGateAtIndex(gateNbr);
				repaint();
			}*/
		}
		// exchange
		if (e.getSource() == popupItems[1]) {
			gate = circuit.getGateAtPos(popupListener.mouseX,
					popupListener.mouseY);
			if (gate != null) {
				gate.exchangeLinks();
				repaint();
			}
		}

		// rename
		if (e.getSource() == popupItems[2]) {
			// System.err.println("popup 2 - rename not implemented");
			String name;
			gate = circuit.getGateAtPos(popupListener.mouseX,
					popupListener.mouseY);
			SimLogRenameGateWin win = new SimLogRenameGateWin(appli, gate.getName());
			win.centerComponent();
			win.setVisible(true);
			if (win.getState() == true) {
				name = win.getName();
			} else
				return;
			circuit.renameGate(gate, name);
			
		}

		// replace
		if (e.getSource() == popupItems[3]) {
			gate = circuit.getGateAtPos(popupListener.mouseX,
					popupListener.mouseY);
			if (gate != null) {
				if ((gate.getType() >= SimLogGate.AND_GATE)
						&& (gate.getType() <= SimLogGate.XOR_GATE)) {
					SimLogReplaceGateWin win = new SimLogReplaceGateWin(appli,
							gate, circuit);
					win.centerComponent();
					win.setVisible(true);
					if (win.getState() == true) {
						circuit.replace(gate, win.getNewType());
						repaint();
					}
					win = null;
				}
			}
		}

		// truth table
		if (e.getSource() == popupItems[4]) {
			truthTableForLED();
		}

		// get formula
		if (e.getSource() == popupItems[5]) {
			getFormula();
		}

	}

	public void changeGridMode() {
		if (showGrid == true)
			showGrid = false;
		else
			showGrid = true;
		repaint();
	}

	public void setPopupGate(SimLogGate g) {
		popupGate = g;
	}

	private void lookForInputs(SimLogGate g, Vector v) {
		SimLogLink link;

		switch (g.getType()) {
		case SimLogGate.SWITCH_GATE:
			if (!v.contains(g))
				v.add(g);
			break;
		case SimLogGate.AND_GATE:
		case SimLogGate.OR_GATE:
		case SimLogGate.NAND_GATE:
		case SimLogGate.NOR_GATE:
		case SimLogGate.XOR_GATE:
			link = g.getInputLink(0);
			lookForInputs(link.getOutputGate(), v);
			link = g.getInputLink(1);
			lookForInputs(link.getOutputGate(), v);
			break;
		case SimLogGate.NOT_GATE:
		case SimLogGate.LED_GATE:
			link = g.getInputLink(0);
			lookForInputs(link.getOutputGate(), v);
			break;
		}
	}

	private void truthTableForLED() {
		SimLogGate gate;

		gate = circuit.getGateAtPos(popupListener.mouseX, popupListener.mouseY);
		if (gate != null) {
			if (gate.getType() != SimLogGate.LED_GATE)
				return;
			Vector vIn, vOut;

			vIn = new Vector();
			vOut = new Vector();

			lookForInputs(gate, vIn);
			vOut.add(gate);

			SimLogTruthTable tt = new SimLogTruthTable(circuit, vIn, vOut);
			tt.generateTable();
			tt.print();
			SimLogTruthTableWin win = new SimLogTruthTableWin(appli, tt);
			win.setVisible(true);
		}
	}

	private void getFormula() {
		SimLogGate gate;

		gate = circuit.getGateAtPos(popupListener.mouseX, popupListener.mouseY);
		if (gate != null) {
			SimLogFormulaWin win = new SimLogFormulaWin(appli, gate);
			win.setVisible(true);
		}
	}
}
