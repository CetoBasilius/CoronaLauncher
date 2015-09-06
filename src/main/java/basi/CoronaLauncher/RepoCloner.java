package basi.CoronaLauncher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class RepoCloner {

	private JFrame formClone;
	private JTextField remoteField;
	private JTextArea logText;
	private JComboBox<String> branchComboBox;
	private JButton cloneButton;
	private String selectedBranch;
	private String projectFolder;

	/**
	 * Create the application.
	 */
	public RepoCloner(String projectFolder) {
		this.projectFolder = projectFolder;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		formClone = new JFrame();
		formClone.setTitle("Clone repository");
		formClone.setBounds(100, 100, 600, 400);
		formClone.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		formClone.toFront();
		
		JPanel remotePanel = new JPanel();
		formClone.getContentPane().add(remotePanel, BorderLayout.NORTH);
		remotePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Remote");
		remotePanel.add(lblNewLabel);
		
		remoteField = new JTextField();
		remotePanel.add(remoteField);
		remoteField.setColumns(28);
		
		JButton fetchButton = new JButton("Fetch");
		fetchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getBranches();
			}
		});
		remotePanel.add(fetchButton);
		
		JPanel clonePanel = new JPanel();
		formClone.getContentPane().add(clonePanel, BorderLayout.CENTER);
		clonePanel.setLayout(new BorderLayout(0, 0));

		branchComboBox = new JComboBox<String>();
		branchComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					selectedBranch = item.toString();
				}
			}
		});
		clonePanel.add(branchComboBox, BorderLayout.NORTH);
		
		cloneButton = new JButton("Clone selected branch from repository");
		clonePanel.add(cloneButton, BorderLayout.SOUTH);
		cloneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cloneRepository();
			}
		});
		cloneButton.setEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		clonePanel.add(scrollPane);
		
		JPanel panelLog = new JPanel();
		scrollPane.setViewportView(panelLog);
		panelLog.setLayout(new BorderLayout(0, 0));
		
		logText = new JTextArea();
		logText.setEditable(false);
		logText.setAutoscrolls(true);
		logText.setWrapStyleWord(true);
		panelLog.add(logText);
	}
	
	private void cloneRepository() {
		String folderName = "";
		if (!this.selectedBranch.equals("master")) {
			File theFile = new File(remoteField.getText());
			String projectName = theFile.getName();
			folderName = projectName.split("\\.(?=[^\\.]+$)")[0] + this.selectedBranch;
		}
		String command = "git clone -b " + this.selectedBranch + " " + remoteField.getText() + " " + folderName;
		
		File projectFileFolder = new File(projectFolder);
		try {            
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command, null, projectFileFolder);
			
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), proc, null, null, logText);            
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), proc, null, new Callback() {
				public void onComplete() {
					logText.setText(logText.getText() + "\nProcess complete.");
				}
			}, logText);
			
			errorGobbler.start();
			outputGobbler.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getBranches() {
		String remoteText = remoteField.getText();
		String command = "git ls-remote -h " + remoteText;
		
		try {            
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command);
			proc.waitFor();
			
			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			List<String> outputList = new ArrayList<String>();
			
			StringBuffer completeLog = new StringBuffer();
			while( true ) {
			    String line = br.readLine();
			    if (line == null) {
			    	break;
			    }
			    outputList.add(line);
			    completeLog.append(line);
			    completeLog.append("\n");
			}
			logText.setText(completeLog.toString());
			
			List<String> branchList = new ArrayList<String>();
			int masterIndex = -1;
			for (int index = 0; index < outputList.size(); index++) {
				String branchString = outputList.get(index);
				String[] parts = branchString.split("/");
				String branchName = parts[parts.length - 1];
				branchList.add(branchName);
				if (branchName.equals("master") == true) {
					masterIndex = index;
				}
			}
			branchComboBox.setModel(new DefaultComboBoxModel(branchList.toArray()));
			branchComboBox.setSelectedIndex(masterIndex);
			selectedBranch = branchList.get(masterIndex);
			cloneButton.setEnabled(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void setVisible(boolean value) {
		this.formClone.setVisible(value);
	}
}
