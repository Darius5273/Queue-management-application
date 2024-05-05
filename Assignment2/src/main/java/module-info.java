module com.example.assignment2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

//    opens com.example.assignment2 to javafx.fxml;
//    exports com.example.assignment2;
    exports GUI;
    opens GUI to javafx.fxml;
    exports Models;
    opens Models to javafx.fxml;
    exports Business_logic;
    opens Business_logic to javafx.fxml;
}