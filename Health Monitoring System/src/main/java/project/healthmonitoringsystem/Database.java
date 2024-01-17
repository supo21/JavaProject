package project.healthmonitoringsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {
    private static final String url = "jdbc:mysql://localhost:3306/health_monitoring_system";
    private static final String username = "root";
    private static final String password = "";

    public Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void storeData(Double weight, String exercise) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Prepare SQL statement
            String sql = "INSERT INTO health_records (weight, exercise) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDouble(1, weight);
                preparedStatement.setString(2, exercise);

                preparedStatement.executeUpdate();
                System.out.println("inserted successfully");

            }
        } catch (Exception e) {
            System.out.println("Error storing data in the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void displayDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/health_monitoring_system");
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM health_records");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("Weight: " + resultSet.getDouble("weight") +
                        ", Exercise: " + resultSet.getString("exercise"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
