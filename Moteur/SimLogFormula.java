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
/* // | class   :  SimLogFormula                                       | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2003                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This class implements a logic formula that represents a logic
 *   expression as a tree.
 *
 *   @version 2.2, 14 October 2003
 *   @author Jean-Michel Richer
 */

package Moteur;

import java.util.Vector;

import Gate.SimLogGate;

public class SimLogFormula {

	//
	// Variables
	//

	private Vector stack; // Expression in postfix form
	private SimLogCircuit circuit;
	private SimLogFormulaNode rootNode; // root node of formula
	private boolean correct; // is the expression correct ?

	/**
	 * Constructor
	 * 
	 * @param v
	 *            Logic expression in postfix notation
	 */

	public SimLogFormula(Vector v) {
		stack = v;
		correct = true;
		rootNode = buildFormula();
	}

	/**
	 * recursively build formula by 'poping' operators from postfix stack
	 * 
	 * @return a SimLogFormulaNode
	 */

	private SimLogFormulaNode buildFormula() {
		String s;
		SimLogFormulaNode node = null;

		if (stack.size() == 0) {
			correct = false;
			return null;
		}
		s = (String) stack.remove(stack.size() - 1);
		switch (s.charAt(0)) {
		case '+':
			node = new SimLogFormulaNode(SimLogGate.OR_GATE, buildFormula(),
					buildFormula());
			break;
		case '.':
			node = new SimLogFormulaNode(SimLogGate.AND_GATE, buildFormula(),
					buildFormula());
			break;

		case '^':
			node = new SimLogFormulaNode(SimLogGate.XOR_GATE, buildFormula(),
					buildFormula());
			break;
		case '-':
			node = new SimLogFormulaNode(SimLogGate.NOT_GATE, buildFormula(),
					null);
			break;
		default:
			node = new SimLogFormulaNode(s, null, null);
			break;
		}
		;
		return node;
	}

	/**
	 * 
	 * 
	 * @param v
	 *            Logic expression in postfix notation
	 */

	private int findNbrNodes(SimLogFormulaNode node) {
		switch (node.getType()) {
		case 0:
			return 1;
		case SimLogGate.OR_GATE:
		case SimLogGate.AND_GATE:
		case SimLogGate.XOR_GATE:
			return 1 + findNbrNodes(node.getLeft())
					+ findNbrNodes(node.getRight());
		case SimLogGate.NOT_GATE:
			return 1 + findNbrNodes(node.getLeft());
		}
		return 0;
	}

	public SimLogFormulaNode getRoot() {
		return rootNode;
	}

	public boolean isCorrect() {
		return correct;
	}
}