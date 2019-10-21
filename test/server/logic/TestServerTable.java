/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.io.PrintWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import server.gameServer.GameServer;

/**
 *
 * @author Faludi Péter
 */
public class TestServerTable {
    private GameServer testServer = Mockito.mock(GameServer.class);
    private Integer testMinbet = 10;
    private Integer testNumOfDecks = 4;
    private int testMinCardsBeforeShuffle = 20;
    private Integer testId = 1;
    private ServerTable testServerTable = new ServerTable( testMinbet, testNumOfDecks, testMinCardsBeforeShuffle, testId);
    private Player testPlayer = Mockito.mock(Player.class);
    
    @Test
    public void TestGetInfo(){
        for(Integer i = 0; i < 10; i++){
            ServerTable testServerTable = new ServerTable( 10*i, i + 1 , i*10 , i+1);
            Integer testMinbet = 10 * i;
            Integer testId = i + 1;
            String[] test = {testId.toString(),testMinbet.toString()};
            assertArrayEquals(test, testServerTable.getInfo());
        }
    }
    
    @Test
    public void TestGetMinBet(){
        for(Integer i = 0; i < 10; i++){
            ServerTable testServerTable = new ServerTable(10*i, i + 1 , i*10 , i+1);
            int testMinbet = 10 * i;
            assertEquals(testMinbet, testServerTable.getMinBet());
        }
    }
    
    @Test
    public void TestAddPlayer(){
        assertFalse(testServerTable.getTable().contains(testPlayer));
        testServerTable.addPlayerToTable(testPlayer);
        assertTrue(testServerTable.getTable().contains(testPlayer));
    }
    
    @Test
    public void TestRemovePlayer(){
        testServerTable.addPlayerToTable(testPlayer);
        assertTrue(testServerTable.getTable().contains(testPlayer));
        testServerTable.removePlayerFromTable(testPlayer);
        assertFalse(testServerTable.getTable().contains(testPlayer));
    }
    
    public TestServerTable() {
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
