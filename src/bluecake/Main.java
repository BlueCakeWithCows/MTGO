package bluecake;

import java.awt.EventQueue;

import bluecake.GUI.GUI;
import bluecake.GUI.Table;
import bluecake.scanner.ScannerManager;

public class Main {

	public static Table frame;

	public static void main(String[] agrs) throws InterruptedException {
		GUI.createGUI();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScannerManager manager = new ScannerManager();
					Thread th = new Thread(manager);
					th.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
