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
/* // | class   :  SimLogRenameWin                                     | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This window helps the user rename a gate (except Switch and LED)
 *
 *   @version 2.1, 14 October 2002
 *   @author Jean-Michel Richer
 */

package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SimLogRenameWin extends JDialog
		implements ActionListener {

		//
		// variables
		//

		private JTextField name;
		private JButton    bYes;
		private JButton    bNo;
		private boolean   state = true;

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

		private JPanel createMessagePanel( ) {
				JPanel panel = new JPanel();
				panel.setLayout( new GridLayout( 1, 2 ) );
				panel.add( new JLabel(" Name : " ) );
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
		 *  @param s SimLogGate name
		 */

		public SimLogRenameWin( JFrame parent, String s ) {
			super( parent, true );

			setLayout( new BorderLayout() );
			name = new JTextField(5);
			name.setText( s );
			add( "Center", createMessagePanel() );
			add( "South", createButtonPanel() );
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
				return name.getText();
    }

}