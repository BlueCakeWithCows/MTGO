package Scanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bluecake.HalfTrade;
import bluecake.Planner;

public abstract class Scanner implements Runnable {
	public Map<String, HalfTrade> cards = Collections.synchronizedMap(new HashMap<String, HalfTrade>());
	protected Long lastUpdate;
	protected List<String> priority = Collections.synchronizedList(new ArrayList<String>());
	public boolean running = false;
	public Planner planner;

	public Scanner(Planner p) {
		this.planner = p;
	}

	public synchronized Long getLastUpdate() {
		return lastUpdate;
	}

	public synchronized void setLastUpdate(Long time) {
		lastUpdate = time;
	}

	public synchronized void requestCard(String card) {
		priority.add(card);
	}

	public synchronized void removeCard(String card) {
		while (priority.remove(card)) {
		}
	}

	protected void add(HalfTrade d) {
		cards.put(d.card, d);
		planner.newCard(d);
		this.setLastUpdate(System.currentTimeMillis());
	}
}
