package Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import bluecake.HalfTrade;
import bluecake.Planner;
import bluecake.util.Log;
import bluecake.util.SimpleSaveLoad;
import bluecake.util.Util;
import bluecake.util.Versions;

public class WikipriceWebCrawler extends WebScanner {
	private final static String IDENTIFIER = "WIKI";
	
	//These values are in seconds
	private static final float DELAY_BETWEEN_PAGE_LOADS = 5;
	private static final float TOO_MANY_REQUESTS_DELAY = 30;
	

	private static final String topFolder = "Wiki/";
	/** File keeps track of last card program scrapped*/
	private static final String file = topFolder + "WikiStatus";
	
	/** Index keeps track of where it found cards. This allows it to jump to specific cards*/
	private static final String index =topFolder+ "WikiDex";
	
	/**List of sets and set sizes for jumping crawling*/
	private static final String sets =topFolder+ "Sets.txt";
	
	/** Missing: [setName]/[card#] */
	private static final String targetURL = "https://www.mtgowikiprice.com/card/";
	
	public WikipriceWebCrawler( ) {
		super(IDENTIFIER);
	}
	//These keep track of the set (by line number in sets) and the card (by url on wikiprice)
	private int currentSet = 0,currentCard;

	
	
	
	private HashMap<String, Integer> CardIDIndex = new HashMap<String, Integer>();;

	private void load() {
		Log.log(Log.WIKI, "Started Loading");
		try {
			Versions.load();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			String cS = SimpleSaveLoad.loadLine(file, 0);
			currentSet = Integer.valueOf(cS);
			currentCard = Integer.valueOf(SimpleSaveLoad.loadLine(file, 1));

			List<String> file = SimpleSaveLoad.load(index);
			for (String s : file) {
				String[] split = s.split("\\|");
				int id = Integer.valueOf(split[1]);
				String name = split[0];
				CardIDIndex.put(name, id);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.log(Log.WIKI, "Finished Loading");
	}

	private int counter;
	private boolean skipWait = false;

	public void getCard(String cards) {
		if(this.cards.containsKey(cards)){
			if(System.currentTimeMillis()-this.cards.get(cards).time< 1000*60*60){
				this.skipWait=true;
				Log.log(Log.WIKI, "Recently scanned " + cards + " not scanning again.");
				return;
			}
		}
		String[] split = cards.split("\\[");
		String set = split[1].replace("]", "");
		int cardId = 0;
		try {
			cardId = CardIDIndex.get(cards);
		} catch (Exception e) {
			Log.log(Log.WIKI, "Card not yet indexed: " + cards);
			skipWait = true;
			return;
		}
		try {
			String stuff = Util.getHTML("https://www.mtgowikiprice.com/card/" + set + "/" + cardId);
			Log.log(Log.WIKI, "Got card " + set + "/" + cardId);
			parse(stuff, cardId);

		} catch (IOException e) {
			e.printStackTrace();
			Log.log(Log.WIKI, "Could not get card " + cards + " by request.");
		}
		return;
	}

	public void getCard() {
		String setName;
		try {
			setName = Versions.versions.get(currentSet)[0];
		} catch (Exception e) {
			currentSet = 0;
			setName = Versions.versions.get(currentSet)[0];
		}
		try {
			int maxCards = Integer.valueOf(Versions.versions.get(currentSet)[1]);
			if (currentCard >= maxCards) {
				currentSet += 1;
				currentCard = 1;
				getCard();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String stuff = Util.getHTML("https://www.mtgowikiprice.com/card/" + setName + "/" + currentCard);
			parse(stuff, currentCard);
			Log.log(Log.WIKI, "Got card " + setName + "/" + currentCard);

		} catch (IOException e) {
			e.printStackTrace();
			Log.log(Log.WIKI, "Could not get card " + setName + "/" + currentCard);
		}
		currentCard += 1;
		save();
	}

	public void save() {
		String[] stuff = new String[] { Integer.toString(currentSet), Integer.toString(currentCard) };
		SimpleSaveLoad.save(file, stuff);
	}

	public boolean cardIsMapped(String card) {
		return CardIDIndex.containsKey(card);
	}

	public HalfTrade parse(String stuff, int id) {
		
		counter++;
		Float sellerPrice = null;
		Float buyerPrice = null;
		String seller = null, buyer = null;

		boolean foundSeller = false;
		boolean foundBuyer = false;

		String[] lines = stuff.split("\n");
		String card = lines[96];
		card = card.replace("&#39;", "'");
		card = card.replace("&quot;", "\"");
		if(card.contains("<span class")){
			 card = lines[150];
				card = card.replace("&#39;", "'");
				card = card.replace("&quot;", "\"");
		}
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains("collection_row sell_row group_boss bot  ") && !foundSeller) {
				if (lines[i + 1].contains("<td class=\" bot_name  \">")) {
					seller = parseName(lines[i + 2]);

					for (int i2 = i; i2 < i + 14; i2++) {
						if (lines[i2].contains("<td class=\" sell_price_round  \">")) {
							sellerPrice = Float.parseFloat(lines[i2 + 1]);
							foundSeller = true;
							break;
						}
					}
				}
			}

			if (lines[i].contains("collection_row buy_row group_boss chain") && !foundBuyer) {
				if (lines[i + 1].contains("<td class=\" bot_name  \">")) {
					buyer = parseName(lines[i + 2]);
					for (int i2 = i; i2 < i + 14; i2++) {
						if (lines[i2].contains("<td class=\" buy_price_round  \">")) {
							buyerPrice = Float.parseFloat(lines[i2 + 1]);
							foundBuyer = true;
							break;
						}
					}
				}
			}

		}
		Log.log(Log.WIKI, card + ":" + buyer + ":" + seller + ":" + buyerPrice + ":" + sellerPrice);
		if (!CardIDIndex.containsKey(card)) {
			String value = card + "|" + id;
			SimpleSaveLoad.append(index, value);
			Log.log(Log.WIKI, "SAVED CARD " + card);
			CardIDIndex.put(value, id);
		}

		HalfTrade deal = new HalfTrade(HalfTrade.WIKIPRICE);
		if (seller != null || buyer != null) {
			counter = 0;
		}
		deal.setBuyer(buyer);
		deal.setSeller(seller);
		deal.buyPrice = buyerPrice;
		deal.sellPrice = sellerPrice;
		deal.card = card;
		deal.setTime();
		add(deal);
		return deal;
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
		this.load();
		while (running) {
			if ( priority.isEmpty()) {
				getCard();
			} else {
				String card = priority.get(0);
				this.getCard(card);
				removeCard(card);
			}
			try {
				if (!skipWait) {
					if (counter > 3) {
						Thread.sleep(30 * 1000);
					} else {
						Thread.sleep(CALL_SPEED * 1000);
					}
				}
				// 1000 milliseconds is one
				// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			skipWait=false;
		}
		running = false;
	}
}
