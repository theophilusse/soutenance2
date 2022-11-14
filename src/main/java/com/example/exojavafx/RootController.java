package com.example.exojavafx;
import java.io.File;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootController {
    @FXML
    protected ImageView imgLogo;

    @FXML
    protected MenuItem bSaveFile; //
    @FXML
    protected MenuItem bSendMail; //
    @FXML
    protected MenuItem bDBSave; //
    @FXML
    protected MenuItem bQuit; //
    @FXML
    protected MenuItem bDBSettings; //
    @FXML
    protected MenuItem bGuide; //
    @FXML
    protected MenuItem bGuideTech; //

    @FXML
    protected TextField fTitle;

    @FXML
    protected ComboBox<String> fGender;

    @FXML
    protected DatePicker fDate;

    @FXML
    protected TextField fPriceMin;

    @FXML
    protected TextField fPriceMax;

    @FXML
    protected TextArea outputZone;
    @FXML
    protected Button bSearch; //
    @FXML
    protected Button bClear; //

    @FXML
    protected Button bRefresh; //

    @FXML
    ArrayList<CheckBox> cSite;

    private ArrayList<String>   site;
    private ArrayList<Script>   script;

    @FXML
    //protected ScrollPane pane;
    protected ScrollPane pane;
    @FXML
    protected HBox hbox;
    @FXML
    protected void initialize()
    {
        refreshSite();
    }

    static int picIndex = 0;
    @FXML
    protected void beast()
    {
        String[] file;

        file = new String[5];
        file[0] = "oiseau.png";
        file[1] = "arbre1.png";
        file[2] = "cabane.png";
        file[3] = "renard.png";
        file[4] = "poussin.png";
        imgLogo.setImage(new Image("file:/home/greta/IdeaProjects/exoJavaFx/target/classes/images/" + file[picIndex++]));
        if (picIndex == file.length)
            picIndex = 0;
        //imgAnimal.setImage(new Image("file:../../../images/" + file[picIndex++ % file.length])); // Marche pas ?
    }

    @FXML
    protected void refreshSite()
    {
        Set<String> scriptFile;
        Iterator<String> iter;
        ListView<CheckBox> listView;
        String[] tmp;
        int k;

        scriptFile = AuthApplication.listFiles(ProgramConstant.scriptDir);
        if (scriptFile == null)
            return ;
        site = new ArrayList<String>();
        script = new ArrayList<Script>();
        pane = new ScrollPane();
        listView = new ListView<CheckBox>();
        cSite = new ArrayList<CheckBox>();
        iter = scriptFile.iterator();
        while (iter.hasNext())
        {
            tmp = iter.next().split("[/]");
            if (tmp[tmp.length - 1].charAt(0) == '.')
                continue;
            site.add(tmp[tmp.length - 1]);
        }
        for (int i = 0; i < site.size(); i++)
            cSite.add(new CheckBox(site.get(i)));
        listView.setItems(FXCollections.observableList(cSite));
        pane.setContent(listView);
        hbox.getChildren().setAll(pane);
    }

    @FXML
    protected void saveFile()
    {
        LaunchTask task;

        task = new LaunchTask("savefile", null);
        if (task == null)
            return ;
        task.run();
    }

    @FXML
    protected void mailSend()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AuthApplication.class.getResource("mail-send.fxml"));
            Scene scene = new Scene(loader.load(), 600, 380);
            Stage stage = new Stage();
            stage.setTitle("Envoi courriel");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void dbSend()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AuthApplication.class.getResource("db-send.fxml"));
            Scene scene = new Scene(loader.load(), 600, 380);
            Stage stage = new Stage();
            stage.setTitle("Transmission BDD");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void dbSettings()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AuthApplication.class.getResource("db-settings.fxml"));
            Scene scene = new Scene(loader.load(), 600, 380);
            Stage stage = new Stage();
            stage.setTitle("Parametres de la base de donnees");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void guide()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AuthApplication.class.getResource("guide.fxml"));
            Scene scene = new Scene(loader.load(), 600, 380);
            Stage stage = new Stage();
            stage.setTitle("Guide utilisateur");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void guideTechnical()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AuthApplication.class.getResource("guideTechnical.fxml"));
            Scene scene = new Scene(loader.load(), 600, 380);
            Stage stage = new Stage();
            stage.setTitle("Guide technique");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private Multi makeInput()
    {
        String tmp;
        Table t;
        Row r;

        t = new Table("ssDff", "title gender date priceMin priceMax", "input");
        r = new Row();
        tmp = fGender.getSelectionModel().getSelectedItem();
        if (tmp == null || tmp.equals("Selectionnez un genre"))
            tmp = "";
        // TODO ----------------------------------------------------------------------------------------------=-=-=-=_+_+_=-+_+_+_=-=-_=_++-_++_+_
        r.addCol(new Multi('s', fTitle.getText()));
        r.addCol(new Multi('s', tmp));
        tmp = fDate.getConverter().toString();
        if (tmp == null || tmp.split("[/]").length != 3)
            tmp = "";
        r.addCol(new Multi('s', tmp));
        r.addCol(new Multi('s', Parser.getNumericType(fPriceMin.getText()) == -1 ? "" : fPriceMin.getText()));
        r.addCol(new Multi('s', Parser.getNumericType(fPriceMax.getText()) == -1 ? "" : fPriceMax.getText()));
        t.insert(r);
        return (new Multi('t', t));
    }

    @FXML
    protected void search()
    {
        ArrayList<LaunchTask> taskList;

        //outputZone.clear();
        taskList = new ArrayList<LaunchTask>();
        for (int i = 0; i < cSite.size(); i++)
            if (cSite.get(i).isSelected())
                taskList.add(new LaunchTask("scrap " + ProgramConstant.scriptDir + File.separator + site.get(i), makeInput()));
        new LaunchTask("search ", new Multi('o', taskList)).run();
        outputZone.setText(AuthApplication.sendBuffer);
    }

    @FXML
    protected void close() {
        Platform.exit();
    }

    public void onPopupClick() throws IOException {
        PopupScene();
    }

    public void PopupScene() throws IOException {
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Popup");
        Label label1 = new Label("Ceci est une popup");
        Button button1 = new Button("fermer");
        button1.setOnAction(e -> popupwindow.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 300, 250);
        //popupwindow.setX(Math.random() * 1080);
        //popupwindow.setY(Math.random() * 720);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
        //popupwindow.show();
    }
}
