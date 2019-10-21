package server.logic;

import java.util.LinkedList;

/**
 * A játékos kezének a valós részét reprezántaló osztály
 * @author Faludi Péter
 */
public class Hand {
    private LinkedList<Card> hand = new LinkedList<>();
    
    public void clear(){
        hand.clear();
    }
    
    public void addCard(Card card){
        hand.add(card);
    }
    
    public int getValue(){
        int value = 0;
        for(Card card : hand){
            value += card.getValue();
        }
        return value;
    }
    
    public int getSize(){
        return hand.size();
    }
    
    public Card getCard(int index){
        return hand.get(index);
    }
    
    protected int rankCount(Rank rank){
        int count = 0;
        for(Card card : hand){
            if(card.getRank() == rank){
                count++;
            }
        }
        return count;
    }
}
