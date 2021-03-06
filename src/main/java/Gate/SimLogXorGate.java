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
/* // | class   :  SimLogXorGate                                       | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  This class implements a logic XOR gate
 *
 *  @version 2.1, 14 October 2002
 *  @author Jean-Michel Richer
 */

import java.awt.*;

import Moteur.SimLogLink;


public class SimLogXorGate extends SimLogGate {

	private static int poly_x[] = { 0, 10, 20, 25, 30, 25, 20, 10, 0, 3, 6, 3,
			0 };
	private static int poly_y[] = { 0, 0, 5, 10, 15, 20, 25, 30, 30, 25, 15, 5,
			0 };
	private static int line_x[] = { 0, 3, 6, 3, 0 };
	private static int line_y[] = { 0, 5, 15, 25, 30 };
	private int poly_a[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private int poly_b[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

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

	public SimLogXorGate(int _x, int _y, String s) {
		super(_x, _y, SimLogGate.XOR_GATE, 2, s);
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
		for (i = 0; i < poly_x.length; i++) {
			poly_a[i] = x + 20 + poly_x[i];
			poly_b[i] = y + 20 + poly_y[i];
		}
		g.fillPolygon(poly_a, poly_b, poly_a.length);
		for (i = 0; i < line_x.length; i++) {
			poly_a[i] = x + 15 + line_x[i];
			poly_b[i] = y + 20 + line_y[i];
		}
		for (i = 0; i < line_x.length - 1; i++) {
			g.drawLine(poly_a[i], poly_b[i], poly_a[i + 1], poly_b[i + 1]);
		}
		paintInputs(g);
		paintOutput(g);
	}

	/**
	 * compute gate output value
	 */

	public void compute() {
		int i, n;
		SimLogLink link;
		SimLogGate gate;

		n = 0;
		value = FALSE;
		for (i = 0; i < maxInputLinks; i++) {
			link = inputLinks[i];
			gate = link.getOutputGate();
			if (gate.getValue() == SimLogGate.UNSET)
				gate.compute();
			if (gate.getValue() == SimLogGate.TRUE)
				++n;
		}
		if (n == 0)
			value = SimLogGate.FALSE;
		else if ((n % 2) == 0) {
			value = SimLogGate.FALSE;
		} else {
			value = SimLogGate.TRUE;
		}
	}
}