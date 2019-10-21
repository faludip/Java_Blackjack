/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Faludi Péter
 */
public class TestDeck {
    private final Deck testDeck = new Deck();
   
    @Test
    public void testSize(){
        assertEquals("Test size!", 52, testDeck.size());
    }
    
    @Test
    public void testIsEmpty(){
        assertFalse("Empty", testDeck.isEmpty());
    }
    
    @Test
    public void testLastCard(){
        int testSize = testDeck.size();
        testDeck.getLastCard();
        assertEquals(testSize - 1, testDeck.size());
    }
    
    @Test
    public void testInitSize(){
        assertEquals(4 * 13, testDeck.size());
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
