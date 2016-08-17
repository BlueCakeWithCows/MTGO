package bluecake;

public class HalfTrade {
	public static int HOTLISTBOT = 0;
	public static int WIKIPRICE = 1;
	
	public String card;
	public int source;
	public String[] buyer, seller;
	
	public HalfTrade(int source){
		this.source=source;
		this.setTime();
	}

	public void setBuyer(String s) {
		if (s == null)
			buyer = null;
		buyer = new String[] { s };
	}

	public void setSeller(String s) {
		if (s == null)
			seller = null;
		seller = new String[] { s };
	}

	public Float buyPrice = null, sellPrice = null;
	public Long time;

	public void setTime() {
		time = System.currentTimeMillis();
	}

	public void setBuyPrice(float val) {
		buyPrice = val;
	}

	public void setSellPrice(float val) {
		buyPrice = val;
	}

	public Object[] toRay() {
		Float temp = buyPrice;
		Float temp2 = sellPrice;
		if (buyer == null) {
			temp = null;
		}
		if (seller == null) {
			temp2 = null;
		}

		return new Object[] { card, null, buyer[0], seller[0], temp, temp2,
				(System.currentTimeMillis() - time) / 1000 };
	}
}
