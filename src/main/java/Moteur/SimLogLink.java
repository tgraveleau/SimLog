/* ---------------------------------------------------------------------------
 *  SimLog v 2.1
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
/* // | class   :  SimLogLink                                          | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

package Moteur;

import Gate.SimLogGate;

/**
 * This class implements a link between two gates
 * 
 * @version 2.1, 14 October 2002
 * @author Jean-Michel Richer
 */

public class SimLogLink {

	//
	// variables
	//

	private SimLogGate outputGate;
	private SimLogGate inputGate;
	private int inputPort;

	/**
	 * default constructor
	 * 
	 * @param out
	 *            output gate from which the link comes from
	 * @param in
	 *            input gate which receives the link
	 * @param port
	 *            input port
	 */

	public SimLogLink(SimLogGate out, SimLogGate in, int port) {
		outputGate = out;
		inputGate = in;
		inputPort = port;
	}

	/**
	 * return input gate
	 * 
	 * @return a gate
	 */

	public SimLogGate getInputGate() {
		return inputGate;
	}

	/**
	 * return output gate
	 * 
	 * @return a gate
	 */

	public SimLogGate getOutputGate() {
		return outputGate;
	}

	/**
	 * return input port
	 * 
	 * @return an integer
	 */

	public int getInputPort() {
		return inputPort;
	}

	/**
	 * set input port
	 * 
	 * @param port
	 *            input port
	 */

	public void setInputPort(int port) {
		inputPort = port;
	}

	public void setInputGate(SimLogGate g) {
		inputGate = g;
	}

	public void setOutputGate(SimLogGate g) {
		outputGate = g;
	}

	/**
	 * remove link. The link is removed from output and input gate connections.
	 */

	public void remove() {
		inputGate.removeInputLink(inputPort);
		outputGate.removeOutputLink(inputGate, inputPort);
	}

}