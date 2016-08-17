package bluecake;

import java.util.Comparator;
import java.util.Vector;

public class FullTrade implements Comparable<FullTrade> {
	public String cardName;
	public HalfTrade buyer, seller;

	public FullTrade(String c) {
		this.cardName = c;
	}

	public Float getNet() {
		try {
			if (buyer == null || seller == null)
				return null;
			return buyer.buyPrice - seller.sellPrice;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public Long getTime() {
		if (buyer == null && seller == null)
			return 0l;
		if (buyer == null)
			return seller.time;
		if (seller == null)
			return buyer.time;
		if (buyer.time < seller.time)
			return buyer.time;
		else
			return seller.time;
	}

	public Float getBuy() {
		if (buyer == null)
			return null;
		return buyer.buyPrice;
	}

	public Float getSell() {
		if (buyer == null)
			return null;
		return buyer.sellPrice;
	}

	public void improve(HalfTrade trade) {
		if (trade.time < getTime())
			return;

		if (buyer == null && trade.buyPrice != null)
			buyer = trade;
		if (seller == null && trade.sellPrice != null)
			seller = trade;
		if (buyer != null) {
			if (buyer.time > trade.time && buyer.source == trade.source)
				buyer = trade;
			if (trade.buyPrice != null && (buyer.buyPrice == null || buyer.buyPrice < trade.buyPrice))
				buyer = trade;
		}
		if (seller != null) {
			if (seller.time > trade.time && seller.source == trade.source)
				seller = trade;

			if (trade.sellPrice != null && (seller.sellPrice == null || seller.sellPrice > trade.sellPrice))
				seller = trade;
		}

	}

	public String toString() {
		if (buyer == null || seller == null)
			return "null";
		if (buyer.buyer[0].equalsIgnoreCase("") || seller.seller[0].contentEquals("")) {
			return "null";
		}
		return String.valueOf(getNet());
	}

	public Vector<Object> getRow() {

		String b = null;
		Float cb = null;
		if (buyer != null) {
			b = buyer.buyer[0];
			cb = buyer.buyPrice;
		}

		String s = null;
		Float cs = null;
		if (seller != null) {
			s = seller.seller[0];
			cs = seller.sellPrice;
		}

		Vector<Object> v = new Vector<Object>();
		v.add(cardName);
		v.add(this);
		v.add(b);
		v.add(s);
		v.add(cb);
		v.add(cs);
		v.add(this.getTime());
		return v;
	}

	static class TradeComparator implements Comparator<FullTrade> {

		@Override
		public int compare(FullTrade o1, FullTrade o2) {
			return o1.compareTo(o2);
		}

	}

	public int compareTo(FullTrade o) {
		if (this.getNet() == o.getNet())
			return 0;
		if (this.getNet() == null) {
			return -1;
		}
		if (o.getNet() == null) {
			return 1;
		}

		if (this.getNet() > o.getNet()) {
			return 1;
		} else {
			return -1;
		}
	}

}