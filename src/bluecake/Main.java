package bluecake;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import bluecake.GUI.GUI;
import bluecake.GUI.Table;
import bluecake.scanner.ScannerManager;

public class Main {

	public static Table frame;
	public static ScannerManager manager;
	public static Planner planner;

	public static void main(String[] agrs) throws InterruptedException, InvocationTargetException {
		GUI.createGUI();

		manager = new ScannerManager();
		planner = new Planner();

		Thread th = new Thread(manager);
		th.start();
		Thread p = new Thread(planner);
		p.start();
		(new Thread(new HTMLRequest())).start();
		planner.registerNotif(GUI.gui.recentTable);
		planner.registerNotif(GUI.gui.filteredTable);

	}

}
