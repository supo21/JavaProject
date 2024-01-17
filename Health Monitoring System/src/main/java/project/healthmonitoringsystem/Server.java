package project.healthmonitoringsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(6666);
            System.out.println("Waiting for connection......");

            while (true) {
                Socket s = ss.accept();
                System.out.println("Connection successful: " + s);

                // Read data from the client
                DataInputStream dis = new DataInputStream(s.getInputStream());
                Double weight = dis.readDouble();
                String exercise = dis.readUTF();

                System.out.println("Received weight: " + weight);
                System.out.println("Received exercise: " + exercise);

                // Store data in the database
                Database database = new Database();
                Database.storeData(weight, exercise);
                System.out.println("Successfully inserted the data into the database");

                // Retrieve data from the database
                String responseData = retrieveDataFromDatabase();

                // Send the response back to the client
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(responseData);

                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String retrieveDataFromDatabase() {
        StringBuilder response = new StringBuilder("Data retrieved from the database:\n");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/health_monitoring_system", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM health_records");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Double weight = resultSet.getDouble("weight");
                String exercise = resultSet.getString("exercise");
                response.append("Weight: ").append(weight).append(", Exercise: ").append(exercise).append("\n");

            }
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
