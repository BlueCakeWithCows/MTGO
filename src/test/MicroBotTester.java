package test;

import java.awt.Toolkit;
import java.io.IOException;

import bluecake.client.MTGOException.MTGOBotOfflineException;
import bluecake.client.MTGOException.MTGORipOffException;
import bluecake.client.MTGOException.MTGOTradeFailedException;
import bluecake.client.MicroBot;
import bluecake.util.SimpleSaveLoad;

public class MicroBotTester {
	static MicroBot bot;

	public static void main(String[] args) throws IOException, InterruptedException, MTGOTradeFailedException, MTGORipOffException {
		bot = new MicroBot();
		Thread.sleep(1000);
		BuyCardTest();

	}
	
	private static void BuyCardTest() throws MTGOTradeFailedException, MTGORipOffException{
		bot.buyCardIfUnder("Oath of Liliana [EMN]", 4);
	}

	// Weird behavior if binder is already active
	private static void ConnectionTest()   {
		
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
