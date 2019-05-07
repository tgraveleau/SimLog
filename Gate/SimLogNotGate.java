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
/* // | class   :  SimLogNotGate                                       | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  This class implements a logic AND gate
 *
 *  @version 2.1, 14 October 2002
 *  @author Jean-Michel Richer
 */

import java.awt.*;

import Moteur.SimLogLink;


public class SimLogNotGate extends SimLogGate {

	private static int poly_x[] = { 0, 25, 0, 0 };
	private static int poly_y[] = { 0, 15, 30, 0 };
	private int poly_a[] = { 0, 0, 0, 0 };
	private int poly_b[] = { 0, 0, 0, 0 };

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

	public SimLogNotGate(int _x, int _y, String s) {
		super(_x, _y, SimLogGate.NOT_GATE, 1, s);
	}

	/**
	 * paint gate following its state
	 */

	public void paint(Graphics g) {
		int i, dist;

		switch (state) {

		case STATE_NORMAL:
		case STATE_FOCUS:
			paintLinks(g);
			paintGrid(g);
			paintName(g);
			g.setColor((state == SimLogGate.STATE_NORMAL) ? GATE_COLOR
					: FOCUS_COLOR);
			for (i = 0; i < poly_x.length; i++) {
				poly_a[i] = x + 20 + poly_x[i];
				poly_b[i] = y + 20 + poly_y[i];
			}
			g.fillPolygon(poly_a, poly_b, poly_a.length);
			g.drawOval(x + 45, y + 32, 5, 6);
			paintInputs(g);
			paintOutput(g);
			break;

		case STATE_MOVING:
			paintLinks(g);
			g.setColor(MOVE_COLOR);
			g.drawRect(x, y, WIDTH, HEIGHT);
			break;

		case STATE_ACTIVE:
			paintLinks(g);
			g.setColor(GATE_COLOR);
			for (i = 0; i < poly_x.length; i++) {
				poly_a[i] = x + 20 + poly_x[i];
				poly_b[i] = y + 20 + poly_y[i];
			}
			g.fillPolygon(poly_a, poly_b, poly_a.length);
			g.drawOval(x + 45, y + 32, 5, 6);
			paintInputs(g);
			paintOutput(g);
			if (value == SimLogGate.TRUE)
				g.drawString(SimLogGate.TRUE_STRING, x + 60, y + 30);
			if (value == SimLogGate.FALSE)
				g.drawString(SimLogGate.FALSE_STRING, x + 60, y + 30);
			break;

		}
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
				value = SimLogGate.TRUE;
			else
				value = SimLogGate.FALSE;
		}
	}
}