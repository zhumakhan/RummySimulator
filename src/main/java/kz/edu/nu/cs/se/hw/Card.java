package kz.edu.nu.cs.se.hw;

public class Card {
	private String card;
	public Card(String c) {
		card = c;
	}
	public String getCardSuit() {
		return String.valueOf(card.charAt(card.length()-1));
	}
	public String getCardRank() {
		return card.substring(0, card.length() - 1);
	}
	public String getCard() {
		return card;
	}
	public String toString() {
		return card;
	}
	public boolean equals(Object obj) { 
		if (!(obj instanceof Card))
			return false;	
		if (obj == this)
			return true;
		return this.card.equals(((Card) obj).card);
	}
	public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.card != null ? this.card.hashCode() : 0);
        return hash;
    }
}
