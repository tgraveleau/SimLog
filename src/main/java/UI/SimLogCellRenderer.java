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
/* // | class   :  SimLogCellRenderer                                  | // */
/* // | author  :                                                      | // */
/* // | date    :                                                      | // */
/* // | place   :                                                      | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  Classe Permettant de Cr�er un Rendu
 *  Pour Afficher la Liste des Masques
 */

package UI;

import javax.swing.*;
import java.awt.*;

public class SimLogCellRenderer extends JLabel implements ListCellRenderer {
	// Champs

	private Color[] TabColors;

	// Constructeur

	public SimLogCellRenderer(Color[] Tab) {
		setOpaque(true);
		TabColors = Tab;
	}

	// Accesseur

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setText(value.toString());

		if (isSelected) {
			setForeground(Color.white);
		} else {
			setForeground(Color.black);
		}
		setBackground(TabColors[index]);

		return this;
	}

} // Fin SimLogCellRenderer
