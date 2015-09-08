package basi.CoronaLauncher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Selector extends JFrame {

	private static final String TAB_NAME_CONFIGURATION = "Configuration";
	private static final String TAB_NAME_PROJECTS = "Projects";

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panelProjects;
	private List<ProjectButton> projectButtons = new ArrayList<ProjectButton>();
	
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

		TabConfiguration tabConfiguration = new TabConfiguration(TAB_NAME_CONFIGURATION);
		tabbedPane.addTab(TAB_NAME_CONFIGURATION, null, tabConfiguration, null);
		
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
		
		updateProjectButtons();
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
		if (Configuration.getInstance().load()) {
			File folder = new File(Configuration.getInstance().getProjectsPath());
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
									Configuration.getInstance().getCoronaPath() + "/Contents/MacOS/Corona Simulator",
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
