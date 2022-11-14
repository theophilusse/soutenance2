package com.example.exojavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;

public class Database {
    private String serverName;
    private String dbName;
    private int portNum;
    private String user;
    private String pass;
    private TextArea output;

    public Database()
    {
        serverName = null;
        dbName = null;
        portNum = -1;
        user = null;
        pass = null;
    }

    public boolean send(TextArea output, String data)
    {
        Row input;

        if (empty(data))
        {
            try {
                AuthApplication.PopupError("Donnee manquante");
            } catch (Exception e) { return (true); }
            return (true);
        }
        try {
            input = new Row();
            input.addCol(new Multi('o', this));
            input.addCol(new Multi('s', data));
            new LaunchTask("dbsend", new Multi('r', input)).run();
            return (false);
        } catch (Exception e) { return (true); }
    }

    private static int randomInt()
    {
        return ((int)(Math.random() * 420000000));
    }

    private static String makeSqlInsert(String data)
    {
        String sqlQuery = "INSERT INTO `search` ( `id`, `label`, `id_gender`, `date`, `prix`, `source` ) VALUES ";
        String[] r;
        String c;
        int n;
        int j;

        if (Parser.isEmpty(data))
            return (null);
        n = 0;
        r = data.split("[\\n]");
        for (int i = 0; i < r.length; i++)
        {
            n = 1;
            sqlQuery += "( " + randomInt(); // Primary key
            j = 0;
            while (j < r[i].length() && n < ProgramConstant.tableSearchWidth)
            {
                if (r[i].charAt(j) == '"')
                {
                    c = Parser.parseStringFormat(r[i].substring(j), false);
                    sqlQuery += ", ";
                    if (n == 2 || n == 4) // Numeric
                    {
                        sqlQuery += "0"; // c Todo
                    }
                    else // String
                    {
                        //sqlQuery += "'" + (Parser.isEmpty(c) ? "NULL" : c) + "'";
                        sqlQuery += "'" + c + "'";
                    }
                    j += c.length() + 2;
                    n++;
                }
                else
                    j++;
            }
            while (n < ProgramConstant.tableSearchWidth)
            {
                if (n != 0)
                    sqlQuery += ", ";
                if (n == 0 || n == 2) // numeric key
                {
                    sqlQuery += n == 0 ? "" + randomInt() : "0"; // c Todo
                }
                else // string
                {
                    sqlQuery += "NULL";
                }
                n++;
            }
            sqlQuery += ")" + (i + 1 < r.length ? "," : ";");
        }
        return (n != ProgramConstant.tableSearchWidth ? null : sqlQuery);
    }

    public static boolean dbsend(Database db, String data)
    {
        Connection conn;
        Statement stm;
        String url;

        if (Parser.isEmpty(data))
        {
            try {
                AuthApplication.Popup("Database", "Pas de donnees a transmettre.");
            } catch (Exception e) { ; }
           return (true);
        }
        if (db == null || db.getPortNum() < 0 || db.getPortNum() > 64000 || Parser.isEmpty(db.getDbName()) ||
                Parser.isEmpty(db.getServerName()) || Parser.isEmpty(db.getUser()) != Parser.isEmpty(db.getPass()))
        {
            try {
                AuthApplication.Popup("Database", "Echec envoie. Veuillez configurer la connexion.");
            } catch (Exception e) { ; }
            return (true);
        }
        try {
            url = "jdbc:mysql://" + db.getServerName() + ":" + db.getPortNum() + "/" + db.getDbName();
            conn = DriverManager.getConnection(url, Parser.isEmpty(db.getUser()) ? "root" : db.getUser(), db.getPass());
            if (conn == null)
            {
                try {
                    AuthApplication.PopupError("Can't establish connection.");
                } catch (Exception ee) { ; }
                return (true);
            }
            stm = conn.createStatement();
            if (stm == null)
            {
                try {
                    AuthApplication.PopupError("Can't create statement.");
                } catch (Exception ee) { ; }
                return (true);
            }
            try {
                conn.setAutoCommit(true);
                int res = stm.executeUpdate(makeSqlInsert(data));
                //conn.commit(); // Commit envoye
            } catch (Exception exception) {
                try {
                    AuthApplication.PopupError("JDBC:: " + exception.getMessage());
                } catch (Exception ee) { ; }
                return (true);
            }
            AuthApplication.Popup("Database", "Insertion reussie.");
            return (false);
        } catch (Exception e)
        {
            try {
                AuthApplication.PopupError("JDBC: " + e.getMessage());
            } catch (Exception ee) { ; }
        };
        return (true);
    }

    private boolean empty(String s)
    {
        return (s == null || s.length() == 0);
    }

    public String getServerName()
    {
        return (serverName);
    }

    public String getDbName()
    {
        return (dbName);
    }

    public String getUser()
    {
        return (user);
    }

    public String getPass()
    {
        return (pass);
    }

    public int getPortNum()
    {
        return (portNum);
    }

    public void setServerName(String serverName)
    {
        if (!empty(serverName))
            this.serverName = serverName;
    }

    public void setDbName(String dbName)
    {
        if (!empty(dbName))
            this.dbName = dbName;
    }

    public void setPortNum(int portNum)
    {
        if (portNum > 0 && portNum < 64000)
            this.portNum = portNum;
    }

    public void setUser(String user)
    {
        if (!empty(user))
            this.user = user;
    }

    public void setPass(String pass)
    {
        if (!empty(pass))
            this.pass = pass;
    }
}
