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
/* // | class   :  SimLogLicenceWin                                    | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  This class implements a window to display software licence
 *
 *  @version 2.2, 14 October2002
 *  @author Jean-Michel Richer
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

public class SimLogLicenceWin extends JDialog
		implements ActionListener {

		//
		// variables
		//

		private JFrame appli;
		private JButton bOk;


		// 
		// Licence to display
		//

		private String message = 
				"<BR><CENTER><H3>LICENCE AGREEMENT</H3></CENTER><BR>" +
				"You are allowed to use, copy and distribute this software<BR>"+
				"as a free software." +
				"Commercial use of this software is strictly prohibited.<BR><BR>" +
				"You can also freely modify the software as long as you keep the<BR>" +
        "program team of the project informed.<BR><BR>" +
				"For further information, error reporting or changes please contact<BR>" +
        "<I>Jean-Michel RICHER</I> at<BR>" +
				"University of Angers,<BR>2 Bd Lavoisier,<BR>49045 Angers Cedex, France<BR>" +
				"Or by email <A HREF=\"mailto:Jean-Michel.Richer@univ-angers.fr\">Jean-Michel.Richer@univ-angers.fr</A><BR>";

		/**
		 *  create a panel with a Ok button
		 *
		 *  @return Panel
		 */
		
		private JPanel createButtonPanel() {
			JPanel p = new JPanel();
			bOk = new JButton( "Ok" );
			bOk.addActionListener( this );
			p.setLayout( new FlowLayout() );
			p.add( bOk );
			return p;
		}

		/**
		 *  create a panel with licence to display
		 *
		 *  @return Panel
		 */
		
		private JPanel createMessagePanel() {
			JPanel p = new JPanel();

			p.setLayout( new BorderLayout() );
			JEditorPane ed = new JEditorPane( "text/html", message );
			ed.setEditable( false );
			ed.setPreferredSize(new Dimension(500,200));
			ed.setBackground(Color.white);
			ed.setFont(new Font("Dialog",Font.PLAIN,10));
			JScrollPane pane = new JScrollPane();
			pane.getViewport().add( ed );
			p.add( "Center", pane );
			return p;
		}

		/**
		 *  Method used to center window on screen
		 */

   	public void centerComponent( ) {
				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension screenDim = tk.getScreenSize();
				screenDim.width  = (screenDim.width - this.getWidth()) / 2;
				screenDim.height = (screenDim.height - this.getHeight()) / 2;
				this.setLocation( screenDim.width, screenDim.height );
    }

		/**
		 *  default constructor
		 *
		 *  @param parent parent frame
		 */

		public SimLogLicenceWin( JFrame parent ) {
				super( parent, true );
				appli = parent;

				getContentPane().setLayout( new BorderLayout() );
				getContentPane().add( "Center", createMessagePanel() );
				getContentPane().add( "South", createButtonPanel() );
				pack();
		}

		/**
		 *  action manager
		 */
				
		public void actionPerformed( ActionEvent e ) {
				if (e.getSource() instanceof JButton) {
					JButton b = (JButton) e.getSource();
					if (b == bOk) {
						dispose();
					}
				}
		}
}