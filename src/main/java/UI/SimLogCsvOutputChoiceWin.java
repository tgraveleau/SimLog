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
	
	public SimLogCsvOutputChoiceWin(SimLogWin appli,SimLogPlaParamWin param, String filename, int nbInput, int nbOutput) {
		setTitle("Choice of output for Karnaugh table");
		this.parent = appli;
		this.fileName = filename;
		this.nbInput = nbInput;
		this.nbOutput = nbOutput;
		this.outputChoice = 1;
		this.param = param;
		this.nbMonome = new int[nbOutput];
		this.enTete = new String[nbOutput];
		this.listeChoix = new JRadioButton[nbOutput];
		createUserInterface();
		pack();
	}
	
	JTable createTable() {
		int i,j;
		String cols[];
		Object data[][];
		
		try {
			FileReader fr = new FileReader(fileName);
			CSVReader csvReader = new CSVReader(fr);
			String[] nextRecord = csvReader.readNext();
			
			for(i = 0; i<3 ;i++) {
				nextRecord = csvReader.readNext();
			}
			
			cols=new String[nbOutput];
			for(i=0 ; i<nbOutput ; i++) {
				cols[i] = nextRecord[i+nbInput];
				enTete[i] = nextRecord[i+nbInput];
			}
			
			data= new Object[1<<nbInput][nbOutput];
			
			for(i=0 ; i<nbOutput ;i++) {
				nbMonome[i] = 0;
			}
			i = 0;
			while((nextRecord = csvReader.readNext()) != null) {
				for(j=0 ; j<nbOutput ; j++) {
					data[i][j] = nextRecord[j+nbInput];
					if(nextRecord[j+nbInput].equals("1")) {
						nbMonome[j]++;
					}
				}
				i++;
			}
			csvReader.close();
			fr.close();
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
			listeChoix[i] = new JRadioButton(enTete[i], true);
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
			param.toKarnaugh(fileName, nbInput, nbOutput, nbMonome[outputChoice], outputChoice);
			dispose();
		}		
	}
	



}
