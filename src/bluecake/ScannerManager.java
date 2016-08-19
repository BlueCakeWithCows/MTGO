package bluecake;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import Scanner.WebScanner;
import Scanner.WikipriceWebCrawler;
import bluecake.logging.HeadLogger;
import bluecake.logging.LogHandler;

public class ScannerManager implements Runnable {
	private static final int DEFAULT_MAP_SIZE = 4;

	private HashMap<String, HashMap<String, TradeInfo>> masterSourceMap;
	private List<WebScanner> scanners;
	private HeadLogger headLog;
	private LogHandler log;
	private static final String ID = "Scanner Manager";

	public ScannerManager(HeadLogger hLog) {
		masterSourceMap = new HashMap<String, HashMap<String, TradeInfo>>();
		scanners = new ArrayList<WebScanner>();
		log = headLog.getNewLog();
		log.setID(ID);
	}

	@Override
	public void run() {
		addScanner(new WikipriceWebCrawler(headLog.getNewLog()));

		
		while(true){
			collectRecentFromScanners();
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
		log.log(HeadLogger.INFO, "Added Scanner " + scanner.identifier);
		Thread th = new Thread(scanner);
		th.start();
		log.log(HeadLogger.INFO, "Thread started for " + scanner.identifier);

	}

	private void createMasterEntryIfNeeded(String card) {
		if (masterSourceMap.containsKey(card))
			return;

		masterSourceMap.put(card, new HashMap<String, TradeInfo>(DEFAULT_MAP_SIZE));
	}

	private void collectRecentFromScanners() {
		synchronized (scanners) {
			for (WebScanner ws : scanners) {

				Collection<TradeInfo> cards = ws.getAndClearRecentTrades();
				if (cards != null) {

					synchronized (masterSourceMap) {
						for (TradeInfo i : cards) {
							createMasterEntryIfNeeded(i.card);
							masterSourceMap.get(i).put(i.getSource(), i);
						}
					}
				}
			}
		}
	}

}
