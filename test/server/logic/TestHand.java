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
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

/**
 *
 * @author Faludi Péter
 */
public class TestHand {
    private Hand testHand = new Hand();
    private Card testCard1 = Mockito.mock(Card.class);
    private Card testCard2 = Mockito.mock(Card.class);
    
    public TestHand() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testHand.addCard(testCard1);
        testHand.addCard(testCard2);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testSize(){
        assertEquals(2, testHand.getSize());
    }
    
    @Test
    public void testgetValue(){
        when(testCard1.getValue()).thenReturn(4);
        when(testCard2.getValue()).thenReturn(5);
        assertEquals(9, testHand.getValue());
    }
    
    @Test
    public void testgetCard(){
        assertEquals(testCard1, testHand.getCard(0));
        assertEquals(testCard2, testHand.getCard(1));
    }
    
    @Test
    public void testClear(){
        testHand.clear();
        assertEquals(0, testHand.getSize());
    }
    
    
    

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
