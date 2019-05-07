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
/* // | class   :  SimLogToolbar                                       | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This class is part of the edition mode and let the user
 *
 *
 *   @version 2.1, 14 October 2002
 *   @author Jean-Michel Richer
 */

package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.Applet;


public class SimLogToolbar extends JToolBar implements ActionListener {

		//
		// Toolbar state
		//

		public final static int STATE_NONE = 0; //
		public final static int STATE_GATE = 1; // add gate
		public final static int STATE_MOVE = 2; // move gate
		public final static int STATE_DELE = 3; // delete gate
		public final static int STATE_LINK = 4; // link two gates
		public final static int STATE_SIML = 5; // simulation


		private String tabWarningMessages[] = {
			"add Switch",
			"add LED",
			"add AND gate",
			"add NAND gate",
			"add OR gate",
			"add NOR gate",
			"add NOT gate",
			"add XOR gate",
			"add Programmable Array - not implemented",
			"To move a gate, click on it and maintain button while moving",
			"Link gates",
			"To remove a window, click on the cross on the top right corner"
		};

		private final String buttonLabels[] = {
				"switch",
				"led",
			  "and",
				"nand",
			  "or" ,
				"nor",
				"not",
				"xor",
				"parray",
				"move",
				"link",
				"dele",
		};

		private final String tabTips[] = {
			"add a switch",
			"add a LED",
			"add a AND gate",
			"add NAND gate",
			"add OR gate",
			"add NOR gate",
			"add NOT gate",
			"add XOR gate",
			"add Programmable Array - not implemented",
			"move a gate",

			"link 2 gates",
			"remove a gate or link"
		};

		private final static int tabStates[] = {
			   STATE_GATE, STATE_GATE,
			   STATE_GATE, STATE_GATE,
			   STATE_GATE, STATE_GATE,
				 STATE_GATE, STATE_GATE,
				 STATE_GATE,
			   STATE_MOVE,
			   STATE_LINK,
				 STATE_DELE
		};

		private final static int tabGates[] = {
			   SimLogGate.SWITCH_GATE, SimLogGate.LED_GATE,
			   SimLogGate.AND_GATE, SimLogGate.NAND_GATE,
			   SimLogGate.OR_GATE, SimLogGate.NOR_GATE,
			   SimLogGate.NOT_GATE, SimLogGate.XOR_GATE,
				 SimLogGate.NONE_GATE,
				 SimLogGate.NONE_GATE,
				 SimLogGate.NONE_GATE, SimLogGate.NONE_GATE,
			 };


		private JButton tabButtons[];
		private int    markedButton = 0;
		private int    oldMarkedButton;

 		private int state = STATE_GATE;
		private SimLogWin appli = null;

		/**
		 *  default constructor
		 *
		 *  @param win parent application
		 */

		public SimLogToolbar( SimLogWin win ) {
				super( "gates", JToolBar.VERTICAL );

				int i;
				JButton b;
				Applet applet;


				appli  = win;
 				applet = win.getApplet();

				tabButtons = new JButton[ buttonLabels.length ];

				if (applet == null) {

						for (i = 0; i < buttonLabels.length; i++) {
							b = new JButton( new ImageIcon( "." + appli.getFileSeparator() + "img" + appli.getFileSeparator() + buttonLabels[i] + ".gif" ) );
							b.setToolTipText( tabTips[i] );
							b.addActionListener( this );
							//b.setFont( font );
							b.setBackground( Color.white );
							b.setForeground( Color.black );
							tabButtons[i] = b;
							add( b );
						}
				} else {
						Image tabImg[] = new Image[ buttonLabels.length ];
						MediaTracker tracker = new MediaTracker(this);

						for (i = 0; i < buttonLabels.length; i++) {
							tabImg[i] = applet.getImage( applet.getDocumentBase(), "img/" + buttonLabels[i] + ".gif" );
							tracker.addImage( tabImg[i], i );
						}

						try {
 							Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            	tracker.waitForAll();
						} catch( Exception e ) {
								System.out.println( e.toString() );
						}

						for (i = 0; i < buttonLabels.length; i++) {
							b = new JButton( new ImageIcon( tabImg[i] ) );
							b.setToolTipText( tabTips[i] );
							b.addActionListener( this );
							//b.setFont( font );
							b.setBackground( Color.white );
							b.setForeground( Color.black );
							tabButtons[i] = b;
							add( b );
						}
				}
				tabButtons[0].setBackground( Color.red );
		}

		/**
		 *  return type of chosen gate
		 *
		 *  @return type of gate (@see SimLogGate)
		 */

		public int getGateType( ) {
			  return tabGates[ markedButton ];
		}

		/**
		 *  return state of Toolbar
		 *
		 *  @return state of Toolbar
		 */

		public int getState( ) {
			  return state;
		}


		/**
		 *  action manager
		 */

		public void actionPerformed( ActionEvent e ) {
				if (e.getSource() instanceof JButton) {
					int i;
					JButton b = (JButton) e.getSource();
					for (i = 0; i < tabButtons.length; i++) {
						if (b == tabButtons[i]) {
							// unmark button
							if (markedButton != -1) {
								tabButtons[markedButton].setBackground( Color.white );
								repaint();
							}
							tabButtons[i].setBackground( Color.red );
							markedButton = i;
							break;
						}
					}
					appli.mainPanel.unsetCanvasIntersectFlag();

					state = tabStates[ markedButton ];
					appli.messageWarning( tabWarningMessages[i] );

				}
		}

		/**
		 *  chose simulation mode in which the Toolabr is not accessible
		 */

		public void simulation() {
				oldMarkedButton = markedButton;
				state = STATE_SIML;
				for (int i = 0; i < tabButtons.length; i++) {
					tabButtons[i].setEnabled( false );
				}
		}

		/**
		 *  chose edition mode in which the Toolbar is accessible
		 */

		public void edition() {
				markedButton = oldMarkedButton;
				state = tabStates[ markedButton ];
				for (int i = 0; i < tabButtons.length; i++) {
					tabButtons[i].setEnabled( true );
				}
		}
}