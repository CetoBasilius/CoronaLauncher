package basi.CoronaLauncher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.SwingConstants;

public class Selector extends JFrame {

	private static final String KEY_PROJECT_PATH = "projectPath";
	private static final String KEY_CORONA_PATH = "coronaPath";
	
	private static final String TAB_NAME_CONFIGURATION = "Configuration";
	private static final String TAB_NAME_PROJECTS = "Projects";

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panelProjects;
	private JTextField pathField;
	private List<ProjectButton> projectButtons = new ArrayList<ProjectButton>();
	private JTextField coronaPathField;
	private JLabel labelStatus;
	protected RepoCloner checkoutWindow;

	/**
	 * Create the frame.
	 */
	public Selector() {
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				JTabbedPane tabbedPane = (JTabbedPane)event.getSource();
				JPanel selectedTab = (JPanel)tabbedPane.getSelectedComponent();
				if (selectedTab.getName() == TAB_NAME_PROJECTS) {
					// Save configuration
					Selector.this.updateProjectButtons();
				}else if(selectedTab.getName() == TAB_NAME_CONFIGURATION){
					// Load configuration?
				}
			}
		});
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel tabProjects = new JPanel();
		tabProjects.setName(TAB_NAME_PROJECTS);
		tabbedPane.addTab(TAB_NAME_PROJECTS, null, tabProjects, null);
		tabProjects.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		tabProjects.add(scrollPane);

		panelProjects = new JPanel();
		scrollPane.setViewportView(panelProjects);
		panelProjects.setLayout(new ModifiedFlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel refreshPanel = new JPanel();
		tabProjects.add(refreshPanel, BorderLayout.SOUTH);

		JButton refreshButton = new JButton("Refresh projects");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateProjectButtons();
			}
		});
		refreshPanel.setLayout(new BorderLayout(0, 0));
		refreshPanel.add(refreshButton);

		// Configuration tab
		JPanel tabConfiguration = new JPanel();
		tabConfiguration.setName(TAB_NAME_CONFIGURATION);
		tabbedPane.addTab(TAB_NAME_CONFIGURATION, null, tabConfiguration, null);
		tabConfiguration.setLayout(new BorderLayout(0, 0));

		JPanel panelConfiguration0 = new JPanel();
		tabConfiguration.add(panelConfiguration0, BorderLayout.CENTER);
		panelConfiguration0.setLayout(new BorderLayout(0, 0));

		JPanel panelProjectPath = new JPanel();
		panelConfiguration0.add(panelProjectPath, BorderLayout.NORTH);

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
		
		JPanel panelConfiguration1 = new JPanel();
		panelConfiguration0.add(panelConfiguration1, BorderLayout.CENTER);
		panelConfiguration1.setLayout(new BorderLayout(0, 0));
		
		JPanel panelCoronaPath = new JPanel();
		panelConfiguration1.add(panelCoronaPath, BorderLayout.NORTH);
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
		
		labelStatus = new JLabel();
		labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
		panelConfiguration1.add(labelStatus, BorderLayout.SOUTH);
		
		JPanel checkoutPanel = new JPanel();
		panelConfiguration1.add(checkoutPanel, BorderLayout.CENTER);
		checkoutPanel.setLayout(new FlowLayout());
		
		JButton checkoutButton = new JButton("Checkout a new repository");
		checkoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkoutWindow == null) {
					checkoutWindow = new RepoCloner(pathField.getText());
				}
				checkoutWindow.setVisible(true);
			}
		});
		checkoutPanel.add(checkoutButton, BorderLayout.NORTH);
		
		JButton saveButton = new JButton("Save configuration");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProperties();
			}
		});
		panelConfiguration0.add(saveButton, BorderLayout.SOUTH);
		
		loadProperties();
		updateProjectButtons();
	}
	
	private void saveProperties() {
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		prefs.put(KEY_PROJECT_PATH, pathField.getText());
		prefs.put(KEY_CORONA_PATH, coronaPathField.getText());
		labelStatus.setText("Configuration saved.");
	}
	
	private void loadProperties() {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		String defaultPathValue = fw.getDefaultDirectory().getAbsolutePath();
		
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		pathField.setText(prefs.get(KEY_PROJECT_PATH, defaultPathValue));
		coronaPathField.setText(prefs.get(KEY_CORONA_PATH, defaultPathValue));
		
		labelStatus.setText("Configuration loaded.");
	}

	private void updateProjectButtons(){
		deleteProjectButtons();
		createProjectButtons();
		this.invalidate();
		this.repaint();
	}

	private void deleteProjectButtons(){
		for (ProjectButton projectButton : projectButtons) {
			panelProjects.remove(projectButton);
		}
	}

	private void createProjectButtons(){
		if (pathField != null) {
			File folder = new File(pathField.getText());
			File[] listOfFiles = folder.listFiles();
	
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isDirectory()) {
					String gitFolderPath = listOfFiles[i].getAbsolutePath() + "/.git";
					File gitFolder = new File(gitFolderPath);
					
					String mainFilePath = listOfFiles[i].getAbsolutePath() + "/main.lua";
					File mainFile = new File(mainFilePath);
					
					if (gitFolder.exists() == true && mainFile.exists() == true) {
						final String absolutePath = listOfFiles[i].getAbsolutePath();
						Callback onLaunch = new Callback(){
							public void onComplete() {
								String[] commands = new String[] {
									coronaPathField.getText() + "/Contents/MacOS/Corona Simulator",
									absolutePath,
								};
								
								try {            
									Runtime rt = Runtime.getRuntime();
									rt.exec(commands);
								} catch (Throwable t) {
									t.printStackTrace();
								}
							}
						};
						
						ProjectButton projectButton = new ProjectButton(listOfFiles[i].getName(), absolutePath, onLaunch);
						panelProjects.add(projectButton);
						projectButtons.add(projectButton);
					}
				}
			}
		}
	}
}
