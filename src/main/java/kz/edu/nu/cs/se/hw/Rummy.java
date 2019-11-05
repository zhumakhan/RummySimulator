package kz.edu.nu.cs.se.hw;

import java.util.*;
/**
 * Starter code for a class that implements the <code>PlayableRummy</code>
 * interface. A constructor signature has been added, and method stubs have been
 * generated automatically in eclipse.
 * 
 * Before coding you should verify that you are able to run the accompanying
 * JUnit test suite <code>TestRummyCode</code>. Most of the unit tests will fail
 * initially.
 * 
 * @see PlayableRummy
 * @see TestRummyCode
 *
 */
public class Rummy implements PlayableRummy {
	public int currentPlayer;//index of String players;
	public Steps currentStep;
	public List<Player> players = new ArrayList<Player>();
	public List<Card> deck = new ArrayList<Card>();
	public List<Card> discardPile = new ArrayList<Card>();
	public ArrayList<ArrayList<Card>> melds = new ArrayList<ArrayList<Card>>();
    
	public Rummy(String... players) {
		if(players.length < 2 )throw new RummyException("NOT_ENOUGH_PLAYERS",RummyException.NOT_ENOUGH_PLAYERS);
    	if(players.length > 6) throw new RummyException("EXPECTED_FEWER_PLAYERS",RummyException.EXPECTED_FEWER_PLAYERS);
		for(String p : players) {
    		this.players.add(new Player(p));
    	}
		final String[] suits = new String[] { "C", "D", "H", "S", "M" };
        final String[] ranks = new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(rank + suit));
            }
        }
    	currentStep = Steps.WAITING;    	
    }

    public String[] getPlayers() {
        String temp [] = new String[players.size()];
        for(int i = 0; i < players.size(); i++) {
        	temp[i] = players.get(i).name;
        }
    	return temp;
    	}

    public int getNumPlayers() {
        return players.size();
    }

    public int getCurrentPlayer() { 
        if(currentPlayer < 0 || currentPlayer >= players.size())throw new RummyException("NOT_VALID_INDEX_OF_PLAYER",RummyException.NOT_VALID_INDEX_OF_PLAYER);
    	return currentPlayer;
    }


    public int getNumCardsInDeck() {
        return deck.size();
    }


    public int getNumCardsInDiscardPile() {
        return discardPile.size();
    }


    public String getTopCardOfDiscardPile() {
    	Card card = discardPile.get(discardPile.size()-1);
    	return card.getCard();
    }

    public String[] getHandOfPlayer(int player) {
    	if(player < 0 || player >= players.size())throw new RummyException("NOT_VALID_INDEX_OF_PLAYER",RummyException.NOT_VALID_INDEX_OF_PLAYER);
    	Player p = players.get(player);
    	String[] temp = new String[p.handCards.size()];
        int i = 0;
    	for(Card c : p.handCards) {
        	temp[i] = c.getCard();
        	i++;
        }
    	return temp;
    }

    public int getNumMelds() {
        return melds.size();
    }

 
    public String[] getMeld(int i) {
    	if(melds.size() == 0 || i < 0 || i >= melds.size())throw new RummyException("NOT_VALID_INDEX_OF_MELD",RummyException.NOT_VALID_INDEX_OF_MELD);
    	String[] temp = new String[melds.get(i).size()];
    	int j = 0;
    	for(Card c : melds.get(i)) {
    		temp[j] = c.getCard();
    		j++;
    	}
    	return temp;
    }


    public void rearrange(String card) {
        if(currentStep != Steps.WAITING)throw new RummyException("EXPECTED_WAITING_STEP",RummyException.EXPECTED_WAITING_STEP);
        Card changeCard = new Card(card);
        deck.remove(changeCard);
        deck.add(changeCard);
    }


    public void shuffle(Long l) {
    	if(currentStep != Steps.WAITING)throw new RummyException("EXPECTED_WAITING_STEP",RummyException.EXPECTED_WAITING_STEP);
    	Random rand = new Random(l);
    	for(int i = 0; i < deck.size(); i++) {
    		int randI1 = rand.nextInt(deck.size());
    		int randI2 = rand.nextInt(deck.size());
    		Card temp = deck.get(randI1);
    		deck.set(randI1,deck.get(randI2));
    		deck.set(randI2, temp);
    	}
    }

   
    public Steps getCurrentStep() {
        return currentStep;
    }

    
    public int isFinished() {
        if(currentStep == Steps.FINISHED)return currentPlayer;
        return -1;
    }

    public void initialDeal() {
    	if(currentStep != Steps.WAITING)throw new RummyException("EXPECTED_WAITING_STEP",RummyException.EXPECTED_WAITING_STEP);
        int numofCards = 0;
    	if(players.size() == 2) {
        	numofCards = 10;
        }else if(players.size() <= 4) {
        	numofCards = 7;
        }else {
        	numofCards = 6;
        }
    	while(numofCards > 0) {
    		for(Player p : players) {
        		p.handCards.add(deck.get(deck.size()-1));
        		deck.remove(deck.size()-1);
        	}
    		numofCards--;
    	}
		discardPile.add(deck.get(deck.size()-1));
		deck.remove(deck.size()-1);
		currentStep = Steps.DRAW;
    }


    public void drawFromDiscard() {
    	if(currentStep != Steps.DRAW)throw new RummyException("EXPECTED_DRAW_STEP",RummyException.EXPECTED_DRAW_STEP);
        players.get(currentPlayer).handCards.add(discardPile.get(discardPile.size()-1));
        players.get(currentPlayer).fromDiscard = discardPile.get(discardPile.size()-1);
        discardPile.remove(discardPile.size()-1);
        currentStep = Steps.MELD;
        
    }

  
    public void drawFromDeck() {
    	if(currentStep != Steps.DRAW)throw new RummyException("EXPECTED_DRAW_STEP",RummyException.EXPECTED_DRAW_STEP);
        if(deck.size() == 0) {
        	Collections.reverse(discardPile);
        	deck = discardPile;
        	discardPile = new ArrayList<Card>();
        	discardPile.add(deck.get(deck.size()-1));
        	deck.remove(deck.size()-1);
        }
    	players.get(currentPlayer).handCards.add(deck.get(deck.size()-1));
        currentStep = Steps.MELD;
        deck.remove(deck.size()-1);
    }

  
    public void meld(String... cards) {
    	 if(currentStep != Steps.MELD && currentStep != Steps.RUMMY )throw new RummyException("EXPECTED_MELD_STEP_OR_RUMMY_STEP",RummyException.EXPECTED_MELD_STEP_OR_RUMMY_STEP);
         boolean allSameSuit = true;
         Card tempC = new Card(cards[0]);
         List<String> ranks = new ArrayList<String>();
         ArrayList<Card> tempCards = new ArrayList<Card>();
         List<Card> refToHandCards = players.get(currentPlayer).handCards;
         if(refToHandCards.size() <= cards.length ) {
        	 return;
         }
         String suit = tempC.getCardSuit();
         for(int i = 0; i < cards.length; i++){
             Card c = new Card(cards[i]);
             if(c.getCardSuit().compareTo(suit) != 0){
                 allSameSuit = false; 
             }
             if(c.getCardRank().equals("J"))ranks.add("11");
             else if(c.getCardRank().equals("Q"))ranks.add("12");
             else if(c.getCardRank().equals("A"))ranks.add("1");
             else ranks.add(c.getCardRank());
         }
         if(allSameSuit){
             Collections.sort(ranks, new Comparator<String>() {
    		    public int compare(String ind1, String ind2) {
    		        return (Integer.parseInt(ind1) > Integer.parseInt(ind2)) == true ? 1 : -1;
    		    }
    		 });            
             String rightMeld = "1 2 3 4 5 6 7 8 9 10 11 12 1";
             String newmeld1 = "";
             String newmeld2 = "";
             for(String r : ranks){
                 newmeld1 = newmeld1.concat(r + " ");
             }
             if(newmeld1.contains("1 "))newmeld2 = newmeld1.substring(2,newmeld1.length()) + " 1";
             if(!rightMeld.contains(newmeld1) && !rightMeld.contains(newmeld2)){
            	 throw new RummyException("NOT_VALID_MELD",RummyException.NOT_VALID_MELD);
             }
             for(String r : ranks) {
            	 for(int j = 0; j < cards.length; j++) {    
            		 Card cardToMeld = new Card(cards[j]);
            		 if(cardToMeld.getCardRank().equals(r) ||
(cardToMeld.getCardRank().equals("A") && r.equals("1")) || (cardToMeld.getCardRank().equals("J") && r.equals("11")) || (cardToMeld.getCardRank().equals("Q") && r.equals("12"))) {
            			 tempCards.add(cardToMeld);
            		 }
            	 }
             }
         }else{
             String tempR = ranks.get(0);
             for(String r : ranks){
                 if(!tempR.equals(r)){
                	 throw new RummyException("EXPECTED_CARDS",RummyException.EXPECTED_CARDS);
                 }
             }
             for(int j = 0; j < cards.length; j++) {
            	 tempCards.add(new Card(cards[j]));
             }
         }
         melds.add(tempCards);
         for(Card c : tempCards) {
    		 refToHandCards.remove(c);
    	 }
         if(currentStep == Steps.RUMMY && refToHandCards.size() == 1){
        	 currentStep = Steps.FINISHED;
         }
    }

  
    public void addToMeld(int meldIndex, String... cards) {
    	 if(currentStep != Steps.MELD && currentStep != Steps.RUMMY )throw new RummyException("EXPECTED_MELD_STEP_OR_RUMMY_STEP",RummyException.EXPECTED_MELD_STEP_OR_RUMMY_STEP);
    	 if(meldIndex < 0 ||  meldIndex >= melds.size())throw new RummyException("NOT_VALID_INDEX_OF_MELD",RummyException.NOT_VALID_INDEX_OF_MELD);
         Card tempC = new Card(cards[0]);
         String suit = tempC.getCardSuit();
         List<String> ranks = new ArrayList<String>();
         ArrayList<Card> tempCards = new ArrayList<Card>();
         List<Card> refToHandCards = players.get(currentPlayer).handCards;
         boolean done = false;
         boolean allSameSuit = true;
         boolean allSameRank = true;
         
        
         for(int j = 0; j < cards.length; j++) {
        	 tempCards.add(new Card(cards[j]));
         }
    	 for(int i = 0; i < cards.length; i++){
             Card c = new Card(cards[i]);
             if(c.getCardSuit().compareTo(suit) != 0){
                 allSameSuit = false; 
             }
             if(c.getCardRank().equals("J"))ranks.add("11");
             else if(c.getCardRank().equals("Q"))ranks.add("12");
             else if(c.getCardRank().equals("A"))ranks.add("1");
             else ranks.add(c.getCardRank());
         }
         if(allSameSuit){
        	 Collections.sort(ranks, new Comparator<String>() {
     		    public int compare(String ind1, String ind2) {
     		        return (Integer.parseInt(ind1) > Integer.parseInt(ind2)) == true ? 1 : -1;
     		    }
     		 });            
              String rightMeld = "1 2 3 4 5 6 7 8 9 10 11 12 1";
              String newmeld1 = "";
              String newmeld2 = "";
              for(String r : ranks){
                  newmeld1 = newmeld1.concat(r + " ");
              }
              if(newmeld1.contains("1 "))newmeld2 = newmeld1.substring(2,newmeld1.length()) + " 1";
              if(!rightMeld.contains(newmeld1) && !rightMeld.contains(newmeld2)){
            	  System.out.println(newmeld1);
            	  System.out.println(newmeld2);
            	  System.out.println(ranks);
             	  throw new RummyException("NOT_VALID_MELD",RummyException.NOT_VALID_MELD);
              }
              String rCardRank = melds.get(meldIndex).get(melds.get(meldIndex).size()-1).getCardRank();
              String lCardRank = melds.get(meldIndex).get(0).getCardRank();
            
              if(!rCardRank.equals("A") && Integer.parseInt(rCardRank) +1 == Integer.parseInt(tempCards.get(0).getCardRank()) ) {
            	  melds.get(meldIndex).addAll(tempCards);
            	  done = true;
              }
              if(!done && !lCardRank.equals("A") && Integer.parseInt(lCardRank) - 1 == Integer.parseInt(tempCards.get(0).getCardRank())){
            	  melds.get(meldIndex).addAll(0,tempCards);
            	  done = true;
              }
          }else{
              String tempR = ranks.get(0);
              for(String r : ranks){
                  if(!tempR.equals(r)){
                      throw new RummyException("NOT_VALID_MELD",RummyException.NOT_VALID_MELD);
                  }
              }
              Card tempCard = melds.get(meldIndex).get(0);
              for(Card c : melds.get(meldIndex)) {
            	 if(!c.getCardRank().equals(tempCard.getCardRank())) {
                		 allSameRank = false;
              	 }
              }
              if(allSameRank && tempCard.getCardRank().equals(tempCards.get(0).getCardRank())) {
            	  melds.get(meldIndex).addAll(tempCards);
            	  done = true;
              }
          }
          if(done) {
        	  for(Card c : tempCards) {
        		  refToHandCards.remove(c);
        	  }
          }
          if(refToHandCards.size() == 0) currentStep = Steps.FINISHED;
    }


    public void declareRummy() {
    	if(currentStep != Steps.MELD  )throw new RummyException("EXPECTED_MELD_STEP",RummyException.EXPECTED_MELD_STEP);
    	currentStep = Steps.RUMMY;
    }

 
    public void finishMeld() {
    	if(currentStep != Steps.MELD && currentStep != Steps.RUMMY ){
    		//currentStep = Steps.DISCARD;
    		throw new RummyException("EXPECTED_MELD_STEP_OR_RUMMY_STEP",RummyException.EXPECTED_MELD_STEP_OR_RUMMY_STEP);
    	}
    	if( currentStep ==Steps.RUMMY && players.get(currentPlayer).handCards.size() != 0 ){
    		currentStep = Steps.DISCARD;
    		throw new RummyException("RUMMY_NOT_DEMONSTRATED",RummyException.RUMMY_NOT_DEMONSTRATED);
    	}
    	currentStep = Steps.DISCARD;
    	//throw rummy not demonstrated exception;
    }

    public void discard(String card) {
    	if(currentStep != Steps.DISCARD)throw new RummyException("EXPECTED_DISCARD_STEP",RummyException.EXPECTED_DISCARD_STEP);
    	Card carD = new Card(card);
    	if(players.get(currentPlayer).fromDiscard.equals(carD)) {
    		throw new RummyException("NOT_VALID_DISCARD",RummyException.NOT_VALID_DISCARD);
    	}
    	if( !players.get(currentPlayer).handCards.contains(carD) ){
    		throw new RummyException("EXPECTED_CARDS",RummyException.EXPECTED_CARDS);
    	}
    	if(players.get(currentPlayer).handCards.size() == 0)throw new RummyException("EXPECTED_CARDS",RummyException.EXPECTED_CARDS);
    	discardPile.add(carD);
    	players.get(currentPlayer).handCards.remove(carD);
    	if(players.get(currentPlayer).handCards.size() == 0)currentStep = Steps.FINISHED;
		else{
			if(players.get(currentPlayer).handCards.size() == 0)
				currentStep = Steps.FINISHED;
			else currentStep = Steps.DRAW;
			currentPlayer = ( currentPlayer + 1 ) % players.size();
		}
    }

}
