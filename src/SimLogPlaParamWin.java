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
/* // | class   :  SimLogPlaParam                                      | // */
/* // | author  :                                                      | // */
/* // | date    :                                                      | // */
/* // | place   :                                                      | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  Classe récupérant les paramètres pour une
 *  interface PLA et créant cette interface si les
 *  paramètres sont correctes 
 */


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class SimLogPlaParamWin extends JFrame 
	implements ActionListener {
 		
  // PARTIE GRAPHIQUE
  
	private SimLogWin  appli;  
  private JTextField tNbVar;
  private JTextField tNbMon;
  private JButton    bOk;
	private JButton    bCancel;

		
  
	private JPanel createUserInterface() {
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());

		tNbVar = new JTextField("",5);
		tNbMon = new JTextField("",5);
		bOk=new JButton("  Ok  ");
		bOk.addActionListener(this);
		bCancel=new JButton("  Cancel  ");
		bCancel.addActionListener(this);

		JPanel centerPanel=new JPanel();
		JPanel centerSubPanel1=new JPanel();
		JPanel centerSubPanel2=new JPanel();
		centerPanel.setLayout(new GridLayout(2,1));
		centerSubPanel1.setLayout(new FlowLayout());
		centerSubPanel2.setLayout(new FlowLayout());
		centerSubPanel1.add(new JLabel("Number of Variables :"));
		centerSubPanel1.add(tNbVar);
		centerSubPanel2.add(new JLabel("Number of monomials :"));
		centerSubPanel2.add(tNbMon);
		centerPanel.add(centerSubPanel1);
		centerPanel.add(centerSubPanel2);

		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(bOk);
		buttonPanel.add(bCancel);

		panel.add("South",buttonPanel);
		panel.add("Center",centerPanel);
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

  // CONSTRUCTEUR
  
  public SimLogPlaParamWin(SimLogWin parent) {
		appli=parent;
	  setTitle("Monomial formula");
	  setResizable(false);
	  //Cursor cur = new Cursor( Cursor.HAND_CURSOR ); 
    getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center",createUserInterface());
		pack();
	}

  // LISTENER
	 
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==bCancel) {
			dispose();
		}
		if (e.getSource()==bOk) {
			String s1, s2;
			boolean error=false;

			s1=tNbVar.getText().trim();
			s2=tNbMon.getText().trim();
  		if ((s1.length()!=0) && (s2.length()!=0)) {
	 			int nbVar, nbMon;

	  		try {
		  		nbVar = Integer.parseInt(s1);
				} catch(NumberFormatException e1) {
			  	nbVar = 0;
					appli.windowWarning("Bad format number for Number of Variables");
					error=true;
				}

		  	try {
			  	nbMon = Integer.parseInt(s2);
				} catch(NumberFormatException e2) {
		  		nbMon = 0;
					if (!error) appli.windowWarning("Bad format number for Number of Monomials");
					error=true;
				}
	  
	  		if ((nbVar<2) || (nbVar>8)) {
					nbVar=0;
					if (!error) appli.windowWarning("Number of variables must be between 2 and 8.");
					error=true;
				}
	  		if ((nbMon<2) || (nbMon>8)) {
					nbMon=0;
					if (!error) appli.windowWarning("Number of monomials must be between 2 and 8.");
					error=true;
				}

				if (!error)  {
			  	SimLogPla win=new SimLogPla(appli,nbVar,nbMon);
					win.show();
					this.dispose();
				}
				
			}
		}
	} 
}
	

