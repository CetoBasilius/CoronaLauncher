package basi.CoronaLauncher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JLabel;
import javax.swing.JTextArea;
class StreamGobbler extends Thread
{
	private InputStream is;
	private JLabel statusLabel;
	private Process process;
	private Callback callback;
	private JTextArea logText;

	StreamGobbler(InputStream is, Process process, JLabel statusLabel, Callback callback, JTextArea logText) {
		this.process = process;
		this.is = is;
		this.statusLabel = statusLabel;
		this.callback = callback;
		this.logText = logText;
	}
	
	StreamGobbler(InputStream is, Process process, JLabel statusLabel, Callback callback) {
		this(is, process, statusLabel, callback, null);
	}
	
	public StreamGobbler(InputStream is, Process process, JLabel statusLabel) {
		this(is, process, statusLabel, null);
	}
	
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			StringBuffer completeLog = new StringBuffer();
			while( true ) {
			    String line = br.readLine();
				if (line != null) {
					completeLog.append(line);
					completeLog.append("\n");
					
					System.out.println(line);
					
					if (statusLabel != null) {
						statusLabel.setText(line);
					}
					if (logText != null) {
						logText.setText(completeLog.toString());
					}
				}
				try {
					process.exitValue();
					if (callback != null) {
						callback.onComplete();
					}
					break;
				} catch (Exception e) {}
			}
			if (statusLabel != null) {
				statusLabel.setToolTipText(completeLog.toString());
			}
		} catch (Exception e) {}
	}
}