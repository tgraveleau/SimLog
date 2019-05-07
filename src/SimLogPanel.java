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
/* // | class   :  SimLogPanel                                         | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This class defines a Panel that includes the canvas used
 *   to draw the circuit
 *
 *   @version 2.2, 14 October 2002
 *   @author Jean-Michel Richer
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SimLogPanel extends JPanel {

		//
		// variables
		//

		private SimLogWin    appli;
		private SimLogCanvas canvas;


		/**
		 *  default constructor
		 *
		 *  @param win parent frame
		 */

		public SimLogPanel( SimLogWin win ) {
				JScrollBar bar;

				appli = win;
				setLayout( new BorderLayout() );

				canvas = new SimLogCanvas( win );
				JScrollPane pane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				bar=pane.getHorizontalScrollBar();
				bar.setValues(0,100,0,1000);
				bar=pane.getVerticalScrollBar();
				bar.setValues(0,100,0,1000);

				pane.getViewport().add( canvas );
				add( pane, BorderLayout.CENTER );

		}


		/**
		 *  remove all gates and links of the current circuit
		 */

		public void emptyGates() {
				canvas.emptyGates();
		}

		/**
		 *  check if circuit is valid, 
		 *  i.e. if all the input and output gates are connected
		 *
		 *  @return true if circuit is valie, false otherwise
		 */

		public boolean validation() {
				return canvas.validation();
		}

		/**
		 *  enter edition mode used to move, remove or rename gates
		 */

		public void edition() {
				appli.toolbar.edition();
				canvas.edition();
		}

		/**
		 *  enter simulation mode
		 */

		public void simulation() {
				appli.toolbar.simulation();
				canvas.simulation();
		}
 
		/**
		 *  repaint the canvas, i.e. repaint circuit
		 */
		
		public void paintCanvas() {
			canvas.repaint();
		}

		/**
		 *  define the circuit to be used
		 */
		
		public void setCircuit( SimLogCircuit c ) {
			canvas.setCircuit( c );
		}

		public void unsetCanvasIntersectFlag() {
			canvas.unsetIntersectFlag();
		}

		public void changeGridMode() {
			canvas.changeGridMode();
		}
}
