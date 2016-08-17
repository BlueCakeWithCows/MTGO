package bluecake;

import java.io.FileNotFoundException;
import java.io.IOException;

import Scanner.HotlistScanner;
import Scanner.WikiScanner;
import bluecake.util.Versions;

public class Planner implements Runnable {
	public Table frame;

	public Planner(Table frame) {
		this.frame = frame;
	}

	WikiScanner wikiScanner;

	@Override
	public void run() {

		HotlistScanner hotScanner = new HotlistScanner(this);
		Thread hotThread = new Thread(hotScanner);
		hotThread.start();

		wikiScanner = new WikiScanner(this);
		Thread wikiThread = new Thread(wikiScanner);
		wikiThread.start();

		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public synchronized void newCard(HalfTrade deal) {
		try {
			if (deal.source == HalfTrade.HOTLISTBOT) {
				wikiScanner.requestCard(deal.card);
			}
			frame.addCard(deal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
