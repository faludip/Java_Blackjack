package dao;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import server.logic.Player;

/**
 *
 * @author Faludi Péter
 * @param <T> Player típus
 */
public abstract class DefaultDao<T extends Player>  {
    
    /**
     * adatbázis url
     */
    protected static final String DATABASE_URL = "jdbc:mysql://localhost:3306/blackjack?zeroDateTimeBehavior=convertToNull&serverTimezone=UTC";
    protected Connection connection;
    protected Properties properties;
    public DefaultDao() {
        properties = new Properties();
        properties.put("user", "nbuser");
        properties.put("password", "nbuser");
    }
    
    /**
     * Összes játékos keresése
     * @return játékosok
     */
    public List<T> findAllPlayers() {
        List<T> items = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(DATABASE_URL, properties);
            PreparedStatement statement = connection.prepareStatement(getFindAllPlayerSql());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                items.add(fromResultSet(resultSet));
            }
            close(statement);
        } catch (SQLException ex) {
        } finally {
            return items;
        }
    }
    
    /**
     * játékos keresése felhasználónév alapján
     * @param username 
     * @return megadott játékos
     */
    public T findPlayerByUsername(String username){
        T item = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL, properties);
            PreparedStatement statement = connection.prepareStatement(getFindPlayerByUsername());
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                item = fromResultSet(resultSet);
            }
            close(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            return item;
        }
    }
    
    /**
     * játékos keresése azonosító  alapján
     * @param id
     * @return megadott játékos
     */
    public T findPlayerById(Integer id) {
        T item = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL, properties);
            PreparedStatement statement = connection.prepareStatement(getFindPlayerByIdSql());
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                item = fromResultSet(resultSet);
            }
            close(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            return item;
        }
    }

    /**
     * Létezik mar a játékos?
     * @param username
     * @param email
     * @return létezik
     * @throws SQLException
     */
    public String alreadyContains(String username,String email) throws SQLException{
        connection = DriverManager.getConnection(DATABASE_URL, properties);
        StringBuilder stringBuilder = new StringBuilder();
        PreparedStatement statement  = connection.prepareStatement(getFindPasswordByUsername());
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            stringBuilder.append("USERNAME--");
        }else {
            stringBuilder.append("OK--");
        }       
        statement  = connection.prepareStatement(getFindDataByEmail());
        statement.setString(1, email);
        resultSet = statement.executeQuery();
        if(resultSet.next()){
            stringBuilder.append("EMAIL");
        }else{
            stringBuilder.append("OK");
        }
        return stringBuilder.toString();
    }
    
    /**
     * játékhoz szükséges adatok kigyujtése
     * @param username
     * @return
     * @throws SQLException
     */
    public List<String>findLoginDatas(String username) throws SQLException{
        connection = DriverManager.getConnection(DATABASE_URL, properties);
        PreparedStatement statement  = connection.prepareStatement(getFindPasswordByUsername());
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        List<String> datas = new ArrayList<>();
        while(resultSet.next()){
            datas.add(resultSet.getString(1));
            datas.add(resultSet.getString(2));
        }
        return datas;
    }
    
    /**
     * új játékos létrehozása
     * @param username
     * @param password
     * @param email
     * @param name
     * @param birthDate
     */
    public void createPlayer(String username,String password,String email,String name,String birthDate) {
        try {
            
            connection = DriverManager.getConnection(DATABASE_URL, properties);
            PreparedStatement statement = (PreparedStatement) fromDatasDataTable(username, password, email, name, birthDate);
            statement.executeUpdate();
            statement = connection.prepareStatement(getFindIdByUsername());
            statement.setString(1 , username);
            ResultSet resultSet = statement.executeQuery();
            statement = (PreparedStatement) fromDatasPlayerTable(resultSet);
            statement.executeUpdate();
            close(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Játékos adatainak frissítése
     * @param entity
     */
    public void update(T entity) {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, properties);
            PreparedStatement statement = (PreparedStatement) fromEntity(getUpdatePlayerSql(), entity, true);
            statement.executeUpdate();
            close(statement);
        } catch (SQLException ex) {
        }
    }

    /**
     * játékos törlése
     * @param id
     */
    public void delete(int id) {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, properties);
            PreparedStatement statement = connection.prepareStatement(getDeletePlayerSql());
            statement.setInt(1, id);
            statement.executeUpdate();
            statement = connection.prepareStatement(getDeleteUserDataSql());
            statement.setInt(1, id);
            statement.executeUpdate();
            close(statement);
        } catch (SQLException ex) {
        }
    }

    /**
     * Adat táblában új felhasználó létrehozása
     * @param username
     * @param password
     * @param email
     * @param name
     * @param birthDay
     * @return új felhasználó
     * @throws SQLException
     */
    protected abstract Statement fromDatasDataTable(String username,String password,String email,String name,String birthDay)throws SQLException;
    
    /**
     * Játék táblában új felhasználó létrehozása
     * @param resultSet
     * @return
     * @throws SQLException
     */
    protected abstract Statement fromDatasPlayerTable(ResultSet resultSet) throws SQLException;
    
    protected abstract String getInsertDataSql();
    
    protected abstract String getFindDataByUsernameSql();

    protected abstract String getUpdatePasswordSql();
    
    protected abstract String getFindPlayerByUsername();
    
    protected abstract String getFindAllPlayerSql();
   
    protected abstract String getFindPlayerByIdSql();

    protected abstract String getInsertPlayerSql();
   
    protected abstract String getUpdatePlayerSql();
   
    protected abstract String getDeletePlayerSql();

    protected abstract String getUpdateMoneySql();
        
    protected abstract String getUpdateWinLoseSql();

    protected abstract String getFindIdByUsername();
    
    protected abstract String getFindPasswordByUsername();
    
    protected abstract String getFindDataByEmail();

    protected abstract T fromResultSet(ResultSet resultSet) throws SQLException;

    protected abstract Statement fromEntity(String query, T entity, boolean withId) throws SQLException;
    
    protected abstract String getDeleteUserDataSql();

    /**
     * Kapcsolat ,statement lezárása
     * @param statement
     * @throws SQLException 
     */
    protected void close(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
    
    
}
