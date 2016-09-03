package bluecake.scanner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import bluecake.misc.TradeInfo;

public class HotListWebScanner extends WebScanner {
	private final String URL = "http://www.mtgotraders.com/hotlist/";
	private WebClient webClient;

	public HotListWebScanner() {
		super("HotList");
	}

	private void load() {
		this.setCardUpdateTime(300);
		running = true;
		log("Loaded");
	}

	@Override
	public void run() {
		try {
			load();
			webClient = configWebClient();

			while (running) {
				HtmlPage page = getPage();
				try {
					if (getPage() != null) {
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
		String[] lines = text.split(System.lineSeparator());
		int i = 0;
		for (; i < lines.length;) {
			if (lines[i].contains("Price")) {
				break;
			}
			i++;
		}
		List<TradeInfo> list = new ArrayList<TradeInfo>();
		while (true) {
			i++;
			String[] split = lines[i].split("\t");
			if (split.length != 4)
				break;

			TradeInfo tInfo = new TradeInfo(identifier);
			tInfo.setCard(split[1] + " [" + split[0] + "]");
			tInfo.setBuy("HotListBot", Float.valueOf(split[3]));

			list.add(tInfo);
			log(tInfo.toString());

		}

		log("Done parsing hotlist: " + URL);
		return list;
	}

	private HtmlPage getPage() {
		HtmlPage page;
		try {
			log("Grabbing Page: " + URL);
			page = webClient.getPage(URL);

			webClient.waitForBackgroundJavaScript(30000);
			log("Running Javascript.");
			page.executeJavaScript("filterNonFoils()");

			HtmlAnchor div = page.getFirstByXPath(
					"//*[@id=\"mainContent\"]/div[2]/div[1]/div[2]/div[4]/div[1]/span[2]/span/ul/li[5]/a");

			page = div.click();

			page.getFirstByXPath("//*[@id=\"toolbar\"]/label[3]/input");
			page.getElementById("toolbar").click();
			return page;
		} catch (FailingHttpStatusCodeException e) {
			gui.log(e.getMessage());
		} catch (MalformedURLException e) {
			gui.log(e.getMessage());
		} catch (IOException e) {
			gui.log(e.getMessage());
		}
		gui.log("Failed to load page: " + URL);
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
