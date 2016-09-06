package bluecake.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import bluecake.misc.CompleteTrade;
import bluecake.misc.TradeInfo;

public class BotManager {
	private MicroBot bot;
	int tickets;
	double ticketsGainedThisRun = 0;
	int ticketsWithdrawn = 0;

	/** This represents a perfect run */
	public BotManager() {
		bot = new MicroBot();
	}

	public void doTrade(TempClient client) throws MTGOException.MTGOConnectionFailedException {
		
		bot.setBuyBinderToActive();
		bot.countTicketsInBuyBinder();
		
		CompleteTrade trade = getTrade();
		
		while (trade == null) {
			TempClient.sleep(30);
			trade = getTrade();
		}
		double buyErrorMargin = trade.seller.sellerPrice + trade.getNet();
		double profit = 0, net = 0, expense = 0;
		int qty = 0;
		
		try {
			bot.confirmBotOnline(trade.getBuyer());
		} catch (MTGOException.MTGOBotOfflineException e) {
			log("One of bots are offline. Cancelling " + trade.card);
			return;
		}
		
		try {
			bot.connectToBot(trade.getSeller());
			log("Connected to Buyer");
			Object[] qtyAndCost = bot.buyCardIfUnder(trade.card, buyErrorMargin);
			expense = (double) qtyAndCost[1];
			qty = (int) qtyAndCost[0];
			log("Bought " + trade.card + " x" + qty + " for a total of " + expense);

			try {
				bot.setSellBinderToActive();
				bot.addCardToSellBinder(trade.card);
				bot.connectToBot("HotListBot");//trade.getBuyer()
				profit = bot.sellAllNoMatterPrice((int) (expense + 2));

				log("Sold inventory for " + profit);

				ticketsGainedThisRun += net;
				tickets = bot.countTicketsInBuyBinder();
			} catch (MTGOException.MTGOCannotFindCardException e) {
				log("Cannot find card inside collection");
			} catch (MTGOException.MTGOTradeFailedException e) {
				log("Trade Unexpected Exit. No sell?");
			}
		} catch (MTGOException.MTGORipOffException e) {
			log("RIPOFF " + trade.card + " x" + trade.buyer);
		} catch (MTGOException.MTGOTradeFailedException e) {
			log("Trade Unexpected Exit. No purchase?");
		} catch (MTGOException.MTGOBotOfflineException e) {
			log("One of bots are offline. Cancelling trade late." + trade.card);
		}

		net = profit - expense;
		log("Current Tix " + tickets);
		log("GAIN: [Run: " + net + "] [Overall: " + ticketsGainedThisRun + "]");

		bot.addTixToAccount((int) ticketsGainedThisRun - ticketsWithdrawn);

	}

	private void log(String s) {
		System.out.println(s);
	}

	private CompleteTrade getTrade() {
		CompleteTrade object = null;
		Socket socket;
		ObjectInputStream stream;
		try {
			socket = new Socket("69.62.148.202", 4444);
			stream = new ObjectInputStream(socket.getInputStream());
			object = (CompleteTrade) stream.readObject();
			socket.close();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return object;

	}
}
