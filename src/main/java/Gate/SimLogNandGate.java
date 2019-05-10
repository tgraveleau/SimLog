package Gate;

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
/* // | class   :  SimLogNandGate                                      | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  This class implements a logic NAND gate
 *
 *   @version 2.1, 14 October 2002
 *   @author Jean-Michel Richer
 */

import java.awt.*;

import Moteur.SimLogLink;


public class SimLogNandGate extends SimLogGate {

	/**
	 * default constructor
	 *
	 * @param _x
	 *            coordinate
	 * @param _y
	 *            coordinate
	 * @param s
	 *            name
	 */

	public SimLogNandGate(int _x, int _y, String s) {
		super(_x, _y, SimLogGate.NAND_GATE, 2, s);
	}

	/**
	 * paint gate following its state
	 */

	public void paint(Graphics g) {
		int i, dist;
		g.setColor(getColorFromState(state));

		switch (state) {

		case STATE_SELECTED:
		case STATE_NORMAL:
		case STATE_FOCUS:
			paintGrid(g);
			paintName(g);
			break;

		case STATE_ACTIVE:
			if (value == SimLogGate.TRUE)
				g.drawString(SimLogGate.TRUE_STRING, x + 60, y + 30);
			if (value == SimLogGate.FALSE)
				g.drawString(SimLogGate.FALSE_STRING, x + 60, y + 30);
			break;
		}
		paintLinks(g);
		g.fillRect(x + 20, y + 20, 20, 30);
		g.fillOval(x + 20, y + 20, 30, 30);
		g.drawOval(x + 50, y + 32, 5, 6);
		paintInputs(g);
		paintOutput(g);
	}

	/**
	 * compute gate output value
	 */

	public void compute() {
		int i;
		SimLogLink link;
		SimLogGate gate;

		value = TRUE;
		for (i = 0; i < maxInputLinks; i++) {
			link = inputLinks[i];
			gate = link.getOutputGate();
			if (gate.getValue() == SimLogGate.UNSET)
				gate.compute();
			if (gate.getValue() == SimLogGate.FALSE)
				value = SimLogGate.FALSE;
		}
		value = (value == SimLogGate.TRUE) ? SimLogGate.FALSE : SimLogGate.TRUE;
	}
}