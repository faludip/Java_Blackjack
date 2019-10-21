/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import client.ChatModel;
import client.ClientController;
import client.LobbyController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import view.panels.BetBox;
import view.panels.ChatBox;

/**
 *
 * @author Faludi Péter
 */
public class TestTable {
    ChatModel testChatModel = Mockito.mock(ChatModel.class);
    LobbyController testLobbyController = Mockito.mock(LobbyController.class);
    ClientController testClientController = Mockito.mock(ClientController.class);
//            new ClientController(testLobbyController, gameSocket, in, out, 0, testChatModel);
    Table testTable = new Table(1, testChatModel, testClientController);
    
    @Test
    public void TestSetChatBox(){
        ChatBox testChatBox = Mockito.mock(ChatBox.class);
        testTable.addChatBox(testChatBox);
        assertEquals(testChatBox, testTable.getRight());
    }
    
    @Test
    public void TestSet(){
        testTable.setBet();
        assertTrue(testTable.getLeft() instanceof BetBox);
    }
    
//    @Test
//    public void TestSet(){
//        
//    }
//    
//    @Test
//    public void TestSet(){
//        
//    }
//    
//    @Test
//    public void TestSet(){
//        
//    }
//    
    public TestTable() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testTable.init();
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
