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
/* // | class   :  SimLogAfficheKarnaugh                               | // */
/* // | author  :                                                      | // */
/* // | date    :                                                      | // */
/* // | place   :                                                      | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  Classe Affichant un Tableau de Karnaugh 
 */


import javax.swing.*;
import java.awt.*;
import java.util.*;


public class SimLogAfficheKarnaugh extends JFrame
{
  // Champs
	
  private SimLogKarnaugh Karnaugh;
	
  private int xZone = 50;
  private int yZone = 40;	
	
  // Nombre de Cases 
  
  private int TailleX;
  private int TailleY;
	
  // matrice contenant les légendes	
  private boolean [][] LegendeH;
  private boolean [][] LegendeV;	
		
  private int NbVar;
  private int NbMonome;	 
	 	
  private boolean initialisation;
	
  // Champs Liés à la Liste
  
  // Vecteur Contenant la Liste des Masques
  
  private Vector VectMask;
  private int SizeVect;
  
  // Tableau des Couleurs
  
  private Color [] TabColors; 
	
  // Tableau de Booleens pour Savoir les Masques à Afficher
  
  private boolean [] CocheMask;
	
	
  // Constructeur
	
  public SimLogAfficheKarnaugh ( boolean[][]Table , int nv , int nm )	
    {
      Karnaugh = new SimLogKarnaugh(Table,nv,nm);
	
	//initialisation des legendes
	LegendeH = new SimLogTableCorres(Karnaugh.getvarH()).Generer();
	LegendeV = new SimLogTableCorres(Karnaugh.getvarV()).Generer();
	
      TailleX = ( (int) Math.pow(2,Karnaugh.getvarH()) + 1 );
      TailleY = ( (int) Math.pow(2,Karnaugh.getvarV()) + 3 );
      
      NbVar = nv;
      NbMonome = nm;
      initialisation = true;

      // On Initialise la Taille du Vecteur avec le Nombre de Masques
      
      SizeVect = Karnaugh.getNbMask();
      VectMask = new Vector(SizeVect);

      // On Initialise la Taille de CocheMask
      
      CocheMask = new boolean [SizeVect];

      // On Initialise la Table des Couleurs

      TabColors = new Color [SizeVect];
      //IniTabColors(SizeVect);
	FixeColors(SizeVect);

      this.setTitle("Karnaugh Interface");
      this.setSize(TailleX*xZone+3,TailleY*yZone+3);
      this.setBackground(Color.white);
      this.setLocation(150,30);
      this.setResizable(false);
      this.show();

    } // Fin Constructeur

  // Renvoie la Formule Simplifiée pour Chaque Bit
  // 1 -> La Variable
  // 0 -> Sa Négation
  // -1 -> Pas Pris en Compte
  
  public int [] CreerMonome ( int[][]TabCases , int NbLigCaseMask )
    {
      int [] resultat = new int [NbVar];

      // Initialisation de la Table Résultat avec la Première Ligne
      
      for ( int k = 0 ; k < NbVar ; k++ ) 
	    { resultat[k] = TabCases[0][k]; }

      // Parcours de Chaque Colonne
      
      for ( int i = 0 ; i < NbVar ; i++ )
	    {
	      for ( int j = 1 ; j < NbLigCaseMask ; j++ )
		    {
		      if ( TabCases[0][i] != TabCases[j][i] )
			    { resultat[i] = -1; }
	        }
        }
      
      return resultat;
    }


  // Méthode Paint

  public void paint ( Graphics g )
    {
      int [][] CaseMask;

      // Initialisation Graphique de la Table 

      // On Efface la Zone Graphique

      g.setColor(Color.white);
      g.fillRect(0,0,TailleX * xZone,TailleY * yZone);
      g.setColor(Color.black);

      // On Trace les Lignes Verticales 
      
      for ( int i = 1 ; i < TailleX ; i++ )
	    { g.drawLine(i * xZone,yZone,i * xZone,(TailleY - 1) * yZone); }
	
      // On Trace les Lignes Horizontales
      
      for ( int j = 2 ; j < TailleY ; j++ ) 
	    { g.drawLine(0,j * yZone,TailleX * xZone,j * yZone); }

      // On Rajoute les Lettres des Variables

      String lettre;
      
      int k;
      int vH = Karnaugh.getvarH(); 
      int vV = Karnaugh.getvarV(); 
      
      g.setColor(Color.blue);
      
      for ( k = 0 ; k < vH ; k++ )
        { 
         lettre = String.valueOf( (char) (65 + k) );
         g.drawString(lettre , (k * 10) + 8 , yZone + (yZone / 2) - 10); 
        }

      g.setColor(Color.red);

      for ( k = 0 ; k < vV ; k++ )
        {
          lettre = String.valueOf( (char) (65 + k + vH) );
          g.drawString(lettre , (k * 10) + 8 , (2 * yZone) - 10);
        }

      // On Affiche la Légende Horizontale

      g.setColor(Color.blue);

int ltab = (int) Math.pow(2,vH);

      // Pour Chaque Entier -> On Convertit en Tableau de Bits
      
      for ( int i = 0 ; i < ltab ; i++ )
	    {
	      int Xaux = ( (i+1) * xZone ) + 5;
	      
	      // Parcours du Tableau de Bits
	      
	      for ( int j = 0 ; j < vH ; j++ )
		    { int chiffre = 0;
		    	if (LegendeH[i][j]){chiffre=1;}	
		      lettre = String.valueOf(chiffre);
		      g.drawString(lettre, Xaux,yZone + (yZone / 2) - 10);
		      Xaux += 10;
		    } 
	    }	
	    
	    
	    
// On Affiche la Légende Verticale	    
	    
g.setColor(Color.red);

 ltab = (int) Math.pow(2,vV);

      // Pour Chaque Entier -> On Convertit en Tableau de Bits
      
      for ( int i = 0 ; i < ltab ; i++ )
	    {
	      int Xaux = 8;
	      int Yaux = ( (i + 2) * yZone ) + (yZone / 2);
	      // Parcours du Tableau de Bits
	      
	      for ( int j = 0 ; j < vV ; j++ )
		    { int chiffre = 0;
		    	if (LegendeV[i][j]){chiffre=1;}	
		      lettre = String.valueOf(chiffre);
		      g.drawString(lettre,Xaux,Yaux);
		      Xaux += 10;
		    } 
	    }
 
	
      // On Affiche les Différents Regroupements
      
      // Abscisse de la Fin de Formule

      int XForm = 8;
      
      g.setColor(Color.black);

      // Parcours de Chacun des Masques pour colorier les masques cochés
      
      for ( int i = 0 ; i < Karnaugh.getNbMask() ; i++ )
	    {
	    
	    
	      	
	      if ( CocheMask[i] )
	        {
	      		// On Initialise en Binaire les Cases du Masque
	      
	     		 int [] Mask = (Karnaugh.getLigStockMask(i));
	      
	     		 boolean [] resultat = Karnaugh.IndicesCasesVides(Mask,NbVar);
	      
		      int vide = Karnaugh.NbCasesVides(resultat,NbVar);
		      int NbLigCaseMask = (int) Math.pow(2,vide);
		      int [][] TabCases = Karnaugh.IniCaseMask(Mask,NbLigCaseMask,vide);
	    
	    	
	
	          for ( int h = 0 ; h < NbLigCaseMask ; h++ )
		        {
			
			int cases = Karnaugh.BitToInt(TabCases[h]);
		         int ligne = (Karnaugh.getTableCorres()).getNbLig(cases);
			int colonne = (Karnaugh.getTableCorres()).getNbCol(cases);
		 
		   	  	g.setColor(TabColors[i]);
	              		g.fillOval( (colonne + 1) * xZone + 2,(ligne + 2) * yZone + 2,xZone - 3,yZone - 3);
		       
		      			
			}//fin du parcours des cases du masque
		} // Fin Dessin des Cases du Masque
		
			
	   }//fin parcours des masques
		
		
	//parcours de tous les masques pour afficher les "1"  et l'initialisation de la liste si besoin	
	
	 for ( int i = 0 ; i < Karnaugh.getNbMask() ; i++ )
	    {
	      // On Initialise en Binaire les Cases du Masque
	      
	      int [] Mask = (Karnaugh.getLigStockMask(i));
	      
	      boolean [] resultat = Karnaugh.IndicesCasesVides(Mask,NbVar);
	      
	      int vide = Karnaugh.NbCasesVides(resultat,NbVar);
	      int NbLigCaseMask = (int) Math.pow(2,vide);
	      int [][] TabCases = Karnaugh.IniCaseMask(Mask,NbLigCaseMask,vide);
	    
	    	
	
	          for ( int h = 0 ; h < NbLigCaseMask ; h++ )
		        {
			
			int cases = Karnaugh.BitToInt(TabCases[h]);
		         int ligne = (Karnaugh.getTableCorres()).getNbLig(cases);
			int colonne = (Karnaugh.getTableCorres()).getNbCol(cases);
		 
		          
	      //on met des 1 dans les cases dont les masques n'ont pas été selectionnés
			
			g.setColor(Color.black);
			g.drawString("1",(colonne+1)*xZone+(xZone/2)-3,(ligne+2)*yZone+(yZone/2)+5);
			}//fin parcours des cases
	
	
	      // On Retrouve la Formule en Parcourant TabCases
	
	      if ( initialisation )
	        {
	          int [] MonoFormule = CreerMonome(TabCases,NbLigCaseMask);
	
	          // Parcours du Monome
	       
	          String Monome = new String();
	          String negation = String.valueOf( (char) 172);
	          
	          for ( int b = 0 ; b < NbVar ; b++ )
		        {
		          if ( MonoFormule[b] == 1 )
		            {
		              lettre = String.valueOf( (char) (65 + b) );
		              Monome = Monome.concat(lettre); 
		            }
		          else if ( MonoFormule[b] == 0 )	
			             {
			               lettre = String.valueOf( (char) (65 + b) );
			               Monome = Monome.concat(negation);
			               Monome = Monome.concat(lettre);
			             }
			    }
		
		      // Ajout du Monome au Vecteur
		  
		      VectMask.add(i,Monome);
	
	        } // Fin Initialisation
	
	    } // Fin Parcours des Masques
	
      initialisation = false;
	
    } // Fin Méthode Paint


//initialise aléatoirement les couleurs

  public void IniTabColors ( int SizeVect )
    {
      int R , G , B ;
  
      for ( int j = 0 ; j < SizeVect ; j++ )
        {
          R = (int) Math.floor( Math.random() * 180 ) + 60;
          G = (int) Math.floor( Math.random() * 180 ) + 60;
          B = (int) Math.floor( Math.random() * 180 ) + 60;
          TabColors[j] = new Color(R,G,B);
        }
    }

  
  
  //fixe les couleurs
  public void FixeColors (int SizeVect)
  {
   
  if (SizeVect>0) TabColors[0] = Color.blue;    
  if (SizeVect>1) TabColors[1] = Color.red;
  if (SizeVect>2) TabColors[2] = Color.green;
  if (SizeVect>3) TabColors[3] = Color.magenta;
  if (SizeVect>4) TabColors[4] = Color.orange;
  if (SizeVect>5) TabColors[5] = Color.cyan;
  
  if (SizeVect>6)
  	{
	int R , G , B ;
  
      for ( int j = 6 ; j < SizeVect ; j++ )
        {
          R = (int) Math.floor( Math.random() * 180 ) + 60;
          G = (int) Math.floor( Math.random() * 180 ) + 60;
          B = (int) Math.floor( Math.random() * 180 ) + 60;
          TabColors[j] = new Color(R,G,B);
        }
	
	
	}
  
  }
  
  // Accesseurs
  
  public Vector getVectMask ()
    { return VectMask; }

  public Color [] getTabColors ()
    { return TabColors; }

  public int getSizeVect ()
    { return SizeVect; }

  public void setCocheMask ( int i , boolean b )
    { CocheMask[i] = b; }

 public int getNbMonome()
 {return NbMonome;}	


}
