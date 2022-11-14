package com.example.exojavafx;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private int            width;
    private ArrayList<Multi> col;

    public Row()
    {
        width = 0;
        this.col = new ArrayList<Multi>();
    }

    public void addCol(Multi c)
    {
        if (c == null)
            return ;
        col.add(c);
        width++;
    }

    public void removeCol(int index)
    {
        if (index < 0 || index >= col.size())
            return ;
        col.remove(index);
    }

    public Row(ArrayList<Multi> col)
    {
        width = col.size();
        if (col == null || col.size() == 0)
            return ;
        this.col = col;
    }

    public Row(List<HtmlElement> col)
    {
        this.col = new ArrayList<Multi>();
        width = col.size();
        if (col == null || col.size() == 0)
            return ;
        for (int i = 0; i < col.size(); i++)
            this.col.add(new Multi('H', col.get(i)));
    }

    public ArrayList<Multi> getCol() {
        return col;
    }

    public Multi getCol(int index) {
        if (index < 0 || index > col.size())
            return (null);
        return col.get(index);
    }

    public void setCol(int index, Multi c)
    {
        if (index > col.size())
            index = col.size();
        else if (index < 0)
            index = 0;
        col.set(index, c == null ? new Multi('E', null) : c);
    }

    public int size()
    {
        return (col.size());
    }

    public String toString()
    {
        String ret;

        ret = "";
        for (int i = 0; i < width; i++)
            ret += "\"" + col.get(i).toString().replaceAll("[\\\\]", "\\\\").replaceAll("[\"]", "\\\"").replaceAll("[']", "\\'") + "\"" + (i + 1 < width ? ";" : "");
        return (ret);
    }
}
