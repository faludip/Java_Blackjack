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

/**
 *
 * @author Faludi Péter
 */
public class TestAdditionalHand {
    private Random random = new Random();
    private RuleHand testAdditionalHand = new RuleHand();
    private Card testAceCard = new Card(Suit.CLUBS, Rank.ACE);
    private Card testTwoCard = new Card(Suit.CLUBS, Rank.TWO);
    private Card testTenCard = new Card(Suit.CLUBS, Rank.TEN);
    
    @Test
    public void testSetBet() {
        int bet = random.nextInt(500);
        testAdditionalHand.setBet(bet);
        assertEquals(bet, testAdditionalHand.getBet());
    }
    
    @Test
    public void testSetDoubleDow() {
        testAdditionalHand.setDoubleDown();
        assertEquals(true, testAdditionalHand.isDoubleDown());
    }
    
    @Test
    public void testSetSplit() {
        testAdditionalHand.setSplitHand();
        assertEquals(true, testAdditionalHand.isSplitHand());
    }
    
    @Test
    public void testIsSoftHand(){
        testAdditionalHand.addCard(testAceCard);
        assertTrue(testAdditionalHand.hasAce());
        testAdditionalHand.addCard(testTwoCard);
        assertTrue(testAdditionalHand.isSoftHand());
        testAdditionalHand.addCard(testTenCard);
        assertFalse(testAdditionalHand.isSoftHand());
    }
    
    public void testGreaterValue(){
        testAdditionalHand.addCard(testAceCard);
        assertTrue(testAdditionalHand.hasAce());
        testAdditionalHand.addCard(testTwoCard);
        assertEquals(testAceCard.getValue() + testTwoCard.getValue(), testAdditionalHand.greaterValue());
    }
    
    public TestAdditionalHand() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
