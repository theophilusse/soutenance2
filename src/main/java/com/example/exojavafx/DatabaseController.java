package com.example.exojavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class DatabaseController {
    @FXML
    protected TextField fHost;
    @FXML
    protected TextField fName;
    @FXML
    protected TextField fPort;
    @FXML
    protected TextField fUser;
    @FXML
    protected TextField fPassword;
    @FXML
    protected Button bClose;

    protected void initialize()
    {
        fHost.setText(AuthApplication.db.getServerName());
        fName.setText(AuthApplication.db.getDbName());
        fPort.setText("" + AuthApplication.db.getPortNum());
        fUser.setText(AuthApplication.db.getUser());
        fPassword.setText(AuthApplication.db.getPass());
    }
    @FXML
    protected void validate()
    {
        String serverName;
        String dbName;
        int    portNum;
        String user;
        String pass;

        serverName = fHost.getText();
        dbName = fName.getText();
        try {
            portNum = Integer.parseInt(fPort.getText());
        } catch (Exception e) { portNum = -1; }
        user = fUser.getText();
        pass = fPassword.getText();
        if (empty(serverName) || empty(dbName) || portNum < 0 || (empty(user) != empty(pass)))
        {
            try {
                AuthApplication.PopupError("Veuillez remplir correctement tout les champs");
            } catch (Exception e) {}
            return ;
        }
        AuthApplication.db.setServerName(serverName);
        AuthApplication.db.setDbName(dbName);
        AuthApplication.db.setPortNum(portNum);
        AuthApplication.db.setUser(user);
        AuthApplication.db.setPass(pass);
        ((Stage)bClose.getScene().getWindow()).close();
    }

    public void keyReleased(KeyEvent k)
    {
        if (k.getCode() == KeyCode.ESCAPE)
            close();
        if (k.getCode() == KeyCode.ENTER)
            validate();
    }

    @FXML
    protected void close()
    {
        ((Stage)bClose.getScene().getWindow()).close();
    }

    private boolean empty(String s)
    {
        return (s == null || s.length() == 0);
    }
    private void clear() {
        fHost.clear();
        fName.clear();
        fPort.clear();
        fUser.clear();
        fPassword.clear();
    }
}
