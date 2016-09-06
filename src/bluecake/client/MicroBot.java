package bluecake.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import bluecake.client.Locations.ImageMark;
import bluecake.client.MTGOException.MTGOBotOfflineException;
import bluecake.client.MTGOException.MTGOCannotFindCardException;
import bluecake.client.MTGOException.MTGORipOffException;
import bluecake.client.MTGOException.MTGOTradeFailedException;
import bluecake.util.SimpleSaveLoad;

public class MicroBot {
	public MyRobot rob;
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

	private int countNumberOfBots() {
		String string = scanner.scan(rob.getScreen(Locations.NumberOfTradePeople));
		string = string.replaceAll("Posts", "");
		string = string.trim();

		int count = Integer.valueOf(string);
		return count;
	}

	public void clickDeleteAndType(Point l, String string) {
		rob.moveTo(l);
		rob.click();
		rob.press(KeyEvent.VK_BACK_SPACE);
		rob.type(string);
		rob.enter();
	}

	private static final int TRADE_TAB = 1;

	public void gotoTab(int tab) {
		if (tab == getTab())
			return;
		switch (tab) {
		case TRADE_TAB:
			rob.moveTo(Locations.TRADE_BUTTON);
			break;
		}
		rob.click();
		waitTillTabLoads(tab);
	}

	private int getTab() {
		if (Locations.TRADE_BUTTON_IMAGEMARK.getMatch(rob.getRobot()))
			return TRADE_TAB;
		return 0;
	}

	private void waitTillTabLoads(int tab) {
		ImageMark mark = null;
		switch (tab) {
		case TRADE_TAB:
			mark = Locations.TRADE_BUTTON_IMAGEMARK;
			break;
		}

		while (!mark.getMatch(rob.getRobot())) {
			sleep(100);
		}
		sleep(200);
	}

	public void confirmBotOnline(String bot) throws MTGOBotOfflineException {
		gotoTab(TRADE_TAB);
		clickDeleteAndType(Locations.TRADE_SEARCH_BUTTON, bot);
		for (int i = 0; i < 5; i++) {
			if (0 != this.countNumberOfBots())
				return;
			sleep(20 + i * 10);
			rob.enter();
		}

		throw new MTGOException.MTGOBotOfflineException();

	}

	private void acceptTrade() throws MTGOTradeFailedException {
		rob.moveTo(Locations.INTRADE_CONFIRM);
		rob.click();
		this.sleep(11000);
		rob.click();
		this.sleep(1500);
		if (this.inTradeWindow()) {
			this.exitScreen();
			throw new MTGOException.MTGOTradeFailedException();
		}
		if (tradeCancelled()) {
			rob.enter();
			throw new MTGOException.MTGOTradeFailedException();
		}
		rob.enter();
		rob.moveTo(300, 300);
		rob.click();
		rob.escape();
		rob.enter();
	}

	public void moveAndClick(Point p) {
		rob.moveTo(p);
		rob.click();
	}

	public boolean inTradeWindow() {
		return scanner.scanFor(rob.getScreen(Locations.TradeCorner), "Trade");
	}

	private static final int TRADE_STATUS_IN_TRADE = 4, TRADE_STATUS_PENDING = 3, TRADE_STATUS_CANCELLED = 2,
			TRADE_STATUS_NONE = 0;

	private int getTradeConnectingStatus() {
		if (inTradeWindow())
			return TRADE_STATUS_IN_TRADE;

		BufferedImage img = rob.getScreen(Locations.TradeBox);
		String string = scanner.scan(img);

		if (string.contains("Trade Invitation"))
			return TRADE_STATUS_PENDING;

		if (string.contains("Trade Canceled"))
			return TRADE_STATUS_CANCELLED;

		return TRADE_STATUS_NONE;
	}

	public void connectToBot(String bot) throws MTGOBotOfflineException, MTGOTradeFailedException {
		this.gotoTab(TRADE_TAB);
		this.confirmBotOnline(bot);

		for (int i = 0; i < 30; i++) {
			this.moveAndClick(Locations.TRADE_SEARCH_BUTTON);
			rob.enter();
			this.moveAndClick(Locations.TRADE_TRADE_BUTTON);
			this.sleep(200);
			int status = getTradeConnectingStatus();
			System.out.println("Trade Status: " + status);
			if (status != MicroBot.TRADE_STATUS_NONE) {
				while (status == TRADE_STATUS_PENDING) {
					status = getTradeConnectingStatus();
					this.sleep(200);
				}
				if (TRADE_STATUS_CANCELLED == status) {
					rob.enter();
				} else if (status == TRADE_STATUS_IN_TRADE) {
					return;
				}
			}
		}
		throw new MTGOException.MTGOTradeFailedException();
	}

	public String getName(String card) {
		return card.split("\\[")[0].trim();
	}

	public String getSet(String card) {
		return card.split("\\[")[1].replace("]", "").trim();
	}

	public Object[] scrapeRows(String card, Rectangle rect) {
		int i = 0;
		String name = getName(card);
		String set = getSet(card);
		String result = null;
		for (; i < 30; i++) {
			Rectangle rectangle = rectangleTrans(i, rect);
			String row = scanner.scan(rob.getScreen(rectangle));
			if (row.contains(name) && row.contains(set)) {
				result = row;
				break;
			}
			if (row.equals(""))
				break;
		}
		return new Object[] { i, result };
	}

	private Rectangle rectangleTrans(int i, Rectangle base) {
		Rectangle r = new Rectangle(base);
		r.translate(0, r.height * i);
		return r;
	}

	public Object[] buyCardIfUnder(String card, double max) throws MTGOTradeFailedException, MTGORipOffException {
		String name = getName(card);
		searchForCard(name);
		this.sleep(1000);
		int i = 0;
		String result = null;

		Object[] ob = scrapeRows(card, Locations.TradeCardRowTemplate);
		i = (int) ob[0];
		result = (String) ob[1];
		try {
			SimpleSaveLoad.append("test", result);
		} catch (Exception e) {

		}
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

	public String readTradeRow(BufferedImage re) {
		String sc = scanner.scan(re).trim();
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
//		for (; i < estimation; i++) {
//			rob.doubleClick();
//			sleep(400);
//		}
		sleep(600);
		String textScan = scanner.scan(rob.getScreen(Locations.ChatArea));
		while (textScan.contains("return") || textScan.contains("remove") || textScan.contains("too many")) {
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
		this.sleep(2000);
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
		if (obs[1] == null)
			i = 0;
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
		this.setBuyBinderToActive();
		rob.moveTo(Locations.COLLECTION_OTHER);
		rob.click();
		rob.moveTo(Locations.COLLECTION_CLEAR_FILTER);
		rob.click();
		rob.moveTo(Locations.COLLECTION_SEARCH_BAR);
		rob.click();
		rob.press(KeyEvent.VK_BACK_SPACE);
		rob.type("ticket");
		rob.enter();
		Point p = new Point(Locations.CollectionCardRowTemplate.x + 30,
				(int) ((0 + .5) * Locations.CollectionCardRowTemplate.getHeight()
						+ Locations.CollectionCardRowTemplate.y));
		rob.moveTo(p);
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
		for (int i = 0; i < 8; i++) {
			rob.moveTo(Locations.COLLECTION_BINDER_OPTIONS.x - 40 + i * 10, Locations.COLLECTION_BINDER_OPTIONS.y);
			rob.click();
		}
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

	public void log(String s) {
		try {
			SimpleSaveLoad.append("logs", s);
		} catch (Exception e) {
		}

		System.out.println(s);
	}
}
