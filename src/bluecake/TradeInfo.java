package bluecake;

public class TradeInfo {
	public void setCard(String card){
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
	
	public void setBuy(String buyer,Float price){
		this.buyer = buyer;
		this.buyerPrice=price;
	}
	public void setSell(String seller,Float price){
		this.seller = seller;
		this.sellerPrice=price;
	}
}
