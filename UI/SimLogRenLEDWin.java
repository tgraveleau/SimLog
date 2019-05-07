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
/* // | class   :  SimLogRenLEDWin                                     | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This window helps the user rename a LED
 *
 *   @version 2.1, 14 October 2002
 *   @author Jean-Michel Richer
 */

package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Gate.SimLogGate;
import Moteur.SimLogCircuit;


public class SimLogRenLEDWin extends JDialog implements ActionListener {

	//
	// variables
	//

	private JComboBox name;
	private JButton bYes;
	private JButton bNo;
	private int initialValue;
	private boolean state = true;
	private boolean tab[];

	/**
	 * create a panel with Ok and Cancel buttons
	 */

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		bYes = new JButton("  Ok  ");
		bYes.addActionListener(this);
		bNo = new JButton(" Cancel ");
		bNo.addActionListener(this);

		panel.add(bYes);
		panel.add(bNo);
		return panel;
	}

	/**
	 * create a message panel
	 * 
	 * @return JPanel
	 */

	private JPanel createMessagePanel(boolean t[], String s) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel(" Old name : "));
		panel.add(new JLabel(s));
		panel.add(new JLabel(" New name : "));
		name = new JComboBox();
		for (int i = 0; i < t.length; i++) {
			if (t[i] == true) {
				name.addItem(new String("L" + String.valueOf(i + 1)));
			}
		}
		name.setSelectedIndex(0);
		panel.add(name);
		return panel;
	}

	/**
	 * method used to center window on screen
	 */

	public void centerComponent() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenDim = tk.getScreenSize();
		screenDim.width = (screenDim.width - this.getWidth()) / 2;
		screenDim.height = (screenDim.height - this.getHeight()) / 2;
		this.setLocation(screenDim.width, screenDim.height);
	}

	/**
	 * constructor
	 * 
	 * @param parent
	 *            parent Frame
	 * @param g
	 *            SimLog LED Gate
	 * @param c
	 *            SimLogCircuit
	 */

	public SimLogRenLEDWin(Frame parent, SimLogGate g, SimLogCircuit c) {
		super(parent, true);
		initialValue = Integer.parseInt(g.getName().substring(1)) - 1;
		tab = c.getAvailLEDNames();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center", createMessagePanel(tab, g.getName()));
		getContentPane().add("South", createButtonPanel());
		pack();
	}

	/**
	 * action manager
	 */

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bYes) {
			state = true;
			dispose();
		} else if (e.getSource() == bNo) {
			state = false;
			dispose();
		}
	}

	/**
	 * return user confirmation
	 * 
	 * @return <code>true</code> if user clicked on the Ok button,
	 *         <code>false</code> if user clicked on the Cancel button
	 */

	public boolean getState() {
		return state;
	}

	/**
	 * return new name
	 * 
	 * @return new name of LED
	 */

	public String getNewName() {
		int n;
		n = Integer.parseInt(((String) name.getSelectedItem()).substring(1)) - 1;
		tab[initialValue] = true;
		tab[n] = false;
		return new String("L" + String.valueOf(n + 1));
	}

}