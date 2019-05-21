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
/* // | class   :  SimLogCircuit                                       | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This class contains the description of the circuit
 *   i.e. all gates
 *
 *   @version 2.1, 14 October 2002
 *   @author Jean-Michel Richer
 */

package Moteur;

import java.io.*;
import java.awt.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Gate.SimLogAndGate;
import Gate.SimLogGate;
import Gate.SimLogLEDGate;
import Gate.SimLogNandGate;
import Gate.SimLogNorGate;
import Gate.SimLogNotGate;
import Gate.SimLogOrGate;
import Gate.SimLogSwitchGate;
import Gate.SimLogXorGate;

public class SimLogCircuit {

	//
	// variables
	//

	private final static String fileIdentifier = "# SimLog v2.1";

	private boolean modified = false;
	
	private int m_height = 0;

	// list of all gates
	private Vector listOfGates;

	// names
	private boolean tabAvailSwitchNames[]; // for switches
	private boolean tabAvailLEDNames[]; // for LEDs
	private int availGateNumber = 1;

	// for display
	int reorganizeX;
	int reorganizeY;

	/**
	 * default constructor
	 */

	public SimLogCircuit() {
		listOfGates = new Vector();
		modified = false;

		tabAvailSwitchNames = new boolean[26];
		tabAvailLEDNames = new boolean[26];
		for (int i = 0; i < 26; i++) {
			tabAvailSwitchNames[i] = true;
			tabAvailLEDNames[i] = true;
		}
	}

	/**
	 * remove all gates and links
	 */

	public void empty() {
		int i;
		SimLogGate gate;

		for (i = 0; i < listOfGates.size(); i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			gate.getOutputLinks().removeAllElements();
		}
		listOfGates.removeAllElements();
		for (i = 0; i < 26; i++) {
			tabAvailSwitchNames[i] = true;
			tabAvailLEDNames[i] = true;
		}
		availGateNumber = 1;
	}

	/**
	 * return list of gates
	 *
	 * @return a vector containing the gates
	 */

	public Vector getListOfGates() {
		return listOfGates;
	}

	/**
	 * return index of gate under mouse position
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return index of gate in vector listOfgates or -1 if no gate is found
	 */

	public int getGateIndexAtPos(int x, int y) {
		int i;
		SimLogGate gate;

		for (i = 0; i < listOfGates.size(); i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.isInside(x, y))
				return i;
		}
		return -1;
	}

	/**
	 * return index of gates in rectangle
	 *
	 * @param x1
	 *            coordinate
	 * @param y1
	 *            coordinate
	 * @param x2
	 *            coordinate
	 * @param y2
	 *            coordinate
	 * @return Vector<SimLogGate>
	 */

	public Vector<SimLogGate> getGatesInRectangle(int x1, int y1, int x2, int y2) {
		int i;
		Vector<SimLogGate> gates = new Vector<SimLogGate>();

		for (i = 0; i < listOfGates.size(); i++) {
			SimLogGate gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.isInside(x1, y1, x2, y2))
				gates.add(gate);
		}
		return gates;
	}

	/**
	 * return gate under mouse position
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return gate in vector listOfgates or null if no gate is found
	 */

	public SimLogGate getGateAtPos(int x, int y) {
		int i;
		SimLogGate gate;

		for (i = 0; i < listOfGates.size(); i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.isInside(x, y))
				return gate;
		}
		return null;
	}

	/**
	 * return index of gate that intersects with given gate
	 *
	 * @param g
	 *            reference gate
	 * @return index of gate in vector listOfgates that intersects with
	 *         reference gate or -1 if no gate is found
	 */

	public int getIntersectedGateIndex(SimLogGate g) {
		SimLogGate gate;

		for (int i = 0; i < listOfGates.size(); i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (g != gate) {
				if (gate.intersects(g.x, g.y))
					return i;
			}
		}
		return -1;
	}

	/**
	 * Reset all the gates to their normal state
	 */
	public void resetGatesState() {
		SimLogGate gate;
		for (int i = 0; i < listOfGates.size(); i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			gate.setNormalState();
		}
	}
	
	/**
	 * return gate that intersects with given gate
	 *
	 * @param g
	 *            reference gate
	 * @return gate in vector listOfgates that intersects with reference gate or
	 *         null if no gate is found
	 */

	public SimLogGate getIntersectedGate(SimLogGate g) {
		SimLogGate gate;

		for (int i = 0; i < listOfGates.size(); i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (g != gate) {
				if (gate.intersects(g.x, g.y))
					return gate;
			}
		}
		return null;
	}

	/**
	 * return gate that could possibly intersect if placed under mouse position
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return gate in vector listOfgates or null if no gate is found
	 */

	public SimLogGate getIntersectedGate(int x, int y) {
		SimLogGate gate;

		for (int i = 0; i < listOfGates.size(); i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.intersects(x, y))
				return gate;
		}
		return null;
	}

	/**
	 * return gate at given index
	 *
	 * @param n
	 *            index of gate in vector listOfGates
	 * @return gate at given index
	 */

	public SimLogGate getGateAtIndex(int n) {
		return (SimLogGate) listOfGates.elementAt(n);
	}

	/**
	 * return index if gate in vector listOfGates
	 *
	 * @param g
	 *            gate to look for
	 * @return index of gate in vector listOfGates
	 */

	public int getGateIndex(SimLogGate g) {
		for (int i = 0; i < listOfGates.size(); i++) {
			if (g == (SimLogGate) listOfGates.elementAt(i))
				return i;
		}
		return -1;
	}

	/**
	 * remove gate at given index
	 *
	 * @param n
	 *            index of gate to remove in vector listOfGates
	 */

	public void removeGateAtIndex(int n) {
		removeGate((SimLogGate) listOfGates.elementAt(n));
		listOfGates.removeElementAt(n);
	}
	
	public void removeGateWithName(SimLogGate g) {
		int index = listOfGates.indexOf(g);
		removeGateAtIndex(index);
	}

	/**
	 * remove gate
	 *
	 * @param g
	 *            gate to remove in vector listOfGates
	 */

	public void removeGate(SimLogGate g) {
		String name = g.getName();

		g.removeAllLinks();
		if (g.getType() == SimLogGate.SWITCH_GATE) {
			if (name != null) {
				//tabAvailSwitchNames[(name.charAt(0)) - 65] = true;
				listOfGates.remove(g);
			}
		} else if (g.getType() == SimLogGate.LED_GATE) {
			if (name != null) {
				//tabAvailLEDNames[Integer.parseInt(name.substring(1)) - 1] = true;
				listOfGates.remove(g);
				//tabAvailLEDNames[(name.charAt(0)) - 65] = true;
			}
		} else {
		}
	}

	/**
	 * remove gate in given area. this method is used when deleting gates with a
	 * pressed, drag and realeased event. Gates are removed only if they are
	 * inside the defined area
	 *
	 * @param r
	 *            area of selection where gates will be removed
	 */

	public void removeGatesIn(Rectangle r) {
		int i;
		SimLogGate gate;

		i = 0;
		while (i < listOfGates.size()) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (r.contains(gate)) {
				listOfGates.removeElementAt(i);
				removeGate(gate);
			} else {
				++i;
			}
		}
	}

	/**
	 * find a name for a switch. The name is given by a letter between A to Z
	 *
	 * @return name of switch
	 */

	private String getSwitchName() {
		int i;

		for (i = 0; i < 26; i++) {
			if (tabAvailSwitchNames[i] == true) {
				tabAvailSwitchNames[i] = false;
				return String.valueOf((char) (65 + i));
			}
		}
		return null;
	}

	/**
	 * find a name for a LED. The name is given by "L" followed by a number
	 * between 1 to 26
	 *
	 * @return name of LED
	 */

	private String getLEDName() {
		int i;

		for (i = 0; i < 26; i++) {
			if (tabAvailLEDNames[i] == true) {
				tabAvailLEDNames[i] = false;
				return new String("L" + String.valueOf(i + 1));
			}
		}
		return null;
	}

	/**
	 * find a name for a standard gate (AND, OR, ...). The name is given by a
	 * "G" followed by a number which is incremented.
	 *
	 * @return name of gate
	 */

	private String getStdGateName() {
		return new String("G" + String.valueOf(availGateNumber++));
	}

	/**
	 *  find a name for a standard gate (AND, OR, ...). The name is given
	 *  by the type of the gate followed by a number which is incremented.
	 *	@param gate the type of the gate
	 *  @return name of gate
	 */

	private String getStdGateName(String gate) {
			return new String( gate + String.valueOf( availGateNumber++ ) );
	}
	
	/**
	 *  add a new gate to the circuit
	 *
	 *  @return 0 mean ok, -1 means <I>no more switch available</I>,
	 *  -2 means <I>no more LED available</I>
	 */

	public int addGate( int x, int y, int type, String name ) {
		modified = true;
		switch( type ) {

			case SimLogGate.AND_GATE:
					listOfGates.add( new SimLogAndGate(x,y,getStdGateName("AND")) );
					break;

			case SimLogGate.NAND_GATE:
					listOfGates.add( new SimLogNandGate(x,y,getStdGateName("NAND")) );
					break;

			case SimLogGate.OR_GATE:
					listOfGates.add( new SimLogOrGate(x,y,getStdGateName("OR")) );
					break;

			case SimLogGate.NOR_GATE:
					listOfGates.add( new SimLogNorGate(x,y,getStdGateName("NOR")) );
					break;

			case SimLogGate.NOT_GATE:
					listOfGates.add( new SimLogNotGate(x,y,getStdGateName("NOT")) );
					break;

			case SimLogGate.XOR_GATE:
					listOfGates.add( new SimLogXorGate(x,y,getStdGateName("XOR")) );
					break;

			case SimLogGate.SWITCH_GATE:
					//name =  getSwitchName();
					if (name != null) {
						listOfGates.add( new SimLogSwitchGate(x,y,name) );
					} else {
						return -1;
					}
					break;

			case SimLogGate.LED_GATE:
					//name = getLEDName();
					if (name != null) {
						listOfGates.add( new SimLogLEDGate(x,y,name) );
					} else {
						return -2;
					}
					break;
		};
		return 0;
	}


	/**
	 * rename a gate
	 *
	 * @param gate
	 *            gate to rename
	 * @param name
	 *            new name of the gate
	 */

	public void renameGate(SimLogGate gate, String name) {
		gate.setName(name);
	}

	/**
	 * try to find if a new name to be assigned to a gate is not already in use
	 *
	 * @param gate
	 *            gate that is being renamed
	 * @param s
	 *            new name
	 * @return true is no other gate has this name, false otherwsise
	 */

	public boolean gateHasAlreadyNameExcept(SimLogGate gate, String s) {
		for (int i = 0; i < listOfGates.size(); i++) {
			SimLogGate g = (SimLogGate) listOfGates.elementAt(i);
			if (g != gate) {
				if (g.getName().equals(s))
					return true;
			}
		}
		return false;
	}

	/**
	 * add a new Link between two gates
	 *
	 * @param out
	 *            gate from which the link comes from (output gate)
	 * @param in
	 *            gate that receives the link (input gate)
	 * @return 0 if ok, otherwise error
	 */

	public int addLink(SimLogGate out, SimLogGate in) {
		int port;
		SimLogLink link;

		if (out.getType() == SimLogGate.LED_GATE)
			return -3;
		if (in.getType() == SimLogGate.SWITCH_GATE)
			return -4;
		if ((port = in.getAvailablePort()) == -1)
			return -6;

		link = new SimLogLink(out, in, port);
		out.addOutputLink(link);
		in.addInputLink(link);

		if (out.checkInput() && out.checkOutput()) {
			if (out.getState() == SimLogGate.STATE_FOCUS)
				out.setNormalState();
		}
		if (in.checkInput() && in.checkOutput()) {
			if (in.getState() == SimLogGate.STATE_FOCUS)
				in.setNormalState();
		}
		modified = true;
		return 0;
	}

	/**
	 * replace gate
	 *
	 */

	public void replace(SimLogGate g, int t) {
		int i;
		SimLogGate g1 = null, g2;
		//int nbGate;

		if (g == null)
			return;
		for (i = 0; i < listOfGates.size(); i++) {
			g1 = (SimLogGate) listOfGates.elementAt(i);
			if (g1 == g)
				break;
		}
		listOfGates.removeElementAt(i);
		g2 = g1.replace(t);
		//g2.setName(g.getGenericName());
		listOfGates.add(g2);
	}

	/**
	 * create a gate that is being loaded from disk and add it to the ector of
	 * gates of the circuit. Parameters of the gate are given as strings
	 *
	 * @param sx
	 *            x coordinate of the gate
	 * @param sy
	 *            y coordinate of the gate
	 * @param st
	 *            type of gate
	 * @param sn
	 *            name of gate
	 */

	private void loadCreateGate(String sx, String sy, String st, String sn) {
		int x, y;

		x = Integer.parseInt(sx);
		y = Integer.parseInt(sy);
		switch (Integer.parseInt(st)) {
		case SimLogGate.AND_GATE:
			listOfGates.add(new SimLogAndGate(x, y, sn));
			break;
		case SimLogGate.NAND_GATE:
			listOfGates.add(new SimLogNandGate(x, y, sn));
			break;
		case SimLogGate.OR_GATE:
			listOfGates.add(new SimLogOrGate(x, y, sn));
			break;
		case SimLogGate.NOR_GATE:
			listOfGates.add(new SimLogNorGate(x, y, sn));
			break;
		case SimLogGate.NOT_GATE:
			listOfGates.add(new SimLogNotGate(x, y, sn));
			break;
		case SimLogGate.XOR_GATE:
			listOfGates.add(new SimLogXorGate(x, y, sn));
			break;
		case SimLogGate.SWITCH_GATE:
			listOfGates.add(new SimLogSwitchGate(x, y, sn));
			break;
		case SimLogGate.LED_GATE:
			listOfGates.add(new SimLogLEDGate(x, y, sn));
			break;
		}
	}

	/**
	 * create and add a link that is read from a file on disk. Parameters are
	 * given as strings.
	 *
	 * @param s1
	 *            index of output gate
	 * @param s2
	 *            index of input gate
	 * @param s3
	 *            port number of input gate
	 */

	private void loadCreateLink(String s1, String s2, String s3) {
		SimLogGate out, in;
		SimLogLink link;

		out = (SimLogGate) listOfGates.elementAt(Integer.parseInt(s1));
		in = (SimLogGate) listOfGates.elementAt(Integer.parseInt(s2));
		link = new SimLogLink(out, in, Integer.parseInt(s3));
		out.addOutputLink(link);
		in.addInputLink(link);
	}

	/**
	 * load a circuit from a file on disk
	 *
	 * @param fileName
	 *            name of file on disk
	 */

	public int load(String fileName) {
		int i;
		int state = 1;
		int nbrGates = 0;
		int nbrLinks;
		int lineNumber = 1;
		BufferedReader in;
		String line;
		StringTokenizer token;
		String s1, s2, s3, s4;
		SimLogGate gate;
		SimLogLink link;

		try {
			in = new BufferedReader(new FileReader(fileName));
			line = in.readLine();
			if (!line.equals(fileIdentifier)) {
				in.close();
				return -2;
			}

			line = in.readLine();
			while (line != null) {

				if (!line.startsWith("#")) {

					// if not a comment, parse line
					switch (state) {

					case 1: // read number of gates
						nbrGates = Integer.parseInt(line);
						state = 2;
						break;

					case 2: // read gates
						token = new StringTokenizer(line);
						if (token.countTokens() == 5) {
							s1 = token.nextToken();
							s2 = token.nextToken();
							s3 = token.nextToken();
							s4 = token.nextToken();
							loadCreateGate(s1, s2, s3, s4);
							--nbrGates;
						}
						if (nbrGates == 0)
							state = 3;
						break;

					case 3: // read number of links
						nbrLinks = Integer.parseInt(line);
						state = 4;
						break;

					case 4:
						token = new StringTokenizer(line);
						if (token.countTokens() == 3) {
							s1 = token.nextToken();
							s2 = token.nextToken();
							s3 = token.nextToken();
							loadCreateLink(s1, s2, s3);
						}
						break;
					}
				}
				line = in.readLine();
				++lineNumber;
			}
			in.close();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}

	/**
	 * save a circuit in file on disk
	 *
	 * @param fileName
	 *            name of file on disk
	 */

	public int save(String fileName) {
		int i;
		int size;
		int nbrLinks;
		Vector outputLinks;
		SimLogGate gate;
		SimLogLink link;
		PrintWriter out;

		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			nbrLinks = 0;
			size = listOfGates.size();

			out.println(fileIdentifier);
			out.println("# Please do not remove the line above");

			// first write gates
			out.println("# number of gates");
			out.println(size);

			out.println("# list of gates : x,y,type,name,number of links");
			for (i = 0; i < size; i++) {
				gate = (SimLogGate) listOfGates.elementAt(i);
				outputLinks = gate.getOutputLinks();
				nbrLinks += outputLinks.size();
				out.print(gate.x + " " + gate.y + " " + gate.getType() + " ");
				out.println(gate.getName() + " " + outputLinks.size());
			}

			// then write links with gate numbers
			out.println("# links");
			out.println(nbrLinks);
			for (i = 0; i < size; i++) {
				gate = (SimLogGate) listOfGates.elementAt(i);
				outputLinks = gate.getOutputLinks();
				for (int j = 0; j < outputLinks.size(); j++) {
					link = (SimLogLink) outputLinks.elementAt(j);
					out.print(i + " " + getGateIndex(link.getInputGate()) + " ");
					out.println(link.getInputPort());
				}
			}
			out.close();

		} catch (IOException e) {
			return -1;
		}
		modified = false;
		return 0;
	}

	/**
	 * return array of available switches
	 *
	 * @return array of boolean related to switches names
	 */

	public boolean[] getAvailSwitchNames() {
		return tabAvailSwitchNames;
	}

	/**
	 * return array of available LEDs
	 *
	 * @return array of boolean related to LEDs names
	 */

	public boolean[] getAvailLEDNames() {
		return tabAvailLEDNames;
	}

	/**
	 * return value to indicate if circuit has been modified
	 *
	 * @return true if circuit has been modified, false otherwise
	 */

	public boolean hasBeenModified() {
		return modified;
	}

	/**
	 * return number of switches
	 *
	 * @return number of switches
	 */

	public int getNbrSwitch() {
		int i, size, n = 0;
		SimLogGate gate;

		size = listOfGates.size();
		for (i = 0; i < size; i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.getType() == SimLogGate.SWITCH_GATE) {
				++n;
			}
		}
		return n;
	}

	/**
	 * return Switch gate given its number
	 *
	 * @param n
	 *            nth switch
	 * @return Switch gate given its number
	 */

	public SimLogGate getSwitch(int n) {
		int i, size;
		SimLogGate gate;

		size = listOfGates.size();
		for (i = 0; i < size; i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.getType() == SimLogGate.SWITCH_GATE) {
				if (n == 0)
					return gate;
				--n;
			}
		}
		return null;
	}

	/**
	 * return number of LEDs
	 *
	 * @return number of LEDs
	 */

	public int getNbrLED() {
		int i, size, n = 0;
		SimLogGate gate;

		size = listOfGates.size();
		for (i = 0; i < size; i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.getType() == SimLogGate.LED_GATE) {
				++n;
			}
		}
		return n;
	}

	/**
	 * return LED gate given its number
	 *
	 * @param n
	 *            nth LED
	 * @return LED gate given its number
	 */

	public SimLogGate getLED(int n) {
		int i, size;
		SimLogGate gate;

		size = listOfGates.size();
		for (i = 0; i < size; i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.getType() == SimLogGate.LED_GATE) {
				if (n == 0)
					return gate;
				--n;
			}
		}
		return null;
	}

	/**
	 * find gate with given name
	 *
	 * @param s
	 *            name of gate
	 * @return gate or null
	 */

	public SimLogGate findGateByName(String s) {
		int i, size;
		SimLogGate gate;

		size = listOfGates.size();
		for (i = 0; i < size; i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			if (gate.getName() != null) {
				if (s.equals(gate.getName()))
					return gate;
			}
		}
		return null;

	}

	/**
	 * build a circuit given root node of a formula
	 *
	 * @param node
	 *            SimLogFormulaNode
	 *
	 */

	public void buildCircuit(SimLogFormulaNode node) {
		SimLogGate g, root;

		empty();
		g = buildCircuitGate(node);
		addGate(0, 0, SimLogGate.LED_GATE, "L01");
		root = (SimLogGate) listOfGates.elementAt(listOfGates.size() - 1);
		addLink(g, root);
	}

	private SimLogGate buildCircuitGate(SimLogFormulaNode node) {
		SimLogGate g = null, gl, gr;

		switch (node.getType()) {
		case 0: // SWITCH
			g = findGateByName(node.getVariable());
			if (g == null) {
				addGate(0, 0, SimLogGate.SWITCH_GATE, node.getVariable());
				g = (SimLogGate) listOfGates.elementAt(listOfGates.size() - 1);
			}
			break;

		case SimLogGate.OR_GATE:
		case SimLogGate.AND_GATE:
		case SimLogGate.XOR_GATE:
			gr = buildCircuitGate(node.getRight());
			gl = buildCircuitGate(node.getLeft());
			addGate(0, 0, node.getType(), null);
			g = (SimLogGate) listOfGates.elementAt(listOfGates.size() - 1);
			addLink(gl, g);
			addLink(gr, g);
			break;

		case SimLogGate.NOT_GATE:
			gl = buildCircuitGate(node.getLeft());
			addGate(0, 0, node.getType(), null);
			g = (SimLogGate) listOfGates.elementAt(listOfGates.size() - 1);
			addLink(gl, g);
			break;
		}
		return g;
	}
	
	
	/**
	 * reorganize circuit for display
	 *
	 */
	//If you want to have original reorganize go to SetPosition()
	public void reorganize() {
		int i, n, nbrLEDs;
		int a;
		SimLogGate gate, LED;

		n = listOfGates.size();
		for (i = 0; i < n; i++) {
			gate = (SimLogGate) listOfGates.elementAt(i);
			gate.x = gate.y = -1;
			gate.locc = gate.rocc = 0;
		}

		reorganizeY = 0;
		nbrLEDs = getNbrLED();
		for (i = 0; i < nbrLEDs; i++) {
			LED = getLED(i);
			reorganizeX = 0;
			a = evaluate(LED, 0);
			// setPositions(LED.locc+1,LED,reorganizeY);
			// reorganizeY+=SimLogGate.HEIGHT;
			// repaint();
		}
		reorganizeY = 0;
		for (i = 0; i < nbrLEDs; i++) {
			LED = getLED(i);
			setPositions(LED.locc + 1, LED, reorganizeY);
			m_height=0;
		}
		System.out.println("\n\n");
	}

	/**
	 * evaluate display
	 *
	 */

	private int evaluate(SimLogGate gate, int depth) {
		int n, i;
		SimLogLink link;
		SimLogGate g;

		n = listOfGates.size();
		for (i = 0; i < n; i++) {
			if (gate == (SimLogGate) listOfGates.elementAt(i)) {
				n = i;
				break;
			}
		}

		// set relative x position
		if (depth > gate.x)
			gate.x = depth;
		if (depth > reorganizeX)
			reorganizeX = depth;
		++depth;

		switch (gate.getType()) {
		case SimLogGate.SWITCH_GATE:
			break;

		case SimLogGate.OR_GATE:
		case SimLogGate.AND_GATE:
		case SimLogGate.XOR_GATE:
		case SimLogGate.NAND_GATE:
		case SimLogGate.NOR_GATE:
			link = gate.getInputLink(0);
			if (link != null) {
				gate.locc = evaluate(link.getOutputGate(), depth);
			}
			link = gate.getInputLink(1);
			if (link != null) {
				gate.rocc = evaluate(link.getOutputGate(), depth);
			}
			break;

		case SimLogGate.NOT_GATE:
		case SimLogGate.LED_GATE:
			link = gate.getInputLink(0);
			if (link != null) {
				g = link.getOutputGate();
				evaluate(g, depth);
				gate.locc = g.locc;
				gate.rocc = g.rocc;
			}
		}
		return 1 + gate.locc + gate.rocc;
	}
	
	//If you want to have original reorganize function replace: gate.y = m_height * 70 + ypos; by gate.y = height * 70 + ypos;
	private void setPositions(int height, SimLogGate gate, int ypos) {
		SimLogLink link;
		SimLogGate g;

		if ((gate.x != -1) && (gate.y != -1))
			return;
		gate.x = (reorganizeX - gate.x) * 100;
		gate.y = m_height * 70 + ypos;
		m_height++;
		if(gate.x<0) {
			gate.x=70;
		}
		if (gate.y > reorganizeY)
			reorganizeY = gate.y;

		System.out.println("pos "+gate.x+" "+gate.y+" // locc :"+gate.locc+" rocc: "+gate.rocc);

		switch (gate.getType()) {
		case SimLogGate.SWITCH_GATE:
			gate.x = 0;
			break;

		case SimLogGate.OR_GATE:
		case SimLogGate.AND_GATE:
		case SimLogGate.XOR_GATE:
		case SimLogGate.NAND_GATE:
		case SimLogGate.NOR_GATE:
			link = gate.getInputLink(0);
			if (link != null) {
				g = link.getOutputGate();
				setPositions(height - (gate.locc - g.locc), g, ypos);
			}
			link = gate.getInputLink(1);
			if (link != null) {
				g = link.getOutputGate();
				setPositions(height + (gate.rocc - g.rocc), g, ypos);
			}
			break;

		case SimLogGate.NOT_GATE:
		case SimLogGate.LED_GATE:
			link = gate.getInputLink(0);
			setPositions(height, link.getOutputGate(), ypos);
			break;
		}
	}
	
	public void toNand() {
		SimLogGate g;
		ArrayList<SimLogGate> arrayListGate = new ArrayList<SimLogGate>();
		for(int i=0 ; i<listOfGates.size() ; i++ ) {
			g=(SimLogGate)listOfGates.elementAt(i);
			arrayListGate.add(g);
		}
		for(int i=0 ; i<arrayListGate.size() ; i++ ) {
			g=arrayListGate.get(i);
			if(g.getType() == SimLogGate.AND_GATE) {
				SimLogGate g1 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g2 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g1);
				g.removeAllLinks();
				addLink(g1, g2);
				addLink(g1, g2);
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g2, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.NOT_GATE) {
				SimLogGate g1 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				listOfGates.addElement(g1);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g1);
				g.removeAllLinks();
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g1, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.OR_GATE) {
				SimLogGate g1 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g2 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g3 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				listOfGates.addElement(g3);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				g.removeAllLinks();
				addLink(g1, g3);
				addLink(g2, g3);
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g3, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.XOR_GATE) {
				SimLogGate g1 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g2 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g3 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g4 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				listOfGates.addElement(g3);
				listOfGates.addElement(g4);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g2);
				addLink(g1, g2);
				addLink(g.inputLinks[1].getOutputGate(), g3);
				addLink(g1, g3);
				addLink(g2, g4);
				addLink(g3, g4);
				g.removeAllLinks();
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g4, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.NOR_GATE) {
				SimLogGate g1 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g2 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g3 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				SimLogGate g4 = new SimLogNandGate(0, 0, getStdGateName("NAND"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				listOfGates.addElement(g3);
				listOfGates.addElement(g4);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g1, g3);
				addLink(g2, g3);
				addLink(g3, g4);
				addLink(g3, g4);
				g.removeAllLinks();
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g4, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
		}
	}
	
	public void toNor() {
		SimLogGate g;
		ArrayList<SimLogGate> arrayListGate = new ArrayList<SimLogGate>();
		for(int i=0 ; i<listOfGates.size() ; i++ ) {
			g=(SimLogGate)listOfGates.elementAt(i);
			arrayListGate.add(g);
		}
		for(int i=0 ; i<arrayListGate.size() ; i++ ) {
			g=arrayListGate.get(i);
			if(g.getType() == SimLogGate.OR_GATE) {
				SimLogGate g1 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g2 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g1);
				g.removeAllLinks();
				addLink(g1, g2);
				addLink(g1, g2);
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g2, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.NOT_GATE) {
				SimLogGate g1 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				listOfGates.addElement(g1);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g1);
				g.removeAllLinks();
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g1, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.AND_GATE) {
				SimLogGate g1 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g2 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g3 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				listOfGates.addElement(g3);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				g.removeAllLinks();
				addLink(g1, g3);
				addLink(g2, g3);
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g3, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.XOR_GATE) {
				SimLogGate g1 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g2 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g3 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g4 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g5 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				listOfGates.addElement(g3);
				listOfGates.addElement(g4);
				listOfGates.addElement(g5);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g1, g3);
				addLink(g2, g3);
				addLink(g.inputLinks[0].getOutputGate(), g4);
				addLink(g.inputLinks[1].getOutputGate(), g4);
				addLink(g3, g5);
				addLink(g4, g5);
				g.removeAllLinks();
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g5, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
			else if(g.getType() == SimLogGate.NAND_GATE) {
				SimLogGate g1 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g2 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g3 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				SimLogGate g4 = new SimLogNorGate(0, 0, getStdGateName("NOR"));
				listOfGates.addElement(g1);
				listOfGates.addElement(g2);
				listOfGates.addElement(g3);
				listOfGates.addElement(g4);
				ArrayList<SimLogGate> listOutputGate = new ArrayList<SimLogGate>();
				for(int j=0 ; j<g.getOutputLinks().size() ; j++) {
					SimLogLink out = (SimLogLink)g.getOutputLinks().elementAt(j);
					SimLogGate gInput = out.getInputGate();
					listOutputGate.add(gInput);
				}
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[0].getOutputGate(), g1);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g.inputLinks[1].getOutputGate(), g2);
				addLink(g1, g3);
				addLink(g2, g3);
				addLink(g3, g4);
				addLink(g3, g4);
				g.removeAllLinks();
				for(int j=0 ; j<listOutputGate.size() ; j++) {
					addLink(g4, listOutputGate.get(j));
				}
				listOfGates.remove(g);
				reorganize();
			}
		}
	}
}