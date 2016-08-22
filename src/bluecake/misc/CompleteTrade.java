package bluecake.misc;

import java.util.Comparator;
import java.util.Vector;

import bluecake.util.Util;

public class CompleteTrade {
	public TradeInfo buyer, seller;
	public String card;

	public CompleteTrade(String card) {
		this.card = card;
	}

	public long getCreationTime() {
		if (buyer == null & seller == null)
			return 0;
		if (buyer == null)
			return seller.getCreationTime();
		if (seller == null)
			return buyer.getCreationTime();
		return Math.min(seller.getCreationTime(), buyer.getCreationTime());
	}

	public boolean isComplete() {
		return (buyer != null && seller != null);
	}

	public Float getBuyerPrice() {
		if (buyer != null)
			return buyer.buyerPrice;
		return null;
	}

	public Float getNet() {
		if (buyer == null || seller == null || buyer.buyerPrice == null || seller.sellerPrice == null)
			return null;
		return buyer.buyerPrice - seller.sellerPrice;
	}

	public Float getNetPercent() {
		if (buyer == null || seller == null || buyer.buyerPrice == null || seller.sellerPrice == null)
			return null;
		return (buyer.buyerPrice - seller.sellerPrice) / seller.sellerPrice;
	}

	public Object getCardName() {
		return card;
	}

	public Float getSellerPrice() {
		if (buyer != null)
			return seller.sellerPrice;
		return null;
	}

	public String getSeller() {
		if (seller != null)
			return seller.seller;
		return null;
	}

	public String getBuyer() {
		if (buyer != null)
			return buyer.buyer;
		return null;
	}

	public String getSellerSource() {
		if (seller != null)
			return seller.source;
		return null;
	}

	public String getBuyerSource() {
		if (buyer != null)
			return buyer.source;
		return null;
	}

	public Integer getSellerAge() {
		if (seller != null)
			return age(seller.getCreationTime());
		return null;
	}

	public Integer getBuyerAge() {
		if (buyer != null)
			return age(buyer.getCreationTime());
		return null;
	}

	public Integer getAge() {
		if (buyer == null && seller == null)
			return null;
		if (buyer == null)
			return getSellerAge();
		if (seller == null)
			return getBuyerAge();
		return Math.max(getSellerAge(), getBuyerAge());
	}

	private int age(Long l) {
		return Util.getAgeFromCreation(l);
	}

	public boolean tryAdd(TradeInfo info) {
		boolean s = false;
		boolean b = false;
		if (buyer == null || buyer.buyer == null) {
			buyer = info;
			b = true;
		}
		if (seller == null || seller.seller == null) {
			seller = info;
			s = true;
		}
		if (!s && info.seller != null) {
			if (this.seller.sellerPrice > info.sellerPrice) {
				this.seller = info;
				s = true;
			}
		}

		if (!b && info.buyer != null) {
			if (this.buyer.buyerPrice < info.buyerPrice) {
				this.buyer = info;
				b = true;
			}
		}

		return (s || b);
	}

	public static Comparator<? super CompleteTrade> getComparator() {
		return new Comparator<CompleteTrade>() {

			@Override
			public int compare(CompleteTrade o1, CompleteTrade o2) {
				if (o1.getNet() == null && o2.getNet() == null)
					return 0;
				if (o1.getNet() == null)
					return 1;
				if (o2.getNet() == null)
					return -1;
				if (o1.getNet() > o2.getNet())
					return 1;
				if (o1.getNet() < o2.getNet())
					return -1;
				
				return 0;
				
			}
		};
	}
}
