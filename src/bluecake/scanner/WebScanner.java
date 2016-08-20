package bluecake.scanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import bluecake.GUI.GUI;
import bluecake.GUI.GUIHandle;
import bluecake.misc.Log;
import bluecake.misc.TradeInfo;

public abstract class WebScanner implements Runnable {
	private HashMap<String, TradeInfo> scannedCards;
	private HashMap<String, TradeInfo> recentlyUpdatedCards;
	private List<String> requestedCards;
	public final String identifier;
	private boolean THREADED_DELETE;
	public boolean running;
	private long CARD_UPDATE_TIME;
	protected GUIHandle gui;

	public WebScanner(String identifier) {
		this.scannedCards = new HashMap<String, TradeInfo>();
		this.recentlyUpdatedCards = new HashMap<String, TradeInfo>();
		this.requestedCards = new ArrayList<String>();
		this.identifier = identifier;
		THREADED_DELETE = true;
		gui = GUI.gui.createAndAddGuiHandle(identifier);
	}

	public boolean hasEnoughTimeHasPassedToUpdateTradeInfo(String card) {
		return (!scannedCards.containsKey(card)
				|| scannedCards.get(card).getCreationTime() + CARD_UPDATE_TIME < System.currentTimeMillis());
	}

	/**
	 * Individual cards WILL NOT be chosen to update unless this time has passed
	 * - behavior varies by Scanner implementation.
	 * 
	 * @param time
	 */
	public void setCardUpdateTime(float time) {
		this.CARD_UPDATE_TIME = (long) (time * 1000);
	}

	/** Should only be called in scanner's thread */
	protected void addTradeInfo(TradeInfo info) {
		info.setSource(identifier);

		scannedCards.put(info.getCard(), info);

		if (THREADED_DELETE) {
			THREADED_DELETE = false;
			recentlyUpdatedCards.clear();
		}
		recentlyUpdatedCards.put(info.getCard(), info);
	}

	public Collection<TradeInfo> getAllTrades() {
		return scannedCards.values();
	}

	/** Returns null if recent trades is empty */ // Not empty but yet to be
													// updated
	public Collection<TradeInfo> getAndClearRecentTrades() {
		if (THREADED_DELETE) {
			return null;
		}
		Collection<TradeInfo> dump = recentlyUpdatedCards.values();
		THREADED_DELETE = true;
		return dump;
	}

	protected void log(String s) {
		log(Log.INFO, s);
	}

	protected void log(int level, String message) {
		GUI.gui.log(level, identifier, message);
	}

	protected String getAndRemoveRequest() {
		synchronized (requestedCards) {
			String card;
			if (requestedCards.size() > 0){
				card = requestedCards.get(0);
				
			}
			else
				return null;

			while (requestedCards.remove(card)) {
			}

			return card;
		}
	}

	public void requestCard(String card) {
		synchronized (requestedCards) {
			requestedCards.add(card);
			
		}
	}

	public long getCardUpdateTime() {
		return this.CARD_UPDATE_TIME;
	}
}
