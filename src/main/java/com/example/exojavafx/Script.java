package com.example.exojavafx;

import com.gargoylesoftware.htmlunit.WebClient;
import org.controlsfx.control.WorldMapView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Script implements Computable {
    private String[]         script;
    //ArrayList<Table> t;

    public Script(String scrapfile, boolean run)
    {
        if (Parser.isEmpty(scrapfile))
            return ;
        try {
            script = Terminal.lireFichierTexte(scrapfile);
            if (Parser.isEmpty(script) || preprocessor())
                throw new Exception();
        }
        catch (Exception e)
        {
            System.out.println("Read file error [" + scrapfile + "]\n" + e.getMessage());
        }
        if (run)
            parse(script, null);
    }

    public Script(String script)
    {
        if (Parser.isEmpty(script))
            return ;
        this.script = script.split("[\n\r]");
        preprocessor();
    }

    public Script(String[] script)
    {
        if (Parser.isEmpty(script))
            return ;
        this.script = script;
        preprocessor();
    }

    public String[] getScript()
    {
        return (script);
    }

    public Multi result(HashMap<String, Multi> parameters)
    {
        return (parse(script, parameters));
    }

    private boolean preprocessor() // Kill comments and do a bunch of stuff
    {
        String      env;
        String      e;
        String      s;

        for (int i = 0; i < script.length; i++)
        {
            script[i] = script[i].trim();
            s = script[i];
            if (s.split("[ \\t]")[0].equals("include")) // Preprocessor verb
            {
                Script scriptFile = new Script(s.substring("include".length()).trim(), false);
                if (scriptFile.getScript() == null)
                {
                    System.out.println(s + " : File not found.");
                    return (true);
                }
                script = Parser.replaceLine(script, i, scriptFile.getScript());
                continue;
            }
            for (int j = 0; j < s.length(); j++)
                if (Parser.isStringDelimiter(s.charAt(j)) != -1)
                {
                    e = Parser.parseString(s.substring(j));
                    if (e == null)
                        continue;
                    j += Parser.parseStringFormat(s.substring(j), false).length() + 1;
                    System.out.println("Found string : [" + e + "] (" + s + ")"); // Debug
                }
                else if (j + 1 < s.length() && s.charAt(j) == '@')
                {
                    j++;
                    e = s.substring(j).split("[ \\t]")[0];
                    env = null;
                    if (e.equals("DATE")) // Insert date
                        env = LocalDate.now().toString();
                    if (e.equals("EASTEREGG")) // Insert easteregg
                        env = File.listRoots().toString();
                    /*
                        etc ...
                    */
                    if (env != null) {
                        script[i] = s.substring(0, Math.max(j - 1, 0)) + "\"" + env + "\"" + s.substring(j + e.length());
                        s = script[i];
                    }
                }
                else if (j + 1 < s.length() && s.charAt(j) == '/' && s.charAt(j + 1) == '/')
                {
                    script[i] = s.substring(0, j).trim();
                    System.out.println("Comment: " + s.substring(j)); // Debug
                    break;
                }
        }
        return (false);
    }

    public void         display()
    {
        if (script == null)
            return ;
        System.out.println("----------------------------------------------");
        for (int i = 0; i < script.length; i++)
            System.out.println(script[i]);
        System.out.println("----------------------------------------------");
    }

    private Multi makeWebClient()
    {
        WebClient w;

        w = new WebClient();
        if (w == null)
            return (new Multi('E', null));
        w.getOptions().setUseInsecureSSL(true);
        w.getOptions().setCssEnabled(false);
        w.getOptions().setJavaScriptEnabled(false);
        return (new Multi('W', w));
    }

    private Multi makeTable(String v)
    {
        String      ax;
        String      bx;
        String[]    e;
        int         len;

        v = v.trim();
        if (v == null || v.length() == 0)
            return (null);
        ax = "";
        bx = "";
        e = v.split("[ \t]"); // TODO Changer de split pour permettre l'insertion de "string" <-- Expression
        len = e.length;
        if (len >= 4 && len % 2 == 0 && e[0].equals("Table")) { // OK
            for (int i = 2; i < len; i++)
                if (i % 2 == 0)
                    ax += e[i]; // Type
                else
                    bx += e[i] + " "; // Col name
            return (new Multi('t', new Table(ax, bx, e[1])));
        }
        return (null);
    }

    public Multi affect(Multi to, String operator, HashMap<String, Multi> stack, String v)
    {
        if (v == null || v.length() == 0 || stack == null || Parser.getOperator(operator) == -1)
            return (null);
        //System.out.println("Assign : " + (new Expression(v, stack)).result(stack)); // Debug
        //System.out.println("Assign : " + Expression.operation(to, operator, (new Expression(v, stack)).result(stack), stack)); // Debug
        return (Expression.operation(to, operator, (new Expression(v, stack)).result(stack), stack));
    }

    private int parseVerb(String[] l, String[] instruction, HashMap<String, Multi> stack, int eip)
    {
        String sym;
        int verb;

        if (l == null || l.length == 0 || instruction.length == 0 || stack == null || eip < 0 || eip >= l.length)
            return (-2);
        sym = Parser.copyWord(instruction[0]);
        verb = Parser.isVerb(sym);
        //System.out.println("Debug VERB : " + verb); // Debug
        if (verb == Parser.isVerb("if")) // If
            eip = Parser.controlIf(l, eip, stack);
        else if (verb == Parser.isVerb("else")) // Else
        {
            if (instruction.length >= 2 && Parser.isVerb(instruction[1]) == Parser.isVerb("if")) // Else if
                eip = Parser.controlElseIf(l, eip, stack);
            else
                eip = Parser.endScope(l, eip, "if", "endif");
        }
        else if (verb == Parser.isVerb("while")) // While
            eip = Parser.controlWhile(l, eip, stack);
        else if (verb == Parser.isVerb("wend")) // Wend
        {
            //System.out.println("WEND"); // Debug
            eip = Parser.startScope(l, eip, "while", "wend");
            if (eip < 0)
                return (-2);
            //System.out.println("Jumping to: [" + l[i] + "]"); // Debug
            return (eip - 1);
        }
        else if (verb == Parser.isVerb("continue")) // Continue
        {
            eip = Parser.startScope(l, eip, "while", "wend");
            if (eip < 0)
                return (eip);
            return (eip - 1);
        }
        else if (verb == Parser.isVerb("break")) // Break
            eip = Parser.endScope(l, eip, "while", "wend");
        else if (verb == Parser.isVerb("label"))
        {
            if (stack.get(Parser.copyWord(instruction[1])) == null)
                stack.put(Parser.copyWord(instruction[1]), new Multi('B', eip));
        }
        else if (verb == Parser.isVerb("goto"))
        {
            Multi label;

            label = stack.get(Parser.copyWord(instruction[1]));
            if (label != null && label.getType() == 'B')
                eip = (int)(stack.get(Parser.copyWord(instruction[1])).getData()) - 1;
        }
        else if (verb == Parser.isVerb("any"))
        {
            Multi value;
            String expr;

            value = stack.get(Parser.copyWord(instruction[1]));
            if (value == null || value.getType() == 'E')
                return (-2);
            expr = "";
            for (int k = 2; k < instruction.length; k++)
                expr += instruction[k] + " ";
            expr = expr.trim();
            value.any((new Expression(expr, stack)).result(stack));
        }
        else if (verb == Parser.isVerb("func")) // Func
        {
            stack.put(Parser.copyWord(instruction[1]), new Multi('Z', new FunctionDefinition(l, eip, stack)));
            //System.out.println("DEBUG FUNC ::: ["+new Multi('Z', new FunctionDefinition(l, eip, stack)+"]"));
            eip = Parser.endScope(l, eip + 1, "func", "endfunc");
        }
        else if (verb == Parser.isVerb("exit")) // Func
            return (-3);
        //System.out.println("EIP = " + eip); // Debug
        return (eip < 0 ? -2 : eip);
    }

    private int parseAffectation(String[] l, int eip, String[] instruction, HashMap<String, Multi> stack)
    {
        String sym;

        if (l == null || l.length == 0 || eip < 0 || eip >= l.length || instruction == null || instruction.length == 0 || stack == null)
            return (-2);
        //sym = Parser.copyWord(instruction[0]); // Original
        sym = Parser.copyVariable(instruction[0]);
        //for (int i = 0; i < instruction.length; i++) // Debug
            //System.out.println("Instruction : [" + instruction[i] + "]"); // Debug
        //System.out.println("Sym [" + sym + "]"); // Debug
        if (Parser.isEmpty(sym))
            return (-2);
        //Parser.displayHashMap(stack); // Debug
        if (stack.containsKey(sym) && stack.get(sym) != null && Parser.isFunctionCall(l[eip])) // Function call
        {
            //System.out.println("NEW EXPRESSION CALL SCRIPT"); // Debug
            new Expression(l[eip].trim(), stack);
            //////////// TODO FunctionCall ----------------
            //System.out.println("Debug FUNCTION CALL"); // Debug
            //String param = Parser.copyParam(l[i]);
            //System.out.println("Parameters: [" + param + "]");
            /// From FunctionCall
            //functionCall(sym, param, stack);
            //////////// TODO FunctionCall----------------
        }
        else if (instruction.length == 1)
            return (eip);
        else if (Parser.isAssignationOperator(instruction[1]) == -1) // Table
        {
            if (instruction[1].equals("Array"))
                stack.put(sym, new Multi('r', new Row()));
            else if (instruction[1].equals("Table"))
                stack.put(sym, makeTable(Parser.skipWord(l[eip], 1)));
            else if (instruction[1].equals("WebClient"))
                stack.put(sym, makeWebClient());
            else
                return (-2); // Not assignation, not table, not verb
        }
        else // Assignation
            stack.put(sym, affect(stack.get(sym), instruction[1], stack, Parser.skipWord(l[eip], 2)));
        //System.out.println("Affectation eip : " + eip); // Debug
        return (eip);
    }

    public static Multi parseNativeFunction(String e, HashMap<String, Multi> stack)
    {
        Multi ret;
        /*
        Multi ret;
        int nativeFunc;

        ret = new Multi('E', null);
        if (Parser.isEmpty(e) || stack == null)
            return (ret);
        e = e.trim();
        nativeFunc = Parser.isNativeFunction(Parser.copyWord(e));
        if (nativeFunc == Parser.isNativeFunction("print") || nativeFunc == Parser.isNativeFunction("println")) // print
        {
            ret = new Expression(l[eip].trim().substring(instruction[0].length()), stack).result(stack);
            System.out.print(ret.toString());
            if (nativeFunc == Parser.isNativeFunction("println"))
                System.out.print("\n");
        }
        else if (nativeFunc == Parser.isNativeFunction("sqrt")) // print
        {

        }
        return (ret);
        */
        if (Parser.isEmpty(e) || stack == null)
            return (new Multi('E', null));
        return (new Expression(e.trim(), stack).result(stack));
    }

    private int parseInstruction(String[] l, String[] instruction, HashMap<String, Multi> stack, int eip)
    {
        if (l == null || l.length == 0 || instruction.length == 0 || stack == null || eip < 0 || eip >= l.length)
            return (-2);
        //System.out.println("Native function : " + Parser.isNativeFunction(Parser.copyWord(instruction[0]))); // Debug
        if (Parser.isNativeFunction(Parser.copyWord(instruction[0])) != -1) // Is native function
            parseNativeFunction(l[eip], stack);
        else // Is affectation
            eip = parseAffectation(l, eip, instruction, stack);
        return (eip);
    }

    public Multi parse(String[] l, HashMap<String, Multi> parameters)
    {
        HashMap<String, Multi>      stack;
        String[]                    instruction;
        int                         verb;

        if (l == null || l.length == 0)
            return (null);
        stack = parameters != null ? parameters : new HashMap<String, Multi>();
        for (int i = 0; i < l.length; i++)
        {
            if (Parser.isEmptyLine(l[i]))
                continue ;
            instruction = l[i].split("[ \t]");
            if (instruction == null || instruction.length == 0)
                continue;
            verb = Parser.isVerb(instruction[0]);
            //Parser.displayHashMap(stack); // Debug TODO ----------------------------------- Print variable stack
            System.out.println("Line(" + i + "): [" + l[i] + "] verb = " + verb); // Debug
            if (verb == -1) // Is instruction
                i = parseInstruction(l, instruction, stack, i);
            else if (verb == Parser.isVerb("return")) // Return
                return (new Expression(Parser.skipWord(l[i], 1), stack).result(stack));
            else // Is verb
                i = parseVerb(l, instruction, stack, i);
            //Parser.displayHashMap(stack); // Debug TODO ----------------------------------- Print variable stack
            if (i < -1)
            {
                if (i == -3) // Exit
                    break;
                System.out.println("Error return null"); // Debug
                return (new Multi('E', null));
            }
        }
        System.out.println("\nExit script"); // Debug
        Parser.displayHashMap(stack); // Debug
        return (new Multi('K', new HashMap<String, Multi>(stack))); // Todo Test
    }
}
