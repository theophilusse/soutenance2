package com.example.exojavafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MailController {

    @FXML
    protected TextField fMail;

    @FXML
    protected void close()
    {
        ((Stage)fMail.getScene().getWindow()).close();
    }

    @FXML
    protected boolean send()
    {
        boolean ret;
        String mail;

        mail = fMail.getText();
        if (mail == null || mail.length() == 0)
        {
            try {
                AuthApplication.PopupError("Enter valid email address"); // Todo ----- https://www.tutorialspoint.com/java/java_sending_email.htm
            } catch (Exception e) {}
            return (true);
        }
        ret = sendMail(ProgramConstant.senderAddress, mail);
        close();
        return (ret);
    }

    private boolean sendMail(String from, String to)
    {
        try {
            System.out.println("Sending email from:["+from+"] to:["+to+"]"); // Debug
            LaunchTask task = new LaunchTask("mail " + from + " " + to, null); // Todo ------------- output
            task.run();
            return (false);
            //AuthApplication.PopupError("Not implemented"); // Todo ----- https://www.tutorialspoint.com/java/java_sending_email.htm
        } catch (Exception e) {} return (true);
    }

    public void keyReleased(KeyEvent k)
    {
        if (k.getCode() == KeyCode.ESCAPE)
            close();
        if (k.getCode() == KeyCode.ENTER)
            send();
    }
}
