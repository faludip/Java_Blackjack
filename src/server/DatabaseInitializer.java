package server;

import dao.PlayerDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Az adattáblákat és a teszt játékos létrehozása(ha létezik azok törlése)
 * @author Faludi Péter
 */
public class DatabaseInitializer {
    
    private Connection connection;
    private final Properties properties;
    private final List<List<String>> userdata = new ArrayList<>(); 
    
    
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/blackjack?zeroDateTimeBehavior=convertToNull&serverTimezone=UTC";
    private final String databaseUrl ;
    private static final String DELETE_PLAYER_TABLE_SQL = "DROP TABLE PLAYER";
    private static final String DELETE_USERDATA_TABLE = "DROP TABLE USERDATA";
    private static final String CREATE_PLAYER_TABLE_SQL =  "CREATE TABLE PLAYER ("
            + "ID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY ,"
            + "USERNAME VARCHAR(100) NOT NULL,"
            + "MONEY DOUBLE(10,3),"
            + "WIN SMALLINT,"
            + "LOST SMALLINT"
            + ")";
    private static final String CREATE_USERDATA_TABLE_SQL = "CREATE TABLE USERDATA("
            + "ID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY ,"
            + "USERNAME VARCHAR(100),"
            + "PASSWORD CHAR(60) NOT NULL,"
            + "EMAIL VARCHAR(255) NOT NULL,"
            + "NAME VARCHAR(255) NOT NULL,"
            + "BIRTH DATE NOT NULL,"
            + "REGISTER DATE NOT NULL"
            + ")";
    
    private DatabaseInitializer() {
        databaseUrl = DATABASE_URL;
        properties = new Properties();
        properties.put("user", "nbuser");
        properties.put("password", "nbuser");
    }
    
    private DatabaseInitializer(String host,int port){
        databaseUrl = "jdbc:mysql://" + host + ":" + port + "/blackjack";
        properties = new Properties();
        properties.put("user", "nbuser");
        properties.put("password", "nbuser");
    }
    
    /**
     * inicializálás
     */
    public void init(){
        deleteTables();
        createTables();
        uploadUserTable();
    }
    
    /**
     * Táblák törlése
     */
    private void deleteTables() {
        try {
            connection = DriverManager.getConnection(databaseUrl, properties);
            Statement statement = connection.createStatement();
            statement.executeUpdate(DELETE_PLAYER_TABLE_SQL);
            statement = connection.createStatement();
            statement.executeUpdate(DELETE_USERDATA_TABLE);
            close(statement);
        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
    }
    /**
     * Táblák létrehozása
     */
    private void createTables() {
        try {
            connection = DriverManager.getConnection(databaseUrl, properties);
            Statement statement = connection.createStatement();
            statement.addBatch(CREATE_PLAYER_TABLE_SQL);
            statement.executeBatch();
            statement = connection.createStatement();
            statement.addBatch(CREATE_USERDATA_TABLE_SQL);
            statement.executeBatch();
            close(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Tábák feltöltése teszt játékosokkal
     */
    private void uploadUserTable() {
        userdata.add(createUserData("test1", "test1", "test1@test.com", "Test Janos", "1996-09-12"));
        userdata.add(createUserData("test2", "test2", "test2@test.com", "Test Bela", "1996-10-12"));
        userdata.add(createUserData("test3", "test3", "test3@test.com", "Test Kalman", "1996-11-12"));
        userdata.add(createUserData("test4", "test4", "test4@test.com", "Test Laszlo", "1996-12-12"));
        for(List<String> array : userdata){
            System.out.println(array.get(4));
            PlayerDao.getInstance().createPlayer(array.get(0),array.get(1), array.get(2), array.get(3),array.get(4));
        }
        
    }
    
    private void close(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
    

    /**
     * Új player lista
     */
    private ArrayList createUserData(String username,String password,String email,String name,String birhDate){
        ArrayList<String> list = new ArrayList<>();
        list.add(username);
        list.add(password);
        list.add(email);
        list.add(name);
        list.add(birhDate);
        return list;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        databaseInitializer.init();
        System.out.println("Succes");
    }
    
    
    
}
