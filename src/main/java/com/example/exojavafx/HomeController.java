package com.example.exojavafx;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class HomeController {
    @FXML
    protected Label labelText;
    @FXML
    protected Button close;

    @FXML
    protected Button delete;

    @FXML
    protected Button validate;
    @FXML
    protected TextField login;
    @FXML
    protected PasswordField password;

    @FXML
    protected void close() {
        Platform.exit();
    }

    @FXML
    protected void clear() {
        login.clear();
        password.clear();
        labelText.setText("");
    }

    @FXML
    protected void validate() {
        String      pwd = "287782ef42356ef3d6551590f1ef0117b71d876df0f9b3eb58d088864770c74c";
        String      usr = "1532e76dbe9d43d0dea98c331ca5ae8a65c5e8e8b99d3e2a42ae989356f6242a";
        String      p;
        String      u;

        p = password.getText();
        u = login.getText();
        if (AuthApplication.sha256(p).equals(pwd) && AuthApplication.sha256(u).equals(usr)) // Access granted
        {
            try {
                HomeScene();
            } catch (IOException e) { e.printStackTrace(); }
        }
        else
        {
            clear();
            labelText.setText("Credential error.");
        }
    }

    public void keyReleased(KeyEvent k)
    {
        if (k.getCode() == KeyCode.ESCAPE)
            close();
        if (k.getCode() == KeyCode.ENTER)
            validate();
    }

    @FXML
    public void HomeScene() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AuthApplication.class.getResource("root.fxml"));
            Scene scene = new Scene(loader.load(), 600, 380);
            Stage stage = (Stage) validate.getScene().getWindow();
            stage.setTitle("Accueil");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}