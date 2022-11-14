package com.example.exojavafx;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionDefinition {
    private Script script;
    private ArrayList<String> paramSym;

    public FunctionDefinition(String[] l, int baseLine, HashMap<String, Multi> stack)
    {
        int from;
        int to;
        int k;
        int index;
        String line;
        String[] prototype;
        String[] code;

        //System.out.println("Dbg A"); // Debug
        if (Parser.isEmpty(l) || baseLine > l.length)
            return;
        //System.out.println("Dbg B"); // Debug
        prototype = l[baseLine].split("[ \t]");
        if (Parser.isEmpty(prototype) || prototype.length < 2 || !prototype[0].equals("func"))
            return;
        //System.out.println("Dbg C"); // Debug
        line = l[baseLine];
        for (index = 0; index < line.length() && line.charAt(index) != '('; index++)
            ;
        from = index + 1;
        if (from >= line.length())
            return;
        //System.out.println("Dbg D"); // Debug
        for (index = line.length() - 1; index > 0 && line.charAt(index) != ')'; index--)
            ;
        to = index;
        //System.out.println("Dbg D' : to:" + to + " from:" + from); // Debug
        if (from <= to)
        {
            String parameters = line.substring(from, to);
            //System.out.println("Dbg E : parameters: {" + parameters + "}"); // Debug
            int offset;
            from = 0;
            paramSym = new ArrayList<String>();
            for (int i = 0; i < parameters.length(); i++)
            {
                if (Parser.isBlank(parameters.charAt(i)))
                    from++;
                else if (parameters.charAt(i) == ',' || i + 1 >= parameters.length())
                {
                    paramSym.add(parameters.substring(from, i + (parameters.charAt(i) == ',' ? 0 : 1)).trim());
                    from = i + 1;
                }
            }
        }
        baseLine++;
        to = -1;
        for (int i = baseLine; i < l.length; i++)
            if (l[i].trim().equals("endfunc")) {
                to = i;
                break;
            }
        //System.out.println("Dbg F"); // Debug
        if (to == -1)
            return ;
        //System.out.println("Dbg G"); // Debug
        code = new String[to - baseLine];
        k = 0;
        for (int i = baseLine; i < to; i++)
            code[k++] = l[i];
        //System.out.println("Dbg H"); // Debug
        script = new Script(code);
    }

    public ArrayList<String> getSym()
    {
        return (paramSym);
    }

    public Script getScript()
    {
        return (script);
    }
}
