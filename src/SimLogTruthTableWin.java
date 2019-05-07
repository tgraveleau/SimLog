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
/* // | class   :  SimLogTruthTableWin                                 | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  September 30, 2003                                  | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */


/**
 *   This is the Truth Table construction and display part
 *
 *   @version 2.2, 30 September 2003
 *   @author Jean-Michel Richer
 */

package src;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Vector;


public class SimLogTruthTableWin extends JFrame
		implements ActionListener {

		//
		// class variables
		//

		private SimLogWin     parent;
		private SimLogCircuit circuit;
		private JButton       bOk;
		private JTable        table;
		private IntegerTableModel tableModel;

		private SimLogTruthTable truthTable;

		/**
 		 *  default constructor
		 *  This method will create a truth table of the entire circuit
		 */

		public SimLogTruthTableWin( SimLogWin appli, SimLogTruthTable t ) {
			setTitle("Truth Table");
			parent=appli;
			truthTable=t;
			createUserInterface();
			pack();
		}


		/**
     *  create a user interface
     */

		JTable createTable() {
			int i,j,nIn, nOut;
			int tabValues[][];
			String cols[];
			Object data[][];

			nIn = truthTable.getNbrInputs();
			nOut= truthTable.getNbrOutputs();
			tabValues=truthTable.getData();


			cols=new String[nIn+nOut];
			for (i=0;i<nIn;i++) {
				cols[i]=new String(truthTable.getInput(i).getName());
			}
			for (i=0;i<nOut;i++) {
				cols[nIn+i]=new String(truthTable.getOutput(i).getName());
			}
			data= new Object[1<<nIn][nIn+nOut];
			nOut+=nIn;
			nIn=1<<nIn;
			for (i=0;i<nIn;i++) {
				for (j=0;j<nOut;j++) {
					//System.err.println(" "+i+" "+j+" ");
					//data[i][j]=new String(" "+tabValues[i][j]+" ");
					data[i][j]=new Integer(tabValues[i][j]);
				}
			}
			tableModel=new IntegerTableModel(cols,data);
			table=new JTable(tableModel);
			return table;
		}

		/**
     *  create a user interface
     */

		public void createUserInterface( ) {
			JPanel panel=new JPanel();
			panel.setLayout(new BorderLayout());
			JPanel buttonPanel=new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			bOk=new JButton("  OK  ");
			bOk.addActionListener(this);
			buttonPanel.add(bOk);

			JScrollPane scrollpane=new JScrollPane(createTable());
			scrollpane.setPreferredSize(new Dimension(200,100));
			scrollpane.getViewport().setBackground(Color.white);

			panel.add("Center",scrollpane);
			panel.add("South",buttonPanel);

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add("Center",panel);
		}



		/**
		 *  Wait for the user to press the Ok button
		 */

		public void actionPerformed( ActionEvent e ) {
			if (e.getSource()==bOk) {
				dispose();
			}
		}


		/**
		 *  Table Model for integer values
		 *
		 */

		class IntegerTableModel extends AbstractTableModel {
        private String[] columnNames;
        private Object[][] data;

				public IntegerTableModel( String cols[], Object mat[][] ) {
					int i;

					columnNames=cols;
					data=mat;
				}

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return false;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
        }

    }
}
