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
 *  Classe r�cup�rant les param�tres pour une
 *  interface PLA et cr�ant cette interface si les
 *  param�tres sont correctes
 */

package UI;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.Vector;

import javax.swing.*;

import com.opencsv.CSVReader;

import Gate.SimLogGate;
import Moteur.SimLogTruthTable;


public class SimLogPlaParamWin extends JFrame implements ActionListener {

	  // PARTIE GRAPHIQUE
	  
		private SimLogWin  appli;  
		private JTextField tNbVar;
	  	private JTextField tNbMon;
	  	private JButton    bOk;
		private JButton    bCancel;
		private JButton	   bLoad;
		private JButton	   bFromCircuit;
		private SimLogCsvOutputChoiceWin choiceWin;
		

			
	  
		private JPanel createUserInterface() {
			JPanel panel=new JPanel();
			panel.setLayout(new BorderLayout());

			tNbVar = new JTextField("",5);
			tNbMon = new JTextField("",5);
			bOk=new JButton("  Ok  ");
			bOk.addActionListener(this);
			bCancel=new JButton("  Cancel  ");
			bCancel.addActionListener(this);
			bLoad = new JButton(" Load CSV ");
			bLoad.addActionListener(this);
			bFromCircuit = new JButton(" Karnaugh from circuit ");
			bFromCircuit.addActionListener(this);

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
			buttonPanel.add(bLoad);
			buttonPanel.add(bFromCircuit);

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
			if(e.getSource()==bLoad) {
				chargerCSV();
			}
			if(e.getSource()==bFromCircuit) {
				circuitToKarnaugh();
			}
		} 
		
		/**
		 * Ouvre une fenêtre qui permet de choisir un fichier et de vérifier que ce dernier
		 * soit un fichier CSV.
		 */
		public void chargerCSV() {
			JFileChooser fc = new JFileChooser();
			Properties p = System.getProperties();
			File currentDir = currentDir = new File( p.getProperty("user.dir" ) );
			fc.setCurrentDirectory( currentDir );
			if (fc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION) {
				String CSVFile = new String( fc.getSelectedFile().toString() );
				currentDir  = fc.getCurrentDirectory();
				if (!CSVFile.endsWith(".csv")) {
					appli.windowWarning("Bad format for file, CSV file required");
				}
				else {
					checkCsvFile(CSVFile);
					this.dispose();
				}
			}
		}
		
		/**
		 * À partir du nom de fichier cette fonction à pour but de vérifier que le fichier CSV soit
		 * rédiger dans la bonne syntaxe. Si ce n'est pas le cas la fonction arrête le traitement.
		 * 
		 * la structure utilisé est celle de la fonction de sauvegarde d'une table de vérité.
		 * @param filename
		 */
		public void checkCsvFile(String filename) {
			try {
				int compteurLigne = 1; //Variable permettant de savoir à quel ligne du fichier est le curseur
				int ligneTableVerite = 0; //Variable qui sert à compté le nombre de ligne dans la table de vérité
				int outputChoice = 0;
				String[] enTete; //Tableau contenant le nom des entrées et sorties
				boolean[][] tableVerite; //tableau contenant les valeurs de la table de vérité.
				
				FileReader fr = new FileReader(filename);
				CSVReader csvReader = new CSVReader(fr);
				String[] nextRecord;
				
				nextRecord = csvReader.readNext();
				int nbrInputs = new Integer(nextRecord[1]);
				if(nbrInputs<2 || nbrInputs>8) {
					throw(new Exception("Outputs must be between 2 and 8, line "+ Integer.toString(compteurLigne)));
				}
				
				nextRecord = csvReader.readNext();
				compteurLigne++;
				int nbrOutputs = new Integer(nextRecord[1]);
				enTete = new String[nbrInputs+nbrOutputs];
				tableVerite = new boolean[1<<nbrInputs][nbrInputs+nbrOutputs];

				nextRecord = csvReader.readNext();
				nextRecord = csvReader.readNext();
				compteurLigne+=2;
				
				/**
				 * Récupération des noms des entrées et des sorties
				 */
				if(nextRecord.length != nbrInputs+nbrOutputs) {
					throw(new Exception("Error on the output and input names, line "+ Integer.toString(compteurLigne)));
				}
				for(int i=0 ; i<nbrInputs+nbrOutputs ; i++) {
					enTete[i] = nextRecord[i];
				}
				
				compteurLigne++;
				
				int nbMonome = 0; //Cette variable sert à compter le nombre de ligne qui ont leur sortie à 1 pour la première LED du tableau
				while((nextRecord = csvReader.readNext()) != null) {
					if(nextRecord.length == nbrInputs+nbrOutputs) {
						for(int i =0 ; i<nextRecord.length ; i++) {
							if(!nextRecord[i].equals("0") && !nextRecord[i].equals("1")) {
								throw(new Exception("Error on the output or input value line "+ Integer.toString(compteurLigne)));
							}
							tableVerite[ligneTableVerite][i] = (nextRecord[i].equals("1"))?true:false;
							
						}
						if(nextRecord[nbrInputs+outputChoice].equals("1")) {
							nbMonome++;
						}
					}
					else
					{
						throw(new Exception("Error on the line length, line " + compteurLigne));
					}
					compteurLigne++;
					ligneTableVerite++;
				}
				
				if(ligneTableVerite != 1<<nbrInputs) {
					throw( new Exception("Truth table is not complete"));
				}
				
				 csvReader.close();
				 fr.close();
				 
				 /**
				  * Dans le cas où il y a plusieurs sorties, une fenêtre est instancié pour savoir sur quel LED il faut
				  * faire le tableau de Karnaugh. Sinon le calcul est directement lancé avec la seule sortie disponible.
				  */
				 if(nbrOutputs>1) {
				 choiceWin = new SimLogCsvOutputChoiceWin(appli,this, tableVerite, enTete, nbrInputs, nbrOutputs);
				 choiceWin.show();
				 }
				 else {
					 toKarnaugh(tableVerite, nbrInputs, outputChoice, nbMonome, outputChoice);
				 }
				
			} catch (Exception e) {
				//e.printStackTrace();
				appli.windowWarning("Error on CSV file. " + e.getMessage());
			}
		}
		
		/**
		 * Fonction qui est appelé lorsque 
		 * 
		 * @param tableVerite Table de vérité correspondant au circuit
		 * @param nbInput Nombre d'entrées
		 * @param nbOutput Nombre de sorties
		 * @param nbMonome Nombre de ligne qui ont une sortie à 1 
		 * @param outputChoice
		 */
		public void toKarnaugh(boolean[][] tableVerite,int nbInput, int nbOutput, int nbMonome, int outputChoice) {
			try {
				/**
				 * La table Pla est une table regroupant les lignes de la table de vérité étant à 1 (d'où le nombre
				 * de ligne égale à NbMonome)
				 * La des tailles des colonnes est de nbInput*2 pour renseigner une variable et sa négation.
				 */
				 int lignePla = 0;
				 boolean [][] TablePla = new boolean [nbMonome][nbInput*2];
				 for(int i=0 ; i<(1<<nbInput) ; i++) {
					 if(tableVerite[i][nbInput+outputChoice]) {
						 for(int j=0 ; j<nbInput ;j++) {
							 if(tableVerite[i][j]) {
								 TablePla[lignePla][j*2] = true;
								 TablePla[lignePla][j*2 +1] = false;
							 }
							 else{
								 TablePla[lignePla][j*2] = false;
								 TablePla[lignePla][j*2 +1] = true;
							 }
						 }
						 lignePla++;
					 }
				 }
				 new SimLogListMask(TablePla,nbInput,nbMonome);
				 
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Lorsqu'un circuit est présent sur le logiciel il est possible de calculer la table de 
		 * Karnaugh à partir de ce dernier.
		 */
		public void circuitToKarnaugh() {
				SimLogGate g;
				Vector vIn;
				Vector vOut;
				int nbMonome = 0;
				
				vIn = new Vector();
				vOut = new Vector();
				for(int i=0 ; i<appli.getCircuit().getListOfGates().size() ; i ++) {
					g =(SimLogGate)appli.getCircuit().getListOfGates().elementAt(i);
					
					if(g.getGenericName().equals("SWITCH")) {
						vIn.add(g);
					}
					if(g.getGenericName().equals("LED")) {
						vOut.add(g);
					}
				}
				
				SimLogTruthTable table = new SimLogTruthTable(appli.getCircuit(), vIn, vOut);
					table.generateTable();
					String[] enTete = new String[vIn.size()+vOut.size()];
					for(int i = 0; i<table.getNbrInputs() ; i++) {
						enTete[i] = table.getInput(i).getName();
					}
					for(int i = 0; i<table.getNbrOutputs() ; i++) {
						enTete[i+table.getNbrInputs()] = table.getOutput(i).getName();
					}
					
					for(int i=0 ; i<(1<<vIn.size()) ; i++) {
						if(table.getDataBool()[i][vIn.size()])nbMonome++;
					}
					
					if(vOut.size() > 1 ) {
						choiceWin = new SimLogCsvOutputChoiceWin(appli,this, table.getDataBool(), enTete, vIn.size(), vOut.size());
						choiceWin.show();
					}
					else {
						toKarnaugh(table.getDataBool(), vIn.size(), vOut.size(), nbMonome, 0);
					}
					this.dispose();

		}
}
