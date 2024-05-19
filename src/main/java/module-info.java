module com.example.ai1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.example.ai1 to javafx.fxml;
    exports com.example.ai1;
}