package bluecake.client;

import bluecake.misc.CompleteTrade;
import bluecake.misc.TradeInfo;

public class BotManager {
	private MicroBot bot;
	int tickets;
	double ticketsGainedThisRun = 0;
	int ticketsWithdrawn = 0;

	/** This represents a perfect run */

	public void doTrade(TempClient client) throws MTGOException.MTGOConnectionFailedException {

		bot.countTicketsInBuyBinder();

		CompleteTrade trade = getTrade();
		double buyErrorMargin = trade.seller.sellerPrice + trade.getNet();
		double profit = 0, net = 0, expense = 0;
		int qty = 0;

		try {
			bot.confirmBotOnline(trade.getBuyer());
			bot.confirmBotOnline(trade.getSeller());
			try {
				bot.connectToBot(trade.getSeller());
				Object[] qtyAndCost = bot.buyCardIfUnder(trade.card, buyErrorMargin);
				expense = (double) qtyAndCost[1];
				qty = (int) qtyAndCost[0];
				log("Bought " + trade.card + " x" + qty + " for a total of " + expense);

				try{
				bot.addCardToSellBinder(trade.card);
				bot.setSellBinderToActive();
				profit = bot.sellAllNoMatterPrice();

				log("Sold inventory for " + profit);

				ticketsGainedThisRun += net;
				tickets = bot.countTicketsInBuyBinder();
				}catch(MTGOException.MTGOCannotFindCardException e){
						log("Cannot find card inside collection");
				}catch(MTGOException.MTGOTradeFailedException e){
					log("Trade Unexpected Exit. No sell?");
				}	
			} catch (MTGOException.MTGORipOffException e) {
				log("RIPOFF " + trade.card + " x" + trade.buyer);
			}catch(MTGOException.MTGOCannotFindCardException e){
					log("Cannot find card within bot inventory");
			}catch(MTGOException.MTGOTradeFailedException e){
				log("Trade Unexpected Exit. No purchase?");
			}
			
			net = profit - expense;
			log("Current Tix " + tickets);
			log("GAIN: [Run: " + net + "] [Overall: " + ticketsGainedThisRun + "]");

			bot.addTixToAccount((int) ticketsGainedThisRun - ticketsWithdrawn);
		} catch (MTGOException.MTGOBotOfflineException e) {
			log("One of bots are offline. Cancelling " + trade.card);
		}
	}

	private void log(String s) {

	}

	private CompleteTrade getTrade() {
		return null;
	}
}
