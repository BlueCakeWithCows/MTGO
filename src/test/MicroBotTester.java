package test;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import bluecake.client.ImageScanner;
import bluecake.client.Locations;
import bluecake.client.MTGOException.MTGOBotOfflineException;
import bluecake.client.MTGOException.MTGORipOffException;
import bluecake.client.MTGOException.MTGOTradeFailedException;
import bluecake.client.MicroBot;
import bluecake.util.SimpleSaveLoad;

public class MicroBotTester {
	static MicroBot bot;
	static ImageScanner scanner;

	public static void main(String[] args)
			throws IOException, InterruptedException, MTGOTradeFailedException, MTGORipOffException {
		bot = new MicroBot();
		scanner = new ImageScanner();
		scanner.init();
		long time = System.currentTimeMillis();
		BufferedImage img = bot.rob.getScreen(Locations.TradeBox);
		System.out.println((double)(System.currentTimeMillis()-time)/1000d);
		String string = scanner.scan(img);	
		System.out.println((double)(System.currentTimeMillis()-time)/1000d);
	}

	private static void ImageTest() throws InterruptedException, IOException {
		Thread.sleep(1000);

		String url = "testImages/spec/tradetest.png";
		File file = new File(url);
		BufferedImage io = ImageIO.read(file);
		System.out.println(bot.readTradeRow(io));
		String card = "Rattlechains [SOI]";
		System.out.println(bot.getName(card));
		System.out.println(bot.getSet(card));

		System.out.println(scanner.scan(io));
	}

	private static void FillBuddies() throws IOException, InterruptedException {
		Thread.sleep(2000);
		List<String> sList = SimpleSaveLoad.load("filter/WhiteList.txt");
		List<String> bList = SimpleSaveLoad.load("filter/WhiteListBuyer.txt");
		sList.addAll(bList);
		for (String s : sList) {
			bot.rob.click();
			bot.rob.type(s);
			bot.rob.enter();
		}
	}

	private static void BuyCardTest() throws MTGOTradeFailedException, MTGORipOffException {
		bot.buyCardIfUnder("Oath of Liliana [EMN]", 4);
	}

	// Weird behavior if binder is already active
	private static void ConnectionTest() {

		try {
			bot.connectToBot("HotListBot");
			System.out.println("Sucess! 1 of 2");
			Toolkit.getDefaultToolkit().beep();
			Toolkit.getDefaultToolkit().beep();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failure");
		}
	}

	private static void ActiveBinderTest() {
		bot.setBuyBinderToActive();
		bot.setSellBinderToActive();
	}

	private static void ConfirmTest() {
		try {
			bot.confirmBotOnline("HotListBot");
			System.out.println("Sucess");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failure");
		}

		try {
			bot.confirmBotOnline("rockySRocks");
			System.out.println("Failure");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Sucess");
		}
	}

	private static void TixTest() throws IOException {

		int tix = bot.countTicketsInBuyBinder();
		Toolkit.getDefaultToolkit().beep();
		System.out.println(tix);
		SimpleSaveLoad.createFileIfNoExist("test", "");
		SimpleSaveLoad.addOrReplace("test", "tickets_to_buy", String.valueOf(tix));

	}
}
