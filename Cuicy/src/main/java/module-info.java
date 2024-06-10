module com.example.cuicy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.cuicy to javafx.fxml;
    exports com.example.cuicy;
}