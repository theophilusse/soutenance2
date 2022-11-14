package com.example.exojavafx;

import com.gargoylesoftware.htmlunit.WebClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sibModel.SendEmail;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthApplication extends Application {

    public static Database  db;
    public static String    sendBuffer;

    @Override
    public void start(Stage stage) throws IOException {
        db = new Database();
        sendBuffer = "";
        FXMLLoader fxmlLoader = new FXMLLoader(AuthApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        WebClient w;
        launch();

        /*
            Script test = new Script("print.scrap", false);

            HashMap<String, Multi> param;

            param = new HashMap<String, Multi>();
            param.put("arg", new Multi('s', "leboncoin.fr"));
            Multi ret;
            ret = test.result(param);
            System.out.println("Return = " + ((HashMap)ret.getData()).get("arg"));
            return ;
        */
        /*
        SendEmail s;
        List<String> to = new List<String>();
        to.add("theophile.trosseau@laposte.net");
        s.emailTo();
        */
    }

    public static String sha256(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void PopupError(String message) throws IOException {
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Error");
        Label label1 = new Label(message);
        Button button1 = new Button("fermer");
        button1.setOnAction(e -> popupwindow.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 300, 250);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    public static void Popup(String title, String message) throws IOException {
        if (Parser.isEmpty(title))
            title = "Message";
        if (Parser.isEmpty(message))
            return ;
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle(title);
        Label label1 = new Label(message);
        Button button1 = new Button("fermer");
        button1.setOnAction(e -> popupwindow.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 300, 250);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    public static Set<String> listFiles(String dir)
    {
        return (Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory()).map(File::getName).collect(Collectors.toSet()));
    }
}