package me.group.freelancerpanel.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHandler {

    @SuppressWarnings("unused")
    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    private static final String DB_URL = "jdbc:mysql://79.127.235.186:3306/s127_CCEProject";
    private static final String USER = "u127_hd6DdSE6j4";
    private static final String PASSWORD = "S+pP^iqw^whxxjB1rBjC8.d@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public String[] getUserByUsername(String username) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, username, email, password FROM users WHERE username = ?")) {
            statement.setString(1, username);

            System.out.println("Executing SQL query...");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String id = String.valueOf(resultSet.getInt("id"));
                    String retrievedUsername = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");

                    return new String[]{id, retrievedUsername, email, password};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
