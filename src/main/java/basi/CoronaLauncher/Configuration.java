package basi.CoronaLauncher;

import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class Configuration {
	
	private static final String KEY_PROJECT_PATH = "projectPath";
	private static final String KEY_CORONA_PATH = "coronaPath";
	private static final String KEY_PROVISIONING_PATH = "provisioningPath";
	
	private String projectPath;
	private String coronaPath;
	private String provisioningPath;
	
	private static Configuration instance;

	public static synchronized Configuration getInstance() {
	    if (instance == null) {
	        instance = new Configuration();
	    }
	    return instance;
	}
	
	private Configuration() {

	}
	
	
	
	public boolean load() {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		String defaultPathValue = fw.getDefaultDirectory().getAbsolutePath();
		
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		projectPath = prefs.get(KEY_PROJECT_PATH, defaultPathValue);
		coronaPath = prefs.get(KEY_CORONA_PATH, defaultPathValue);
		provisioningPath = prefs.get(KEY_PROVISIONING_PATH, defaultPathValue);
		
		return true;
	}
	
	public boolean save() {
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		prefs.put(KEY_PROJECT_PATH, projectPath);
		prefs.put(KEY_CORONA_PATH, coronaPath);
		prefs.put(KEY_PROVISIONING_PATH, provisioningPath);
		
		return true;
	}
	
	public String getCoronaPath() {
		return coronaPath;
	}
	
	public String getProjectsPath() {
		return projectPath;
	}
	
	public String getProvisioningPath() {
		return provisioningPath;
	}
	
	public void setCoronaPath(String newPath) {
		coronaPath = newPath;
	
	}
	
	public void setProjectsPath(String newPath) {
		projectPath = newPath;
	}

	public void setProvisioningPath(String newPath) {
		provisioningPath = newPath;
	}
}
