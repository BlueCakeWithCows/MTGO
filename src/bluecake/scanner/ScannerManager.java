package bluecake.scanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import bluecake.GUI.GUI;
import bluecake.GUI.GUIHandle;
import bluecake.misc.TradeInfo;

public class ScannerManager implements Runnable {
	private static final int DEFAULT_MAP_SIZE = 4;

	private HashMap<String, HashMap<String, TradeInfo>> masterSourceMap;
	private HashMap<String, HashMap<String, TradeInfo>> recentUpdate;

	private List<WebScanner> scanners;

	private static final String ID = "Scanner Manager";
	private GUIHandle gui;

	public ScannerManager() {
		masterSourceMap = new HashMap<String, HashMap<String, TradeInfo>>();
		recentUpdate = new HashMap<String, HashMap<String, TradeInfo>>();
		scanners = new ArrayList<WebScanner>();
		gui = GUI.gui.createAndAddGuiHandle(ID);
	}

	@Override
	public void run() {
		addScanner(new WikipriceWebCrawler());
		addScanner(new HotListWebScanner());
		addScanner(new DojoScanner());
		addScanner(new ClanTeamScanner());
		while (true) {
			collectRecentFromScanners();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void addScanner(WebScanner scanner) {
		synchronized (scanners) {
			scanners.removeIf(new Predicate<WebScanner>() {
				@Override
				public boolean test(WebScanner arg0) {
					Class<? extends WebScanner> c = scanner.getClass();
					return c.isInstance(arg0);
				}
			});
			scanners.add(scanner);
		}
		GUI.gui.log(ID, "Added Scanner " + scanner.identifier);
		Thread th = new Thread(scanner);
		th.start();
		GUI.gui.log(ID, "Thread started for " + scanner.identifier);

	}

	private void createMasterEntryIfNeeded(String card) {
		if (masterSourceMap.containsKey(card))
			return;

		masterSourceMap.put(card, new HashMap<String, TradeInfo>(DEFAULT_MAP_SIZE));
	}

	private void createRecentEntryIfNeeded(String card) {
		if (recentUpdate.containsKey(card))
			return;
		recentUpdate.put(card, new HashMap<String, TradeInfo>(DEFAULT_MAP_SIZE));
	}

	public HashMap<String, HashMap<String, TradeInfo>> getAndClearRecent() {
		synchronized (recentUpdate) {
			HashMap<String, HashMap<String, TradeInfo>> temp = recentUpdate;
			recentUpdate.clear();
			return temp;
		}
	}

	@SuppressWarnings("unchecked")
	/** Returns copy of master map. This is a SHALLOW copy */
	public HashMap<String, HashMap<String, TradeInfo>> getMaster() {
		return (HashMap<String, HashMap<String, TradeInfo>>) masterSourceMap.clone();
	}

	private void collectRecentFromScanners() {
		synchronized (scanners) {
			for (WebScanner ws : scanners) {

				Collection<TradeInfo> cards = ws.getAndClearRecentTrades();
				if (cards != null) {

					synchronized (masterSourceMap) {
						for (TradeInfo i : cards) {
							createMasterEntryIfNeeded(i.card);
							masterSourceMap.get(i.card).put(i.getSource(), i);
						}
					}
					synchronized (recentUpdate) {
						for (TradeInfo i : cards) {
							createRecentEntryIfNeeded(i.card);
							recentUpdate.get(i.card).put(i.getSource(), i);
							GUI.gui.table.tryAddNewTrade(i);

							for (WebScanner w : scanners) {
								if (i.getSource().equals(HotListWebScanner.source))
									if (!i.getSource().equalsIgnoreCase(w.identifier)) {
										w.requestCard(i.card);
									}
							}
						}
					}
				}
			}
		}
	}

}
