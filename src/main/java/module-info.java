module com.example.restaurantapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.restaurantapp to javafx.fxml;
    exports com.example.restaurantapp;
}