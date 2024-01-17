package project.healthmonitoringsystem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientGUI extends Application {

    private TextField weightField;
    private TextField exerciseField;
    private TextArea dataTextArea;
    private Socket socket;
    private DataOutputStream outputStream;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Health System");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        Label weightLabel = new Label("Weight:");
        Label exerciseLabel = new Label("Exercise:");

        weightField = new TextField();
        exerciseField = new TextField();

        Button submitButton = new Button("Submit");
        Button refreshButton = new Button("Refresh");
        Button displayButton = new Button("Display");

        submitButton.setOnAction(e -> sendDataToServer());
        refreshButton.setOnAction(e -> refreshData());
        displayButton.setOnAction(e -> retrieveAndDisplayData());

        dataTextArea = new TextArea();
        dataTextArea.setEditable(false);

        grid.add(weightLabel, 0, 0);
        grid.add(weightField, 1, 0);
        grid.add(exerciseLabel, 0, 1);
        grid.add(exerciseField, 1, 1);
        grid.add(submitButton, 0, 2);
        grid.add(refreshButton, 1, 2);
        grid.add(displayButton, 2, 2);
        grid.add(dataTextArea, 0, 3, 3, 1);

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);

        // Establish the socket connection once when the application starts
        try {
            socket = new Socket("localhost", 6666);
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to connect to server: " + e.getMessage());
        }

        primaryStage.show();
    }

    private void sendDataToServer() {
        Double weight = Double.parseDouble(weightField.getText());
        String exercise = exerciseField.getText();

        if (String.valueOf(weight).isEmpty() || exercise.isEmpty()) {
            showAlert("Error", "Please enter both weight and exercise.");
            return;
        }

        try {
            // Sending data to the server
            outputStream.writeDouble(weight);
            outputStream.writeUTF(exercise);
            outputStream.flush(); // Make sure the data is sent immediately

            showAlert("Success", "Data sent to server successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to connect to server: " + e.getMessage());
        }
    }

    private void refreshData() {
        weightField.clear();
        exerciseField.clear();
        dataTextArea.clear();
    }

    private void retrieveAndDisplayData() {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

            // Receiving and displaying data from the server
            String serverResponse = inputStream.readUTF();
            System.out.println("Received response from server: " + serverResponse);

            // Update UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                System.out.println("Updating UI on JavaFX Application Thread");
                dataTextArea.appendText(serverResponse);
                showAlert("Success", "Data retrieved and displayed successfully!");
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to connect to server: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        // Close the socket when the application exits
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        super.stop();
    }
}
