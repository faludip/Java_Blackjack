package server.logic;

import java.util.Collections;
import java.util.LinkedList;

/**
 * A Shoe-t reprezentáló osztály
 * @author Faludi Péter
 */


/*A dealing shoe or dealer's shoe is a gaming device, mainly used in casinos, to hold multiple decks of playing cards.
More information: https://en.wikipedia.org/wiki/Shoe_(cards)

*/
public class Shoe {
    private LinkedList<Card> shoe = new LinkedList<>();
    private int numOfDecks;
    
    /**
     * Legenerál egy shoe-t a paraméterben kapott számú pakliból
     * @param numOfDecks paklik száma
     */
    public Shoe(int numOfDecks){
        this.numOfDecks = numOfDecks;
        for(int i = 0; i < numOfDecks; i++){
            this.addDeck(new Deck());
        }
        this.shuffle();
    }

    /**
     * Pakli hozzáadása
     * @param deck pakli
     */
    protected void addDeck(Deck deck) {
        while(deck.size() > 0){
            shoe.add(deck.getLastCard());
        }
    }

    /**
     * shoe keverése
     */
    public void shuffle() {
        Collections.shuffle(shoe);
    }
    
    /**
     *
     * @return shoe lapjainak száma
     */
    public int getSize(){
        return shoe.size();
    }
    

    /**
     * A shoe legfelső lapjával visszatérő függvény
     * Törli a lapot egyben
     * @return
     */
    public Card dealNextCard(){
        Card card = shoe.getLast();
        shoe.removeLast();
        return card;
    }
  
    
}