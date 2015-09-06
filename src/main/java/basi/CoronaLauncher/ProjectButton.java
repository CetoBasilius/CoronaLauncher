package basi.CoronaLauncher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class ProjectButton extends JPanel{

	private static final int WIDTH_PANEL = 160;
	private static final int HEIGHT_PANEL = 180;
	private static final long serialVersionUID = 1L;
	private String projectURL;
	private JLabel labelProgress;
	private JLabel labelHash;
	private Callback onLaunch;

	public ProjectButton(String projectName, String absolutePath, Callback onLaunch){
		super();
		
		this.setPreferredSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));
		this.projectURL = absolutePath;
		this.onLaunch = onLaunch;
		
		String gameName = projectName;
		String iconPath = absolutePath + "/Icon.png";

		this.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.setLayout(new BorderLayout(0, 0));

		JLabel labelName = new JLabel(gameName);
		labelName.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(labelName, BorderLayout.NORTH);

		ImagePanel imagePanel = new ImagePanel(iconPath);
		this.add(imagePanel, BorderLayout.CENTER);

		JPanel panelButtons = new JPanel();
		this.add(panelButtons, BorderLayout.SOUTH);
		panelButtons.setLayout(new BorderLayout(0, 0));
		
		JPanel panelLabels = new JPanel();
		panelButtons.add(panelLabels, BorderLayout.NORTH);
		panelLabels.setLayout(new BorderLayout(0, 0));
		
		labelHash = new JLabel("");
		labelHash.setHorizontalAlignment(SwingConstants.CENTER);
		panelLabels.add(labelHash, BorderLayout.NORTH);
		
		labelProgress = new JLabel("");
		labelProgress.setHorizontalAlignment(SwingConstants.CENTER);
		panelLabels.add(labelProgress, BorderLayout.CENTER);

		JButton updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				final String[] commands = new String[3];
				commands[0] = "git reset --hard";
				commands[1] = "git pull";
				commands[2] = "git submodule foreach git pull origin master";
				
				final File projectDirectory = new File(projectURL);
				
				Callback completedCommand = new Callback() {
					private int timesCompleted = 0;
					public void onComplete() {
						timesCompleted += 1;
						if (timesCompleted >= commands.length) {
							String hashCommand = "git --git-dir " + projectURL + "/.git rev-parse --short HEAD";
							try {            
								Runtime rt = Runtime.getRuntime();
								Process proc = rt.exec(hashCommand, null, projectDirectory);
								
								StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), proc, labelHash, null);            
								StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), proc, labelHash, null);
								
								errorGobbler.start();
								outputGobbler.start();
							} catch (Throwable t) {
								t.printStackTrace();
							}
							labelProgress.setText("Update complete");
						}
					}
				};
				
				for (String command : commands) {
					try {            
						Runtime rt = Runtime.getRuntime();
						Process proc = rt.exec(command, null, projectDirectory);
						
						StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), proc, labelProgress, null);            
						StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), proc, labelProgress, completedCommand);
						
						errorGobbler.start();
						outputGobbler.start();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}
		});
		panelButtons.add(updateButton, BorderLayout.CENTER);

		JButton launchButton = new JButton("Launch");
		launchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ProjectButton.this.onLaunch != null) {
					ProjectButton.this.onLaunch.onComplete();
				}
			}
		});
		panelButtons.add(launchButton, BorderLayout.SOUTH);
	}
}
