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
/* // | class   :  SimLogNewAutoNamingWin                              | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 12, 2003                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This window helps the user define the number of switch and LED
 *   that will be used for the circuit
 *
 *   @version 2.2, 12 October 2003
 *   @author Jean-Michel Richer
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;

public class SimLogNewAutoNamingWin extends JDialog 
		implements ActionListener {

		//
		// variables
		//

		private SimLogWin  appli=null;
		private boolean    state;
		private int        nbrGates;
		private JTextField tabGateNames[];
		private JButton    bOk;
	
		/**
		 *  create a panel with Ok button
		 *
		 *  @return JPanel
		 */

    private JPanel createButtonPanel( ) {
				JPanel panel = new JPanel();
				panel.setLayout( new FlowLayout() );
				bOk = new JButton( "  Ok  " );
				bOk.addActionListener( this );
				panel.add( bOk );
				return panel;
    }

		/**
		 *  create a message panel
		 *
		 *  @return JPanel
		 */

		private JPanel createMessagePanel( ) {
				int i;
				JPanel panel = new JPanel();

				panel.setLayout( new GridLayout( nbrGates, 2 ) );

				tabGateNames=new JTextField[nbrGates];
				for (i=0;i<nbrGates;i++) {
				  panel.add(new JLabel(Integer.toString(i+1)));
					tabGateNames[i]=new JTextField(5);
					panel.add(tabGateNames[i]);
				}
				return panel;
    }

		/**
		 *  method used to center window on screen
		 */

   public void centerComponent( ) {
					 Toolkit tk = Toolkit.getDefaultToolkit();
					 Dimension screenDim = tk.getScreenSize();
					 screenDim.width  = (screenDim.width - this.getWidth()) / 2;
					 screenDim.height = (screenDim.height - this.getHeight()) / 2;
					 this.setLocation( screenDim.width, screenDim.height );
    }

		/**
		 *  constructor
		 *
		 *  @param parent parent Frame
		 *  @param s SimLogGate name
		 */

		public SimLogNewAutoNamingWin( SimLogWin parent, int nbr, int type ) {
			super( parent, true );
			if (type==SimLogGate.SWITCH_GATE) 
				setTitle("Name Switch gates");
			else if (type==SimLogGate.LED_GATE) 
				setTitle("Name LED gates");
			else
				setTitle(" ");
						
			appli=parent;

			nbrGates=nbr;
			getContentPane().setLayout( new BorderLayout() );
			getContentPane().add( "Center", createMessagePanel() );
			getContentPane().add( "South", createButtonPanel() );
			pack();
		}

		/**
		 *  check if gate names are all filled
     *
     *  @return 0 = ok, 1 = gate is empty, 2 = gate has same name
		 */

		private int checkNames() {
			int i;
			String s;

			for (i=0;i<nbrGates;i++) {
				s=tabGateNames[i].getText().trim();

				if (s.length()==0) return 1;
				if (appli.getCircuit().findGateByName(s)!=null) {
					appli.windowWarning("Another gate is already named '"+s+"'");
					tabGateNames[i].setText(" ");
					return 2;
				}
			}
			return 0;
		}

		/**
		 *  action manager
		 */

		public void actionPerformed( ActionEvent e ) {
				int check;

				if (e.getSource() == bOk) {
					check=checkNames();
					if (check==0) {
					  state = true;
					  dispose();
					} else {
						if (check==1)
						  appli.windowWarning("You must define all gate names !");
					}
				}
    }

		/**
		 *  return user confirmation
		 *
		 *  @return <code>true</code> if user clicked on the Ok button,
		 *          <code>false</code>  if user clicked on the Cancel button
		 */

    public boolean getState( ) {
				return state;
    }

		public Vector getNames() {
			Vector v=new Vector();
			for (int i=0;i<nbrGates;i++) {
				v.add(tabGateNames[i].getText().trim());
			}
			return v;
		}

}