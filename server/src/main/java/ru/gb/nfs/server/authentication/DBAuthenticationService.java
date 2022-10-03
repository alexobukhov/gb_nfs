package ru.gb.nfs.server.authentication;

import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class DBAuthenticationService implements AuthenticationService {

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public DBAuthenticationService() {
        try {
            connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/mainDB.db");
        statement = connection.createStatement();
    }

    private static void disconnect() throws SQLException {
        connection.close();
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        String username = "";
        String passwordDB = "";

        try {
            resultSet = statement.executeQuery(String.format("SELECT * FROM auth WHERE login = '%s'", login));
            if (resultSet.isClosed()) {
                return null;
            }

            username = resultSet.getString("username");
            passwordDB = resultSet.getString("password");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ((passwordDB != null) && (passwordDB.equals(password))) ? username : null;
    }

    private static void updateUsername(User user) throws SQLException {
        statement.executeUpdate(String.format("UPDATE auth SET username = '%s' WHERE login = '%s'",
                user.getUsername(), user.getLogin()));
    }

    @Override
    public void startAuthentication() {

    }

    @Override
    public void endAuthentication() {

    }
}
