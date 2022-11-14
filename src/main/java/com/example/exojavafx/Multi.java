package com.example.exojavafx;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.*;

public class Multi<T, N> {
    private char type;
    private T    data;

    public Multi()
    {
        this.type = 'o';
        this.data = null;
    }

    public Multi(char type, T data)
    {
        //System.out.println("NEW MULTI = " + type + " data:" + data); // Debug
        this.type = type;
        this.data = data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public char getType()
    {
        return (type);
    }

    public T getData()
    {
        return (data);
    }

    public Multi any(Multi value)
    {
        Table t;
        Row r;

        if (getType() != 'r' && getType() != 't')
            return (new Multi('E', null));
        if (value == null)
            value = new Multi('E', null);
        if (getType() == 'r')
        {
            r = (Row)getData();
            for (int i = 0; i < r.size(); i++)
                r.setCol(i, value);
        }
        else if (getType() == 't')
        {
            t = (Table)getData();
            for (int i = 0; i < t.length(); i++)
                new Multi('r', t.getRow(i)).any(value);
        }
        return (new Multi(getType(), getData()));
    }

    public Multi type()
    {
        return (new Multi('c', getType()));
    }

    public Multi length()
    {
        if (getType() == 's')
            return (new Multi('i', ((String)getData()).length()));
        else if (getType() == 'r') // Todo ARRAY LENGTH ------------------------------------------------------
            return (new Multi('i', ((Row)getData()).size()));
        else if (getType() == 't')
            return (new Multi('i', ((Table)getData()).length()));
        //return (new Multi('i', 0));
        //return (new Multi('i', toString().length()));
        return (new Multi('E', null));
    }

    public Multi width()
    {
        if (getType() == 't')
            return (new Multi('i', ((Table)getData()).width()));
        return (new Multi('E', null));
    }

    public Multi split(ArrayList<Multi> param)
    {
        ArrayList<Multi>  list;
        String[]          array;
        String regexp;
        String tmp;

        System.out.println("DEBUG SPLIT ::: ["+param.size()+"]"); // Debug
        if (param == null || param.size() == 0)
            regexp = " ";
        else if (param.size() != 1)
            return (new Multi('E', null));
        else
            regexp = ((String)param.get(0).getData());
        System.out.println("SPLIT :: A ["+getType()+"]"); // Debug
        if (getType() != 's')
            tmp = toString();
        else
            tmp = (String)getData();
        System.out.println("SPLIT :: B ["+tmp+"]["+regexp+"]"); // Debug
        if (Parser.isEmpty(tmp) || Parser.isEmpty(regexp))
            return (new Multi('E', null));
        System.out.println("SPLIT :: C"); // Debug
        array = tmp.split(regexp);
        list = new ArrayList<Multi>();
        for (int i = 0; i < array.length; i++)
        {
            System.out.println("SPLIT ADD ["+array[i]+"]"); // Debug
            list.add(new Multi('s', array[i]));
        }
        System.out.println("SPLIT :: D"); // Debug
        return (new Multi('r', new Row(list)));
    }

    public Multi trim()
    {
        return (getType() == 's' ? new Multi('s', ((String)getData()).trim()) : new Multi('E', null));
    }

    public Multi uppercase()
    {
        return (getType() == 's' ? new Multi('s', ((String)getData()).toUpperCase()) : new Multi('E', null));
    }

    public Multi lowercase()
    {
        return (getType() == 's' ? new Multi('s', ((String)getData()).toLowerCase()) : new Multi('E', null));
    }

    public Multi capitalize()
    {
        boolean up;
        String str;
        String ret;
        char c;

        str = (String)getData();
        ret = "";
        up = true;
        for (int i = 0; i < str.length(); i++)
        {
            c = str.charAt(i);
            if (up && Parser.isAlphabetic(c))
            {
                ret += ("" + c).toUpperCase();
                up = false;
            }
            else if (Parser.isBlank(c) || c == '.')
                up = true;
        }
        return (getType() == 's' ? new Multi('s', ret) : new Multi('E', null));
    }

    public Multi substring(ArrayList<Multi> param)
    {
        if (param == null || param.size() < 1 || param.size() > 2 || getType() != 's' || param.get(0).getType() != 'i' ||
                (param.size() == 2 && param.get(1).getType() != 'i'))
            return (new Multi('E', null));
        System.out.println("DEBUG SUBSTRING ::: " + ((String)getData()).substring((int)param.get(0).getData())); // Debug
        if (param.size() == 1)
            return (new Multi('s', ((String)getData()).substring((int)param.get(0).getData())));
        return (new Multi('s', ((String)getData()).substring((int)param.get(0).getData(), (int)param.get(1).getData())));
    }

    public Multi escape()
    {
        if (getType() != 's')
            return (new Multi('E', null));
        if (Parser.isEmpty((String)getData()))
            return (new Multi('s', ""));
        System.out.println("DEBUG ESCAPE ::: " + (String)getData()); // Debug
        return (new Multi('s', Parser.escape((String)getData())));
    }

    public Multi replace(ArrayList<Multi> param)
    {
        String ret;
        char src;
        char dst;

        if (getType() != 's' || param == null || param.size() != 2 || param.get(0).getType() != 'c' || param.get(1).getType() != 'c')
            return (new Multi('E', null));
        ret = (String)getData();
        src = (char)param.get(0).getData();
        dst = (char)param.get(1).getData();
        return (new Multi('s', ret.replace(src, dst)));
    }

    public Multi replaceAll(ArrayList<Multi> param)
    {
        String ret;
        String regex;
        String dst;

        if (getType() != 's' || param == null || param.size() != 2 || param.get(0).getType() != 's' || param.get(1).getType() != 's')
            return (new Multi('E', null));
        ret = (String)getData();
        regex = (String)param.get(0).getData();
        dst = (String)param.get(1).getData();
        System.out.println("REPLACEALL : [" + ret + "]["+regex+"]["+dst+"]"); // Debug
        return (new Multi('s', ret.replaceAll(regex, dst)));
    }

    public Multi replaceFirst(ArrayList<Multi> param)
    {
        String ret;
        String regex;
        String dst;

        if (getType() != 's' || param == null || param.size() != 2 || param.get(0).getType() != 's' || param.get(1).getType() != 's')
            return (new Multi('E', null));
        ret = (String)getData();
        regex = (String)param.get(0).getData();
        dst = (String)param.get(1).getData();
        return (new Multi('s', ret.replaceFirst(regex, dst)));
    }

    public Multi get(ArrayList<Multi> param)
    {
        int index;

        if (param == null || param.size() != 1 || (getType() != 'r' && getType() != 's' && getType() != 't') || param.get(0).getType() != 'i')
            return (new Multi('E', null));
        index = (int) param.get(0).getData();
        if (getType() == 'r')
        {
            if (getData() == null)
                return (new Multi('E', null));
            if (index < 0)
                index = ((Row)getData()).size() + index;
            if (index >= ((Row)getData()).size() || index < 0)
                return (new Multi('E', null));
            return (((Row)getData()).getCol(index));
        }
        else if (getType() == 't')
        {
            if (index < 0)
                index = ((Table)getData()).length() + index;
            if (index >= ((Table)getData()).length() || index < 0)
                return (new Multi('E', null));
            return (new Multi('r', ((Table)getData()).getRow(index)));
        }
        else if (getType() == 's')
        {
            if (index < 0)
                index = ((String)getData()).length() + index;
            if (index >= ((String)getData()).length() || index < 0)
                return (new Multi('E', null));
            return (new Multi('c', ((String)getData()).charAt(index)));
        }
        return (new Multi('E', null));
    }

    public Multi set(ArrayList<Multi> param)
    {
        int index;

        if (param == null || param.size() != 2 || param.get(0).getType() != 'i' || (getType() != 'r' && getType() != 's' && getType() != 't'))
            return (new Multi('E', null));
        index = (int)param.get(0).getData();
        if (index < 0)
            return (new Multi('E', null));
        if (getType() == 'r')
        {
            ((Row)getData()).setCol(index, param.get(1));
            //return (param.get(1));
            return (new Multi('r', getData()));
        }
        else if (getType() == 's')
        {
            if ((param.get(1).getType() != 's' && param.get(1).getType() != 'c') || index >= ((String) getData()).length())
                return (new Multi('E', null));
            data = (T)(((String) getData()).substring(0, (int) param.get(0).getData()) + param.get(1).getData().toString() + ((String) getData()).substring((int) param.get(0).getData() + 1));
            return (new Multi('s', data));
        }
        else if (getType() == 't')
        {
            if (param.get(1).getType() != 'r' || index >= ((Table)getData()).length())
                return (new Multi('E', null));
            ((Table)getData()).insert((Row)param.get(1).getData(), (int)param.get(0).getData());
            return (new Multi('t', getData()));
        }
        return (new Multi('E', null));
    }

    public Multi add(ArrayList<Multi> param)
    {
        System.out.println("ADD METHOD (" + param.size() + ")"); // Debug
        if (param.size() != 0)
            System.out.println("DEBUG PARAM ::" + param.get(0).getType() + ":: " + param.get(0)); // Debug
        if (param == null || param.size() != 1 || (getType() != 'r' && getType() != 't') || (getType() == 't' && param.get(0).getType() != 'r'))
            return (new Multi('E', null));
        if (getType() == 'r')
        { // Todo
            //System.out.println("DEBUG PARAM ::" + param.get(0).getType() + ":: " + param.get(0)); // Debug
            ((Row) getData()).addCol(param.get(0));
        } //
        else if (getType() == 't' && param.get(0).getType() == 'r')
        { // Todo
            //System.out.println("Debug add TABLE { " + (Row) param.get(0).getData() + " }"); ///
            ((Table) getData()).insert((Row) param.get(0).getData());
        } //
        return (param.get(0));
    }

    public Multi remove(ArrayList<Multi> param)
    {
        int index;
        String buf;
        Multi ret;
        Table t;

        if (param == null || param.size() != 1 || param.get(0).getType() != 'i' || (getType() != 'r' && getType() != 's' && getType() != 't'))
            return (new Multi('E', null));
        index = (int)param.get(0).getData();
        if (index < 0)
            return (new Multi('E', null));
        if (getType() == 'r')
        {
            if (index >= ((Row)getData()).size())
                return (new Multi('E', null));
            ret = ((Row)getData()).getCol(index);
            ((Row)getData()).removeCol(index);
            return (ret);
        }
        else if (getType() == 's')
        {
            if (index >= ((String)getData()).length())
                return (new Multi('E', null));
            if (index == 0)
                buf = ((String)getData()).substring(1);
            else if (index == ((Row)getData()).size())
                buf = ((String)getData()).substring(0, ((Row)getData()).size() - 1);
            else
                buf = ((String)getData()).substring(0, (int) param.get(0).getData() - 1) + ((String)getData()).substring((int) param.get(0).getData() + 1);
            return (new Multi('s', buf));
        }
        else if (getType() == 't')
        {
            t = (Table)getData();
            if (index >= t.length())
                return (new Multi('E', null));
            ret = new Multi('r', t.getRow(index));
            t.remove(index);
            return (ret);
        }
        return (new Multi('E', null));
    }

    public Multi pow(ArrayList<Multi> param)
    {
        double d;

        if (Parser.isNumericType(getType()) == -1 || param == null || param.size() != 1 || Parser.isNumericType(param.get(0).getType()) == -1)
            return (new Multi('E', null));
        d = Math.pow((double)getData(), (double)param.get(0).getData());
        if (getType() == 'c')
            return (new Multi('c', (char)((int)d % 255)));
        if (getType() == 'i')
            return (new Multi('i', (int)d));
        if (getType() == 'f')
            return (new Multi('f', (float)d));
        if (getType() == 'l')
            return (new Multi('l', (float)d));
        if (getType() == 'd')
            return (new Multi('d', (float)d));
        return (new Multi('E', null));
    }

    public Multi min(ArrayList<Multi> param)
    {
        Row r;
        Table t;
        Multi v;
        Multi min;

        if (param != null && param.size() == 1 && Parser.isNumericType(param.get(0).getType()) != -1)
        {
            if (Parser.isNumericType(getType()) == -1)
                return (new Multi('E', null));
            min = param.get(0);
            if ((double)min.getData() < (double)getData())
                return (new Multi(min.getType(), min.getData()));
            return (new Multi(getType(), getData()));
        }
        if (getType() != 'r' && getType() != 't')
            return (new Multi('E', null));
        min = new Multi('i', Integer.MAX_VALUE);
        if (getType() != 'r')
        {
            r = (Row)getData();
            for (int i = 0; i < r.size(); i++)
            {
                v = r.getCol(i);
                if (Parser.isNumericType(v.getType()) == -1)
                    continue;
                if ((double)min.getData() > (double)v.getData())
                    min = v;
            }
            return (min == null || (int)min.getData() == Integer.MAX_VALUE ? new Multi('E', null) : min);

        }
        else if (getType() != 't')
        {
            t = (Table)getData();
            for (int i = 0; i < t.length(); i++)
            {
                v = new Multi('r', t.getRow(i)).min(null);
                if (Parser.isNumericType(v.getType()) == -1)
                    continue;
                if ((double)min.getData() > (double)v.getData())
                    min = v;
            }
            return (min == null || (int)min.getData() == Integer.MAX_VALUE ? new Multi('E', null) : min);
        }
        return (new Multi('E', null));
    }

    public Multi max(ArrayList<Multi> param)
    {
        Row r;
        Table t;
        Multi v;
        Multi max;

        if (param != null && param.size() == 1 && Parser.isNumericType(param.get(0).getType()) != -1)
        {
            if (Parser.isNumericType(getType()) == -1)
                return (new Multi('E', null));
            max = param.get(0);
            if ((double)max.getData() > (double)getData())
                return (new Multi(max.getType(), max.getData()));
            return (new Multi(getType(), getData()));
        }
        if (getType() != 'r' && getType() != 't')
            return (new Multi('E', null));
        max = new Multi('i', Integer.MIN_VALUE);
        if (getType() != 'r')
        {
            r = (Row)getData();
            for (int i = 0; i < r.size(); i++)
            {
                v = r.getCol(i);
                if (Parser.isNumericType(v.getType()) == -1)
                    continue;
                if ((double)max.getData() < (double)v.getData())
                    max = v;
            }
            return (max == null || (int)max.getData() == Integer.MIN_VALUE ? new Multi('E', null) : max);

        }
        else if (getType() != 't')
        {
            t = (Table)getData();
            for (int i = 0; i < t.length(); i++)
            {
                v = new Multi('r', t.getRow(i)).max(null);
                if (Parser.isNumericType(v.getType()) == -1)
                    continue;
                if ((double)max.getData() < (double)v.getData())
                    max = v;
            }
            return (max == null || (int)max.getData() == Integer.MIN_VALUE ? new Multi('E', null) : max);
        }
        return (new Multi('E', null));
    }

    public Multi avg()
    {
        int i;
        Row r;
        Table t;
        Multi v;
        double avg;

        if (getType() != 'r' && getType() != 't')
            return (new Multi('E', null));
        avg = 0;
        i = 0;
        if (getType() != 'r')
        {
            r = (Row)getData();
            while (i < r.size())
            {
                v = r.getCol(i);
                if (Parser.isNumericType(v.getType()) == -1)
                    continue;
                avg += (double)v.getData();
                i++;
            }
            return (i == 0 ? new Multi('E', null) : new Multi('d', avg / i));
        }
        else if (getType() != 't')
        {
            t = (Table)getData();
            while (i < t.length())
            {
                v = new Multi('r', t.getRow(i)).avg();
                if (Parser.isNumericType(v.getType()) == -1)
                    continue;
                avg += (double)v.getData();
                i++;
            }
            return (i == 0 ? new Multi('E', null) : new Multi('d', avg / i));
        }
        return (new Multi('E', null));
    }

    public Multi sqrt()
    {
        double d;

        if (Parser.isNumericType(getType()) == -1)
            return (new Multi('E', null));
        d = Math.sqrt((double)getData());
        if (getType() == 'c')
            return (new Multi('c', (char)((int)d % 255)));
        if (getType() == 'i')
            return (new Multi('i', (int)d));
        if (getType() == 'f')
            return (new Multi('f', (float)d));
        if (getType() == 'l')
            return (new Multi('l', (float)d));
        if (getType() == 'd')
            return (new Multi('d', (float)d));
        return (new Multi('E', null));
    }

    public Multi abs()
    {
        double d;

        if (Parser.isNumericType(getType()) == -1)
            return (new Multi('E', null));
        d = Math.abs((double)getData());
        if (getType() == 'c')
            return (new Multi('c', (char)((int)d % 255)));
        if (getType() == 'i')
            return (new Multi('i', (int)d));
        if (getType() == 'f')
            return (new Multi('f', (float)d));
        if (getType() == 'l')
            return (new Multi('l', (float)d));
        if (getType() == 'd')
            return (new Multi('d', (float)d));
        return (new Multi('E', null));
    }

    public Multi floor()
    {
        double d;

        if (Parser.isNumericType(getType()) == -1)
            return (new Multi('E', null));
        d = Math.floor((double)getData());
        if (getType() == 'f')
            return (new Multi('f', (float)d));
        if (getType() == 'd')
            return (new Multi('d', (float)d));
        return (new Multi('E', null));
    }

    public Multi ceil()
    {
        double d;

        if (Parser.isNumericType(getType()) == -1)
            return (new Multi('E', null));
        d = Math.ceil((double)getData());
        if (getType() == 'f')
            return (new Multi('f', (float)d));
        if (getType() == 'd')
            return (new Multi('d', (float)d));
        return (new Multi('E', null));
    }

    public Multi clamp(ArrayList<Multi> param)
    {
        double a;
        double b;
        double c;

        if (Parser.isNumericType(getType()) == -1 || param == null || param.size() != 2 ||
                Parser.isNumericType(param.get(0).getType()) == -1 || Parser.isNumericType(param.get(1).getType()) == -1)
            return (new Multi('E', null));
        a = Double.valueOf(getData().toString());
        b = Double.valueOf(param.get(0).getData().toString());
        c = Double.valueOf(param.get(1).getData().toString());
        if (b < c)
        {
            if (a < b)
                return (new Multi(param.get(0).getType(), param.get(0).getData()));
            if (a > c)
                return (new Multi(param.get(1).getType(), param.get(1).getData()));
        }
        return (new Multi(getType(), getData()));
    }

    public Multi sort() {
        // Todo not implemented
        return (new Multi('E', null));
        /*
        Row r;
        Table t;
        ArrayList<Multi> v;

        if (getType() != 'r' && getType() != 't')
            return (new Multi('E', null));
        if (getType() == 'r')
        {
            r = (Row)getData();
            v = r.getCol();
            if (v != null)
            {
                Collection.sort();
            }
            return (new Multi('r', r));
        }
        else if(getType() == 't')
        {
            ;
        }
        return (new Multi('E', null));
        */
    }

    public Multi loadPage(ArrayList<Multi> param)
    {
        Multi ret;
        String url;
        WebClient w;
        HtmlPage page;

        if (getType() != 'W' || param == null || param.size() != 1 || param.get(0).getType() != 's')
            return (new Multi('E', null));
        //w = (WebClient)param.get(0).getData();
        w = (WebClient)getData();
        url = (String)param.get(0).getData();
        page = null;
        try {
            return (new Multi('P', w.getPage(url)));
        } catch (Exception e)
        {
            System.out.println("loadPage failed");
            System.out.println(e.getMessage());
            return (new Multi('E', null));
        }
    }

    public Multi close()
    {
        if (getType() != 'W')
            return (new Multi('b', false));
        ((WebClient)getData()).close();
        return (new Multi('b', true));
    }

    public Multi getNodeName()
    {
        if (getType() != 'H')
            return (new Multi('E', null));
        return (new Multi('s', ((HtmlElement)getData()).getNodeName()));
    }

    public Multi getByXPath(ArrayList<Multi> param)
    {
        List<HtmlElement> e;
        Row r;

        if (param == null || param.size() != 1 || (getType() != 'H' && getType() != 'P') || param.get(0).getType() != 's'
                || Parser.isEmpty(((String)param.get(0).getData())))
            return (new Multi('E', null));
        e = null;
        r = null;
        try {
            System.out.println("DEBUG XPATH ["+param.get(0).getData()+"]"); // Debug
            if (getType() == 'H')
                e = (((HtmlElement) getData()).getByXPath(((String) param.get(0).getData())));
            else if (getType() == 'P')
                e = (((HtmlPage) getData()).getByXPath(((String) param.get(0).getData())));
            if (e == null)
                throw new Exception();
            r = new Row();
            if (r == null)
                throw new Exception();
            for (int i = 0; i < e.size(); i++) // Debug
                System.out.println("GETBYXPATH ===== ["+e.get(i)+"]"); // Debug
            for (int i = 0; i < e.size(); i++)
                r.addCol(new Multi('H', e.get(i)));
            return (new Multi('r', r));
        }
        catch (Exception ex) { return (new Multi('r', new Row())); }
    }

    public Multi getText()
    {
        String text;

        if (getType() != 'H' && getType() != 'P')
            return (new Multi('E', null));
        text = "";
        if (getType() == 'H')
            text = ((HtmlElement)getData()).getTextContent();
        else if (getType() == 'P')
            text = ((HtmlPage)getData()).getTextContent();
        return (new Multi('s', text));
    }

    public Multi getAttribute(ArrayList<Multi> param)
    {
        String text;

        System.out.println("ENTER GET ATTRIBUTE error["+(getType() != 'H' || param == null || param.size() != 1 || param.get(0).getType() != 's')+"]"); // Debug
        System.out.println("BUG [" + getType() + "]"); // Debug
        System.out.println("BUG [" + (param == null) + "]"); // Debug
        System.out.println("BUG [" + (param.size() != 1) + "]"); // Debug
        System.out.println("BUG [" + (param.get(0).getType() != 's') + "]"); // Debug
        if (getType() != 'H' || param == null || param.size() != 1 || param.get(0).getType() != 's')
            return (new Multi('E', null));
        System.out.print("GET ATTRIBUT [" + (String)param.get(0).getData() + "] == ["); // Debug
        text = ((HtmlElement)getData()).getAttribute((String)param.get(0).getData());
        System.out.println(text + "]"); // Debug
        return (Parser.isEmpty(text) ? new Multi('E', null) : new Multi('s', text));
    }

    public String getNegative()
    {
        if (type == 's')
            return (Parser.reverseString((String)data));
        else if (type == 'b')
            return ((boolean)data ? "false" : "true");
        else if (type == 'c')
            return ("-" + (int)data);
        else if (type == 'i')
            return ("-" + Integer.toString((int)data));
        else if (type == 'd')
            return ("-" + Double.toString((double)data));
        else if (type == 'f')
            return ("-" + Float.toString((float)data));
        else if (type == 'l')
            return ("-" + Long.toString((long)data));
        return ("");
    }

    public String toString()
    {
        if (type == 's' || type == 'c' || type == 'O' || type == 'v') // String, Char, Operator, Verb
            return ((String)data);
        else if (type == 'b')
            return ((boolean)data ? "true" : "false");
        else if (type == 'i')
            return (Integer.toString((int)data));
        else if (type == 'd')
            return (Double.toString((double)data));
        else if (type == 'f')
            return (Float.toString((float)data));
        else if (type == 'l')
            return (Long.toString((long)data));
        ////
        else if (type == 'z')
            return (((FunctionCall)data).toString());
        else if (type == 'Z')
            return (((FunctionDefinition)data).toString());
        else if (type == 'D') // Date
            return (((Date)data).toString());
        else if (type == 'P') // Page
            return (((HtmlPage)data).getTitleText());
        else if (type == 'H') // HtmlElement
            return (((HtmlElement)data).getTextContent());
        else if (type == 'W') // WebClient
            return (((WebClient)data).toString());
        else if (type == 'r') // Row (Array)
            return (((Row)data).toString());
        else if (type == 't')
            return (((Table)data).getName() + "[" + ((Table)data).length() + "]");
        else if (type == 'e') // Expression
            return (((Expression)data).toString());
        else if (type == 'T') // Ternary expression
            return (((TernaryExpression)data).toString());
        else if (type == 'E') // Empty
            return ("NULL");
        else if (type == 'C')
            return (String.valueOf((int)data)); // Cast
        else if (type == 'B')
            return (Integer.toString((int)data)); // Label
        else if (type == 'K')
            return ("HashMap"); // HashMap
        else if (type == 'o') // Object
            return (data.toString());
        return ("Unknown");
    }
}
