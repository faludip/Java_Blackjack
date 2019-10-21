package server.logic;

/**
 * Ez az osztály tárololja a játékos játék során felhasznált információit
 * @author Faludi Péter
 */
public class RuleHand extends Hand{
    private int bet = 0;
    private boolean doubleDown = false;
    private boolean splitHand = false;
    private Card doubleDownCard ;

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean isDoubleDown() {
        return doubleDown;
    }

    public void setDoubleDown() {
        this.doubleDown = true;
    }

    public boolean isSplitHand() {
        return splitHand;
    }

    public void setSplitHand() {
        this.splitHand = true;
    }

    public Card getDoubleDownCard() {
        return doubleDownCard;
    }

    public void setDoubleDownCard(Card card) {
        this.doubleDownCard = card;
        super.addCard(card);
    }
    
    public boolean isSoftHand(){
        return this.hasAce() && getValue() < 12;
    }

    protected boolean hasAce() {
        return super.rankCount(Rank.ACE) > 0;
    }
    
    public int greaterValue(){
        if(isSoftHand()){
           return super.getValue() + 10;
        }
        return getValue();
    }
    
    
    
    
}
