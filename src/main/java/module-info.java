module com.example.exojavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires htmlunit;
    requires sib.api.v3.sdk;
    requires java.sql;

    opens com.example.exojavafx to javafx.fxml;
    exports com.example.exojavafx;
}