package com.example.restaurantapp;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         statusLabel;
    @FXML private Button        enterBtn;
    @FXML private Rectangle     glowRect;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), glowRect);
        pulse.setFromX(1.0); pulse.setToX(1.02);
        pulse.setFromY(1.0); pulse.setToY(1.02);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), enterBtn);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("⚠ Please fill in all fields.");
            return;
        }

        // Accept any username and password as long as both are filled
        openMainScreen();
    }

    private void openMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/restaurantapp/Main.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 860, 700);
            scene.getStylesheets().add(
                    getClass().getResource("/com/example/restaurantapp/Style.css").toExternalForm()
            );

            javafx.stage.Stage stage =
                    (javafx.stage.Stage) enterBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("DEE Restaurant — Point of Sale");
            stage.setResizable(true);

        } catch (Exception e) {
            statusLabel.setText("Error loading main screen.");
            e.printStackTrace();
        }
    }
}