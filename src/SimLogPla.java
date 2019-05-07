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
/* // | class   :  SimLogPla                                           | // */
/* // | author  :                                                      | // */
/* // | date    :                                                      | // */
/* // | place   :                                                      | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  interface PLA
 */

package src;


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class SimLogPla extends JFrame
	implements ActionListener {

  // variables

  // dimensions de la zone graphique en pixels

  private int TailleX;
  private int TailleY;

 // dimensions (arbitraires) d'une zone d'intersection pour la PlaInterface

  private int xZone = 40;
  private int yZone = 50;

  // nombre de variables et nombre de monomes
  private int NbVar;
  private int NbMonome;

  // boolean permettant de savoir si l'utilisateur a fait une erreur
  private boolean erreur = false;

	private JButton         bSimplify;
	private JButton         bClose;
	private SimLogPlaCanvas canvas;
	private SimLogWin       appli;


	private JPanel createUserInterface() {
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());

		canvas=new SimLogPlaCanvas(this,NbVar,NbMonome);
		bSimplify=new JButton("Simplify");
		bSimplify.addActionListener(this);
		bClose=new JButton("Close");
		bClose.addActionListener(this);

		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(bSimplify);
		buttonPanel.add(bClose);

		panel.add("South",buttonPanel);
		panel.add("Center",canvas);
		return panel;
	}

  /**
	 *  Constructor
	 */

	public SimLogPla(SimLogWin parent,int nv,int nm) {
		setTitle("PLA");
		appli=parent;
  	// initialisation des parametres
  	NbVar = nv;
  	NbMonome = nm;

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center",createUserInterface());
		setResizable(false);
  	setLocationRelativeTo(this);
		pack();
	}

	public void actionPerformed( ActionEvent e ) {
		if (e.getSource()==bSimplify) {
			simplify();
		}
		if (e.getSource()==bClose) {
			dispose();
		}
	}

	private void simplify() {
		if (NbVar>5) {
			JOptionPane.showMessageDialog(null,"More than 4 variables : the table may be difficult to understand","Information : ",1);
		}
		SimLogListMask sllm = new SimLogListMask(canvas.getTablePla(),NbVar,NbMonome);
	}


}