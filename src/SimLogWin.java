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
/* // | class   :  SimLogWin                                           | // */
/* // | author  :  Jean Michel RICHER                                  | // */
/* // |            Jean-Michel.Richer@univ-angers.fr                   | // */
/* // | date    :  October 14, 2002                                    | // */
/* // | place   :  LERIA, University of Angers, France                 | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */


/**
 *   This is the main application window
 *
 *   @version 2.2, 14 October 2002
 *   @author Jean-Michel Richer
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Properties;
import javax.swing.*;
import java.util.Vector;


public class SimLogWin extends JFrame 
		implements ActionListener {

		// 
		// class variables
		//

		public  SimLogToolbar  toolbar;
		public  SimLogPanel    mainPanel;
		private SimLogCircuit  circuit;
		private String         circuitName; 
		private JTextField     message;

		private SimLogApplet   applet = null;

		private String fileSeparator;
		private File currentDir;

		/**
     *  default constructor
     */

		public SimLogWin( ) {
			super( "SimLog v2.1" );

			Properties p = System.getProperties();
			currentDir = new File( p.getProperty("user.dir" ) );	
			fileSeparator = p.getProperty( "file.separator" );

			createUserInterface();					
		}

		/**
     *  default constructor
     */

		public SimLogWin( SimLogApplet app ) {
			super( "SimLog v2.0" );

			applet = app;

			currentDir = new File( ".");	
 			fileSeparator = "//";

			createUserInterface();					
		}

		/**
     *  create a user interface
     */

		public void createUserInterface( ) {

			circuit = new SimLogCircuit();
			circuitName = null;

			Font font = new Font( "SansSerif", Font.PLAIN, 10 );
					
			message = new JTextField( );
			message.setEditable( false );
			message.setFont( font );
			toolbar = new SimLogToolbar( this );
				
			JPanel toolbarPanel = new JPanel();
			toolbarPanel.setLayout( new FlowLayout() );
			toolbarPanel.add( toolbar );

			JPanel topPanel = new JPanel();
			topPanel.setLayout( new BorderLayout() );
		  getContentPane().add( topPanel );
			mainPanel = new SimLogPanel( this );	
			//topPanel.add( toolbar, BorderLayout.NORTH );
			topPanel.add( mainPanel, BorderLayout.CENTER );
			topPanel.add( toolbarPanel, BorderLayout.WEST );
			topPanel.add( message, BorderLayout.SOUTH );

			setJMenuBar( createMenuBar() );
			//createGatePopup();

			this.addWindowListener( new WindowAdapter() {
					public void windowClosing( WindowEvent e ) { 
							saveAndExit();
					}
			} );
			setSize( new Dimension ( 650, 650 ) );					
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

		//
		// Labels of menus used to create menu bar.
		// - Any submenu starts with the "Menu" string
		// - Any string beginning with "!" defines a CheckboxMenu item
		//   followed by its default state "1" means active, "0" inactive
   	// - the string "-" means a separator
		//

		private String menuLabels[] = { 
			"Menu", "File", "New", "Load", "Save", "Quit", 
		  "Menu", "Action", "Edition", "Simulation",
			"Menu", "Display", "!1", "Gate name", "Grid", 
			"Menu", "Tools", "Formula", "Karnaugh", "Nand/Nor", "Truth table", "Reorganize",
      "Menu", "?", "About", "Licence" };

		private MenuItem menuItems[];

		private JMenu menuFile;
		private JMenuItem menuFileNew;
		private JMenuItem menuFileLoad;
		private JMenuItem menuFileSave;
		private JMenuItem menuFileQuit;

		private JMenu menuAction;
	  private JMenuItem menuActionEdit;
	  private JMenuItem menuActionSimul;

		private JMenu menuDisplay;
		private JMenuItem menuDisplayGateName;
		private JCheckBoxMenuItem menuDisplayGrid;

		private JMenu menuTools;
		private JMenuItem menuToolsFormula;
		private JMenuItem menuToolsKarnaugh;
		private JMenuItem menuToolsNandNor;
		private JMenuItem menuToolsTruthTable;
		private JMenuItem menuToolsReorganize;

		private JMenu menuInfos;
		private JMenuItem menuInfosAbout;
		private JMenuItem menuInfosLicence;

		/**
		 *
		 */

		private JMenuItem createMenuItem( JMenu m, String s, char mne ) {
			JMenuItem mi = new JMenuItem( s );
			mi.setMnemonic( mne );
			mi.addActionListener( this );
		  m.add( mi );
		  return mi;
		}

		/**
		 *
		 */

		private JCheckBoxMenuItem createCheckBoxMenuItem( JMenu m, String s) {
			JCheckBoxMenuItem mi=new JCheckBoxMenuItem(s);
			mi.addActionListener( this );
		  m.add( mi );
		  return mi;
		}		

		/**
     *  Create a menu bar using the menuLabels array
     *
		 *  @return a menu bar
     */

		public JMenuBar createMenuBar() {
			JMenuItem mi;

			JMenuBar menubar = new JMenuBar();
			
			menuFile = new JMenu( "File" );
			menuFile.setMnemonic( 'F' );
			menubar.add( menuFile );
			menuFileNew  = createMenuItem( menuFile, "New", 'N' );
			menuFileLoad = createMenuItem( menuFile, "Load", 'L' );
			menuFileSave = createMenuItem( menuFile, "Save", 'S' );
			menuFileQuit = createMenuItem( menuFile, "Quit", 'Q' );

			menuAction = new JMenu( "Action" );
			menuAction.setMnemonic( 'A' );
			menubar.add( menuAction );
			menuActionEdit  = createMenuItem( menuAction, "Edition", 'E' );
			menuActionSimul = createMenuItem( menuAction, "Simulation", 'S' );


			menuDisplay = new JMenu( "Display" );
			menuDisplay.setMnemonic( 'D' );
			menubar.add( menuDisplay );
			menuDisplayGateName = createMenuItem( menuDisplay, "Gate name", 'G' );
			menuDisplayGrid     = createCheckBoxMenuItem(menuDisplay, "Grid");


			menuTools = new JMenu( "Tools" );
			menuTools.setMnemonic( 'T' );
			menubar.add( menuTools );
			menuToolsFormula  = createMenuItem( menuTools, "Formula", 'F' );
			menuToolsKarnaugh = createMenuItem( menuTools, "Karnaugh", 'K' );
			menuToolsNandNor  = createMenuItem( menuTools, "Nand/Nor", 'N' );
			menuToolsTruthTable=createMenuItem( menuTools, "Truth Table", 'T' );
			menuToolsReorganize=createMenuItem( menuTools, "Reorganize", 'R' );

			menuInfos = new JMenu( "?" );
			menuInfos.setMnemonic( '?' );
			menubar.add( menuInfos );
			menuInfosAbout = createMenuItem( menuInfos, "About", 'A' );
			menuInfosLicence = createMenuItem( menuInfos, "Licence", 'L' );


			return menubar;
		}

		/**
		 *  Display a message to inform the user that a functionality
		 *  is not implemented
		 */

		public void notImplemented( ) {
				JOptionPane w = new JOptionPane();
				w.showMessageDialog( this, "This functionality is not yet implemented" , "Not implemented", 
					JOptionPane.WARNING_MESSAGE );			
		}

		/**
		 *  Display a message to inform the user that a fonctionality
     *  can not be used in applet mode
		 */

		public void notAllowedInAppletMode() {
				JOptionPane w = new JOptionPane();
				w.showMessageDialog( this, "Not allowed in Applet mode" , "Not Allowed", 
					JOptionPane.WARNING_MESSAGE );						
		}

		/**
		 *  Display a warning message in the message textfield
		 */

		public void messageWarning( String s ) {
				message.setText( s );
		}

		/**
		 *  empty the message textfield
		 */

		public void noMessage() {
				message.setText( " " );
		}

		/**
		 *  open a window displaying a message 
		 *
		 *  @param msg	message to display
		 */

		public void windowWarning( String msg ) {
				JOptionPane w = new JOptionPane();
				w.showMessageDialog( this, msg, "Warning", JOptionPane.WARNING_MESSAGE );
		}

		/**
		 *  open a window displaying a message and wait for answer of 
		 *  type Yes/No
		 *
		 *  @param msg	message to display
		 *  @return <code>true</code> if the answer was Yes, <code>false</code>
		 *          otherwise
		 */

		public boolean yesno( String msg ) {
				JOptionPane w = new JOptionPane();
				if (w.showConfirmDialog( this, msg, "", JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE, null ) == 0)
					return true;
				else
					return false;
		}


		/**
		 *
		 */

		public void newCircuit() {
				SimLogNewCircuitWin win=new SimLogNewCircuitWin(this);
				win.centerComponent();
				win.show();
				if (win.getState()==true) {
					int i, n;
					boolean auto;
					SimLogGate gate;
					Vector gateNames;

					mainPanel.emptyGates();

					// treat switches

					n=win.getNbrInputs();
					auto=win.getSwitchAutomaticNaming();
					if (auto==false) {
						SimLogNewAutoNamingWin win2=new SimLogNewAutoNamingWin(this,n,SimLogGate.SWITCH_GATE);
						win2.centerComponent();
						win2.show();
						gateNames=win2.getNames();
					} else {
						char tab[] = new char [1];
						gateNames=new Vector();
						for (i=0;i<n;i++) {
							tab[0]=(char)(65+i);
							gateNames.add(new String(tab));
						}
					}
					for (i=1;i<=n;i++) {
						circuit.addGate(20,20+(SimLogGate.HEIGHT+10)*(i-1),SimLogGate.SWITCH_GATE,(String)gateNames.elementAt(i-1));
					}
					gateNames.removeAllElements();
					gateNames=null;

					// treat LEDs

					n=win.getNbrOutputs();
					auto=win.getLEDAutomaticNaming();
					if (auto==false) {
						SimLogNewAutoNamingWin win2=new SimLogNewAutoNamingWin(this,n,SimLogGate.LED_GATE);
						win2.centerComponent();
						win2.show();
						gateNames=win2.getNames();
					} else {
						char tab[] = new char [3];
						tab[0]='L';
						gateNames=new Vector();
						for (i=1;i<=n;i++) {
							tab[1]=(char)(48+i/10);
							tab[2]=(char)(48+i%10);
							gateNames.add(new String(tab));
						}
					}
					for (i=1;i<=n;i++) {
						circuit.addGate(200,20+(SimLogGate.HEIGHT+10)*(i-1),SimLogGate.LED_GATE,(String)gateNames.elementAt(i-1));
					}
					gateNames.removeAllElements();
					gateNames=null;
						
					mainPanel.edition();
				}
				win=null;
		}


		private void enterFormula() {
				SimLogFormulaWin win=new SimLogFormulaWin(this);

				win.show();
				if (win.getState()==true) {
					circuit.buildCircuit(win.getRoot());
					circuit.reorganize();
				}
				mainPanel.edition();
		}

		private void karnaugh() {
			SimLogPlaParamWin win = new SimLogPlaParamWin(this);
			win.centerComponent();
			win.show();
		}

		/**
		 *  manager for menu items
		 */
		
		public void actionPerformed( ActionEvent e ) {
			
				if (e.getSource() == menuFileNew) {
						newCircuit();
				} else if (e.getSource() == menuFileLoad) {
						if (applet == null) loadCircuit();
						else notAllowedInAppletMode();
				} else if (e.getSource() == menuFileSave) {
						if (applet == null) saveCircuit();
						else notAllowedInAppletMode();
				} else if (e.getSource() == menuFileQuit) {
						saveAndExit();
				} else if (e.getSource() == menuActionEdit) {
						mainPanel.edition();
				} else if (e.getSource() == menuActionSimul) {
						if (!mainPanel.validation()) {
							windowWarning( "Circuit is not valid	" );
						} else {
							mainPanel.simulation();
						}
				} else if (e.getSource() == menuToolsFormula) {
					  enterFormula();
				} else if (e.getSource() == menuToolsKarnaugh) {
						karnaugh();
				} else if (e.getSource() == menuToolsNandNor) {
						notImplemented();
				} else if (e.getSource() == menuToolsTruthTable) {
						if (!mainPanel.validation()) {
							windowWarning( "Circuit is not valid	" );
						}  else {
							if ((circuit.getNbrSwitch()>0) && (circuit.getNbrLED()>0)) {
								SimLogTTSelWin win=new SimLogTTSelWin(this);
								win.show();
							}
						}
						mainPanel.edition();
				} else if (e.getSource() == menuToolsReorganize) {
						circuit.reorganize();
						repaint();
				} else if (e.getSource() == menuDisplayGrid) {
						mainPanel.changeGridMode();
				} else if (e.getSource() == menuInfosAbout) {
						about();
				} else if (e.getSource() == menuInfosLicence) {
						licence();
				}
		 
		}


		/**
		 *  save the current circuit displayed
		 */

		public void saveCircuit() {
				boolean save =  true;
				JFileChooser fc = new JFileChooser();
				
				if (fc.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION) {
					String name = fc.getSelectedFile().toString();
					if (name.length() > 0) {
						File file = new File( name );
						if (file.exists()) {
							save = yesno( "File exits. Do you want to overwrite file ?" );
						}
					}
					if (save) {
						circuitName = new String( name );
						circuit.save( circuitName );
						mainPanel.edition();
					}
				}
		}
	

		/**
		 *  replace current circuit by a circuit on disk
		 */

		public void loadCircuit() {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory( currentDir );
				if (fc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION) {
					circuitName = new String( fc.getSelectedFile().toString() );
					currentDir  = fc.getCurrentDirectory();
					mainPanel.emptyGates();
					circuit.load( circuitName );
				  mainPanel.edition();
				}
		}

		/**
		 *  Save circuit if needed and exit
		 */

		public void saveAndExit() {
				if (applet == null) {
					if (circuit.hasBeenModified()) {
			    	if (yesno( "Do you want to save file ?" ) == true) {
							if (circuitName == null) {
								saveCircuit();
							} else {
								circuit.save( circuitName );
							}
						}
					}
				}
				if (applet == null)
					System.exit(0);
				else
					applet.destroyFrame();
		}


		/**
     *  open a window displaying authors names
     */

		public void about() {
				SimLogContribWin win = new SimLogContribWin( this );
				win.centerComponent();
				win.show();
				win = null;
		}

		/**
     *  open a window displaying licence
     */

		public void licence() {
				SimLogLicenceWin win = new SimLogLicenceWin( this );
				win.centerComponent();
				win.show();
				win = null;
		}

		/**
		 *  return variable containing the description of the circuit
		 *
		 *  @return variable containing the description of the circuit
		 */

		public SimLogCircuit getCircuit() {
				return circuit;
		}

		/**
		 *  return file separator
		 */

		public String getFileSeparator() {
				return fileSeparator;
		}

		/**
		 *
		 */

		public Applet getApplet( ) {
				return applet;
		}

		/**
		 *  Main method
		 */

		public static void main( String args[] ) {
				SimLogWin win;

				win = new SimLogWin();
				win.pack();
				win.centerComponent();
				win.show();
		}			 
}