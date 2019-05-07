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
/* // | class   :  SimLogRenSwitchWin                                  | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This window helps the user rename a Switch
 *
 *   @version 2.1, 14 October 2002
 *   @author Jean-Michel Richer
 */

package src;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SimLogRenSwitchWin extends JDialog
		implements ActionListener {

		private JComboBox  name;
		private JButton    bYes;
		private JButton    bNo;
		private boolean   state = true;
		private int       initialValue;
		private boolean   tab[];

		/**
		 *  create a panel with Ok and Cancel buttons
		 *
		 *  @return JPanel
		 */

    private JPanel createButtonPanel( ) {
						JPanel panel = new JPanel();
						panel.setLayout( new FlowLayout() );
						bYes = new JButton( "  Ok  " );
						bYes.addActionListener( this );
						bNo  = new JButton( " Cancel " );
						bNo.addActionListener( this );

						panel.add( bYes );
						panel.add( bNo );
						return panel;
    }

		/**
		 *  create a message panel
		 *
		 *  @return JPanel
		 */

		private JPanel createMessagePanel( boolean t[], int n, String s ) {
				char tabNames[] = new char[1];
				//tabNames[1] = '\n';

				JPanel panel = new JPanel();
				panel.setLayout( new GridLayout( 2, 2 ) );
				panel.add( new JLabel(" Old name : " ) );
				panel.add( new JLabel( s ) );
				panel.add( new JLabel(" New name : " ) );
				name = new JComboBox();
				for (int i = 0; i < t.length; i++) {
					if (t[i] == true) {
						tabNames[0] = (char) (65+i);
						name.addItem( new String( tabNames  ) );
					}
				}
				name.setSelectedIndex(n);
				panel.add( name );
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
		 *  @param g SimLog Switch Gate
		 *  @param c SimLogCircuit
		 */

		public SimLogRenSwitchWin( JFrame parent, SimLogGate g, SimLogCircuit c ) {
			super( parent, true );

			getContentPane().setLayout( new BorderLayout() );
			tab = c.getAvailSwitchNames();
			initialValue = (int) g.getName().charAt(0)-65;
			getContentPane().add( "Center", createMessagePanel( tab, initialValue, g.getName() ) );
			getContentPane().add( "South", createButtonPanel() );
			pack();
		}

		/**
		 *  action manager
		 */

		public void actionPerformed( ActionEvent e ) {
				if (e.getSource() == bYes) {
						state = true;
						dispose();
				} else if (e.getSource() == bNo) {
						state = false;
						dispose();
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

		/**
		 *  return new name
		 *
		 *  @return new name of Switch
		 */

    public String getNewName( ) {
				int n;
				String s;

				s = (String) name.getSelectedItem();
				n = (int) s.charAt(0);
				tab[ initialValue ] = true;
				tab[ n-65 ] = false;
				//System.out.println( name.getSelectedItem() +" "+ n) ;
				return new String( String.valueOf( (char) n ) );
    }
}