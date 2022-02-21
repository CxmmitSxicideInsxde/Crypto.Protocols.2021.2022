module com.example.apps {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.apps to javafx.fxml;
    exports com.example.apps;
}