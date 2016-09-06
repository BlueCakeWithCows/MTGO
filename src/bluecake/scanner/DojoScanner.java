package bluecake.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import bluecake.misc.TradeInfo;

public class DojoScanner extends WebScanner {
	private final String URL = "https://www.dojotradebots.com/pricelist";
	private WebClient webClient;

	public DojoScanner() {
		super("Dojo");
	}



	private Float pTD(String s) {
		if (s.length() > 1)
			return Float.valueOf(s);
		return null;
	}

	private TradeInfo readRow(String string) {
		String[] s = string.split("	");
		String set = s[0];
		String name = s[1];
		String card = name + " [" + set + "]";
		Float sel = pTD(s[3]);
		;
		Float buy = pTD(s[4]);
		;
		String buyer = getBuyer(s[5]);
		String seller = getSeller(s[5]);

		TradeInfo info = new TradeInfo(identifier);
		info.setCard(card);
		info.setSell(seller, (Float) sel);
		info.setBuy(buyer, buy);
		return info;
	}

	private String getSeller(String string) {
		String[] split = string.split(",");
		for (String s : split) {
			if (s.contains("DTS")) {
				s = s.replace("DTS", "_DojoTradeSelling");
				s = s.split("\\[")[0];
				return s;
			}
		}
		return null;
	}

	private String getBuyer(String string) {
		String[] split = string.split(",");
		for (String s : split) {
			if (s.contains("DTB")) {
				s = s.replace("DTB", "_DojoTradeBuying");
				s = s.split("\\[")[0];
				return s;
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
				HtmlPage page = getPage();
				try {
					if (page != null) {
						List<TradeInfo> tInfoList = getInfo(page.asText());
						addCards(tInfoList);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log(e.getMessage());
				}
				sleep();
			}

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

	private List<TradeInfo> getInfo(String text) {
		System.out.println(text);
		String[] lines = text.split(System.lineSeparator());
		int i = 0;
		for (; i < lines.length;) {
			if (lines[i].contains("Set") && lines[i].contains("Rarity")) {
				i++;
				break;
			}
			i++;
		}
		List<TradeInfo> list = new ArrayList<TradeInfo>();
		while(lines[i].length()>0) {
			TradeInfo tInfo = this.readRow(lines[i]);
			list.add(tInfo);
			log(tInfo.toString());
			i++;
		}

		log("Done parsing DojoBot: " + URL);
		return list;
	}

	private HtmlPage getPage() {
		HtmlPage page;
		try {
			log("Grabbing Page: " + URL);
			page = webClient.getPage(URL);

			webClient.waitForBackgroundJavaScript(30000);
			log("Running Javascript.");
			HtmlInput a = (HtmlInput) page.getElementById("TextCardName");
			a.setValueAttribute("");
			a.focus();
			page = (HtmlPage) a.type('\n');
			webClient.waitForBackgroundJavaScript(30000);
			return page;
		} catch (Exception e) {

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
