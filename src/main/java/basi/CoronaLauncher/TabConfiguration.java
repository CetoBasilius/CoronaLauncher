package basi.CoronaLauncher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TabConfiguration extends JPanel{

	private static final long serialVersionUID = 1L;
	private JTextField pathField;
	private JTextField coronaPathField;
	private JLabel labelStatus;
	protected RepoCloner checkoutWindow;
	private JTextField provisioningPathfield;
	

	public TabConfiguration(String name) {
		this.setName(name);
		this.setLayout(new BorderLayout(0, 0));

		JPanel panelConfiguration0 = new JPanel();
		this.add(panelConfiguration0, BorderLayout.CENTER);
		panelConfiguration0.setLayout(new BoxLayout(panelConfiguration0, BoxLayout.Y_AXIS));

		JPanel panelProjectPath = new JPanel();
		panelConfiguration0.add(panelProjectPath);

		JLabel labelPath = new JLabel("Project path");
		panelProjectPath.add(labelPath);

		pathField = new JTextField();
		pathField.setEditable(false);
		panelProjectPath.add(pathField);
		pathField.setColumns(22);

		JButton buttonPath = new JButton("Browse");
		buttonPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.showOpenDialog(null);
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile != null && selectedFile.exists() == true) {
					pathField.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		panelProjectPath.add(buttonPath);
		
		JPanel panelCoronaPath = new JPanel();
		panelConfiguration0.add(panelCoronaPath);
		panelCoronaPath.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel labelPathCorona = new JLabel("Corona SDK");
		panelCoronaPath.add(labelPathCorona);
		
		coronaPathField = new JTextField();
		coronaPathField.setEditable(false);
		panelCoronaPath.add(coronaPathField);
		coronaPathField.setColumns(22);
		
		JButton buttonCoronaPath = new JButton("Browse");
		buttonCoronaPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.showOpenDialog(null);
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile != null && selectedFile.exists() == true) {
					coronaPathField.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		panelCoronaPath.add(buttonCoronaPath);
		
		JPanel panelProvisioningPath = new JPanel();
		panelConfiguration0.add(panelProvisioningPath);
		panelProvisioningPath.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel labelProvisioning = new JLabel("Profiles Path");
		panelProvisioningPath.add(labelProvisioning);
		
		provisioningPathfield = new JTextField();
		panelProvisioningPath.add(provisioningPathfield);
		provisioningPathfield.setColumns(22);
		
		JButton buttonProvisioningPath = new JButton("Browse");
		buttonProvisioningPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.showOpenDialog(null);
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile != null && selectedFile.exists() == true) {
					provisioningPathfield.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		panelProvisioningPath.add(buttonProvisioningPath);
		
		JPanel panelOptions = new JPanel();
		panelConfiguration0.add(panelOptions);
		panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.PAGE_AXIS));
		
		JButton checkoutButton = new JButton("Checkout a new repository");
		checkoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelOptions.add(checkoutButton);
		checkoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkoutWindow == null) {
					checkoutWindow = new RepoCloner(pathField.getText());
				}
				checkoutWindow.setVisible(true);
			}
		});
		
		JButton deleteProfilesButton = new JButton("Delete all profiles");
		deleteProfilesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelOptions.add(deleteProfilesButton);
		deleteProfilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteProfiles();
			}
		});
		
		JButton saveButton = new JButton("Save configuration");
		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelOptions.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProperties();
			}
		});
		
		labelStatus = new JLabel();
		labelStatus.setText("Status");
		labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelOptions.add(labelStatus);
		labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
		
		loadProperties();
	}
	
	private void deleteProfiles() {
		String message = "You will delete all files in \"" + provisioningPathfield.getText() + "\"";
	    String title = "Delete provisioning profiles";

	    int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
	    if (reply == JOptionPane.OK_OPTION) {
	    	File profileDirectory = new File(provisioningPathfield.getText());
			for (File file: profileDirectory.listFiles()) {
				file.delete();
			}
	    }
	}
	
	private void saveProperties() {
		Configuration.getInstance().setCoronaPath(coronaPathField.getText());
		Configuration.getInstance().setProjectsPath(pathField.getText());
		Configuration.getInstance().setProvisioningPath(provisioningPathfield.getText());
		
		if (Configuration.getInstance().save() == true) {
			labelStatus.setText("Configuration saved.");
		} else {
			labelStatus.setText("Configuration could not be saved.");
		}
	}
	
	private void loadProperties() {
		if (Configuration.getInstance().load() == true) {
			pathField.setText(Configuration.getInstance().getProjectsPath());
			coronaPathField.setText(Configuration.getInstance().getCoronaPath());
			provisioningPathfield.setText(Configuration.getInstance().getProvisioningPath());
			
			labelStatus.setText("Configuration loaded.");
		} else {
			labelStatus.setText("Configuration could not be loaded.");
		}
		
	}
}
