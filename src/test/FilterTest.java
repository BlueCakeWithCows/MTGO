package test;

import bluecake.Planner;
import bluecake.misc.CompleteTrade;
import bluecake.misc.TradeFilter;
import bluecake.misc.TradeInfo;

public class FilterTest {
	public static void main(String[] args){
		TradeFilter filter = Planner.getDefaultFilter();
		
		TradeInfo seller = new TradeInfo("Wiki");
		seller.setCard("Awesome [NOP]");
		seller.setSell("JBStore2", 1f);
		System.out.println("seller "  +filter.check(seller));
		
		TradeInfo buyer = new TradeInfo("HotlistBot");
		buyer.setBuy("HotListBot", 2f);
		buyer.setCard("Awesome [NOP]");
		System.out.println("buyer "  +filter.check(buyer));
		
		CompleteTrade trade = new CompleteTrade(buyer.card);
		trade.tryAdd(buyer);
		trade.tryAdd(seller);
		System.out.println("percent: " +  trade.getNetPercent() + "vs" + filter.MIN_PERCENT_GAIN);
		System.out.println("overall [should pass] "  +filter.check(trade));
		
		trade.buyer.buyerPrice=1f;
		System.out.println("overall [should fail] "  +filter.check(trade));
		trade.buyer.buyerPrice=1.04f;
		System.out.println("overall [should pass] "  +filter.check(trade));
		
		
	}
}
