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
/* // | class   :  SimLogNameSwitchWin                                 | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 13, 2003                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This window helps the user rename a Switch
 *
 *   @version 2.2, 13 October 2003
 *   @author Jean-Michel Richer
 */

package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SimLogNameSwitchWin extends JDialog implements ActionListener {

	private SimLogWin appli = null;
	private JTextField name = null;
	private JButton bYes;
	private JButton bNo;
	private boolean state = true;

	/**
	 * create a panel with Ok and Cancel buttons
	 * 
	 * @return JPanel
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

	private JPanel createMessagePanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel(" Name : "));
		name = new JTextField(6);
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
	 *            SimLog Switch Gate
	 * @param c
	 *            SimLogCircuit
	 */

	public SimLogNameSwitchWin(SimLogWin win) {
		super(win, true);
		setTitle("New Switch name");
		appli = win;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center", createMessagePanel());
		getContentPane().add("South", createButtonPanel());
		pack();
	}

	/**
	 * action manager
	 */

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bYes) {
			if (alreadyExistsName()) {
				System.out.println("ALREADY EXISTS !");
				appli.messageWarning("Gate with same name already exists !");
				appli.windowWarning("Gate with same name already exists !");
			} else {
				System.out.println("OK");
				state = true;
				dispose();
			}
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
	 * check if another gate has already the same name
	 * 
	 * return true if another gate has already the same name
	 */

	private boolean alreadyExistsName() {
		String s = name.getText();
		s.trim();
		if (appli.getCircuit().findGateByName(s) != null)
			return true;
		return false;
	}

	/**
	 * return new name
	 * 
	 * @return new name of Switch
	 */

	public String getName() {
		return name.getText().trim();
	}
}