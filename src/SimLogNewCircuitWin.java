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
/* // | class   :  SimLogNewCircuitWin                                 | // */
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

public class SimLogNewCircuitWin extends JDialog 
		implements ActionListener {

		//
		// variables
		//

		private JComboBox  tNbrInputs;
		private JCheckBox  inputBox;
		private JComboBox  tNbrOutputs;
		private JCheckBox  outputBox;
		private JButton    bOk;
		private JButton    bCancel;
		private boolean    state = true;

	
		/**
		 *  create a panel with Ok and Cancel buttons
		 *
		 *  @return JPanel
		 */

    private JPanel createButtonPanel( ) {
				JPanel panel = new JPanel();
				panel.setLayout( new FlowLayout() );
				bOk = new JButton( "  Ok  " );
				bOk.addActionListener( this );
				bCancel = new JButton( " Cancel " );
				bCancel.addActionListener( this );

				panel.add( bOk );
				panel.add( bCancel );
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
				
				tNbrInputs = new JComboBox();
				for (i=1;i<=20;i++) {
				  tNbrInputs.addItem(Integer.toString(i));
				}
				tNbrOutputs= new JComboBox();
				for (i=1;i<=20;i++) {
				  tNbrOutputs.addItem(Integer.toString(i));
				}
				inputBox = new JCheckBox("automatic naming");
				inputBox.setSelected(true);
				outputBox = new JCheckBox("automatic naming");
				outputBox.setSelected(true);

				JPanel p1=new JPanel();
				p1.setLayout(new FlowLayout());
				p1.add( new JLabel("Number of switches : " ) );
				p1.add( tNbrInputs );
				p1.add( inputBox );

				JPanel p2=new JPanel();
				p2.setLayout(new FlowLayout());
				p2.add( new JLabel("Number of LEDs     : ") );
				p2.add( tNbrOutputs );
				p2.add( outputBox );

				panel.setLayout( new GridLayout( 2, 1 ) );
				panel.add( p1 );
				panel.add( p2 );
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

		public SimLogNewCircuitWin( JFrame parent ) {
			super( parent, true );
			setTitle("New circuit");
			getContentPane().setLayout( new BorderLayout() );
			getContentPane().add( "Center", createMessagePanel() );
			getContentPane().add( "South", createButtonPanel() );
			pack();
		}

		/**
		 *  action manager
		 */

		public void actionPerformed( ActionEvent e ) {
				if (e.getSource() == bOk) {
					state = true;
					dispose();
				} else if (e.getSource() == bCancel) {
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
		 *  return number of switches
		 *
		 *  @return number of Switches
		 */

    public int getNbrInputs( ) {
			  return tNbrInputs.getSelectedIndex()+1;
    }

		/**
		 *  return number of LEDs
		 *
		 *  @return number of LEDs
		 */

    public int getNbrOutputs( ) {
			  return tNbrOutputs.getSelectedIndex()+1;
    }

		/**
		 *  return flag for automatic naming of switches
		 *
		 *  @return true if automatic naming, false otherwise
		 */

    public boolean getSwitchAutomaticNaming( ) {
			  return inputBox.isSelected();
    }

		/**
		 *  return flag for automatic naming of LEDs
		 *
		 *  @return true if automatic naming, false otherwise
		 */

    public boolean getLEDAutomaticNaming( ) {
			  return outputBox.isSelected();
    }



}