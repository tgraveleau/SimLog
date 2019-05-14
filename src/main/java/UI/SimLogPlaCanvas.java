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
/* // | authors :  Sebastien RIDE, Xavier STEPHAN, Jean-Michel RICHER  | // */
/* // | date    :  May 2003                                            | // */
/* // | place   :  University of Angers                                | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This canvas is used to display the PLA selections
 *
 *   @version 2.2, October 2003
 *   @author Jean-Michel Richer
 */

package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class SimLogPlaCanvas extends JPanel implements MouseListener {
	// variables

	// dimensions de la zone graphique en pixels

	private int TailleX;
	private int TailleY;

	// dimensions (arbitraires) d'une zone d'intersection pour la PlaInterface

	private int xZone = 30;
	private int yZone = 40;

	// nombre de variables et nombre de monomes
	private int NbVar;
	private int NbMonome;

	// boolean permettant de savoir si l'utilisateur a fait une erreur
	private boolean erreur = false;

	// matrice qui repr�sente le PLA
	private boolean[][] TablePla;

	private SimLogPla appli;

	/**
	 * Constructor
	 * 
	 * @param nv
	 *            number of boolean variables
	 * @param nm
	 *            number of monomials
	 */

	public SimLogPlaCanvas(SimLogPla parent, int nv, int nm) {
		appli = parent;
		// initialisation des parametres
		NbVar = nv;
		NbMonome = nm;

		// dimension des Y : on rajoute 3 au monome pour prendre en compte
		// - la ligne du message d'erreur
		// - la ligne des noms des variables (lettres)
		// - le bouton pour faire la table de Karnaugh
		// dimension des X : on multiplie par deux car on a deux colonnes par
		// variables (var+n�gation)
		// on rajoute var*16 car on affiche en bout de ligne le monome
		// au maximum on a "var" variables, et il faut 16 pixels pour afficher
		// la lettre

		TailleX = 2 * NbVar * xZone + NbVar * 16;
		TailleY = (NbMonome + 3) * yZone;
		TablePla = new boolean[NbMonome][NbVar * 2];

		// initialisation de tablePla
		for (int i = 0; i < NbVar * 2; i++) {
			for (int j = 0; j < NbMonome; j++) {
				TablePla[j][i] = false;
			}
		}

		// parametres d'affichage de la fenetre
		setPreferredSize(new Dimension(TailleX, TailleY));

		// ajout du listener

		addMouseListener(this);
	}

	// PAINT

	public void paintComponent(Graphics g) {
		int Xaux = (xZone / 2);
		int Yaux = yZone;
		String lettre;

		// on efface la zone graphique
		g.setColor(Color.white);
		g.fillRect(0, 0, TailleX, TailleY);

		// on trace les lettres et les lignes verticales
		for (int i = 0; i < (NbVar * 2); i++) {
			// changement de couleur pour les n�gations
			if ((i % 2) == 0) {
				g.setColor(Color.black);
			} else {
				g.setColor(Color.blue);
			}
			// d�termination de la lettre
			lettre = String.valueOf((char) (65 + (i / 2)));
			g.drawString(lettre, Xaux, Yaux);
			g.drawLine(Xaux, yZone + (yZone / 2), Xaux, TailleY - 2 * yZone);
			Xaux = Xaux + xZone;
		}

		// on trace les lignes horizontales
		g.setColor(Color.black);
		Xaux = (xZone / 2);
		for (int i = 0; i < NbMonome; i++) {
			Yaux = Yaux + yZone;
			g.drawLine(Xaux, Yaux, TailleX, Yaux);
		}

		// on trace les n�gations au dessus des lettres
		g.setColor(Color.blue);
		Yaux = yZone - 12;
		Xaux = xZone + (xZone / 2);

		for (int i = 0; i < NbVar; i++) {
			g.drawLine(Xaux - 2, Yaux, Xaux + 8, Yaux);
			Xaux = Xaux + 2 * xZone;
		}

		// dessin des points a partir de TablePla
		g.setColor(Color.red);
		for (int i = 0; i < NbVar * 2; i++) {
			for (int j = 0; j < NbMonome; j++) {
				if (TablePla[j][i]) {
					g.fillOval((i * xZone + (xZone / 2)) - 5,
							(j * yZone + 2 * yZone) - 5, 10, 10);
				}
			}
		}

		// on "dessine" la formule
		g.setColor(Color.magenta);
		Xaux = NbVar * 2 * xZone;
		Yaux = 2 * yZone - 5;
		for (int j = 0; j < NbMonome; j++) {
			for (int i = 0; i < (NbVar * 2); i++) {
				if (TablePla[j][i]) {
					// on trace la lettre
					lettre = String.valueOf((char) (65 + (i / 2)));
					g.drawString(lettre, Xaux, Yaux);
					// on trace la negation si besoin
					if ((i % 2) == 1) {
						g.drawLine(Xaux - 2, Yaux - 12, Xaux + 8, Yaux - 12);
					}
					// on d�cale l'indice Xaux pour la lettre suivante
					Xaux += 15;
				}
			} // fin pourX
			Xaux = NbVar * 2 * xZone;
			Yaux += yZone;
		} // fin pourY

	} // fin paint

	// MOUSE LISTENER

	// mise a jour de la matrice au click de souris

	public void mouseClicked(MouseEvent e) {
		int coordX = e.getX();
		int coordY = e.getY();

		int divX = (coordX / xZone);
		// les deux premieres zones sont pour la barre de la fenetre et les
		// variables
		int divY = (coordY / yZone) - 2;
		// determination de la ligne de la matrice
		if ((coordY % yZone) > (yZone / 2))
			divY++;
		// test de validit� des coordonn�es
		if ((divY >= 0) && (divX <= NbVar * 2) && (divY < NbMonome)) {
			// v�rification que l'on n'a pas une variable et sa n�gation sur la
			// meme ligne
			if ((divX % 2) == 0) {
				if (TablePla[divY][divX + 1] == false) {
					TablePla[divY][divX] = !TablePla[divY][divX];
					erreur = false;
					repaint();
				} else {
					erreur = true;
					repaint();
				}
			} else {
				// on veut la n�gation (indice impair)
				if (TablePla[divY][divX - 1] == false) {
					TablePla[divY][divX] = !TablePla[divY][divX];
					erreur = false;
					repaint();
				} else {
					erreur = true;
					repaint();
				}
			}
		}
	} // fin mouseclicked

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public int getNbVar() {
		return NbVar;
	}

	public int getNbMonome() {
		return NbMonome;
	}

	public boolean[][] getTablePla() {
		return TablePla;
	}

}