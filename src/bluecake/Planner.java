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
import bluecake.client.Client;
import bluecake.misc.CompleteTrade;
import bluecake.misc.TradeFilter;
import bluecake.misc.TradeInfo;

public class Planner implements Runnable {
	// My job is to decide what is, and what is not, valuable to mankind
	GUIHandle gui;
	String id = "Planner";

	private List<CompleteTrade> realList;
	private HashMap<String, Long> recentCards;

	public Planner() {
		gui = GUI.gui.createAndAddGuiHandle(id);
		realList = new ArrayList<>();
		this.filter = this.createDefaultFilter();
		recentCards = new HashMap<String,Long>();
		
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
				e.printStackTrace();
			}
			gui.log("Auto updating...");
			logic();
			if (Client.client == null)
				Client.init(this);
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
					if (filter.check(source) && (!recentCards.containsKey(source.card)
							|| filter.checkAge(recentCards.get(source.card)))) {
						gui.log("Passed First Filter");
						trade.tryAdd(source);
					}
				}

				if (filter.check(trade)) {

					System.out.println(realList.add(trade));

				}

			}
			Collections.sort(realList, CompleteTrade.getComparator());

		}
		gui.log("Done updating. Entries: " + realList.size());
	}

	public void flag(String card) {
		recentCards.put(card, System.currentTimeMillis());
	}
	
	private TradeFilter createDefaultFilter(){
		TradeFilter f = new TradeFilter();
		f.COMPLETE=true;
		f.MAX_AGE = (long) (5 * 60 * 1000);
		f.MAX_PRICE = 4f;
		f.MIN_PERCENT_GAIN = .03f;
		f.MIN_PROFIT_GAIN = .01f;
		f.TIME_BETWEEN_IDENTICAL_CARDS=(long) (60*1000*60*6);
		List<String> cList = new ArrayList<String>();
		cList.add("Urza's");
		cList.add("Mountain");
		cList.add("Swamp");
		cList.add("Forest");
		cList.add("Island");
		cList.add("Plains");
		
		f.cardBlacklist = cList;	
		List<String> bList = new ArrayList<String>();
		bList.add("HotListBot");
		f.validBuyers = bList;	
		List<String> sList = new ArrayList<String>();
		sList.add("NinjaBots");
		sList.add("botomagic");
		sList.add("shop_pearl");
		sList.add("MTGOCardMarket2");
		sList.add("JBStore2");
		sList.add("MTGOCardMarket");
		f.validSellers = sList;	
		return f;
	}
}
