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
/* // | class   :  SimLogTruthTableSelectionWin                        | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 16, 2003                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *   This window helps the user define the number of switch and LED
 *   that will be used for the circuit
 *
 *   @version 2.2, 16October 2003
 *   @author Jean-Michel Richer
 */

package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Vector;

public class SimLogTTSelWin extends JDialog
   implements ActionListener {


		//

		SimLogWin appli;
		SimLogCircuit circuit;

		JButton  bClose;
		JButton  bNext;
		JButton  bPrev;
	  JButton  bGenerate;

		int        nbrOutputGates;
		SimLogGate tabOutputGates[];
		String     tabOutputNames[];
		int        tabSelectedIndices[];

		JTabbedPane paned;
		Vector listOfInputs;

		// Variables for first Panel
		MyTableModel tableOfOutputsModel;
		JCheckBox selectAllOutputs;
		JTable tableOfOutputs;
		String colsOfTableOfOutputs[]={ "LED", "Select" };
		Object dataOfTableOfOutputs[][];

		SelectInputTableModel tableOfInputsModel;
		JTable tableOfInputs;


		private void addInputIfMissing(SimLogGate g) {
			if (!listOfInputs.contains(g)) listOfInputs.add(g);
		}

		private void lookForInputsForGate(SimLogGate g) {
			SimLogLink link;

			switch(g.getType()) {
				case SimLogGate.SWITCH_GATE:
				  addInputIfMissing(g);
					break;
				case SimLogGate.AND_GATE:
				case SimLogGate.OR_GATE:
				case SimLogGate.NAND_GATE:
				case SimLogGate.NOR_GATE:
				case SimLogGate.XOR_GATE:
					link=g.getInputLink(0);
					lookForInputsForGate(link.getOutputGate());
					link=g.getInputLink(1);
					lookForInputsForGate(link.getOutputGate());
				break;
				case SimLogGate.NOT_GATE:
				case SimLogGate.LED_GATE:
					link=g.getInputLink(0);
					lookForInputsForGate(link.getOutputGate());
				break;
			}
		}

		private void lookForInputs(int tab[]) {
			int i;

			for (i=0;i<tab.length;i++) {
				if (tab[i]==1) lookForInputsForGate(tabOutputGates[i]);
			}
		}


		/**
		 *  obtain number of Outputs (LEDs)
		 *
		 */

		private void getCircuitInformation() {
			int i;

			nbrOutputGates=circuit.getNbrLED();
			tabOutputGates=new SimLogGate[nbrOutputGates];
			tabOutputNames=new String[nbrOutputGates];
			for (i=0;i<nbrOutputGates;i++) {
				tabOutputGates[i]=circuit.getLED(i);
				tabOutputNames[i]=tabOutputGates[i].getName();
			}
		}

		/**
		 *  create a panel with Ok and Cancel buttons
		 *
		 *  @return JPanel
		 */

    private JPanel createButtonPanel( ) {
				JPanel panel = new JPanel();
				panel.setLayout( new FlowLayout() );
				bClose = new JButton( " Close " );
				bClose.addActionListener( this );
				panel.add( bClose );
				return panel;
    }


		JPanel createFirstPanel() {
			int i;

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());

			dataOfTableOfOutputs=new Object[nbrOutputGates][2];
			for (i=0;i<nbrOutputGates;i++) {
				dataOfTableOfOutputs[i][0]=tabOutputNames[i];
				dataOfTableOfOutputs[i][1]=new Boolean(false);
			}

			tableOfOutputsModel=new MyTableModel(dataOfTableOfOutputs);
			//tableOfOutputs=new JTable(dataOfTableOfOutputs,colsOfTableOfOutputs);
			tableOfOutputs=new JTable(tableOfOutputsModel);

			JScrollPane scrollpane=new JScrollPane(tableOfOutputs);
			scrollpane.setPreferredSize(new Dimension(200,100));


			selectAllOutputs=new JCheckBox("select all");
			selectAllOutputs.addActionListener(this);

			JPanel northPanel=new JPanel();
			northPanel.setLayout(new FlowLayout());
			northPanel.add(new JLabel(" "));
			northPanel.add(selectAllOutputs);

			JPanel southPanel=new JPanel();
			bNext=new JButton(">>");
			bNext.addActionListener(this);
			southPanel.setLayout(new FlowLayout());
			southPanel.add(bNext);

			panel.add("North",northPanel);
			panel.add("East",new JLabel("    "));
			panel.add("West",new JLabel("    "));
			panel.add("Center",scrollpane);
			panel.add("South",southPanel);
			return panel;
		}


		JPanel createSecondPanel() {
			int i;

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());

			tableOfInputsModel=new SelectInputTableModel();
			tableOfInputs=new JTable(tableOfInputsModel);

			JScrollPane scrollpane=new JScrollPane(tableOfInputs);
			scrollpane.setPreferredSize(new Dimension(200,100));

			panel.add("Center",scrollpane);
			bPrev=new JButton("<<");
			bPrev.addActionListener(this);
			bGenerate=new JButton("Generate");
			bGenerate.addActionListener(this);

			JPanel bPanel=new JPanel();
			bPanel.setLayout(new FlowLayout());
			bPanel.add(bPrev);
			bPanel.add(bGenerate);

			panel.add("South",bPanel);
			return panel;
		}

		JPanel createUserInterface() {
			JPanel topPanel=new JPanel();
			topPanel.setLayout(new BorderLayout());

			paned=new JTabbedPane();
			paned.add("Input",createFirstPanel());
			paned.add("Column",createSecondPanel());

		  topPanel.add("Center",paned);
			topPanel.add("South",createButtonPanel());

			return topPanel;
		}

		private void updateTable(int tab[]) {
			SimLogGate gate;
			int i, n;
			String cols[];

			listOfInputs.removeAllElements();
			lookForInputs(tab);
			n=listOfInputs.size();
			cols=new String[n];
			for (i=0;i<n;i++) {
				gate=(SimLogGate)listOfInputs.elementAt(i);
				cols[i]=gate.getName();
				//System.out.println("INput "+gate.getName());
			}
			tableOfInputsModel=new SelectInputTableModel(cols);
			tableOfInputs.setModel(tableOfInputsModel);
			repaint();
		}


		/**
		 *  Constructor
		 *
		 *  @param parent parent application
		 */

		public SimLogTTSelWin( SimLogWin parent ) {
			setTitle("Truth Table Selection");
			appli=parent;
			circuit=appli.getCircuit();
			getCircuitInformation();
			getContentPane().add(createUserInterface());
			listOfInputs=new Vector();
			pack();
		}

		/**
		 *  action manager
		 */

		public void actionPerformed( ActionEvent e ) {
				if (e.getSource() == bClose) {
					dispose();
				} else if (e.getSource() == bNext) {
					int i;

					if (tableOfOutputsModel.hasSelected()) {
						tabSelectedIndices=new int[tableOfOutputsModel.getRowCount()];
						tableOfOutputsModel.getSelected(tabSelectedIndices);
						updateTable(tabSelectedIndices);
						paned.setSelectedIndex(1);
					}
				} else if (e.getSource() == bPrev) {
					paned.setSelectedIndex(0);
				} else if (e.getSource() == bGenerate) {
					generate();
				} else if (e.getSource() == selectAllOutputs) {
					if (selectAllOutputs.isSelected())
						tableOfOutputsModel.selectAll();
					else
						tableOfOutputsModel.deselectAll();
				}

    }

		/**
		 *  Table Model for LED selection
		 *
		 */

		class MyTableModel extends AbstractTableModel {
        final String[] columnNames = {"LED Name",
                                      "Select" };
        private Object[][] data;

				public MyTableModel( Object tab[][] ) {
					data=tab;
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
            if (col == 0) {
                return false;
            } else {
                return true;
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
/*
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }
*/
            if (data[0][col] instanceof Integer
                    && !(value instanceof Integer)) {
                //With JFC/Swing 1.1 and JDK 1.2, we need to create
                //an Integer from the value; otherwise, the column
                //switches to contain Strings.  Starting with v 1.3,
                //the table automatically converts value to an Integer,
                //so you only need the code in the 'else' part of this
                //'if' block.
                //XXX: See TableEditDemo.java for a better solution!!!
                try {
                    data[row][col] = new Integer(value.toString());
                    fireTableCellUpdated(row, col);
                } catch (NumberFormatException e) {
/*
                    JOptionPane.showMessageDialog(appli,
                        "The \"" + getColumnName(col)
                        + "\" column accepts only integer values.");
*/
                }
            } else {
                data[row][col] = value;
                fireTableCellUpdated(row, col);
            }
/*
            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
*/
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }

				public void selectAll() {
					int i;
					for (i=0;i<getRowCount();i++) {
							setValueAt(new Boolean(true),i,1);
					}
				}

				public void deselectAll() {
					int i;
					for (i=0;i<getRowCount();i++) {
							setValueAt(new Boolean(false),i,1);
					}
				}

				public void getSelected(int tab[]) {
					int i;

					for (i=0;i<getRowCount();i++) {
						Boolean b=(Boolean)getValueAt(i,1);
						if (b.booleanValue()==true)
							tab[i]=1;
						else
							tab[i]=0;
					}
				}

				public boolean hasSelected() {
					int i;

					for (i=0;i<getRowCount();i++) {
						Boolean b=(Boolean)getValueAt(i,1);
						if (b.booleanValue()==true) return true;
					}
					return false;
				}
    }



		/**
		 *  Table Model for SWITCH selection
		 *
		 */

		class SelectInputTableModel extends AbstractTableModel {
        private String[] columnNames;
        private Object[][] data;

				public SelectInputTableModel( ) {
					int i;
					columnNames=new String[2];
					columnNames[0]=new String("c1");
					columnNames[1]=new String("c2");
					data=new Object[1][2];
		 			data[0][0]=new Integer(1);
		 			data[0][1]=new Integer(2);
				}

				public SelectInputTableModel( String cols[] ) {
					int i;

					columnNames=cols;
					data=new Object[1][cols.length];
					for (i=0;i<cols.length;i++) data[0][i]=new Integer(1);
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


		private void generate() {
			int i,j,n;
			String s;
			SimLogGate g;
			Vector vIn, vOut;

			vIn=new Vector();
			vOut=new Vector();

			n=tableOfInputs.getColumnCount();
			for (i=0;i<n;i++) {
				s=tableOfInputs.getColumnName(i);
				for (j=0;j<circuit.getNbrSwitch();j++) {
					g=circuit.getSwitch(j);
					if (s.equals(g.getName())) {
						vIn.add(g);
						break;
					}
				}
			}
			for (i=0;i<tabSelectedIndices.length;i++) {
				if (tabSelectedIndices[i]==1) vOut.add(tabOutputGates[i]);
			}
			SimLogTruthTable tt=new SimLogTruthTable(circuit,vIn,vOut);
			tt.generateTable();
			tt.print();
			SimLogTruthTableWin win=new SimLogTruthTableWin(appli,tt);
			win.show();
		}


}