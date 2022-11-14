package com.example.exojavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GuideController {
    @FXML
    protected TextArea sGuide;

    @FXML
    protected void initialize()
    {
        String[] buf;
        String guide;

        guide = "";
        try {
            buf = Terminal.lireFichierTexte("guideUtilisateur.txt");
            if (buf == null)
                throw new Exception();
            for (int i = 0; i < buf.length; i++)
                guide += buf[i] + "\n";
            sGuide.setText(guide);
        }
        catch (Exception e)
        {
            sGuide.setText("Guide introuvable");
        }
    }

    public void keyReleased(KeyEvent k)
    {
        if (k.getCode() == KeyCode.ESCAPE)
            close();
        if (k.getCode() == KeyCode.ENTER)
            close();
    }

    @FXML
    protected void close()
    {
        ((Stage)sGuide.getScene().getWindow()).close();
    }
}
