package com.example.exojavafx;

import java.util.ArrayList;
import java.util.Locale;

public class Table {
    private int width;
    private String type; // ex: "ssfD"
    private String name; // ex: "Recherche LBC"
    private String[] colName; // ex: "Titre Description Prix Date"
    private ArrayList<Row> row;

    public Table(String type, String colName, String name)
    {
        if (type == null || colName == null || name == null || name.length() == 0 || type.length() < 1)
            return ;
        this.name = name;
        this.colName = colName.split(" ");
        width = type.length();
        if (this.colName.length != width)
            return ;
        this.type = type.toLowerCase();
        row = new ArrayList<Row>();
        //infoTable();
    }

    public void infoTable()
    {
        String out;

        out = " -- Table " + name + " type:[" + type + "] ";
        for (String s : colName)
            out += " ; " + s;
        System.out.print(out + "\n"); // Debug
    }

    public int length()
    {
        return (row.size());
    }

    public int width()
    {
        return (width);
    }

    public String           getColName(int index)
    {
        if (index < 0 || index >= colName.length)
            return (null);
        return (colName[index]);
    }
    public Row              getRow(int index)
    {
        if (index < 0 || index > row.size())
            return (null);
        return (row.get(index));
    }

    public ArrayList<Multi> getCol(int index)
    {
        ArrayList<Multi> ret;

        if (index < 0 || index > width)
            return (null);
        ret = new ArrayList<Multi>();
        for (int i = 0; i < row.size(); i++)
            ret.add(row.get(i).getCol(index));
        return (ret);
    }

    public ArrayList<Multi> getCol(String col)
    {
        if (colName == null || colName.length == 0)
            return (null);
        for (int i = 0; i < colName.length; i++)
            if (colName[i].equals(col))
                return (getCol(i));
        return (null);
    }

    public String getName()
    {
        return (name);
    }

    public boolean badRow(Row r)
    {
        int                 size;

        if (r == null)
            return (true);
        size = r.size();
        if (width != size)
            return (true);
        for (int i = 0; i < size; i++)
            if (r.getCol(i).getType() != type.charAt(i))
                return (true);
        return (false);
    }

    public boolean insert(Row r)
    {
        //return (badRow(r) ? true : row.add(r));
        return (row.add(r));
    }

    public boolean insert(Row r, int index)
    {
        /*
            if (badRow(r))
                return (true);
        */
        if (index < 0 || index >= row.size())
            return (true);
        row.add(index, r);
        return (false);
    }

    public boolean setRow(Row r, int index)
    {
        /*
            if (badRow(r))
                return (true);
        */
        if (index < 0 || index >= row.size())
            return (true);
        row.set(index, r);
        return (false);
    }

    public boolean remove(int index)
    {
        if (index < 0 || index > row.size())
            return (true);
        row.remove(index);
        return (false);
    }

    public Row pop()
    {
        int index;
        Row r;

        index = row.size() - 1;
        if (index < 0)
            return (null);
        r = row.get(index);
        row.remove(index);
        return (r);
    }

    public String info(boolean vertical)
    {
        String      ret;
        char        c;

        ret = "Table " + name + " (#" + row.size() + "):" + (vertical ? "" : "\n");
        for (int i = 0; i < width; i++)
        {
            ret += (vertical ? "\n" : " || ") + colName[i] + " ";
            c = type.charAt(i);
            if (c == 's')
                ret += "(STRING)";
            else if (c == 'c')
                ret += "(CHAR)";
            else if (c == 'i')
                ret += "(INT)";
            else if (c == 'd')
                ret += "(DOUBLE)";
            else if (c == 'f')
                ret += "(FLOAT)";
            else if (c == 'l')
                ret += "(LONG)";
            else if (c == 'D')
                ret += "(DATE)";
            else if (c == 't')
                ret += "(TABLE)";
            else if (c == 'e')
                ret += "(EXPRESSION)";
            else if (c == 'O')
                ret += "(OPERATOR)";
            else if (c == 'v')
                ret += "(VERB)";
            else if (c == 'b')
                ret += "(BOOL)";
            else if (c == 'o')
                ret += "(OBJECT)";
            else
                ret += "Unknown";
        }
        return (ret + "\n");
    }

    public String toString()
    {
        String ret;

        ret = "";
        for (int i = 0; i < row.size(); i++)
            ret += i + ": " + row.get(i).toString();
        return (ret);
    }

    public String toString(boolean info)
    {
        String ret;

        ret = info ? info(false) : "";
        for (int i = 0; i < row.size(); i++)
            ret += i + ": " + row.get(i).toString();
        return (ret);
    }
}
