package server.logic;

/**
 * A kátyákat reprezentáló osztály
 * @author Faludi Péter
 */
public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public int getValue(){
        return rank.getValue();
    }
    
    public Rank getRank(){
        return this.rank;
    }
    
    @Override
    public String toString(){
        return rank.toString() + "_of_" + suit.toString();
    }
    
}
