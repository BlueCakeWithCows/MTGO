package bluecake;

import java.io.IOException;
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
import bluecake.util.SimpleSaveLoad;

public class Planner implements Runnable {
	// My job is to decide what is, and what is not, valuable to mankind
	GUIHandle gui;
	String id = "Planner";
	private String file;
	private List<CompleteTrade> realList;
	private HashMap<String, Long> recentCards;
	private List<Notifables> notifable;

	public void registerNotif(Notifables notif) {
		synchronized (notifable) {
			notifable.add(notif);
		}
	}

	public Planner() {
		gui = GUI.gui.createAndAddGuiHandle(id);
		realList = new ArrayList<>();
		this.filter = this.getDefaultFilter();
		gui.log("BlackListed Cards");
		dumpLog(filter.cardBlacklist);
		gui.log("Sellers");
		dumpLog(filter.validSellers);
		gui.log("Buyers");
		dumpLog(filter.validBuyers);
		recentCards = new HashMap<String, Long>();
		notifable = new ArrayList<Notifables>();
	}

	private void dumpLog(List<String> list) {
		for (String s : list) {
			gui.log(s);
		}
	}

	private TradeFilter filter;

	public boolean running;

	@Override
	public void run() {
		running = true;
		loadRecent();
		while (running) {
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gui.log("Auto updating...");
			logic();
			notif();
		}

		running = false;
		gui.log("Goodnight");
	}

	private void loadRecent() {
		try {
			List<String> list = SimpleSaveLoad.load(file);
			for (String s : list) {
				String[] ss = s.split(":");
				recentCards.put(ss[0], Long.valueOf(ss[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<CompleteTrade> getList() {
		List<CompleteTrade> trade;
		synchronized (realList) {
			trade = new ArrayList<>(realList);
		}
		return trade;
	}

	public void unflag(String card) {
		synchronized (recentCards) {
			recentCards.put(card, (long) 0);
			notif();
		}
		try {
			SimpleSaveLoad.addOrReplace(file, card, "0");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CompleteTrade getSingleTradeAndMark() {
		if (realList.size() < 1)
			return null;
		CompleteTrade trade = realList.get(0);
		synchronized (trade) {
			realList.remove(trade);
		}
		flag(trade.card);
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
			int i = 0;
			for (String card : bigMap.keySet()) {

				CompleteTrade trade = new CompleteTrade(card);
				for (TradeInfo source : bigMap.get(card).values()) {
					if (filter.check(source) && (!recentCards.containsKey(source.card)
							|| filter.checkAge(recentCards.get(source.card)))) {
						trade.tryAdd(source);
					}
				}

				if (filter.check(trade)) {
					realList.add(trade);
				}

			}
			Collections.sort(realList, CompleteTrade.getComparator());

		}
		gui.log("Done updating. Entries: " + realList.size());
	}

	private void notif() {
		synchronized (notifable) {
			for (Notifables n : notifable)
				n.ping();
		}
	}

	public void flag(String card) {
		recentCards.put(card, System.currentTimeMillis());
		try {
			SimpleSaveLoad.addOrReplace(file, card, String.valueOf(System.currentTimeMillis()));
			notif();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static TradeFilter getDefaultFilter() {
		TradeFilter f = new TradeFilter();
		f.COMPLETE = true;
		f.MAX_AGE = (long) (5 * 60 * 1000);
		f.MIN_PROFIT_GAIN = .01f;
		f.MIN_PERCENT_GAIN = .03f;
		f.TIME_BETWEEN_IDENTICAL_CARDS = (long) (60 * 1000 * 60 * 4);

		try {
			List<String> cList = SimpleSaveLoad.load("filter/BlackList.txt");
			f.cardBlacklist = cList;
			f.cardBlacklist = cList;
			List<String> sList;
			sList = SimpleSaveLoad.load("filter/WhiteList.txt");
			f.validSellers = sList;
			List<String> bList = SimpleSaveLoad.load("filter/WhiteListBuyer.txt");
			f.validBuyers = bList;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return f;
	}

	public List<Object[]> getRecentList() {
		List<Object[]> ray = new ArrayList<Object[]>();
		for (String key : recentCards.keySet()) {
			long time = recentCards.get(key);
			if (!filter.checkAge(time)) {
				ray.add(new Object[] { key, time });
			}
		}
		return ray;
	}
}
