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
import java.util.function.Consumer;

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
			for (JMenuItem mi : canvas.popupItems) {
				mi.setEnabled(gate != null);
			}
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
			} else {
				canvas.popupItems[7].setEnabled(true);
			}
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}

// Main class

public class SimLogCanvas extends JPanel implements MouseListener,
		MouseMotionListener, ActionListener, KeyListener {

	//
	// variables
	//

	private int xMouse, yMouse; // mouse position

	private SimLogWin appli;
	private SimLogToolbar toolbar;

	public SimLogCircuit circuit;
	private Vector<SimLogGate> listOfGates;

	// Parameters for popup menu
	JPopupMenu popup;
	JMenuItem popupItems[];

	private PopupListener popupListener;
	private SimLogGate popupGate = null;

	// parameters used when moving a gate

	private Vector<SimLogGate> gatesToMove = new Vector<SimLogGate>();
	private Vector<Integer> gatesToMoveDX = new Vector<Integer>();
	private Vector<Integer> gatesToMoveDY = new Vector<Integer>();
	private Vector<Integer> gatesToMoveXOld = new Vector<Integer>();
	private Vector<Integer> gatesToMoveYOld = new Vector<Integer>();

	private boolean showGrid = false;

	private boolean isCtrlKeyDown = false;

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

	// Gate for copy
	private SimLogGate gateToCopy = null;
	private Vector<SimLogGate> gatesToCopy = new Vector<SimLogGate>();
	private int nbCopy = 0;
	
	// Selection parameters
	private Vector<SimLogGate> selectedGates = new Vector<SimLogGate>();
	private boolean isSelecting = false;
	private int selectedAreaX1;
	private int selectedAreaX2;
	private int selectedAreaY1;
	private int selectedAreaY2;
	
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
		addKeyListener(this);
		setFocusable(true);

		createPopupMenu();
		popupListener = new PopupListener(this, popup);
		addMouseListener(popupListener);

		// setBackground( Color.black );
		setBackground(Color.white);
		setSize(new Dimension(200, 200));
		setPreferredSize(new Dimension(200, 200));
	}

	public void createPopupMenu() {
		popupItems = new JMenuItem[8];
		popupItems[0] = new JMenuItem("delete");
		popupItems[1] = new JMenuItem("exchange links");
		popupItems[2] = new JMenuItem("rename gate");
		popupItems[3] = new JMenuItem("replace gate");
		popupItems[4] = new JMenuItem("truth table");
		popupItems[5] = new JMenuItem("get formula");
		popupItems[6] = new JMenuItem("copy \t(Ctrl+c)");
		popupItems[7] = new JMenuItem("paste \t(Ctrl+v)");

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

		if (toolbar.getGateType() == SimLogGate.SWITCH_GATE || toolbar.getGateType() == SimLogGate.LED_GATE) {
			SimLogNameWin win = new SimLogNameWin(appli);
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
		for (i = 75; i < 10000; i += 80) {
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
		
		if (isSelecting) {
			int x1, x2, y1, y2;
			if (selectedAreaX1 <= selectedAreaX2) {
				x1 = selectedAreaX1;
				x2 = selectedAreaX2;
			} else {
				x1 = selectedAreaX2;
				x2 = selectedAreaX1;
			}
			if (selectedAreaY1 <= selectedAreaY2) {
				y1 = selectedAreaY1;
				y2 = selectedAreaY2;
			} else {
				y1 = selectedAreaY2;
				y2 = selectedAreaY1;
			}
			g.drawRect(x1, y1, x2-x1, y2-y1);
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

	/**
	 * Add a gate in the selected gates and put its state to "selected"
	 * @param g
	 */
    private void selectGate(SimLogGate g) {
    	if (!selectedGates.contains(g) ) {
    		g.setSelectedState();		
    		selectedGates.add(g);    
    	} else if (isCtrlKeyDown) {
    		g.setNormalState();
    		selectedGates.remove(g);
    	}
    }
    
    /**
     * Reset states of selected gates and clear the selected gates array
     */
    private void resetSelectedGates() {
		if (!isCtrlKeyDown ) {
			circuit.resetGatesState();
			selectedGates.clear();
		}
    }
    
    private void initGatesToMove(int x, int y) {
    	for (SimLogGate g : selectedGates) {
    		gatesToMove.add(g);
    		gatesToMoveXOld.add(g.x);
    		gatesToMoveYOld.add(g.y);
    		gatesToMoveDX.add(g.x - x);
    		gatesToMoveDY.add(g.y - y);
    	}
    }
    
    private void resetMovingGates() {
    	gatesToMove.clear();
    	gatesToMoveXOld.clear();
		gatesToMoveYOld.clear();
		gatesToMoveDX.clear();
		gatesToMoveDY.clear();
    }
    
    private void moveGates(int x, int y) {
    	for (int i=0; i<gatesToMove.size(); i++) {
    		SimLogGate gateToMove = gatesToMove.get(i);
    		gateToMove.moveTo(x + gatesToMoveDX.get(i), y + gatesToMoveDY.get(i));
    	}
//		gateToMove.setMovingState();
		repaint();
    }
    
	// ============================================================
	// methods for KeyListener
	// ============================================================

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
    	
    	if (e.isControlDown()) {
    		isCtrlKeyDown = true;
    		switch (e.getKeyCode()) {
	    		case KeyEvent.VK_S:
	    			appli.saveCircuit();
	    			break;
	    		case KeyEvent.VK_C:
	    			copy();
	    			break;
	    		case KeyEvent.VK_V:
	    			paste();
	    			break;
	    		case KeyEvent.VK_E:
	    			edition();
	    			break;
	    		case KeyEvent.VK_R:
	    			simulation();
	    			break;
	    		
	    			
    		}
    	}
    	else {
    		switch (e.getKeyCode()) {
	    		case KeyEvent.VK_DELETE:
	    			for (SimLogGate g : selectedGates) {
	    				//circuit.removeGateWithName(g);
	    				circuit.removeGate(g);
	    			}
	    			repaint();
	    			break;
	    		case  KeyEvent.VK_S:
	    			//SWITCH
	    			toolbar.selectTab(1);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.SWITCH_GATE);
	    			break;
	    		case  KeyEvent.VK_M:
	    			toolbar.selectTab(0);
	    			toolbar.setState(toolbar.STATE_SELECTED);
	    			toolbar.setGateType(SimLogGate.NONE_GATE);
	    			//mouse
	    			break;
	    		case  KeyEvent.VK_L:
	    			//Light
	    			toolbar.selectTab(2);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.LED_GATE);
	    			break;
	    		case  KeyEvent.VK_A:
	    			//AND
	    			toolbar.selectTab(3);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.AND_GATE);
	    			break;
	    		case  KeyEvent.VK_W:
	    			//NAND
	    			toolbar.selectTab(4);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.NAND_GATE);
	    			break;
	    		case  KeyEvent.VK_O:
	    			//OR
	    			toolbar.selectTab(5);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.OR_GATE);
	    			break;
	    		case  KeyEvent.VK_P:
	    			//NOR
	    			toolbar.selectTab(6);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.NOR_GATE);
	    			break;
	    		case  KeyEvent.VK_EXCLAMATION_MARK:
	    			//NOT
	    			toolbar.selectTab(7);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.NOT_GATE);
	    			break;
	    		case  KeyEvent.VK_X:
	    			//XOR
	    			toolbar.selectTab(8);
	    			toolbar.setState(toolbar.STATE_GATE);
	    			toolbar.setGateType(SimLogGate.XOR_GATE);
	    			break;
	    		case  KeyEvent.VK_H:
	    			//LINK
	    			toolbar.selectTab(10);
	    			toolbar.setState(toolbar.STATE_LINK);
	    			toolbar.setGateType(SimLogGate.NONE_GATE);
	    			break;
	    		/*case  KeyEvent.VK_D:
	    			//Delete
	    			toolbar.selectTab(11);
	    			toolbar.setState(toolbar.STATE_DELE);
	    			toolbar.setGateType(SimLogGate.NONE_GATE);
	    			listOfGates = circuit.getListOfGates();
//	    			for (SimLogGate g : listOfGates) {
//	    				g.paintClose(g); 
//	    			}
	    			break;*/
    		}
    	}
    	repaint();
    }

    public void keyReleased(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
    		isCtrlKeyDown = false;
    	}
    }

    private void copy() {
		if (!selectedGates.isEmpty()) {
	    	// On copie les gates sélectionnées
			gatesToCopy.clear();
	    	for (SimLogGate g : selectedGates) {
				gatesToCopy.add(g);
	    	}
			nbCopy = 0;
		}
    }
    private void paste() {
		if (!gatesToCopy.isEmpty()) {
	    	for (SimLogGate g : gatesToCopy) {
				nbCopy++;
				g.setNormalState();
				SimLogGate newGate = (SimLogGate) g.clone();
				newGate.x += 20*nbCopy;
				newGate.y += 20*nbCopy;
				newGate.setName("clone_" + nbCopy + " " +newGate.getName());
				listOfGates.add(newGate);
	    	}
			repaint();
		}
    }
    
	// ============================================================
	// methods for MouseListener
	// ============================================================

	/**
	 * Mouse Clicked Used when in GATE state to deposit a gate or in DELEte
	 * state to remove gate under mouse
	 */

	public void mouseClicked(MouseEvent e) {
		requestFocusInWindow();
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
		requestFocusInWindow();
		SimLogGate gate;
		int x = e.getX(), y = e.getY();
		switch (toolbar.getState()) {

		case SimLogToolbar.STATE_SELECTED:
//		case SimLogToolbar.STATE_MOVE:
			gate = circuit.getGateAtPos(x, y);
			if (gate != null) {
				// Si la touche ctrl n'est pas appuyée et que la gate n'est pas sélectionnée, on reset les autres
				if (gate.getState() != SimLogGate.STATE_SELECTED && !isCtrlKeyDown) {
					resetSelectedGates();
					
				}
				selectGate(gate);
				initGatesToMove(x,y);
			} else {
				resetMovingGates();
				resetSelectedGates();
				// On initialise la zone de sélection
				isSelecting = true;
				selectedAreaX1 = x;
				selectedAreaY1 = y;
				selectedAreaX2 = x;
				selectedAreaY2 = y;
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
			if (!gatesToMove.isEmpty()) {
				moveGates(x,y);
			} else if (isSelecting) {
				// On commence la zone de sélection
				selectedAreaX2 = x;
				selectedAreaY2 = y;
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
			if (!gatesToMove.isEmpty()) {
		    	for (int i=0; i<gatesToMove.size(); i++) {
		    		SimLogGate gateToMove = gatesToMove.get(i);
					if (circuit.getIntersectedGate(gateToMove) != null) {
						gateToMove.moveTo(gatesToMoveXOld.get(i), gatesToMoveYOld.get(i));
					}
					gateToMove.setSelectedState();
				}
				resetMovingGates();
			}
			if (isSelecting) {
				isSelecting = false;
				// On récupère toutes les gates dans le rectangle et on les passe en selected
				Vector<SimLogGate> gatesToSelect = circuit.getGatesInRectangle(
						Math.min(selectedAreaX1,selectedAreaX2),
						Math.min(selectedAreaY1,selectedAreaY2),
						Math.max(selectedAreaX1,selectedAreaX2),
						Math.max(selectedAreaY1,selectedAreaY2)
						);
				resetSelectedGates();
				for (int i=0; i<gatesToSelect.size(); i++) {
					selectGate(gatesToSelect.get(i));
				}
			}
			break;

		case SimLogToolbar.STATE_LINK:
			gate = circuit.getGateAtPos(x, y);
			if (gate != null) {
				dstGateToLink = gate;
				addLink();
			}
			srcGateToLink = null;
			dstGateToLink = null;
			break;

		case SimLogToolbar.STATE_DELE:
			deleEndX = e.getX();
			deleEndY = e.getY();
			deleteArea();
			deleFlag = false;
			break;

		}
		repaint();
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
		appli.toolbar.edition();
		appli.topBar.edition();
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
		appli.toolbar.simulation();
		appli.topBar.simulation();
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
		gate = circuit.getGateAtPos(popupListener.mouseX,
				popupListener.mouseY);

		// delete
		if (e.getSource() == popupItems[0]) {
			/*int gateNbr = circuit.getGateIndexAtPos(popupListener.mouseX,
					popupListener.mouseY);
			System.out.println(gateNbr);*/

			//circuit.removeGateWithName(gate);
			circuit.removeGate(gate);
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
			if (gate != null) {
				gate.exchangeLinks();
				repaint();
			}
		}

		// rename
		if (e.getSource() == popupItems[2]) {
			// System.err.println("popup 2 - rename not implemented");
			String name;
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

		// copy
		if (e.getSource() == popupItems[6]) {
			selectGate(gate);
			copy();
		}

		// paste
		if (e.getSource() == popupItems[7]) {
			paste();
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
	
	public void focus() {
		requestFocus();
	}
	
	
}
