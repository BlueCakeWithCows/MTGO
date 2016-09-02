package bluecake.misc;

import java.io.Serializable;

public class TradeInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1778280440494239784L;

	public TradeInfo(String source) {
		creationTime = System.currentTimeMillis();
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getCard() {
		return card;
	}

	public void setSource(String identifier) {
		this.source = identifier;
	}

	public String buyer, seller;
	public String source;
	public String card;
	public Float buyerPrice, sellerPrice;
	private long creationTime;

	public void setBuy(String buyer, Float price) {
		this.buyer = buyer;
		this.buyerPrice = price;
	}

	public void setSell(String seller, Float price) {
		this.seller = seller;
		this.sellerPrice = price;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getSource() {
		return source;
	}

	public String toString(){
		return card+":"+seller+":"+sellerPrice+":"+buyer+":"+buyerPrice+":"+creationTime+":"+source;
	}
}
