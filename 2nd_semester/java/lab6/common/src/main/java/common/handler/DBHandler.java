package common.handler;

import common.exceptions.UserException;
import common.network.User;

import java.sql.*;

public class DBHandler {
    private static final String jdbcUrl = System.getenv("DATABASE_URL");
    private static final String username = System.getenv("DATABASE_USERNAME");
    private static final String password = System.getenv("DATABASE_PASSWORD");

    static {
        try(Connection connection = DriverManager.getConnection(jdbcUrl, username, password)){
            try(Statement stmt = connection.createStatement()){
                String createUsersTable = "CREATE TABLE IF NOT EXISTS users" +
                                           "(username VARCHAR(100), password VARCHAR(64));";

                stmt.execute(createUsersTable);

                String createRoutesTable = "CREATE TABLE IF NOT EXISTS routes" +
                                           "(id SERIAL PRIMARY KEY, name TEXT, " +
                                           "coordinates_x INT, coordinates_y INT," +
                                           "date_created TIMESTAMP, " +
                                           "from_x INT, from_y FLOAT, from_z FLOAT, from_name TEXT," +
                                           "to_x INT, to_y FLOAT, to_z FLOAT, to_name TEXT," +
                                           "distance INT);";

                stmt.execute(createRoutesTable);
            }
        } catch (SQLException e){
            IOHandler.println(e.getMessage());
            System.exit(0);
        }
    }

    public static Connection getConnection(){
        Connection connection = null;

        try{
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e){
            IOHandler.println("Could not connect to database: " + e.getMessage());
            System.exit(0);
        }

        return connection;
    }

    public static User checkUserPresence(User user){
        Connection connection = getConnection();

        String selectUserQuery = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(selectUserQuery)){
            stmt.setString(1, user.getUsername());

            try (ResultSet resultSet = stmt.executeQuery()){
                if (resultSet.next()){
                    return new User(resultSet.getString(1), resultSet.getString(2));
                }
            }
        } catch (SQLException e){
            IOHandler.println(e.getMessage());
        }

        return null;
    }

    public static User createUser(User user) throws UserException {
        if(checkUserPresence(user) != null){
            throw new UserException("user already exists");
        }

        String addUserQuery = "INSERT INTO users VALUES(?, ?);";

        try(Connection connection = getConnection()){
            try (PreparedStatement stmt = connection.prepareStatement(addUserQuery)){
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());

                stmt.executeUpdate();
            }
        } catch (SQLException e){
            return null;
        }

        return user;
    }

    public static boolean checkUserPassword(User userToCheck) throws UserException{
        User user = checkUserPresence(userToCheck);

        if (user != null){
            return user.getPassword().equals(userToCheck.getPassword());
        } else{
            throw new UserException("user does not exist");
        }
    }

    //TODO: implement methods for route object (create, get, update etc.)
}
