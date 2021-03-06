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
/* // | class   :  SimLogListMask                                      | // */
/* // | author  :                                                      | // */
/* // | date    :                                                      | // */
/* // | place   :                                                      | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  Classe Affichant une Liste Capable de S�lectionner les Diff�rents Masques
 */

package UI;

import javax.swing.*;
import javax.swing.event.*;

import Moteur.SimLogAnaSynt;
import Moteur.SimLogFormula;

import java.awt.*;
import java.awt.event.*;

public class SimLogListMask extends JFrame {
	// Champs

	private int NbMask;
	private int Longueur;
	private int Hauteur;

	private JList liste;
	private JPanel conteneur = new JPanel();
	private JButton GenColors;
	private JButton GenCircuit;

	private SimLogAfficheKarnaugh AffKar;

	// Constructeur

	public SimLogListMask(boolean[][] Table, int nv, int nm) {
		// Cr�ation d'un Affichage d'une Table de Karnaugh AffKar

		AffKar = new SimLogAfficheKarnaugh(Table, nv, nm);

		// Initialisation des Champs

		NbMask = AffKar.getSizeVect();
		Longueur = 150;
		Hauteur = (NbMask * 16) + 100;

		// Cr�ation de la Fenetre

		this.setTitle("Maskes");
		this.setSize(Longueur, Hauteur);
		this.setLocation(0, 30);
		this.setResizable(false);
		this.setVisible(true);

		// si la table de Karnaugh n'est pas vide
		if (NbMask > 0) {

			JOptionPane.showMessageDialog(null,
					"Click on the Maskes List to Select Monomes",
					"Information : How to Use the List", 1);

			Cursor cur = new Cursor(Cursor.HAND_CURSOR);

			// Bouton de G�n�ration Al�atoire de Couleurs

			GenColors = new JButton("Generate New Colors");
			GenColors.setBackground(Color.lightGray);
			GenColors.setForeground(Color.black);
			GenColors.setBorder(BorderFactory.createRaisedBevelBorder());
			GenColors.setFocusPainted(false);
			GenColors.setCursor(cur);
			GenColors.setPreferredSize(new Dimension(Longueur, 20));
			
			GenCircuit = new JButton("Generate to circuit");

			// Cr�ation de la Liste

			liste = new JList(AffKar.getVectMask());
			String s = (String)AffKar.getVectMask().elementAt(0);
			System.out.println(s);
			liste.setCellRenderer(new SimLogCellRenderer(AffKar.getTabColors()));
			JScrollPane jsplist = new JScrollPane(liste);

			BorderLayout LayoutParam = new BorderLayout();
			conteneur.setLayout(LayoutParam);
			conteneur.add(BorderLayout.NORTH, GenColors);
			conteneur.add(BorderLayout.CENTER, jsplist);
			conteneur.add(BorderLayout.SOUTH, GenCircuit);

			this.setContentPane(conteneur);
			this.setVisible(true);

			GenColors.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					AffKar.IniTabColors(NbMask);
					liste.setCellRenderer(new SimLogCellRenderer(AffKar
							.getTabColors()));
					AffKar.repaint();
				}
			});
			
			GenCircuit.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					createCircuit();
					
				}
			});

			// Ajout du Listener

			liste.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					for (int k = 0; k < NbMask; k++) {
						if (liste.isSelectedIndex(k)) {
							AffKar.setCocheMask(k, true);
						}

						else {
							AffKar.setCocheMask(k, false);
						}

					}

					AffKar.repaint();

				} // Fin valueChanged

			}); // Fin Listener

		}// fin if nbmask > 0

		else {
			JOptionPane.showMessageDialog(null,
					"There is no Formula to Simplify !",
					"Error : No Monomes Available", 2);
		}

	} // Fin Constructeur
	
	private void createCircuit() {
		String s = "";//¬
		for(int i=0 ; i<AffKar.getVectMask().size();i++) {
			s += "(";
			String s2 = (String)AffKar.getVectMask().elementAt(i);
			for(int j=0 ; j<s2.length() ; j++) {
				if(s2.charAt(j) == '¬') {
					s +="-";
				}
				else if(j != s2.length()-1) {
					s += s2.charAt(j);
					s += ".";
				}
				else {
					s += s2.charAt(j);
				}
				
			}
			if(i != AffKar.getVectMask().size()-1) {
				s+=")+";
			}
			else {
				s+=")";
			}
		}
		SimLogAnaSynt anasynt = new SimLogAnaSynt(s);
		anasynt.expr();
		if (anasynt.isCorrect()) {
			// anasynt.printStack();
			SimLogFormula formula = new SimLogFormula(anasynt.getStack());
			if (formula.isCorrect())
				SimLogWin.getCircuit().buildCircuit(formula.getRoot());
				SimLogWin.getCircuit().reorganize();
				SimLogPanel.leCanvas().repaint();
		}
	}

} // Fin SimLogListMask
