package me.group.freelancerpanel.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/FreelancerProject";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public String[] getUserByUsername(String username) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT user_id, username, email, password FROM freelancer WHERE username = ?")) {
            statement.setString(1, username);

            System.out.println("Executing SQL query...");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("user_id");
                    String retrievedUsername = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");

                    System.out.println("User retrieved: ID=" + id + ", Username=" + retrievedUsername);
                    return new String[]{String.valueOf(id), retrievedUsername, email, password};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
