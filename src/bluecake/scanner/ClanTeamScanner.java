package bluecake.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import bluecake.misc.TradeInfo;

public class ClanTeamScanner extends WebScanner {
	private final String URL = "http://mtgoclanteam.com/Cards?edition=";
	private WebClient webClient;

	public ClanTeamScanner() {
		super("ClanTeam");
	}

	public static void main(String[] args) {
		ClanTeamScanner scanner = new ClanTeamScanner();
		scanner.load();
		List<TradeInfo> list = scanner.getPage(2);
		for (TradeInfo t : list) {
			scanner.log(t.toString());
		}

	}

	private Float pTD(String s) {
		if (s.length() > 1)
			return Float.valueOf(s);
		return null;
	}

	private TradeInfo readRow(String string, String set2) {
		String[] s = string.split("	");
		String set = set2;
		String name = s[0];
		String card = name + " [" + set + "]";
		Float sel = pTD(s[3]);
		;
		Float buy = pTD(s[2]);
		;
		String buyer = getBuyer(s[4]);
		String seller = getSeller(s[4]);

		TradeInfo info = new TradeInfo(identifier);
		info.setCard(card);
		info.setSell(seller, (Float) sel);
		info.setBuy(buyer, buy);
		return info;
	}

	private String getSeller(String string) {
		String[] split = string.split(",");
		for (String s : split) {
			if (s.contains("CT")) {
				s = s.replace("CT", "ClanTeam");
				s = s.split("\\(")[0].trim();
				return s;
			}
			if (s.contains("All")) {
				return "ClanTeam";
			}
		}
		return null;
	}

	private String getBuyer(String string) {
		String[] split = string.split(",");
		for (String s : split) {
			if (s.contains("CT")) {
				s = s.replace("CT", "ClanTeam");
				s = s.split("\\(")[0].trim();
				return s;
			}
			if (s.contains("all")) {
				return "ClanTeam";
			}
		}
		return null;
	}

	private void load() {
		this.setCardUpdateTime(300);
		running = true;
		log("Loaded");
		webClient = configWebClient();

	}

	@Override
	public void run() {
		try {
			load();

			while (running) {
				for (int i = 0; i < giantString.split(" ").length; i++) {
					addCards(getPage(i));
				}
				sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(e.getMessage());
		} finally {
			running = false;
			webClient.close();
			log("Closing. Goodbye.");
		}
	}

	private void addCards(List<TradeInfo> listToAdd) {
		for (TradeInfo i : listToAdd) {
			this.addTradeInfo(i);
		}
	}

	private List<TradeInfo> getInfo(String text, String set) {
		System.out.println(text);
		String[] lines = text.split(System.lineSeparator());
		int i = 0;
		for (; i < lines.length;) {
			if (lines[i].contains("Card") && lines[i].contains("Rarity")) {
				i++;
				break;
			}
			i++;
		}
		List<TradeInfo> list = new ArrayList<TradeInfo>();
		while (!lines[i].contains("Showing 1")) {

			TradeInfo tInfo = this.readRow(lines[i], set);
			list.add(tInfo);
			log(tInfo.toString());
			i++;
		}

		log("Done parsing DojoBot: " + URL + set);
		return list;
	}

	private String giantString = "EMN EMA SOI OGW PZ1 BFZ EXP ORI MM2 TPR DTK FRF KTK M15 VMA JOU BNG THS M14 DGM GTC RTR M13 AVR DKA ISD";

	private List<TradeInfo> getPage(int i) {
		HtmlPage page = null;
		try {
			String set = giantString.split(" ")[i];
			log("Grabbing Page: " + URL + set);
			page = webClient.getPage(URL + set);
			webClient.waitForBackgroundJavaScript(30000);

			List<TradeInfo> list = getInfo(page.asText(), set);
			return list;
			// log("Running Javascript.");
			// HtmlInput a = (HtmlInput) page.getElementById("TextCardName");
			// a.setValueAttribute("");
			// a.focus();
			// page = (HtmlPage) a.type('\n');
			// webClient.waitForBackgroundJavaScript(30000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private WebClient configWebClient() {
		WebClient webClient = null;
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		log("Web Client Initialized");
		return webClient;
	}

	private void sleep() {
		try {
			Thread.sleep(this.getCardUpdateTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
