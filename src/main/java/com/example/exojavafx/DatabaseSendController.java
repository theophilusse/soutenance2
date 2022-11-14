package com.example.exojavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class DatabaseSendController {
    @FXML
    protected Button bValidate;
    @FXML
    protected Button bClose;

    @FXML
    protected void send()
    {
        Database.dbsend(AuthApplication.db, AuthApplication.sendBuffer);
        close();
    }

    public void keyReleased(KeyEvent k)
    {
        if (k.getCode() == KeyCode.ESCAPE)
            close();
        if (k.getCode() == KeyCode.ENTER)
            send();
    }

    @FXML
    protected void close()
    {
        ((Stage)bClose.getScene().getWindow()).close();
    }
}
