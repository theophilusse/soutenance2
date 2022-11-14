package com.example.exojavafx;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;

import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class LaunchTask extends Thread {

    private String[] e;
    private Multi input;
    private Multi retValue;

    public LaunchTask(String param, Multi input)
    {
        if (Parser.isEmpty(param))
            return ;
        e = param.split("[ \\t]");
        this.retValue = null;
        this.input = input;
    }

    public Multi result()
    {
        if (this.isAlive())
            return (null);
        return (retValue == null ? new Multi('E', null) : retValue);
    }

    private void taskScrap()
    {
        String[] filename;
        HashMap<String, Multi> arg;
        Multi ret;
        Table t;
        String out;

        arg = new HashMap<String, Multi>();
        filename = e[1].split("[/]");
        if (Parser.isEmpty(filename))
            return ;
        out = "";
        arg.put("scriptName", new Multi('s', filename[filename.length - 1])); // Domain name
        arg.put("inputData", input); // Domain name
        try {
            retValue = new Script(e[1], false).result(arg);
            if (retValue != null && retValue.getType() == 'K')
            {
                ret = ((HashMap<String, Multi>)retValue.getData()).get("outputTable");
                if (ret != null) // Todo REMETTRE ------------
                    //throw new Exception(); // Todo REMETTRE ------------
                {
                    if (ret.getType() != 't') {
                        out += "Bad return value. Not a table.\n";
                        System.out.println(out);
                        //throw new Exception(); // Todo REMETTRE ------------
                    }
                    t = (Table)ret.getData();
                    /*
                    for (int i = 0; i < t.width(); i++)
                        out += t.getColName(i) + " \t";
                    out += "\n";
                    */
                    for (int i = 0; i < t.length(); i++)
                        out += t.getRow(i) + "\n";
                }
                retValue = new Multi('s', out);
            }
            /*else
            {
                out += retValue == null ? "Bad return value.\n" : retValue + "\n";
                throw new Exception();
            }*/
        }
        catch (Exception e)
        {
            retValue = null;
            e.printStackTrace();
            if (!Parser.isEmpty(e.getMessage()))
                out += e.getMessage() + "\n";
            out += "Script error.";
            try {
                AuthApplication.PopupError(out);
            } catch (Exception ee) { ; }
        }
    }

    private boolean sendMail(String mailFrom, String mailTo) { // Todo ---------------------- ---------------------- ---------------------- ---------------------- !!!
        if (Parser.isEmpty(mailTo) || Parser.isEmpty(ProgramConstant.sendinblueAPIKey))
        {
            try {
                if (Parser.isEmpty(ProgramConstant.sendinblueAPIKey))
                    AuthApplication.PopupError("Please configure your API key in ProgramConstant.java");
            } catch (Exception e) { ; }
            return (true);
        }
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(ProgramConstant.sendinblueAPIKey); // Todo

        try {
            // Expéditeur
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(mailFrom);
            sender.setName(mailFrom.split("[@]")[0].split("[\\.]]")[0]);

            System.out.println("From: " + mailFrom.split("[@]")[0].split("[\\.]]")[0]); // Debug
            // Destinataire
            List<SendSmtpEmailTo> toList = new ArrayList<SendSmtpEmailTo>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();

            to.setEmail(mailTo);
            toList.add(to);

            // Pièce jointe
            SendSmtpEmailAttachment attachment = new SendSmtpEmailAttachment();
            attachment.setName("data.csv");
            attachment.setContent(AuthApplication.sendBuffer.getBytes());
            List<SendSmtpEmailAttachment> attachmentList = new ArrayList<SendSmtpEmailAttachment>();
            attachmentList.add(attachment);

            // Propriétés du mail
            //Properties headers = new Properties();
            //headers.setProperty("Some-Custom-Name", "unique-id-1234");
            Properties params = new Properties();
            //params.setProperty("parameter", "My param value");      // ??
            params.setProperty("subject", "Recherche");              // sujet du mail
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(toList);
            sendSmtpEmail.setHtmlContent("<html><body><h4>Bonjour,<br><br>Vous trouverez en pièce jointe, le fichier texte " +
                    "contenant les résultats de votre recherche.</h4></body></html>");         // corps du mail
            sendSmtpEmail.setSubject("{{params.subject}}");
            sendSmtpEmail.setAttachment(attachmentList);
            sendSmtpEmail.setHeaders(new Properties());
            sendSmtpEmail.setParams(params);

            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            try {
                AuthApplication.Popup("Mail", response.getMessageId()); // Test
            } catch (Exception e) { ; }
        } catch (Exception e) {
            try {
                AuthApplication.PopupError("Failed to send mail. Sendinblue (MTA) says: " + e.getMessage());
            } catch (Exception ee) { ; }
            return (true);
        }
        return (false);
    }

    private void taskMail()
    {
        try
        {
            sendMail(e[1], e[2]);
        }
        catch (Exception ee)
        {
            try {
                AuthApplication.Popup("Mail", "Send mail from " + e[1] + " to " + e[2] + " : FAILURE");
            } catch (Exception eee) { ; }
        }
    }

    private void taskDb()
    {
        Row in;
        Multi tmp;
        Database db;
        String data;
        String error;

        error = "Send database failed: Bad thread input.";
        if (input == null || input.getType() != 'r')
        {
            try {
                AuthApplication.PopupError(error);
            } catch (Exception e) { ; }
            return ;
        }
        in = (Row)input.getData();
        if (in == null)
        {
            try {
                AuthApplication.PopupError(error);
            } catch (Exception e) { ; }
            return ;
        }
        tmp = in.getCol(0);
        if (tmp == null || tmp.getType() != 'o')
        {
            try {
                AuthApplication.PopupError(error);
            } catch (Exception e) { ; }
            return ;
        }
        db = (Database)tmp.getData();
        if (db == null)
        {
            try {
                AuthApplication.PopupError(error);
            } catch (Exception e) { ; }
            return ;
        }
        tmp = in.getCol(1);
        if (tmp == null || tmp.getType() != 's')
        {
            try {
                AuthApplication.PopupError(error);
            } catch (Exception e) { ; }
            return ;
        }
        data = (String)tmp.getData();
        if (Parser.isEmpty(data))
        {
            try {
                AuthApplication.PopupError("Send database failed: No data to send.");
            } catch (Exception e) { ; }
            return ;
        }
        if (Database.dbsend(db, data))
        {
            try {
                AuthApplication.PopupError("Send database failed: Network error.");
            } catch (Exception e) { ; }
            return ;
        }
        try {
            AuthApplication.Popup("Success", "Send database success.");
        } catch (Exception e) { ; }
    }

    public void taskSaveFile()
    {
        File file;
        FileChooser fileChooser;

        file = null;
        try {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer dans un fichier");
            file = fileChooser.showSaveDialog(new Stage());
            if (file != null)
            {
                Terminal.ecrireFichier(file.toString(), new StringBuffer(AuthApplication.sendBuffer));
                AuthApplication.Popup("Save", "Data successfully saved in [" + file.toString() + "].");
            }
        }
        catch (Exception e)
        {
            try {
                AuthApplication.PopupError("Error saving file" + (file != null ? " (" + file.toString() + ")." : "."));
            } catch (Exception ee) { ; }
        }
    }

    public void taskSearch()
    {
        ArrayList<LaunchTask> taskList;
        String output;
        Multi tmp;

        if (input == null || input.getType() != 'o')
            return;
        taskList = (ArrayList<LaunchTask>)input.getData();
        output = "";
        try {
            for (int i = 0; i < taskList.size(); i++)
                taskList.get(i).run();
            for (int i = 0; i < taskList.size(); i++)
            {
                tmp = null;
                while (tmp == null)
                    tmp = taskList.get(i).result();
                if (tmp.getType() != 'E')
                {
                    System.out.println("OUT:: [" + tmp + "]"); // Debug
                    output += tmp;
                }
            }
        }
        catch (Exception e)
        {
            try {
                AuthApplication.PopupError("Thread join error");
                //outputZone.setText("Thread join error");
            } catch (Exception ee) { ; }
        }
        if (Parser.isEmpty(output))
        {
            try {
                AuthApplication.Popup("Scrap", "No output.");
            } catch (Exception e) { ; }
        }
        else
            AuthApplication.sendBuffer = output;
    }

    public void run()
    {
        // LaunchTask t = new LaunchTask("scrap leboncoin");
        // LaunchTask t = new LaunchTask("scrap fnac");
        // LaunchTask t = new LaunchTask("mail");
        // LaunchTask t = new LaunchTask("dbsend");
        // ...
        if (Parser.isEmpty(e))
            return ;
        if (e[0].equals("scrap") && e.length == 2)
            taskScrap();
        else if (e[0].equals("mail") && e.length == 3)
            taskMail();
        else if (e[0].equals("dbsend"))
            taskDb();
        else if (e[0].equals("savefile"))
            taskSaveFile();
        else if (e[0].equals("search"))
            taskSearch();
    }
}
