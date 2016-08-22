package bluecake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import bluecake.GUI.GUI;
import bluecake.GUI.GUIHandle;
import bluecake.misc.CompleteTrade;
import bluecake.misc.TradeFilter;
import bluecake.misc.TradeInfo;

public class Planner implements Runnable {
	// My job is to decide what is, and what is not, valuable to mankind
	GUIHandle gui;
	String id = "Planner";

	private List<CompleteTrade> realList;

	public Planner() {
		gui = GUI.gui.createAndAddGuiHandle(id);
		realList = new ArrayList<>();
		filter = new TradeFilter();
	}

	private TradeFilter filter;

	public boolean running;

	@Override
	public void run() {
		running = true;

		while (running) {
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gui.log("Auto updating...");
			logic();	
		}

		running = false;
		gui.log("Goodnight");
	}

	public List<CompleteTrade> getList() {
		List<CompleteTrade> trade;
		synchronized (realList) {
			trade = new ArrayList<>(realList);
		}
		return trade;
	}

	public void forceUpdate() {
		logic();
	}

	private void logic() {
		gui.log("Updating The List");
		synchronized (realList) {
			realList.clear();

			HashMap<String, HashMap<String, TradeInfo>> bigMap = Main.manager.getMaster();
			System.out.println(bigMap.keySet().size());
			int i = 0;
			for (String card : bigMap.keySet()) {
			
				CompleteTrade trade = new CompleteTrade(card);
				for (TradeInfo source : bigMap.get(card).values()) {
					if (filter.check(source))
						trade.tryAdd(source);
				}
				
				if (filter.check(trade)){
					
					System.out.println(realList.add(trade));
					
				}
				
			}
			Collections.sort(realList, CompleteTrade.getComparator());

		}
		gui.log("Done updating. Entries: " + realList.size());
	}
}
