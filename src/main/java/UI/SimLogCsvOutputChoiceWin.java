package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import com.opencsv.CSVReader;

public class SimLogCsvOutputChoiceWin extends JFrame implements ActionListener {
	private SimLogWin     parent;
	private SimLogPlaParamWin param;
	private JButton       bOk;
	private JTable        table;
	private int			  nbInput;
	private int			  nbOutput;
	private String		  fileName;
	private int			  outputChoice;
	private int[]		  nbMonome;
	private String[] 	  enTete;
	private JRadioButton[] listeChoix;
	private boolean[][]	   tableVerite;
	
	public SimLogCsvOutputChoiceWin(SimLogWin appli,SimLogPlaParamWin param, boolean[][] tableVerite, String[] enTete, int nbInput, int nbOutput) {
		setTitle("Choice of output for Karnaugh table");
		this.parent = appli;
		this.nbInput = nbInput;
		this.nbOutput = nbOutput;
		this.outputChoice = 1;
		this.param = param;
		this.nbMonome = new int[nbOutput];
		this.enTete = enTete;
		this.listeChoix = new JRadioButton[nbOutput];
		this.tableVerite = tableVerite;
		createUserInterface();
		pack();
	}
	
	JTable createTable() {
		int i,j;
		String cols[];
		Object data[][];
		
		try {
			
			cols=new String[nbOutput];
			for(i=0 ; i<nbOutput ; i++) {
				cols[i] = enTete[i+nbInput];
			}
			data= new Object[1<<nbInput][nbOutput];
			
			for(i=0 ; i<nbOutput ;i++) {
				nbMonome[i] = 0;
			}
			i = 0;
			for(i=0 ; i<(1<<nbInput) ; i++) {
				for(j=0 ; j<nbOutput ; j++) {
					data[i][j] = (tableVerite[i][j+nbInput])? "1" : "0";
					if(tableVerite[i][j+nbInput]) {
						nbMonome[j]++;
					}
				}
			}
			table=new JTable(data,cols);
			return table;
		}catch(Exception e) {
			parent.windowWarning(e.getMessage());
		}
		return null;
	}
	
	public void createUserInterface() {
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		bOk=new JButton("  OK  ");
		bOk.addActionListener(this);
		buttonPanel.add(bOk);
		ButtonGroup x = new ButtonGroup();
		

		JScrollPane scrollpane=new JScrollPane(createTable());
		scrollpane.setPreferredSize(new Dimension(200,100));
		scrollpane.getViewport().setBackground(Color.white);

		for(int i=0 ; i<nbOutput ; i++) {
			listeChoix[i] = new JRadioButton(enTete[i+nbInput], true);
			x.add(listeChoix[i]);
			buttonPanel.add(listeChoix[i]);
		}
		
		panel.add("Center",scrollpane);
		panel.add("South",buttonPanel);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center",panel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==bOk) {
			for(int i = 0 ; i<nbOutput ; i++) {
				if(listeChoix[i].isSelected()) {
					outputChoice = i;
				}
			}
			param.toKarnaugh(tableVerite, nbInput, nbOutput, nbMonome[outputChoice], outputChoice);
			dispose();
		}		
	}
	



}
