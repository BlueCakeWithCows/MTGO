package bluecake;

import java.awt.EventQueue;

import Scanner.HotlistScanner;

public class Main {
	
	public static Table frame;
	public static Planner planner;
	public static void main(String[] agrs) throws InterruptedException {
		
		new Thread(new Runnable() {
			public void run() {
				try {
					frame = new Table();
					frame.setVisible(true);
					planner = new Planner(frame);
					new Thread(planner).start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}

}
