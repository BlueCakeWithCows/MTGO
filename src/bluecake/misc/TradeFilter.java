package bluecake.misc;

import java.util.List;

public class TradeFilter {
	public Long MAX_AGE;
	public Float MAX_PRICE;
	public Float MIN_PERCENT_GAIN;
	public Float MIN_PROFIT_GAIN;
	public boolean COMPLETE = false;
	public List<String> blacklist, validBuyers, validSellers;

	/** Blacklist and other lists not done */
	public boolean check(CompleteTrade t) {
		return true;
//		
//		if (COMPLETE && !t.isComplete())
//			return false;
//
//		if (MAX_AGE != null && System.currentTimeMillis() < MAX_AGE + t.getCreationTime())
//			return false;
//
//		if (MAX_PRICE != null && (t.getSellerPrice() != null || MAX_PRICE <= t.getSellerPrice() ))
//			return false;
//
//		if (MIN_PROFIT_GAIN != null && (t.getNet() != null || MIN_PROFIT_GAIN <= t.getNet()))
//			return false;
//		
//		if (MIN_PERCENT_GAIN != null && (t.getNetPercent() != null || MIN_PERCENT_GAIN <= t.getNetPercent()))
//			return false;
//
//		return true;

	}

	public boolean check(TradeInfo info) {
		// TODO Auto-generated method stub
		return true;
	}
}
