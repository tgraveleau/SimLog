package src;

import java.util.Vector;


public class SimLogTruthTable {

	int nbrInputs;
	int nbrOutputs;
	SimLogGate tabInputs[];
	SimLogGate tabOutputs[];

	int nbrRows;
	int nbrCols;
	int tabData[][]=null;

	SimLogCircuit circuit;

	/**
	 *  Constructor
	 *
	 *	@param c SimLogCircuit
	 *  @param vIn Vector of input gates (switches)
	 *  @param vOut Vector of output gates (LEDs)
	 */

	public SimLogTruthTable(SimLogCircuit c, Vector vIn, Vector vOut) {
		int i;

		circuit=c;
		nbrInputs=vIn.size();
		tabInputs=new SimLogGate[nbrInputs];
		for (i=0;i<nbrInputs;i++) tabInputs[i]=(SimLogGate)vIn.elementAt(i);
		nbrOutputs=vOut.size();
		tabOutputs=new SimLogGate[nbrOutputs];
		for (i=0;i<nbrOutputs;i++) tabOutputs[i]=(SimLogGate)vOut.elementAt(i);
	}

	public void generateTable() {
		int i,j,val,digit[];


		nbrCols=nbrInputs+nbrOutputs;
		nbrRows=1 << nbrInputs;
System.out.println(nbrInputs+" "+nbrOutputs+" "+nbrRows+" "+nbrCols);
		tabData=new int[nbrRows][nbrCols];
		digit=new int[nbrInputs];
		for (i=0;i<nbrRows;i++) {
			decToBin(i,nbrInputs,digit);
			for (j=0;j<nbrInputs;j++) {
				val=digit[nbrInputs-1-j];
				tabData[i][j]=val;
				if (val==1)
					tabInputs[j].setValue(SimLogGate.TRUE);
				else
					tabInputs[j].setValue(SimLogGate.FALSE);
			}
			simulation();
			for (j=0;j<nbrOutputs;j++) {
				if (tabOutputs[j].getValue()==SimLogGate.TRUE)
					val=1;
				else
					val=0;
				tabData[i][nbrInputs+j]=val;
			}
		}
	}

		/**
		 *  Convert a decimal number into a binary representation
		 *
		 *  @param n decimal number to convert
		 *  @param max length of table containing the binary representation
		 *  @param tab array containing the binary digits
		 */

		void decToBin( int n, int max, int tab[] ) {
			int i, r;

			i=0;
			while (n>1) {
				tab[i++]=n%2;
				n=n>>1;
			}
			if (n!=0) tab[i++]=n;
			while (i<max) tab[i++]=0;
		}

		/**
		 *  enter simulation mode during which you can only change the
		 *  switches
		 */

		public void simulation( ) {
				int i;
				SimLogGate g;
				Vector listOfGates;

				listOfGates=circuit.getListOfGates();
				for (i=0;i<listOfGates.size();i++) {
					g = (SimLogGate) listOfGates.elementAt(i);
					if (g.getType()!=SimLogGate.SWITCH_GATE)
						g.setValue( SimLogGate.UNSET );
				}
				for (i=0;i<listOfGates.size();i++) {
					g = (SimLogGate) listOfGates.elementAt(i);
					if (g.getType()==SimLogGate.LED_GATE) {
						g.compute();
					}
				}
		}

	public int getNbrInputs() {
		return nbrInputs;
	}

	public int getNbrOutputs() {
		return nbrOutputs;
	}

	public SimLogGate getInput(int n) {
		return tabInputs[n];
	}

	public SimLogGate getOutput(int n) {
		return tabOutputs[n];
	}

	public int [][] getData() {
		return tabData;
	}

	public void print() {
		int i,j;

		if (tabData==null) return ;
		for (i=0;i<nbrRows;i++) {
			for (j=0;j<nbrCols;j++) {
				System.out.print("|"+tabData[i][j]+"|");
			}
			System.out.println(" ");
		}
	}

	public int [][] getMonomials(int n) {
		int i,j,nbL,nbV,nbM=0;
		int data[][];

		nbV=getNbrInputs();
		nbL=1 << getNbrInputs();
		for (i=0;i<nbL;i++) {
			if (tabData[i][nbV+n]==1) ++ nbM;
		}
		data=new int[nbM][nbrInputs*2];
		for (i=0;i<nbM;i++) {
			for (j=0;j<nbrInputs*2;j++) {
				data[i][j]=0;
			}
		}
		nbM=0;
		for (i=0;i<nbL;i++) {
			if (tabData[i][nbV+n]==1) {
				for (j=0;j<nbV;j++) {
					if (tabData[i][j]==0) {
						data[nbM][j*2+1]=1;
					} else {
						data[nbM][j*2]=1;
					}
				}
			}
		}
		return data;
	}
}

