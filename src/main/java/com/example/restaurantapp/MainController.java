package com.example.restaurantapp;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label            timeLabel;
    @FXML private CheckBox         cbDinner0, cbDinner1, cbDinner2, cbDinner3;
    @FXML private RadioButton      rbDessert0, rbDessert1, rbDessert2, rbDessert3;
    @FXML private ComboBox<String> drinkCombo;
    @FXML private TextField        totalField;
    @FXML private TextField        tenderedField;
    @FXML private TextField        changeField;
    @FXML private Button           purchaseBtn;
    @FXML private Button           resetBtn;
    @FXML private Button           exitBtn;
    @FXML private Label            statusBar;

    private OrderModel  model;
    private ToggleGroup dessertGroup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new OrderModel();
        setupDessertGroup();
        setupDrinkCombo();
        setupTenderedListener();
        startClock();
    }

    private void setupDessertGroup() {
        dessertGroup = new ToggleGroup();
        rbDessert0.setToggleGroup(dessertGroup);
        rbDessert1.setToggleGroup(dessertGroup);
        rbDessert2.setToggleGroup(dessertGroup);
        rbDessert3.setToggleGroup(dessertGroup);
    }

    private void setupDrinkCombo() {
        drinkCombo.getItems().clear();
        for (int i = 0; i < OrderModel.DRINK_ITEMS.length; i++) {
            String opt = OrderModel.DRINK_ITEMS[i] +
                    (i > 0 ? String.format("  M%.2f", OrderModel.DRINK_PRICES[i]) : "");
            drinkCombo.getItems().add(opt);
        }
        drinkCombo.getSelectionModel().selectFirst();
    }

    private void setupTenderedListener() {
        tenderedField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double tendered = Double.parseDouble(newVal);
                model.setAmountTendered(tendered);
                double change = model.getChange();
                changeField.setText(String.format("%.2f", change));
            } catch (NumberFormatException ex) {
                changeField.setText("0.00");
            }
        });
    }

    private void startClock() {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy   HH:mm:ss");
        Timeline clock = new Timeline(
                new KeyFrame(Duration.seconds(1), e ->
                        timeLabel.setText("🕐  " + LocalDateTime.now().format(fmt))
                )
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void onDinnerChanged() {
        model.setDinnerSelected(0, cbDinner0.isSelected());
        model.setDinnerSelected(1, cbDinner1.isSelected());
        model.setDinnerSelected(2, cbDinner2.isSelected());
        model.setDinnerSelected(3, cbDinner3.isSelected());
        refreshTotal();
    }

    @FXML
    private void onDessertChanged() {
        if      (rbDessert0.isSelected()) model.setDessertIndex(0);
        else if (rbDessert1.isSelected()) model.setDessertIndex(1);
        else if (rbDessert2.isSelected()) model.setDessertIndex(2);
        else if (rbDessert3.isSelected()) model.setDessertIndex(3);
        refreshTotal();
    }

    @FXML
    private void onDrinkChanged() {
        model.setDrinkIndex(drinkCombo.getSelectionModel().getSelectedIndex());
        refreshTotal();
    }

    @FXML
    private void handlePurchase() {
        if (model.getTotalAmount() == 0) {
            showAlert(Alert.AlertType.WARNING, "No Items Selected",
                    "Please select at least one item before purchasing.");
            return;
        }
        double tendered;
        try {
            tendered = Double.parseDouble(tenderedField.getText().trim());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Please enter a valid numeric amount in the Amount Tendered field.");
            return;
        }
        if (tendered < model.getTotalAmount()) {
            showAlert(Alert.AlertType.ERROR, "Insufficient Payment",
                    "Sorry — amount tendered is less than the total.\n" +
                            "Please check your balance and try again.");
            return;
        }

        saveReceipt(tendered);

        showAlert(Alert.AlertType.INFORMATION, "Purchase Successful",
                "✅ Thank you for dining at DEE'S Restaurant!\n\n" +
                        "Total:    M" + String.format("%.2f", model.getTotalAmount()) + "\n" +
                        "Tendered: M" + String.format("%.2f", tendered) + "\n" +
                        "Change:   M" + String.format("%.2f", model.getChange()) + "\n\n" +
                        "Receipt saved to receipt.txt\nEnjoy your food!");

        statusBar.setText("✅ Purchase saved — " + model.getTimestamp());
        statusBar.setTextFill(Color.web("#00ff88"));
    }

    @FXML
    private void handleReset() {
        model.reset();
        cbDinner0.setSelected(false);
        cbDinner1.setSelected(false);
        cbDinner2.setSelected(false);
        cbDinner3.setSelected(false);
        dessertGroup.selectToggle(null);
        drinkCombo.getSelectionModel().selectFirst();
        totalField.setText("0.00");
        tenderedField.clear();
        changeField.setText("0.00");
        statusBar.setText("🔄 Form reset — ready for new order.");
        statusBar.setTextFill(Color.web("#e67e22"));
    }

    @FXML
    private void handleExit() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Exit");
        confirm.setHeaderText("Are you sure you want to exit?");
        confirm.setContentText("Any unsaved order will be lost.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) Platform.exit();
        });
    }

    private void refreshTotal() {
        totalField.setText(String.format("%.2f", model.getTotalAmount()));
        try {
            double tendered = Double.parseDouble(tenderedField.getText());
            model.setAmountTendered(tendered);
            changeField.setText(String.format("%.2f", model.getChange()));
        } catch (NumberFormatException ignored) {}
    }

    private void saveReceipt(double tendered) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("receipt.txt", true))) {
            pw.println("================================================");
            pw.println("         DEE'S RESTAURANT");
            pw.println("              RECEIPT");
            pw.println("================================================");
            pw.println("  Date & Time : " + model.getTimestamp());
            pw.println("------------------------------------------------");
            pw.println("  ITEMS ORDERED:");
            List<String> lines = model.getOrderLines();
            for (String line : lines) pw.println(line);
            pw.println("------------------------------------------------");
            pw.printf ("  TOTAL AMOUNT    : M%.2f%n", model.getTotalAmount());
            pw.printf ("  AMOUNT TENDERED : M%.2f%n", tendered);
            pw.printf ("  CHANGE          : M%.2f%n", model.getChange());
            pw.println("================================================");
            pw.println("  YOU ARE WELCOME AT DEE'S RESTAURANT");
            pw.println("  ...ENJOY YOUR FOOD!");
            pw.println("================================================");
            pw.println();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "File Error",
                    "Could not save receipt:\n" + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}