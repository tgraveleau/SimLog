package src;

public class SimLogTableCorres
{

private int [][]TableCorres;
private int ligne;
private int colonne;
private boolean[][] TableBin;

public SimLogTableCorres (int NBV)
{
ligne = (int)Math.pow(2,NBV);
colonne = NBV;
TableBin = Generer();

int index = 0;
int varv = (colonne/2);
int varh = colonne - varv;



TableCorres = new int [ligne][2];

int indcol = (int)Math.pow(2,varh)/2;
int indexcol = 0;

for (int i=0;i<indcol;i++)
	{
	for (int j=0;j<(int)Math.pow(2,varv);j++)
		{
		int indlig = BitToInt(TableBin[index]);
		TableCorres[indlig][0] = indexcol;
		TableCorres[indlig][1] = j;
		index++;
		}//fin pour j
	indexcol++;
	for (int j=(int)Math.pow(2,varv)-1;j>=0;j--)
		{
		int indlig = BitToInt(TableBin[index]);

		TableCorres[indlig][0] = indexcol;
		TableCorres[indlig][1] = j;
		index++;
		}//fin pour j
	indexcol++;
	}//fin for i

}//fin constructeur


 public void AffCorres ()
   {
	 System.out.println("num :		col		lig");

     for ( int i = 0 ; i < ( (int) Math.pow(2,colonne) ) ; i++ )
	   { System.out.println(""+i+": 	  	    "+TableCorres[i][0]+"  		   "+TableCorres[i][1]); }
   }



//renvoie l'entier correspondant au tableau binaire
public int BitToInt(boolean [] bit)
{
 int tmp = 0;

      for ( int k = 0 ; k < colonne ; k++ )
	    {
	    int aux=0;
	    if (bit[k]){aux=1;}
	    tmp = tmp + ( aux * ( (int) Math.pow(2,(colonne-k-1)) ) );

	    }

      return tmp;


}


//renvoie le nombre de 1
public int NbTrue (boolean [] tab)
{
int compteur=0;

for (int i=0;i<colonne;i++)
{
if (tab[i]) compteur++;
}

return compteur;
}

//renvoie vrai si le nombre de 1 est pair
public boolean NbTruePair(boolean [] tab)
{
return (NbTrue(tab)%2==0);
}


//recopie un tableau tab1 dans tab2
public void CopieTab(boolean [] tab1,boolean []tab2)
{
for (int i=0;i<colonne;i++)
	tab2[i] = tab1[i];


}



//creation de la matrice avec le code gray

public boolean[][] Generer()
{
boolean [][] Mat = new boolean [ligne][colonne];

for (int i=1;i<ligne;i++)
	{
	//on recopie la ligne pr�c�dente dans la ligne courante
	CopieTab(Mat[i-1],Mat[i]);

	if (NbTruePair(Mat[i-1]))
		//on change le dernier bit
		{
		Mat[i][colonne-1] = !Mat[i-1][colonne-1];}
		//on change le bit a gauche du bit � 1 le plus � droite
		else
		{
		int j=colonne-1;
		while (!Mat[i-1][j])
		 	j--;

		Mat[i][j-1] = !Mat[i-1][j-1];

		}



	}//fin pour
return Mat;
}


//affichage
public void aff()
{
for (int i=0;i<ligne;i++)
	{
	for (int j=0;j<colonne;j++)
		{
		if (TableBin[i][j])
			{System.out.print("1 ");}
			else
			{System.out.print("0 ");}
		}
	System.out.println("");

	}

}


// Accesseurs

  // Renvoie le Num�ro de Colonne � Partir du Num�ro de Case

  public int getNbCol ( int NCase )
    {
      return TableCorres[NCase][0];
    }

  // Renvoie le Num�ro de Ligne � Partir du Num�ro de Case

  public int getNbLig ( int NCase )
    {
      return TableCorres[NCase][1];
    }

  // Renvoie l'Indice de TableCorres � Partir des Coordonn�es
  // de Case de la Table de Karnaugh

  public int getIndice ( int col , int lig )
    {
      // Nombre de Cases Dans TableCorres

      int NbCase = (int) Math.pow( 2 , colonne );

      // Nombre de Lignes de la Table de Karnaugh

      int NbLig = (int) Math.pow( 2 , ( colonne / 2 ) );

      // Nombre de Colonnes de la Table de Karnaugh

      int NbCol = (int) Math.pow( 2 , colonne - ( colonne / 2 ) );

	  // Teste que les Num�ros de Colonne et Ligne sont Corrects

      if ( ( col < NbCol ) && ( lig < NbLig ) )
	    {
		  for ( int i = 0 ; i < NbCase ; i++ )
			{
			  if ( (TableCorres[i][0] == col) && (TableCorres[i][1] == lig) )
				{ return i; }
			}

	    } // Fin Test des Num�ros de Colonne et Ligne

      return -1;

    }




}

