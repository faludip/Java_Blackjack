package server.logic;

/**
 * A kártya színét reprezentáló felsoroló
 * @author Faludi Péter
 */
public enum Suit {
    CLUBS,HEARTS,SPADES,DIAMONDS;
    
    @Override
    public String toString(){
        return this.name().toLowerCase();
    }
    
}
