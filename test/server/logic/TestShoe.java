/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

/**
 *
 * @author Faludi Péter
 */
public class TestShoe {
    private Random rand = new Random();
    private int testNumOfDecks = rand.nextInt(5);
    private Shoe testShoe = new Shoe(testNumOfDecks);
    private Deck testDeck = Mockito.mock(Deck.class);
    
    public TestShoe() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testGetSize(){
        Mockito.when(testDeck.size()).thenReturn(52);
        assertEquals(testNumOfDecks * testDeck.size(), testShoe.getSize());
    }
    
    @Test
    public void testAddDeck(){
        Mockito.when(testDeck.size()).thenReturn(52);
        assertEquals(testNumOfDecks * testDeck.size(), testShoe.getSize());
        testShoe.addDeck(new Deck());
        assertEquals((testNumOfDecks + 1)* testDeck.size(), testShoe.getSize());
    }
    
    @Test
    public void testDealNextCard(){
        Mockito.when(testDeck.size()).thenReturn(52);
        assertEquals(testNumOfDecks * testDeck.size(), testShoe.getSize());
        testShoe.dealNextCard();
        assertEquals(testNumOfDecks * testDeck.size() -1, testShoe.getSize());
    }
    
//    @Test
//    public void testGetSize(){
//        assertEquals(testNumOfDecks * 52, testShoe.getSize());
//    }
//    
//    @Test
//    public void testGetSize(){
//        assertEquals(testNumOfDecks * 52, testShoe.getSize());
//    }
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
