package bluecake.GUI;

import java.awt.EventQueue;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class GUIHandle {
	private boolean logAll = false;
	private String identifier;
	private JTextArea console;
	private JScrollPane consolePane, settingsPane;
	private JTabbedPane consoleTabs,settingsTab;

	public GUIHandle(String Identifier, JTabbedPane consoleTabs, JTabbedPane settingsTabs) {
		this.consoleTabs = consoleTabs;
		this.settingsTab = settingsTabs;
		
		this.identifier = Identifier;
		this.consolePane = new JScrollPane();
		console = new JTextArea();
		consolePane.setViewportView(console);
		

		this.settingsPane = new JScrollPane();
		
	}

	public void setLogAll(boolean b) {
		this.logAll = b;
	}

	public void log(String key, String text) {
		if (logAll || key.equalsIgnoreCase(identifier)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					console.append(text + "\n");
				}
			});
		}
	}

	public void load() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					consoleTabs.addTab(identifier, consolePane);
					settingsTab.addTab(identifier, settingsPane);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void log(String text){
		GUI.gui.log(identifier, text);
		
	}
}
