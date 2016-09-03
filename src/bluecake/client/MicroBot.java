package bluecake.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.gargoylesoftware.htmlunit.javascript.host.Location;

import bluecake.client.MTGOException.MTGOBotOfflineException;
import bluecake.client.MTGOException.MTGOCannotFindCardException;
import bluecake.client.MTGOException.MTGORipOffException;
import bluecake.client.MTGOException.MTGOTradeFailedException;

public class MicroBot {
	private MyRobot rob;
	private ImageScanner scanner;

	public MicroBot() {
		rob = new MyRobot();
		scanner = new ImageScanner();
		scanner.init();
	}

	public void setBuyBinderToActive() {
		rob.moveTo(Locations.COLLECTION_BUTTON);
		rob.click();
		sleep(3000);

		rob.moveTo(Locations.BUDGET_BINDER);
		rob.click();
		activateBinder();
	}

	public void confirmBotOnline(String bot) throws MTGOBotOfflineException {
		rob.moveTo(Locations.TRADE_BUTTON);

		rob.click();
		this.sleep(3000);
		rob.moveTo(Locations.TRADE_SEARCH_BUTTON);
		rob.click();
		rob.press(KeyEvent.VK_BACK_SPACE);
		rob.type(bot);
		rob.enter();
		try {
			for (int i = 0; i < 3; i++) {
				rob.moveTo(Locations.BUDDIES_BUTTON);
				rob.click();
				rob.moveTo(Locations.TRADE_BUTTON);
				this.sleep(3000);
				String string = scanner.scan(rob.getScreen(Locations.NumberOfTradePeople));
				string = string.replaceAll("Posts", "");
				string = string.trim();

				int count = Integer.valueOf(string);
				if (count > 0)
					return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new MTGOException.MTGOBotOfflineException();

	}

	private void acceptTrade() throws MTGOTradeFailedException {
		rob.moveTo(Locations.INTRADE_CONFIRM);
		rob.click();
		this.sleep(2000);
		rob.click();
		this.sleep(1500);

		if (tradeCancelled()) {
			rob.enter();
			throw new MTGOException.MTGOTradeFailedException();
		}
		rob.enter();
	}

	public void connectToBot(String bot) throws MTGOBotOfflineException {
		this.confirmBotOnline(bot);
		rob.moveTo(Locations.TRADE_TRADE_BUTTON);
		rob.click();
		boolean loop = true;
		while (loop) {
			rob.moveTo(Locations.TRADE_TRADE_BUTTON);
			rob.quickClick();
			this.sleep(500);
			while (loop) {
				BufferedImage img = rob.getScreen(Locations.TradeBox);

				boolean invit = scanner.scanFor(img, "Trade Invitation");
				boolean cancel = scanner.scanFor(img, "Trade Canceled");
				if (!invit && !cancel)
					loop = false;
				else if (cancel) {
					rob.enter();
					break;
				}
			}
		}
	}

	public String getName(String card) {
		return card.split("\\[")[0].trim();
	}

	public String getSet(String card) {
		return card.split("\\[")[1].replace("]", "").trim();
	}

	private Object[] scrapeRows(String card, Rectangle rect) {
		int i = 0;
		String name = getName(card);
		String set = getSet(card);
		String result = null;
		for (; i < 30; i++) {
			String row = readTradeRow(i, rect);
			System.out.println(row);
			if (row.contains(name) && row.contains(set)) {
				result = row;
				System.out.println("success");
				break;
			}
			if (row.equals(""))
				break;
		}
		return new Object[] { i, result };
	}

	public Object[] buyCardIfUnder(String card, double max) throws MTGOTradeFailedException, MTGORipOffException {
		String name = getName(card);
		String set = getSet(card);
		searchForCard(name);
		this.sleep(1000);
		int i = 0;
		String result = null;

		Object[] ob = scrapeRows(card, Locations.TradeCardRowTemplate);
		i = (int) ob[0];
		result = (String) ob[1];
		if (result == null) {
			exitScreen();
			throw new MTGOTradeFailedException();

		}

		int qty = Integer.valueOf(result.split(" ")[0]);
		qty = Math.min(qty, 4);
		int x = Locations.TradeCardRowTemplate.x + 20, y = Locations.TradeCardRowTemplate.y;
		y += (i + .5f) * Locations.TradeCardRowTemplate.height;
		rob.moveTo(x, y);
		for (int i2 = 0; i2 < qty; i2++) {
			rob.doubleClick();
			this.sleep(400);
		}
		this.sleep(1200);

		String needToParseTix = scanner.scan(rob.getScreen(Locations.Intrade_Ticket_Check));
		needToParseTix = needToParseTix.split("/")[0].trim();
		double expense = 0;
		if (needToParseTix.length() == 0)
			expense = 0;
		else
			expense = Double.valueOf(needToParseTix);
		if (expense > Math.ceil(max * qty)) {
			exitScreen();
			throw new MTGOException.MTGORipOffException();
		}
		acceptTrade();
		return new Object[] { qty, expense };
	}

	private void exitScreen() {
		rob.moveTo(Locations.EXIT);
		rob.click();
	}

	private boolean tradeCancelled() {
		return scanner.scanFor(rob.getScreen(Locations.TradeBox), "Trade Canceled");

	}

	private void searchForCard(String card) {
		rob.moveTo(Locations.INTRADE_RESET_FILTER);
		rob.click();
		rob.moveTo(Locations.INTRADE_SEARCH_BAR);
		rob.click();
		rob.press(KeyEvent.VK_BACK_SPACE);
		rob.type(card);
		rob.enter();

	}

	private String readTradeRow(int i, Rectangle re) {
		Rectangle r = re;
		r.translate(0, r.height * i);
		BufferedImage img = rob.getScreen(r);
		String sc = scanner.scan(img).trim();
		return sc;
	}

	public int sellAllNoMatterPrice(int estimation) throws MTGOTradeFailedException {
		this.sleep(10000);
		rob.moveTo(Locations.INTRADE_OTHER);
		rob.click();
		rob.moveTo(Locations.INTRADE_RESET_FILTER);
		rob.click();
		rob.moveTo(Locations.TRADE_SEARCH_BUTTON);
		rob.click();
		rob.press(KeyEvent.VK_BACK_SPACE);
		rob.type("ticket");
		rob.enter();
		int x = Locations.TradeCardRowTemplate.x + 20, y = Locations.TradeCardRowTemplate.y;
		y += (0 + .5f) * Locations.TradeCardRowTemplate.height;
		int i = 0;
		rob.moveTo(x, y);
		for (; i < estimation; i++) {
			rob.doubleClick();
			sleep(400);
		}
		sleep(600);
		String textScan = scanner.scan(rob.getScreen(Locations.ChatArea));
		while(textScan.contains("return") || textScan.contains("remove") || textScan.contains("too many")){
			i--;
			rob.moveTo(Locations.MyTakenItem);
			rob.doubleClick();
			sleep(1000);
			textScan = scanner.scan(rob.getScreen(Locations.ChatArea));
		}
		acceptTrade();
		return i;
	}

	public int countTicketsInBuyBinder() {
		rob.moveTo(Locations.BUDGET_BINDER);
		rob.click();
		BufferedImage img = rob.getScreen(Locations.NumberOfThingsInBinder);
		String scanned = scanner.scan(img);
		try {
			scanned = scanned.trim();
			System.out.println(scanned);
			return Integer.valueOf(scanned);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public void addCardToSellBinder(String card) throws MTGOCannotFindCardException {
		String name = getName(card);
		String set = getSet(card);
		rob.moveTo(Locations.COLLECTION_BUTTON);
		rob.click();
		this.sleep(1000);
		rob.moveTo(Locations.SELL_BINDER);
		rob.click();
		rob.moveTo(Locations.COLLECTION_CARDS);
		rob.click();
		rob.moveTo(Locations.COLLECTION_CLEAR_FILTER);
		rob.click();
		rob.moveTo(Locations.COLLECTION_SEARCH_BAR);
		rob.click();
		rob.press(KeyEvent.VK_BACK_SPACE);
		rob.type(name);
		rob.enter();
		Object[] obs = scrapeRows(card, Locations.CollectionCardRowTemplate);
		int i = (int) obs[0];
		if(obs[1]==null)
			throw new MTGOException.MTGOCannotFindCardException();
		Point p = new Point(Locations.CollectionCardRowTemplate.x + 30,
				(int) ((i + .5) * Locations.CollectionCardRowTemplate.getHeight()
						+ Locations.CollectionCardRowTemplate.y));
		rob.moveTo(p);
		for (int i2 = 0; i2 < 4; i2++) {
			rob.doubleClick();
			this.sleep(400);
		}
	}

	public void addTixToAccount(int tix) {
		rob.moveTo(Locations.COLLECTION_BUTTON);
		rob.click();
		this.sleep(1000);
		rob.moveTo(Locations.COLLECTION_OTHER);
		rob.click();
		rob.moveTo(Locations.COLLECTION_CLEAR_FILTER);
		rob.click();
		rob.moveTo(Locations.COLLECTION_SEARCH_BAR);
		rob.click();
		rob.press(KeyEvent.VK_BACK_SPACE);
		rob.type("ticket");
		rob.enter();
		rob.moveTo(Locations.Collections_First);
		for (int i = 0; i < tix; i++) {
			rob.doubleClick();
			sleep(400);
		}
	}

	public void setSellBinderToActive() {
		rob.moveTo(Locations.COLLECTION_BUTTON);
		rob.click();
		sleep(3000);

		rob.moveTo(Locations.SELL_BINDER);
		rob.click();
		activateBinder();

	}

	private void activateBinder() {
		rob.moveTo(Locations.COLLECTION_BINDER_OPTIONS);
		rob.click();
		rob.moveTo(Locations.COLLECTION_BINDER_OPTIONS_ACTIVE);
		rob.click();
		rob.moveTo(Locations.COLLECTION_BINDER_OPTIONS_OK);
		rob.click();
	}

	public void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
