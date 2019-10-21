package dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import server.BcryptHashing;
import server.logic.Player;

/**
 *
 * @author Faludi Péter
 */
public class PlayerDao extends DefaultDao<Player>{
    
    private static final String INSERT_DATA_SQL = "INSERT INTO USERDATA (USERNAME, PASSWORD, EMAIL, NAME, BIRTH, REGISTER) VALUES(?,?,?,?,?,?)";
    private static final String FIND_DATA_BY_USERNAME_SQL = "SELECT * FROM USERDATA WHERE LIKE = ?";
    private static final String UPDATE_PASSWORD_SQL = "UPDATE USERDATA SET PASSWORD = ? WHERE ID = ?";
    private static final String FIND_PLAYER_BY_USERNAME_SQL = "SELECT * FROM PLAYER WHERE USERNAME =?";
    private static final String FIND_ALL_PLAYER_SQL = "SELECT * FROM PLAYER";
    private static final String FIND_PLAYER_BY_ID_SQL = "SELECT * FROM PLAYER WHERE ID = ?";
    private static final String INSERT_PLAYER_SQL = "INSERT INTO PLAYER(USERNAME, MONEY, WIN, LOST) VALUES(?,?,?,?)";
    private static final String UPDATE_PLAYER_SQL = "UPDATE PLAYER SET  MONEY = ?, WIN = ? , LOST = ? WHERE ID = ?";
    private static final String DELETE_PLAYER_SQL = "DELETE FROM PLAYER WHERE ID = ?";
    private static final String DELETE_USERDATA_SQL = "DELETE FROM USERDATA WHERE ID = ?";
    private static final String UPDATE_MONEY_SQL = "UPDATE PLAYER SET MONEY = ? WHERE ID = ?";
    private static final String UPDATE_WIN_LOSE_SQL = "UPDATE PLAYER SET WIN = ? , LOST = ? WHERE ID = ?";
    private static final String FIND_ID_BY_USERNAME_SQL = "SELECT ID,USERNAME FROM USERDATA WHERE USERNAME LIKE ?";
    private static final String FIND_PASSWORD_BY_USERNAME = "SELECT USERNAME,PASSWORD FROM USERDATA WHERE USERNAME LIKE ?";
    private static final String FIND_DATA_BY_EMAIL = "SELECT ID FROM USERDATA WHERE EMAIL = ?";
    private double startingMoney = 2500.0;
    
    private PlayerDao(){
    }
    
    /**
     *
     * @return osztály példány
     */
    public static PlayerDao getInstance(){
        return DefaultPlayerDaoHolder.INSTANCE;
    }
    
    @Override
    protected Player fromResultSet(ResultSet resultSet) throws SQLException {
        Player player = new Player(resultSet.getInt(1), 
                resultSet.getString(2),
                resultSet.getDouble(3), 
                resultSet.getInt(4),
                resultSet.getInt(5));
        return player;
    }
    
    @Override
    protected Statement fromEntity(String query, Player player, boolean withId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, player.getMoney());
        statement.setInt(2, player.getWin());
        statement.setInt(3, player.getLose());
        if (withId) {
            statement.setInt(4, player.getId());
        }
        return statement;
    }
    
    @Override
    protected Statement fromDatasDataTable(String username,String password,String email,String name,String birthDay) throws SQLException{
        PreparedStatement statement = connection.prepareStatement(INSERT_DATA_SQL);
        statement.setString(1, username);
        statement.setString(2, BcryptHashing.makeSaltedHash(password));
        statement.setString(3, email);
        statement.setString(4, name);
        statement.setDate(5, Date.valueOf(birthDay));
        statement.setDate(6, Date.valueOf(LocalDate.now()));
        return statement;
    }
    
    @Override
    protected Statement fromDatasPlayerTable(ResultSet resultSet) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_PLAYER_SQL);
        resultSet.next();
        statement.setString(1, resultSet.getString(2));
        statement.setDouble(2, startingMoney);
        statement.setInt(3, 0);
        statement.setInt(4, 0);
        return statement;
    }

    @Override
    protected  String getInsertDataSql() {
        return INSERT_DATA_SQL;
    }

    @Override
    protected  String getFindDataByUsernameSql() {
        return FIND_DATA_BY_USERNAME_SQL;
    }

    protected  String getUpdatePasswordSql() {
        return UPDATE_PASSWORD_SQL;
    }

    @Override
    protected  String getFindPlayerByUsername() {
        return FIND_PLAYER_BY_USERNAME_SQL;
    }

    @Override
    protected  String getFindAllPlayerSql() {
        return FIND_ALL_PLAYER_SQL;
    }

    @Override
    protected  String getFindPlayerByIdSql() {
        return FIND_PLAYER_BY_ID_SQL;
    }

    @Override
    protected  String getInsertPlayerSql() {
        return INSERT_PLAYER_SQL;
    }

    @Override
    protected  String getUpdatePlayerSql() {
        return UPDATE_PLAYER_SQL;
    }

    @Override
    protected  String getDeletePlayerSql() {
        return DELETE_PLAYER_SQL;
    }

    @Override
    protected String getUpdateMoneySql() {
        return UPDATE_MONEY_SQL;
    }

    @Override
    protected String getUpdateWinLoseSql() {
        return UPDATE_WIN_LOSE_SQL;
    }

    @Override
    protected String getFindIdByUsername() {
        return FIND_ID_BY_USERNAME_SQL;
    }

    @Override
    protected String getDeleteUserDataSql() {
        return DELETE_USERDATA_SQL;
    }
    
    @Override
    protected String getFindPasswordByUsername() {
        return FIND_PASSWORD_BY_USERNAME;
    }
    
    @Override
    protected String getFindDataByEmail() {
        return FIND_DATA_BY_EMAIL;
    }
    
    /**
     *
     * @param startingMoney
     */
    public void setStartingMoney(double startingMoney) {
        this.startingMoney = startingMoney;
    }

   
    
    
    
    
    
    

    private static class DefaultPlayerDaoHolder {
        private static final PlayerDao INSTANCE = new PlayerDao();
    }
    
}
