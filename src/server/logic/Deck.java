package server.logic;

import java.util.LinkedList;

/**
 * Paklit reprezentló osztály
 * @author Faludi Péter
 */
public class Deck {
    
    private LinkedList<Card> deck;

    public Deck() {
        this.deck = new LinkedList<>();
        for(Suit suit : Suit.values()){
            for(Rank rank : Rank.values()){
                deck.add(new Card(suit,rank));
            }
        }
    }
    
    
    public boolean isEmpty(){
        return deck.isEmpty();
    }
    
    public Card getLastCard(){
        Card card = deck.getLast();
        deck.removeLast();
        return card;
    }
    
    
    
    public int size(){
        return deck.size();
    }
    
    
}
