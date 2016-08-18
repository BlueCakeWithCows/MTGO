package Scanner;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import bluecake.HalfTrade;
import bluecake.Planner;
import bluecake.util.Log;

public class HotlistScanner extends CardScanner {
	public HotlistScanner(Planner p) {
		super(p);
		
	}

	public static int SLEEP_TIME = 300;

	@Override
	public void run() {
		running = true;
		WebClient webClient = configWebClient();
		Map<String, HalfTrade> map = Collections.synchronizedMap(new HashMap<String, HalfTrade>());
		while (running) {

			try {
				String html = "http://www.mtgotraders.com/hotlist/#/";
				Log.log(Log.HOT, "Loading Page: " + html);
				HtmlPage page = webClient.getPage(html);
				webClient.waitForBackgroundJavaScript(30000);
				Log.log(Log.HOT, "Page Loaded. Running Javascript");
				page.executeJavaScript("filterNonFoils()");

				HtmlAnchor div = page.getFirstByXPath(
						"//*[@id=\"mainContent\"]/div[2]/div[1]/div[2]/div[4]/div[1]/span[2]/span/ul/li[5]/a");

				page = div.click();

				page.getFirstByXPath("//*[@id=\"toolbar\"]/label[3]/input");
				page.getElementById("toolbar").click();
				Log.log(Log.HOT, "Javascript Done. Parsing Page." + html);

				String text = page.asText();
				String[] lines = text.split(System.lineSeparator());
				int i = 0;
				for (; i < lines.length + 1;) { // Deliberate Crash Here incase
												// something fucked up happens
					if (lines[i].contains("Price")) {
						break;
					}
					i++;
				}
				while (true) {
					i++;
					String[] split = lines[i].split("\t");
					if (split.length != 4)
						break;
					String name = split[1] + " [" + split[0] + "]";
					float buyPrice = Float.valueOf(split[3]);
					String[] buyer = { "HotListBot", "HotListBot2" };
					HalfTrade d = new HalfTrade(HalfTrade.HOTLISTBOT);
					d.card = name;
					d.buyer = buyer;
					d.buyPrice = buyPrice;
					d.setTime();
					this.add(d);
					Log.log(Log.HOT, "Got Card "+name+ " for "+ buyPrice);
					
				}
				this.cards = map;
				Log.log(Log.HOT, "Done parsing hotlist." + html);


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.setLastUpdate(System.currentTimeMillis());
			sleep();
		}
		running = false;
		webClient.close();
	}

	private WebClient configWebClient() {
		WebClient webClient = null;
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		Log.log(Log.HOT, "Web Client Initialized");
		return webClient;
	}

	private void sleep() {
		try {
			Thread.sleep(SLEEP_TIME * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
