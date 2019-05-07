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
/* // | class   :  SimLogKarnaugh                                      | // */
/* // | author  :                                                      | // */
/* // | date    :                                                      | // */
/* // | place   :                                                      | // */
/* // ------------------------------------------------------------------ // */
/* //////////////////////////////////////////////////////////////////////// */

/**
 *  Classe Gérant un Tableau de Karnaugh 
 */


public class SimLogKarnaugh 
{	
  // Champs
	
  // Nombre de Variables et Nombre de Monomes
  
  private int NbVar;
  private int NbMonome;
	
  // Nombre de Variables Horizontales et Verticales dans la Table de Karnaugh
  
  private int varH;
  private int varV;
		
  // Tableau de Booléen décrivant l'expression logique
  
  private boolean [][] Matrice;
	
  // Table de Karnaugh
  
  private boolean [][] TableK;
	
  // Table des Correspondances entre la Table de Booléens et la Table de Karnaugh	
  
  private SimLogTableCorres TableCorres;

  // Résultat du Regroupement
	
  private int [][] StockMask;
  private int NbMask;


  // Constructeur

  public SimLogKarnaugh ( boolean[][]Table , int nv , int nm )
    {
	  NbVar = nv;
	  NbMonome = nm;
	
	  // Initialisation des Dimensions de la Table de Karnaugh
	  
	  varV = NbVar / 2;
	  varH = NbVar - varV;
	
	  // Initialisation de TablePla
	  
	  Matrice = new boolean [NbMonome][NbVar*2];	
	 
	  for ( int i = 0 ; i < (NbVar * 2) ; i++ )
	    {
	      for ( int j = 0 ; j < NbMonome ; j++ )
	        { Matrice[j][i] = Table[j][i]; }
		}	
	
	  // Initialisation de la TableCorres
	  // Pour Chaque Case de la Table de Karnaugh
	  // On Stocke la Colonne puis la Ligne Correspondante
	 
	  TableCorres = new SimLogTableCorres(NbVar);
	
	  // Création de la Table de Karnaugh
	
	  TableK = new boolean [ (int) Math.pow(2,varV) ][ (int) Math.pow(2,varH) ];
	
	  // Ajout des Monomes dans la Table de Karnaugh
	  
	  InitKarnaugh();
	
	  // Regroupement
	
	  NbMask = 0;
	  StockMask = regroupement(StockMask,NbMask);	
    
 
    } // Fin Constructeur	


/***********************************************************************************/
/*********************** INITIALISATION DE KARNAUGH ********************************/
/***********************************************************************************/


  public void InitKarnaugh () 
    {      
      for ( int i = 0 ; i < NbMonome ; i++ )
	    {
	      int [] bit = new int [NbVar];		
	      int vide , nombre , k = 0;
	      int [][] TableCase;
	      int [][] Verite;
	      boolean [] resultat;
	  
	      reset_bit(bit,NbVar);
	  
	      for ( int j = 0 ; j < NbVar * 2 ; j = j + 2 )
	        {	  
	          // On Détermine la Valeur des Bits
		  
		      if ( Matrice[i][j] && !Matrice[i][j+1] )
		        { 
		          bit[k] = 1;
		          k++;
		        }
    	
    	      if ( !Matrice[i][j] && Matrice[i][j+1] )
		        { 
		          bit[k] = 0;
		          k++;
		        }
    		   
              if ( !Matrice[i][j] && !Matrice[i][j+1] )
		        { k++; }	
	          
	          if (Matrice[i][j] && Matrice[i][j+1])
	  	        {
	  	          // Si on a une Variable et sa Négation 
	  	          // On met la Ligne à Vide pour ne pas la Prendre en Compte
		          
		          j = NbVar * 2;
		          reset_bit(bit,NbVar);
		        }		    
            }       	
	 
	      // On Traite les Cases Vides Initialisées à -1
	      
	      resultat = IndicesCasesVides(bit,NbVar);
	      vide = NbCasesVides(resultat,NbVar);
	  
	      // Si la Ligne est Vide -> On ne la Prend pas en Compte
	
	      if ( vide == NbVar )
	  	    {
		      //JOptionPane.showMessageDialog(null,"Line "+i+" incorrect : not used","Information : Some Lines Are Erroned",1);
		      System.out.println( "line " + i + " incorrect or empty : not used" );
		    }
	      else
	        {
	  	      nombre = (int) Math.pow(2,vide);
	          TableCase = new int [nombre][NbVar];
	  
	 	      // On Initialise la Matrice
	  	      
	  	      for ( int l = 0 ; l < NbVar ; l++ )
	    		{	
	      		  for ( int m = 0 ; m < nombre ; m++ )
	        		{
	         		 TableCase[m][l] = bit[l];
	        		}
	    		}
	  
	  	      // Calcule la Table de Vérité des Cases à -1
	 	  
	 	      Verite = TableVerite(vide);
	  
	          // On Met à Jour la Matrice
	 	  
	 	      if ( vide > 0 )
	            {
	      	      int t = 0;
	          
	              for ( int r = 0 ; r < NbVar ; r++ )
	                {
	          	      if ( TableCase[0][r] == -1 )
	                    { 
	             	      CopieColMat(Verite,TableCase,t,r,nombre);
	             	      t++;
	            	    }
	        	    }
        	    }
	 
	 	      // On Modifie la Table de Karnaugh
	 	  
	 	      for ( int y = 0 ; y < nombre ; y++ )
	 		    {
			      int indice = BitToInt(TableCase[y]);
		          int l = TableCorres.getNbLig(indice);
		 	      int c = TableCorres.getNbCol(indice);
		 	      TableK[l][c] = true;	
		        }
	 
	        }
        } 
    }  


  // Initialise le Tableau de Bits à -1
  
  public void reset_bit ( int[]bit , int l )
    {
      for ( int k = 0 ; k < l ; k++ )
	    { bit[k] = -1; } 	
    }	

  // Pour Convertir un Tableau de Bits en Entier 	
  
  public int BitToInt ( int[]bit )
    {
      int tmp = 0;
  
      for ( int k = 0 ; k < NbVar ; k++ )
	    { tmp = tmp + ( bit[k] * ( (int) Math.pow(2,(NbVar-k-1)) ) ); } 	
  
      return tmp;
    }	

  // Pour Créer le Tableau de Bits Correspondant à l'Entier x
  // l : Taille du Tableau de Bits 

  public void IntToBit ( int[]bit , int x , int l )
    {
      int k , div , mod;
  
      if ( l > 0 )
        {
          reset_bit(bit,l);
          k = l - 1;
          div = x / 2;
          mod = x % 2;
  
          while ( div != 0 )
            {
              bit[k] = mod;
              mod = div % 2;
              div = div / 2;
              k--;
            }
  
          bit[k] = mod;
  
          if ( k != 0 )
            {
              for ( int i = 0 ; i < k ; i++ ) 	
                { bit[i] = 0; }	
            }
        }	
    }

  // Calcul la Table de Vérité pour NbVar
  
  public int [][] TableVerite ( int Var )
    { 
      int [] bit = new int [NbVar];
      int l = (int) Math.pow(2,Var);
      int [][] Verite = new int [l][Var];
  
      for ( int i = 0 ; i < l ; i++ )
	    {
          IntToBit(bit,i,Var);  
          
          for ( int j = 0 ; j < Var ; j++ )
	        { Verite[i][j] = bit[j]; }
        } 	
      
      return Verite;
    }

  // Affichage d'un Tableau de Bits
  
  public void AfficheBit ( int[]bit , int l )
    {
      for ( int i = 0 ; i < l ; i++ )
	    { System.out.print(bit[i]); } 	
      System.out.println( " ");
    }	

  // Renvoie un Tableau Contenant les Indices des Bits à -1
  
  public boolean [] IndicesCasesVides ( int[]bit , int l )
    {
      boolean [] Indices = new boolean [l];
  
      for ( int k = 0 ; k < l ; k++ )
	    {
          if ( bit[k] == -1 )
            { Indices[k] = true; } 	
          else
            { Indices[k] = false; }  
        } 	
  
      return Indices;
    }	

  // Renvoie le Nombre de Cases à -1 dans T
  
  public int NbCasesVides ( boolean[]T , int l )
    {
      int nombre = 0;
  
      for ( int k = 0 ; k < l ; k++ )
	    {
          if ( T[k] == true )
            { nombre++; } 	
        } 	
  
      return nombre;
    }	

  // Remplit la Matrice dst à partir de la Matrice src
  // c1 : La Colonne à Copier
  // c2 : La Colonne à Modifier
  
  public void CopieColMat ( int[][]src , int[][]dst , int c1 , int c2 , int NbLignes )
    {
      for ( int i = 0 ; i < NbLignes ; i++ )
	    { dst[i][c2] = src[i][c1]; }
    }

  // Copie 
  
  public void CopieMask ( int[]src , int[][]dst , int l )
    {
      for ( int i = 0 ; i < NbVar ; i++ )
	    { dst[l][i] = src[i]; }
    }


/**********************************************************************************/
/******************************** REGROUPEMENT ************************************/
/**********************************************************************************/


  // On Teste si le Masque M est un "Sous Masque" d'un Masque Accepté et Stocké dans StockMask

  public boolean EstSousMaskLig ( int[]M , int[]StockMask , int NbMask ) 
    {
      // Pour le Masque de la Table StockMask
	  
	  for ( int j = 0 ; j < NbVar ; j++ )
		{
		  if ( StockMask[j] != -1 )
		    {
			  if ( StockMask[j] != M[j] )
			    { return false; }			
			}
		}	

      return true;
    }

  public boolean EstSousMask ( int[]M , int[][]StockMask , int NbMask )
    {
      if ( NbMask > 0 )
        {
          for ( int i = 0 ; i < NbMask ; i++ )
	        {
	          if ( EstSousMaskLig( M , StockMask[i] , NbMask ) )
		        { return true; }
	        }

          return false;
        }
      else
        { return false; }
    }


//remplacela ligne de l'intersection par la derniere ligne 
 public int RempMask(int [][]StockMask,int NbMask,int inter)
{

for (int i=0;i<NbVar;i++)
	{StockMask[inter][i] = StockMask[NbMask-1][i];}
NbMask--;
return NbMask;
}


//test pour les intersections
public boolean EstIntersection ( int intersec,int []InterMask , int[][]StockMask , int NbMask )
    {
      if ( NbMask > 0 )
        {
	int i=0;
          while (i < NbMask)
	        {
		  if (i != intersec)	
			{
	          	if ( EstSousMaskLig( InterMask , StockMask[i] , NbMask ) )
		        	{ return true; }
			}
		   i++;		
	        }

          return false;
        }
      else
        { return false; }
    }


//supprime les intersections de la table StockMask

public int SuppInterMask(int [][]StockMask, int NbMask)
{
  for (int i=0;i<NbMask;i++)
      {	
	int vide = NbCasesVides( IndicesCasesVides( StockMask[i] , NbVar ) , NbVar );
      
      // Nombre de Lignes dans CaseMask
      
      int NbLigCaseMask = (int) Math.pow(2,vide);
      int [][] CaseMask;
      CaseMask = IniCaseMask( StockMask[i] , NbLigCaseMask , vide );
      
    
      
 //on teste si les cases du masque ne sont pas toutes incluses dans les autres masques de StockMask
	boolean IntersectionRecouverte = true;
	int d = 0;
	while (IntersectionRecouverte && (d < NbLigCaseMask))
		{
		if (!EstIntersection(i,CaseMask[d],StockMask, NbMask)) {IntersectionRecouverte = false;}
		d++;
		} 
	     
	     //si le masque est une intersection on le supprime


	     if (IntersectionRecouverte)
	     	{
		//on supprime le masque qui est une intersection
		NbMask = RempMask(StockMask,NbMask,i);
		//on teste la ligne échangée
		i--; 
		
		}
	     
	        	

	}//fin parcours des masques
return NbMask;
}
  // On Initialise la Matrice qui Contient Toutes les Cases du Masque (CaseMask)

  public int [][] IniCaseMask ( int[]Mask , int NbLigCaseMask , int vide )
    {
      int [][] CaseMask = new int [NbLigCaseMask][NbVar];

      // On Initialise CaseMask
	  
	  for ( int l = 0 ; l < NbVar ; l++ )
	    {	
	      for ( int m = 0 ; m < NbLigCaseMask ; m++ )
	        { CaseMask[m][l] = Mask[l]; }
	    }
	  
	  // Calcule la Table de Vérité des Cases à -1
	  
	  int [][] Verite = TableVerite(vide);
	  
	  // On Met à Jour la Matrice
	  
	  if ( vide > 0 )
	    {
	      int t = 0;
	      
	      for ( int r = 0 ; r < NbVar ; r++ )
	        {
	          if ( CaseMask[0][r] == -1 )
	            { 
	              CopieColMat(Verite,CaseMask,t,r,NbLigCaseMask);
	              t++;
	            }
	        }
        }
 
      return CaseMask;
    }

  // Fonction qui Teste si un Masque peut être Appliqué à Partir de sa Table CaseMask
  
  public boolean ValidMask ( int[][]CaseMask , int NbLigCaseMask )
    {
      int i = 0;
      boolean valid = true;
      
      while ( valid && ( i < NbLigCaseMask ) )
	    {
	      // On Convertit le Nombre Binaire en Entier
	      
	      int NumCase = BitToInt(CaseMask[i]);
	      valid = TableK[ TableCorres.getNbLig(NumCase) ][ TableCorres.getNbCol(NumCase) ];
	      i++;
	    }

      return valid;
    }

  // Renvoie le Nombre de 1 dans le Tableau
  
  public int NbUn ( int[]Tableau )
    {
      int nb = 0;
      
      for ( int i = 0 ; i < NbVar ; i++ )
	    {
	      if ( Tableau[i] == 1 )
		    { nb++; }
	    }

      return nb;
    }

  
  // Renvoie le Tableau des Masques Acceptés ainsi que leur Nombre

  public int [][] regroupement ( int[][]StockMask , int NbMask )
    {
      StockMask = new int [ (int) Math.pow(2,NbVar) ][NbVar];
      int [][] Mask = new int [1][NbVar];
      
      // On Initialise la Table Mask
      
      for ( int i = 0 ; i < NbVar ; i++ )
	    { Mask[0][i] = -1; }

      // Nombre de Masques Acceptés
      
      NbMask = 0;

      // Définition des Variables pour la Table qui Contient les Cases d'un Masque

      // Nombre de Cases à -1
      
      int vide = NbCasesVides( IndicesCasesVides( Mask[0] , NbVar ) , NbVar );
      
      // Nombre de Lignes dans CaseMask
      
      int NbLigCaseMask = (int) Math.pow(2,vide);
      int [][] CaseMask;
      CaseMask = IniCaseMask( Mask[0] , NbLigCaseMask , vide );

	  // Cas du Plus Grand Masque (Que des -1)	
      
      if ( ValidMask( CaseMask , NbLigCaseMask ) )
        {
          // On a que des 1 dans la Table de Karnaugh
          
          CopieMask( Mask[0] , StockMask , NbMask );
          NbMask++;
        }
      else
        {
          // On Génère Tous les Autres Masques
          // On Génère la Table de Vérité qui Donne les Positions des Colonnes non Vide
          
          int [][] Verite = TableVerite(NbVar);

          for ( int i = 1 ; i < NbVar + 1 ; i++ )
	        { 
	          // Pour Chaque Bloc de Masques Ayant le même Nombre de -1
	          
	          int NbLigMask = (int) Math.pow(2,i);
	 
		      // On Génère une Petite Table de Vérité Provisoire
		      
		      int [][] VeriteTemp = TableVerite(i);
		
		      // Parcours de la Table de Vérité
		      
		      int NbLigVerite = (int) Math.pow(2,NbVar);
		      
		      for ( int l = 0 ; l < NbLigVerite ; l++ )
			    {
			      // On Regarde si la Ligne Contient i fois "1"
			      
			      if ( NbUn( Verite[l] ) == i )
				    { 
				      int ColVeriteTemp = 0; 
				      Mask = new int [NbLigMask][NbVar];
		
				      // Initialisation de Mask
				      
				      for ( int k = 0 ; k < NbLigMask ; k++ )
				        { reset_bit(Mask[k],NbVar); }
		
				      for ( int c = 0 ; c < NbVar ; c++ )
					    { if ( Verite[l][c] == 1 )
					        {
				              CopieColMat( VeriteTemp , Mask , ColVeriteTemp , c , NbLigMask );
					          ColVeriteTemp++;				 
					        }
					    } // Fin Parcours de la Ligne dans Vérité
				
				      // Parcours de Mask
	    
	                  for ( int w = 0 ; w < NbLigMask ; w++ )
	                    {
		             
				  // On Teste si le Masque de la Ligne w est un Sous Masque
		
		                  if ( !EstSousMask( Mask[w] , StockMask , NbMask ) )
		                    {
		                      // On Teste si le Masque est Acceptable
		                      // Nombre de Cases à -1
		                  
		                      vide = NbCasesVides( IndicesCasesVides( Mask[w] , NbVar ) , NbVar );
		                  
		                      // Nombre de Lignes dans CaseMask
		                  
		                      NbLigCaseMask = (int) Math.pow(2,vide);
		                      CaseMask = IniCaseMask(Mask[w],NbLigCaseMask,vide);
		            	  	
											
					//si le masque est valide
		            	      if (ValidMask(CaseMask,NbLigCaseMask))
			                    {				                
				                  CopieMask( Mask[w] , StockMask , NbMask );
				                  NbMask++;
				                
						}// Fin le Masque est Valide
						
						
		                    } // Fin le Masque n'est pas un Sous Masque
				    
							    
				    
				    
		                } // Fin du Test du Petit Bloc
				    } // Fin on Trouve un 1
				} // Fin Parcours des Lignes de Vérité
	        } // Fin Grand Bloc 
        }	 


	//une fois tous les masques trouvés, il faut enlever les masques qui sont des intersections
	
	NbMask = SuppInterMask(StockMask,NbMask);
      
      setNbMask(NbMask);	
      return StockMask;
    } // Fin Regroupement


  public int getvarH ()
    { return varH; }

  public int getvarV ()
    { return varV; }

  public int getNbMask ()
    { return NbMask; }

  public int [] getLigStockMask ( int l )
    { return StockMask[l]; }

  public void setNbMask ( int nb )
    { NbMask = nb; }

  public SimLogTableCorres getTableCorres ()
    { return TableCorres; }

//affichage table de karnaugh

public void afficheKarnaugh()
{
for (int l=0; l<Math.pow(2,varV);l++)

	{
	for (int c=0; c<Math.pow(2,varH) ;c++)
		{
		System.out.print(" "+TableK[l][c]);
		}
	System.out.println();
	}


}





}
