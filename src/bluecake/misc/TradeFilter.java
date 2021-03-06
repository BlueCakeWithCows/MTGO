package bluecake.misc;

import java.util.List;

public class TradeFilter {
	public Long MAX_AGE;
	public Float MAX_PRICE;
	public Float MIN_PERCENT_GAIN;
	public Float MIN_PROFIT_GAIN;
	public boolean COMPLETE = false;
	public List<String> cardBlacklist, validBuyers, validSellers;
	public Long TIME_BETWEEN_IDENTICAL_CARDS;

	/** Blacklist and other lists not done */
	public boolean check(CompleteTrade t) {
		if (isBlackListed(t.card)) {
			return false;
		}
		if (COMPLETE && !t.isComplete())
			return false;
		if (MAX_AGE != null && System.currentTimeMillis() -MAX_AGE>  t.getCreationTime())
			return false;
		if (MAX_PRICE != null && (t.getSellerPrice() != null || MAX_PRICE <= t.getSellerPrice()))
			return false;
		if(validBuyers!=null && !validBuyers.contains(t.getBuyer())){
			return false;
		}
		if(validSellers!=null && !validSellers.contains(t.getSeller())){
			return false;
		}
		if(t.getNet() == null || t.getNetPercent()==null)
			return false;
		if (MIN_PROFIT_GAIN != null && (MIN_PROFIT_GAIN >= t.getNet()))
			return false;
		
		if (MIN_PERCENT_GAIN != null && (MIN_PERCENT_GAIN >= t.getNetPercent()))
			return false;
		return true;
	}

	public boolean check(TradeInfo info) {
		if (isBlackListed(info.card)) {
			return false;
		}
		if (MAX_AGE != null && System.currentTimeMillis() - MAX_AGE> info.getCreationTime())
			return false;
		if (validBuyers == null || validBuyers.contains(info.buyer)) {
			return true;
		}
		if (validSellers == null || validSellers.contains(info.seller)) {
			return true;
		}
		return false;

	}
	
	public boolean isBlackListed(String card){
		if(cardBlacklist ==null)
			return false;
		for(String s: cardBlacklist){
			if(card.toLowerCase().contains(s.toLowerCase()))
				return true;
		}
		return false;
	}

	public boolean checkAge(Long time) {
		if (this.TIME_BETWEEN_IDENTICAL_CARDS != null
				&& System.currentTimeMillis() < this.TIME_BETWEEN_IDENTICAL_CARDS + time)
			return false;
		return true;

	}
}
