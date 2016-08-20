package bluecake.scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

import bluecake.misc.TradeInfo;
import bluecake.util.SimpleSaveLoad;
import bluecake.util.Util;

public class WikipriceWebCrawler extends WebScanner {
	private final static String IDENTIFIER = "WIKI";

	// These values are in seconds
	private static final float DELAY_BETWEEN_PAGE_LOADS = 5;
	private static final float TOO_MANY_REQUESTS_DELAY = 30;

	private static final String topFolder = "Wiki/";
	/** File keeps track of last card program scrapped */
	private static final String file = topFolder + "WikiStatus";

	/**
	 * Index keeps track of where it found cards. This allows it to jump to
	 * specific cards
	 */
	private static final String index = topFolder + "WikiDex";

	/** List of sets and set sizes for jumping crawling */
	private static final String sets = topFolder + "Sets.txt";

	/** Missing: [setName]/[card#] */
	private static final String targetURL = "https://www.mtgowikiprice.com/card/";

	public WikipriceWebCrawler() {
		super(IDENTIFIER);
	}

	// These keep track of the set (by line number in sets) and the card (by url
	// on wikiprice)
	private int currentSet = 0, currentCard;

	private boolean running;

	public boolean getRunning() {
		return running;
	}

	public void terminate() {
		running = false;
	}

	private HashMap<String, Integer> CardIDIndex = new HashMap<String, Integer>();;

	/** Return false if irrecoverable error */
	private boolean load() {
		this.setCardUpdateTime(60 * 60);

		log("Loading Sets: " + sets);
		if (!loadSets(sets))
			return false;

		log("Loading Last Card Location");
		if (!loadLastCardLocation(file))
			return false;

		log("Loading Card Index");
		if (!loadCardIndex(index))
			return false;

		log("Loading Complete!");
		return true;
	}

	private boolean skipWait = false;

	private Pair<String, Integer> chooseNextCard() {

		String card;
		String set = "";
		int id = 0;
		card = this.getAndRemoveRequest();
		while (card != null) {
			String[] split = card.split("\\[");
			set = split[1].replace("]", "");
			if (CardIDIndex.containsKey(card) && this.hasEnoughTimeHasPassedToUpdateTradeInfo(card)) {
				id = CardIDIndex.get(card);
				log("Chose " + card + " on request.");
				break;
			} else {
				log("Card " + card + " not yet indexed.");
			}
			card = this.getAndRemoveRequest();
		}

		if (id == 0) {
			// This area has bad error handling compared to rest
			currentCard++;
			if (currentCard > Integer.valueOf(this.versions.get(this.currentSet)[1])) {
				currentSet++;
				currentCard = 0;
			}
			if (currentSet > versions.size() - 1) {
				currentSet = 0;
				currentCard = 0;
			}
			save();
			try {
				set = this.versions.get(this.currentSet)[0];
				id = this.currentCard;
			} catch (Exception e) {
				log(e.getMessage());
			}

			log("Chose " + set + " " + id + " not a request.");
		}
		return Pair.of(set, id);
	}

	private TradeInfo getCard(String set, int cardID) {
		String url = targetURL + set + "/" + cardID;
		String html;
		try {
			html = Util.getHTML(url);
		} catch (IOException e) {
			log(e.getMessage());
			log("Could not load page.");
			return null;
		}
		TradeInfo info = parse(html, cardID);
		return info;
	}

	public void save() {
		String[] stuff = new String[] { Integer.toString(currentSet), Integer.toString(currentCard) };
		SimpleSaveLoad.save(file, stuff);
	}

	private int failed_trade_counter;

	private String cleanupCardString(String card) {
		card = card.replace("&#39;", "'");
		card = card.replace("&quot;", "\"");
		return card;
	}

	public TradeInfo parse(String html_page, int cardID) {

		TradeInfo tInfo = new TradeInfo(IDENTIFIER);

		String[] lines = html_page.split("\n");
		String card = "";
		try {

			card = lines[96];
			if (card.contains("<span class"))
				card = lines[150];
			card = cleanupCardString(card);

			for (int i = 0; i < lines.length; i++) {
				if (lines[i].contains("collection_row sell_row group_boss bot  ") && tInfo.seller == null) {
					if (lines[i + 1].contains("<td class=\" bot_name  \">")) {
						for (int i2 = i; i2 < i + 14; i2++) {
							if (lines[i2].contains("<td class=\" sell_price_round  \">")) {
								try {
									tInfo.setSell(parseName(lines[i + 2]), Float.parseFloat(lines[i2 + 1]));
								} catch (NumberFormatException e) {
									tInfo.seller = null;
									log(e.getMessage());
								}
								break;
							}
						}
					}
				}

				if (lines[i].contains("collection_row buy_row group_boss chain") && tInfo.buyer == null) {
					if (lines[i + 1].contains("<td class=\" bot_name  \">")) {
						for (int i2 = i; i2 < i + 14; i2++) {
							if (lines[i2].contains("<td class=\" buy_price_round  \">")) {
								try {
									tInfo.setBuy(parseName(lines[i + 2]), Float.parseFloat(lines[i2 + 1]));
								} catch (NumberFormatException e) {
									tInfo.seller = null;
									log(e.getMessage());
								}
								break;
							}
						}
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			log(e.getMessage());
		}
		tInfo.setCard(card);
		log(tInfo.toString());

		if (!CardIDIndex.containsKey(card)) {
			String value = card + "|" + cardID;
			SimpleSaveLoad.append(index, value);
			log("SAVED CARD " + card);
			CardIDIndex.put(value, cardID);
		}

		if (tInfo.seller == null && tInfo.buyer == null) {
			failed_trade_counter++;
			log("Seller and Buyer are both null. Possibly nothing?");
		} else
			failed_trade_counter = 0;

		return tInfo;
	}

	private static String parseName(String name) {
		if (name.startsWith("<a")) {
			String newName = name.split("/i>")[1];
			newName = newName.trim();
			name = newName.split("</a")[0];
		}
		return name;
	}

	@Override
	public void run() {

		running = true;
		this.skipWait = false;
		this.load();

		while (running) {
			Pair<String, Integer> card;
			// May be a problem with the following line
			while (!(card = this.chooseNextCard()).getLeft().equalsIgnoreCase("")) {
				if (this.hasEnoughTimeHasPassedToUpdateTradeInfo(card.getLeft()))
					break;
			}
			TradeInfo trade = this.getCard(card.getLeft(), card.getRight());
			if (trade != null)
				this.addTradeInfo(trade);
			try {
				if (skipWait)
					;
				else {
					if (failed_trade_counter > 2)
						Thread.sleep((long) (TOO_MANY_REQUESTS_DELAY * 1000));
					else
						Thread.sleep((long) (DELAY_BETWEEN_PAGE_LOADS * 1000));
				}

			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			skipWait = false;
		}
		running = false;
	}

	private List<String[]> versions;

	private boolean loadSets(String url) {

		versions = new ArrayList<String[]>();
		try {
			List<String> file = SimpleSaveLoad.load(url);
			for (String line : file) {
				String l = line.split("-")[0];
				String l2 = line.split("-")[1];
				versions.add(new String[] { l, l2 });
			}

		} catch (FileNotFoundException e) {
			log("Could not find file at: " + url);
			return false;
		} catch (IOException e) {
			log(e.getMessage());
			return false;
		}
		log(versions.size() + " sets found!");

		return true;
	}

	private boolean loadLastCardLocation(String url) {
		String cS;

		try {
			if (SimpleSaveLoad.createFileIfNoExist(url, "0\n0"))
				log("Wikistatus file not found. Creating: " + url);
			cS = SimpleSaveLoad.loadLine(url, 0);
			currentSet = Integer.valueOf(cS);
			currentCard = Integer.valueOf(SimpleSaveLoad.loadLine(url, 1));
			log("Set is " + currentSet + " and card is " + currentCard);
		} catch (NumberFormatException e) {
			log("Invalid status file: " + url);
			log("Using default status values");
			currentSet = 0;
			currentCard = 1;
		} catch (IOException e) {
			log(e.getMessage());
			log("Using default status values");
			currentSet = 0;
			currentCard = 1;
		}
		return true;
	}

	private boolean loadCardIndex(String index) {
		try {
			if (SimpleSaveLoad.createFileIfNoExist(index, ""))
				log("Did not find index at:" + index + "\nCreating blank index");

			List<String> file = SimpleSaveLoad.load(index);
			for (String s : file) {
				String[] split = s.split("\\|");
				int id = Integer.valueOf(split[1]);
				String name = split[0];
				CardIDIndex.put(name, id);
			}
		} catch (IOException e) {
			log(e.getMessage());
		}

		for (String s : CardIDIndex.keySet()) {
			System.out.println(s);
		}
		return true;
	}
}
