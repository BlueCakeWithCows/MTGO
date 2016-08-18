package Scanner;

import java.util.Collection;
import java.util.HashMap;

import bluecake.LogHandler;
import bluecake.TradeInfo;

public abstract class WebScanner implements Runnable {
	private HashMap<String, TradeInfo> scannedCards;
	private HashMap<String, TradeInfo> recentlyUpdatedCards;
	
	private final String identifier;
	private boolean THREADED_DELETE;
	protected LogHandler log;

	public WebScanner(String identifier, LogHandler log) {
		this.identifier = identifier;
		log.setID(identifier);
		THREADED_DELETE = true;
	}

	/** Should only be called in scanner's thread*/
	protected void addTradeInfo(TradeInfo info) {
		info.setSource(identifier);
		
		scannedCards.put(info.getCard(), info);
		
		if(THREADED_DELETE){
			THREADED_DELETE = false;
			recentlyUpdatedCards.clear();
		}
		recentlyUpdatedCards.put(info.getCard(), info);
	}

	public Collection<TradeInfo> getAllTrades() {
		return scannedCards.values();
	}

	/**Returns null if recent trades is empty */ //Not empty but yet to be updated
	public Collection<TradeInfo> getAndClearRecentTrades() {
		if (THREADED_DELETE) {
			return null;
		}
		Collection<TradeInfo> dump = recentlyUpdatedCards.values();
		THREADED_DELETE = true;
		return dump;
	}
}
