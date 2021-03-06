package basi.CoronaLauncher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class ProjectButton extends JPanel{

	private static final String ICON_DEFAULT_PATH = "/Icon@2x.png";
	private static final int WIDTH_PANEL = 180;
	private static final int HEIGHT_PANEL = 200;
	private static final long serialVersionUID = 1L;
	private String projectURL;
	private JLabel labelProgress;
	private JLabel labelHash;
	private Callback onLaunch;
	private JButton launchButton;
	private JButton updateButton;

	public ProjectButton(String projectName, String absolutePath, Callback onLaunch){
		super();
		
		this.setPreferredSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));
		this.projectURL = absolutePath;
		this.onLaunch = onLaunch;
		
		String gameName = projectName;
		String iconPath = absolutePath + ICON_DEFAULT_PATH;

		this.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.setLayout(new BorderLayout(0, 0));

		JLabel labelName = new JLabel(gameName);
		Font font = labelName.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		labelName.setFont(boldFont);
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

		updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateButton.setEnabled(false);
				launchButton.setEnabled(false);
				
				final String[] commands = new String[7];
				commands[0] = "git reset --hard";
				commands[1] = "git pull";
				commands[2] = "git submodule update --init --recursive";
				commands[3] = "git submodule foreach git checkout master";
				commands[4] = "git submodule foreach git pull origin master";
				commands[5] = "git submodule update";
				commands[6] = "git rev-parse --short HEAD";
				
				final File projectDirectory = new File(projectURL);
				
				Callback completedCommand = new Callback() {
					private int indexCommand = 0;
					
					public synchronized void onComplete() {
						if (indexCommand < commands.length) {
							try {            
								Runtime rt = Runtime.getRuntime();
								Process proc = rt.exec(commands[indexCommand], null, projectDirectory);
								
								StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), proc, labelHash, null);            
								StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), proc, labelHash, this);
								
								errorGobbler.start();
								outputGobbler.start();
							} catch (Throwable t) {
								t.printStackTrace();
							}
							labelProgress.setText("Updating...");
						} else {
							String tooltipText = labelHash.getToolTipText();
							tooltipText = "<html>" + tooltipText.replace("\n", "<br>") + "</html>";
							labelHash.setToolTipText(tooltipText);
							labelProgress.setText("Update complete");
							
							updateButton.setEnabled(true);
							launchButton.setEnabled(true);
						}
						indexCommand += 1;
					}
				};
				
				completedCommand.onComplete();
			}
		});
		panelButtons.add(updateButton, BorderLayout.WEST);

		launchButton = new JButton("Launch");
		launchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ProjectButton.this.onLaunch != null) {
					ProjectButton.this.onLaunch.onComplete();
				}
			}
		});
		panelButtons.add(launchButton, BorderLayout.EAST);
	}
}
