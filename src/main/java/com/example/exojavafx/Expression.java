package com.example.exojavafx;

import com.gargoylesoftware.htmlunit.javascript.host.Element;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;

public class Expression implements Computable {
    private String expr;
    private ArrayList<Multi>        elements;

    public Expression(String expression, HashMap<String, Multi> stack)
    {
        String                  word;
        String                  number;
        String                  s;
        char[]                  e;
        int                     from;
        int                     to;
        int                     i;
        int                     len;
        int                     spaceLeft;

        elements = new ArrayList<Multi>();
        if (expression == null)
            return ;
        expression = expression.trim();
        expr = expression;
        len = expression.length();
        if (len == 0)
            return ;
        e = expression.toCharArray();
        i = 0;
        from = -1;
        //System.out.println("EXPRESSION : [" + stack + "]"); // Debug
        //System.out.println("Debug A : [" + expression + "]"); // Debug
        while (++from < len)
        {
            if (Parser.isBlank(e[from]))
                continue;
            //System.out.println("Debug B : [" + expression.substring(from) + "]"); // Debug
            spaceLeft = len - from;
            word = Parser.copyWordDumb(expression.substring(from));
            //word = Parser.copyVariable(expression.substring(from));
            number = Parser.copyNumber(expression.substring(from));
            if (word == null || (word.length() == 0 && Parser.isStringDelimiter(expression.charAt(from)) == -1))
                continue;
            System.out.println("Exp(" + from + ")" + expression.substring(from)); // Debug
            System.out.println("Word(" + word + ")"); // Debug
            if (word.equals("s")) // Debug
                System.out.println("BREAKPOINT"); // Debug
            //System.out.println("Num(" + from + ")" + number); // Debug
            //System.out.println("IsVariable(\"" + (expression.substring(from, from + (spaceLeft > 2 ? 2 : spaceLeft))).trim() + "\")"); // Debug
            if (Parser.isVerb(word) != -1) // meh
            {
                System.out.println("Debug C"); // Debug
                if (word.charAt(0) == ':')
                    continue;
                if (word.charAt(0) == '?') // Ternary expression
                {
                    to = from + 1;
                    while (to < expression.length() && expression.charAt(to) != ':') {
                        if (Parser.isStringDelimiter(expression.charAt(to)) != -1)
                            to += Parser.parseStringFormat(expression.substring(to), false).length() + 2;
                        else if (expression.charAt(to) == '(')
                            to += Parser.copyExpression(expression.substring(to)).length() + 2;
                        else
                            to++;
                    }
                    if (to >= expression.length())
                        return ;
                    elements.add(new Multi('v', "?"));
                    to++;
                    //System.out.println("TERNARY LEFT : [" + expression.substring(from + 1, to - 2).trim() + "]"); // Debug
                    //elements.add(new Multi('e', new Expression(expression.substring(from + 1, to - 2).trim(), stack)));
                    elements.add(new Multi('e', new Expression(expression.substring(from + 1, to - 2).trim(), stack)));
                    from = to;
                    while (to < expression.length() && expression.charAt(to) != ')') {
                        if (Parser.isStringDelimiter(expression.charAt(to)) != -1)
                            to += Parser.parseStringFormat(expression.substring(to), false).length() + 2;
                        else if (expression.charAt(to) == '(')
                            to += Parser.copyExpression(expression.substring(to)).length() + 2;
                        else
                            to++;
                    }
                    //System.out.println("TERNARY RIGHT : [" + expression.substring(from, Math.min(to, expression.length())).trim() + "]"); // Debug
                    //elements.add(new Multi('e', new Expression(expression.substring(from, to - 1).trim(), stack)));
                    elements.add(new Multi('e', new Expression(expression.substring(from, Math.min(to, expression.length())).trim(), stack)));
                    from = to;
                    continue;
                }
                else
                    elements.add(new Multi('v', word));
                from += word.length();
                continue;
            }
            else if (spaceLeft >= 2 && Parser.isStringDelimiter(e[from]) != -1) // Constant strings
            {
                System.out.println("Debug H"); // Debug
                s = Parser.parseString(expression.substring(from));
                System.out.println("PARSE STRING : [" + s + "]"); // Debug
                elements.add(new Multi('s', s));
                from += Parser.parseStringFormat(expression.substring(from), false).length() + 2;
                continue ;
            }
            //else if (Parser.isNativeFunction(word.split("\\.")[0]) != -1) // Native Function
            else if (Parser.isNativeFunction(word) != -1) // Native Function
            {
                //System.out.println("Expression Native function"); // Debug
                int function;

                from += Parser.copyVariable(word).length();
                //from += word.length();
                System.out.println("Debug D"); // Debug
                if (from == -1 || from > e.length || e[from] != '(')
                    return ; // Missing parameters
                else
                {
                    String expr;

                    expr = expression.substring(from);
                    System.out.println("function call : [" + expr + "]"); // Debug
                    function = Parser.isNativeFunction(word.split("\\.")[0]);
                    to = from + Parser.closingOffset('(', ')', expr);
                    if (to == from - 1)
                        return ;
                    // Todo test ------------
                    Multi ret;
                    String param;

                    ret = new Multi('E', null);
                    param = Parser.copyParam(expr);

                    //System.out.println("function param : [" + param + "]"); // Debug

                    if (function == Parser.isNativeFunction("shell")) // Shell
                        ret = shell(Parser.parameterList(param, stack)); // Todo
                    else if (function == Parser.isNativeFunction("read")) // Shell
                        ret = read(Parser.parameterList(param, stack)); // Todo
                    else if (function == Parser.isNativeFunction("write")) // Shell
                        ret = write(Parser.parameterList(param, stack)); // Todo
                    else if (function == Parser.isNativeFunction("free") && stack.containsKey(param)) // Free
                        stack.remove(param);
                    else if (function == Parser.isNativeFunction("declared")) // Declared
                        ret = new Multi('b', stack.containsKey(param));
                    else if (function == Parser.isNativeFunction("rand")) // Rand
                        ret = new Multi('d', Math.random());
                    else if (function == Parser.isNativeFunction("sqrt")) // Sqrt
                    {
                        ret = new Expression(param, stack).result(stack);
                        if (ret != null && Parser.isNumericType(ret.getType()) != -1)
                            ret = new Multi('d', Math.sqrt((double)ret.getData()));
                    }
                    else if (function == Parser.isNativeFunction("print") || function == Parser.isNativeFunction("println")) // Print
                    {
                        ret = new Expression(param, stack).result(stack);
                        System.out.print(ret.toString());
                        if (function == Parser.isNativeFunction("println"))
                            System.out.print("\n");
                    }
                    else if (function == Parser.isNativeFunction("pause")) // Pause
                    {
                        ret = new Expression(param, stack).result(stack);
                        if (!Parser.isEmpty(ret) && Parser.isNumericType(ret.getType()) != -1) // Todo batman
                        {
                            try {
                                Thread.sleep((int)ret.getData());
                            } catch (Exception error) { ; }
                            ret = new Multi(ret.getType(), ret.getData());
                        }
                        else
                            ret = new Multi('E', null);
                    }
                    else if (function == Parser.isNativeFunction("script")) // Pause
                        ret = script(Parser.parameterList(param, stack));
                    // Todo test ------------
                    //elements.add(new Multi('z', new FunctionCall(word, expression.substring(from + 1, to - 1), stack))); // Original TODO
                    elements.add(ret);
                    from = Parser.skipBlank(expression, from + Parser.getNativeFunction(function).length() + param.length() + 1);
                    continue ;
                }
            }
            else if (Parser.isVariable((expression.substring(from, from + (spaceLeft > 2 ? 2 : spaceLeft))).trim().split("[\\.]")[0]))
            {
                System.out.println("Debug E"); // Debug
                boolean negative = false;
                if (e[from] == '-' || (e[from] == '!' && from + 1 < e.length && (Parser.isAlphabetic(e[from + 1]) || e[from + 1] == '_')))
                {
                    from++;
                    negative = true;
                }
                to = from;
                /* // Original
                while (to < len && Parser.isAlphaNum(e[to]))
                    to++;
                s = expression.substring(from, to);
                */
                s = Parser.copyVariable(expression.substring(from)); // Test
                to += s.length(); // Test
                System.out.println("Debug E' [" + s + "]"); // Debug
                if (s.equals("true") || s.equals("false"))
                    elements.add(new Multi('b', s.equals("true")));
                else if (s.equals("null"))
                    elements.add(new Multi('E', null));
                else if (stack.get(s) != null)//stack.containsKey(s))
                {
                    System.out.println("Debug EEEE"); // Debug
                    if (stack.get(s).getType() == 'Z')
                    {
                        //////////// TODO ----------------
                        //System.out.println("Debug FUNCTION CALL"); // Debug
                        String param = Parser.copyParam(expression.substring(to));
                        /// From FunctionCall
                        //System.out.println("Function Expression: " + expression); // Debug TODO NULL !!!
                        //System.out.println("Function Parameters: " + param); // Debug TODO NULL !!!
                        Multi retVal;
                        //System.out.println("Debug function call"); // Debug
                        retVal = new FunctionCall(s, param, stack).run(stack);
                        //System.out.println("Retval = " + retVal); // Debug
                        if (retVal != null)
                        {
                            if (negative && Parser.isNumericType(retVal.getType()) != -1)
                            {
                                switch (retVal.getType())
                                {
                                    case 's': elements.add(new Multi('s', retVal.getNegative())); break;
                                    case 'b': elements.add(new Multi('b', retVal.getNegative().equals("true"))); break;
                                    case 'c': elements.add(new Multi('c', (char)(Integer.valueOf(retVal.getNegative()) % 256))); break;
                                    case 'i': elements.add(new Multi('i', Integer.valueOf(retVal.getNegative()))); break;
                                    case 'f': elements.add(new Multi('f', Float.valueOf(retVal.getNegative()))); break;
                                    case 'd': elements.add(new Multi('d', Double.valueOf(retVal.getNegative()))); break;
                                    case 'l': elements.add(new Multi('l', Long.valueOf(retVal.getNegative()))); break;
                                }
                            }
                            else
                                elements.add(retVal);
                        }
                        to = from + s.length() + param.length() + 2; // Test
                        //System.out.println("from : " + expression.substring(from));
                        //System.out.println("to : " + expression.substring(to));
                        //////////// TODO ----------------
                    }
                    else
                    {
                        int suffix;

                        suffix = Parser.getSuffix(expression.substring(to));
                        System.out.println("IS VARIABLE suffix:[" + expression.substring(to) + "] == " + suffix); // Debug
                        if (suffix != -1)
                        {
                            if (suffix == Parser.getSuffix("++") || suffix == Parser.getSuffix("--"))
                            {
                                stack.put(s, Expression.operation(stack.get(s), "+", new Multi('i', suffix == Parser.getSuffix("++") ? 1 : -1), stack));
                                from++;
                            }
                            else
                            { // Native methods
                                // Todo ----------------------------------------------------------------------
                                // Todo METHOD CALL ETAIT ICI
                                //System.out.println("NATIVE METHOD : [" + expression.substring(to) + "] -> {" + methodCall(stack.get(s), expression.substring(to), stack) + "}"); // Debug
                                elements.add(methodCall(stack.get(s), expression.substring(to), stack));
                                while (expression.charAt(to) == '.')
                                {
                                    to++;
                                    while (Parser.isAlphaNum(expression.charAt(to)))
                                        to++;
                                    if (expression.charAt(to) == '(')
                                        to += Parser.copyParam(expression.substring(to)).length() + 1;
                                }
                                // Todo ----------------------------------------------------------------------
                            }
                        }
                        else
                            elements.add(stack.get(s));
                    }
                }
                else
                {
                    System.out.println("Undefined variable");
                    return ; // Undefined variable
                }
                from = to;
                continue;
            }
            else if (!Parser.isEmpty(number)) // Numeric value
            {
                System.out.println("Debug F"); // Debug

                //System.out.println("isNumericType: " + number + " ? " + Parser.isNumericType(e[from + number.length()])); // Debug
                //int type = number.length() < 2 ? -1 : Parser.isNumericType(number.charAt(number.length() - 1));
                int type = from + number.length() >= e.length ? -1 : Parser.isNumericType(e[from + number.length()]);
                //System.out.println("Numeric type: " + type); // Debug
                if (type == -1)
                {
                    if (Parser.listIndex(number.toCharArray(), '.') != -1)
                        elements.add(new Multi('d', Double.valueOf(number)));
                    else
                        elements.add(new Multi('i', Integer.valueOf(number)));
                }
                else
                {
                    from++;
                    if (Parser.getNumericType(type) == 'c')
                        elements.add(new Multi('c', (char)(Integer.valueOf(number) % 256)));
                    else if (Parser.getNumericType(type) == 'i')
                        elements.add(new Multi('i', Integer.valueOf(number)));
                    else if (Parser.getNumericType(type) == 'd')
                        elements.add(new Multi('d', Double.valueOf(number)));
                    else if (Parser.getNumericType(type) == 'f')
                        elements.add(new Multi('f', Float.valueOf(number)));
                    else if (Parser.getNumericType(type) == 'l')
                        elements.add(new Multi('l', Long.valueOf(number)));
                }
                from += number.length();
                continue ;
            }
            else if (Parser.isOperator("" + e[from]))
            {
                System.out.println("Debug G"); // Debug
                String op;

                op = Parser.copyOperator(expression.substring(from));
                //System.out.println("OPERATOR : [" + expression.substring(from) + "]"); // Debug
                //System.out.println("OPERATOR : [" + op + "]"); // Debug
                if (Parser.isEmpty(op) || !Parser.isOperator(op))
                    return ; // Unknown operator
                elements.add(new Multi('O', op));
                from = Parser.skipBlank(expression, from) + op.length();
                continue;
            }
            else if (spaceLeft >= 3 && Parser.isDelimiter("" + e[from]))
            {
                System.out.println("Debug I"); // Debug
                if (e[from] == '(') // Block
                {
                    String expr;
                    String[] ternary;

                    //System.out.println("NEWBLOCK [" + expression.substring(from) + "]"); // Debug
                    expr = Parser.copyExpression(expression.substring(from));
                    if (Parser.isEmpty(expr))
                        continue;
                    ternary = Parser.isTernary(expr);
                    //System.out.println("expr : [" + expr + "]"); // Debug
                    if (Parser.isCast(expr) != -1) // Cast
                        elements.add(new Multi('C', Parser.isCast(expr)));
                    else if (ternary != null) // Ternary expression
                    {
                        /*
                        for (int debug = 0; debug < 3; debug++)
                            System.out.println("Ternary(" + debug + ") : [" + ternary[debug] + "]"); // Debug
                        */
                        elements.add(new Multi('T', new TernaryExpression(new Expression(ternary[0], stack), new Expression(ternary[1], stack), new Expression(ternary[2], stack))));
                    }
                    else // Expression
                        elements.add(new Multi('e', new Expression(expr, stack)));
                    from += expr.length() + 1;
                    // TODO debug ------------------------
                    continue ;
                }
            }
        }
        //System.out.println("Expression length: " + elements.size()); // Debug
    }

    public Multi shell(ArrayList<Multi> param)
    {
        return (new Multi('s', "Not implemented")); // Todo
    }

    public Multi read(ArrayList<Multi> param)
    {
        String[] data;
        String text;

        if (param == null || param.size() != 1 || param.get(0).getType() != 's')
            return (new Multi('E', null));
        try {
            data = Terminal.lireFichierTexte((String) param.get(0).getData());
            if (data == null)
                return (new Multi('E', null));
            text = "";
            for (int i = 0; i < data.length; i++)
                text += data[i] + "\n";
            return (new Multi('s', text));
        }
        catch (Exception e) { return (new Multi('E', null)); }
    }

    public Multi write(ArrayList<Multi> param)
    {
        if (param == null || param.size() != 2 || param.get(0).getType() != 's' || param.get(1).getType() != 's')
            return (new Multi('E', null));
        try {
            Terminal.ecrireFichier((String)param.get(0).getData(), new StringBuffer((String)param.get(1).getData()));
            return (new Multi('b', true));
        }
        catch (Exception e) { return (new Multi('b', false)); }
    }

    public Multi script(ArrayList<Multi> param)
    {
        HashMap<String, Multi> map;
        String file;
        Script s;
        int len;

        if (param == null || param.size() == 0 || param.get(0).getType() != 's')
            return (new Multi('E', null));
        len = param.size();
        file = (String)param.get(0).getData();
        map = new HashMap<String, Multi>();
        for (int i = 1; i < len; i++)
            map.put("arg_" + i, param.get(i));
        try {
            s = new Script(file, false);
            return (s.result(map.size() == 0 ? null : map));
        } catch (Exception e)
        {
            System.out.println("Script failure."); // Debug
            return (new Multi('E', null));
        }
    }

    private static Multi methodCall(Multi left, String call, HashMap<String, Multi> stack) {
        String func;
        String param;
        Multi ret;
        Multi in;
        int method;
        int suffix;
        int to;

        if (left == null || Parser.isEmpty(call) || stack == null)
            return (left == null ? new Multi('E', null) : left);
        in = left;
        to = 0;
        if (call.charAt(0) != '.')
            return (left);
        //System.out.println("METHOD CALL [" + call + "]"); // Debug
        to++;
        if (to >= call.length())
            //return (new Multi('E', null));
            return (left);
        func = Parser.copyVariable(call.substring(to));
        //System.out.println("ft=[" + func + "]"); // Debug
        to += func.length();
        if (to >= call.length())
            //return (new Multi('E', null));
            return (left);
        param = Parser.copyParam(call.substring(to));
        to += param.length() + 2;
        System.out.println("ft=[" + func + "]("+param+")"); // Debug
        method = Parser.isNativeMethod(func);
        System.out.println("Method: " + method); // Debug
        System.out.println("SOURCE: ("+in.getType()+")[" + in + "]"); // Debug
        if (method == -1)
            //return (new Multi('E', null));
            return (left);
        ret = null;
        // TODO NATIVE FUNCTIONS --------------------------------------------------------------------------
        if (method == Parser.isNativeMethod("type"))
            ret = in.type();
        else if (method == Parser.isNativeMethod("length"))
            ret = in.length();
        else if (method == Parser.isNativeMethod("width"))
            ret = in.width();
        else if (method == Parser.isNativeMethod("split"))
            ret = in.split(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("trim"))
            ret = in.trim();
        else if (method == Parser.isNativeMethod("uppercase"))
            ret = in.uppercase();
        else if (method == Parser.isNativeMethod("lowercase"))
            ret = in.lowercase();
        else if (method == Parser.isNativeMethod("capitalize"))
            ret = in.capitalize();
        else if (method == Parser.isNativeMethod("substring"))
            ret = in.substring(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("escape"))
            ret = in.escape(); /////
        else if (method == Parser.isNativeMethod("replace"))
            ret = in.replace(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("replaceAll"))
            ret = in.replaceAll(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("replaceFirst"))
            ret = in.replaceFirst(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("get"))
            ret = in.get(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("set"))
            ret = in.set(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("add"))
            ret = in.add(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("remove"))
            ret = in.remove(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("pow"))
            ret = in.pow(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("min"))
            ret = in.min(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("max"))
            ret = in.max(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("sqrt"))
            ret = in.sqrt(); /////
        else if (method == Parser.isNativeMethod("abs"))
            ret = in.abs(); /////
        else if (method == Parser.isNativeMethod("floor"))
            ret = in.floor(); /////
        else if (method == Parser.isNativeMethod("ceil"))
            ret = in.ceil(); /////
        else if (method == Parser.isNativeMethod("clamp"))
            ret = in.clamp(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("sort"))
            ret = in.sort(); /////
        else if (method == Parser.isNativeMethod("loadPage"))
            ret = in.loadPage(Parser.parameterList(param, stack)); /////
        else if (method == Parser.isNativeMethod("close"))
            ret = in.close(); /////
        else if (method == Parser.isNativeMethod("getByXPath"))
        {
            ret = in.getByXPath(Parser.parameterList(param, stack)); /////
            if (ret != null && ret.getType() == 'E')
                return (ret);
        }
        else if (method == Parser.isNativeMethod("getNodeName"))
        {
            ret = in.getNodeName(); /////
            if (ret != null && ret.getType() == 'E')
                return (ret);
        }
        else if (method == Parser.isNativeMethod("getText"))
        {
            ret = in.getText(); /////
            if (ret != null && ret.getType() == 'E')
                return (ret);
        }
        else if (method == Parser.isNativeMethod("getAttribute"))
        {
            System.out.println("CALL GET ATTRIBUTE"); // Debug
            ret = in.getAttribute(Parser.parameterList(param, stack)); /////
            if (ret != null && ret.getType() == 'E')
                return (ret);
        }
        //else if (method == Parser.isNativeMethod("any"))
        //ret = ret.any(Parser.parameterList(param, stack)); /////
        // TODO NATIVE FUNCTIONS --------------------------------------------------------------------------
        //System.out.println("RET: [" + ret + "]"); // Debug
        if (ret == null || ret.getType() == 'E')
            //return (new Multi('E', null));
            return (left);
        //to += func.length();
        //to += param.length() + 2;
        //return (ret);
        //System.out.println("METHOD RETURN ::: " + (to >= call.length() ? ret : methodCall(ret, call.substring(to), stack)));
        return (to >= call.length() ? ret : methodCall(ret, call.substring(to), stack));
    }

    private static Multi operationFloat(Multi left, int operation, Multi right)
    {
        float[] f = { left.getType() == 'f' ? (float)left.getData() : Float.valueOf(left.getData().toString()), Float.valueOf(right.getData().toString()) };

        if (operation == Parser.getOperator("+"))
            return (new Multi('f', f[0] + f[1]));
        if (operation == Parser.getOperator("-"))
            return (new Multi('f', f[0] - f[1]));
        if (operation == Parser.getOperator("*"))
            return (new Multi('f', f[0] * f[1]));
        if (operation == Parser.getOperator("/"))
            return (new Multi('f', f[0] / f[1]));
        if (operation == Parser.getOperator("%"))
            return (new Multi('f', f[0] % f[1]));
        if (operation == Parser.getOperator("!="))
            return (new Multi('b', f[0] != f[1]));
        if (operation == Parser.getOperator("=="))
            return (new Multi('b', f[0] == f[1]));
        if (operation == Parser.getOperator(">"))
            return (new Multi('b', f[0] > f[1]));
        if (operation == Parser.getOperator(">="))
            return (new Multi('b', f[0] >= f[1]));
        if (operation == Parser.getOperator("<"))
            return (new Multi('b', f[0] < f[1]));
        if (operation == Parser.getOperator("<="))
            return (new Multi('b', f[0] <= f[1]));
        return (null);
    }

    private static Multi operationString(Multi left, int operation, Multi right)
    {
        Multi numerical;
        Multi string;

        if (Parser.isNumericType(left.getType()) != -1 || Parser.isNumericType(right.getType()) != -1)
        {
            if (Parser.isNumericType(left.getType()) != -1) {
                numerical = left;
                string = right;
            }
            else
            {
                numerical = right;
                string = left;
            }
        }
        else
        {
            numerical = null;
            string = null;
        }
        if (operation == Parser.getOperator("+"))
        {
            //System.out.println("String + : " + new Multi('s', left.getData().toString() + right.toString()));
            return (new Multi('s', left.getData().toString() + right.toString()));
        }
        if (numerical != null && operation == Parser.getOperator("*"))
        {
            double l;
            l = Double.valueOf(numerical.getData().toString());
            //return (new Multi('s', ((int) numerical.getData()) + right.toString()));
            //System.out.println("l :: " + l); // Debug
            return (new Multi('s', ((String)string.getData()).repeat((int)(Math.floor(l))) + ((String)string.getData()).substring(0, (int)((l - (int)l) * ((String)string.getData()).length()))));
        }
        if (operation == Parser.getOperator("!="))
            return (new Multi('b', !left.getData().toString().equals(right.getData())));
        if (operation == Parser.getOperator("=="))
            return (new Multi('b', left.getData().toString().equals(right.getData())));
        if (operation == Parser.getOperator(">"))
            return (new Multi('b', left.getData().toString().length() > right.getData().toString().length()));
        if (operation == Parser.getOperator(">="))
            return (new Multi('b', left.getData().toString().length() >= right.getData().toString().length()));
        if (operation == Parser.getOperator("<"))
            return (new Multi('b', left.getData().toString().length() < right.getData().toString().length()));
        if (operation == Parser.getOperator("<="))
            return (new Multi('b', left.getData().toString().length() <= right.getData().toString().length()));
        return (null);
    }

    private static Multi operationBoolean(Multi left, int operation, Multi right)
    {
        boolean l;
        boolean r;

        System.out.println("BOOLEAN OPERATION TYPES : [" + left.getType() + "][" + right.getType() + "]"); // Debug
        if (left.getType() != 'b' || right.getType() != 'b')
            return (null);
        l = (boolean)left.getData();
        r = (boolean)right.getData();
        System.out.println("OPERATION BOOL ... " + operation + " [" + Parser.getOperator("||") + "]"); // Debug
        if (operation == Parser.getOperator("=="))
            return (new Multi('b', l == r));
        if (operation == Parser.getOperator("||"))
            return (new Multi('b', l || r));
        if (operation == Parser.getOperator("&&"))
            return (new Multi('b', l && r));
        if (operation == Parser.getOperator("!="))
            return (new Multi('b', l != r));
        System.out.println("OPERATION BOOL ... NULL"); // Debug
        return (null);
    }

    private static Multi operationChar(Multi left, int operation, Multi right)
    {
        char[] c = { (char)left.getData(), right.getType() == 'c' ? (char)right.getData() : (char)(Integer.valueOf(Double.valueOf(right.getData().toString()).toString().split("\\.")[0]) % 256) };

        if (operation == Parser.getOperator("+"))
            return (new Multi('c', c[0] + c[1]));
        if (operation == Parser.getOperator("-"))
            return (new Multi('c', c[0] - c[1]));
        if (operation == Parser.getOperator("8"))
            return (new Multi('c', c[0] * c[1]));
        if (operation == Parser.getOperator("/"))
            return (new Multi('c', c[0] / c[1]));
        if (operation == Parser.getOperator("%"))
            return (new Multi('c', c[0] % c[1]));
        if (operation == Parser.getOperator("!="))
            return (new Multi('b', c[0] != c[1]));
        if (operation == Parser.getOperator("=="))
            return (new Multi('b', c[0] == c[1]));
        if (operation == Parser.getOperator(">"))
            return (new Multi('b', c[0] > c[1]));
        if (operation == Parser.getOperator(">="))
            return (new Multi('b', c[0] >= c[1]));
        if (operation == Parser.getOperator("<"))
            return (new Multi('b', c[0] < c[1]));
        if (operation == Parser.getOperator("<="))
            return (new Multi('b', c[0] <= c[1]));
        return (null);
    }

    private static Multi operationInt(Multi left, int operation, Multi right)
    {
        if (Parser.isWholeNumber(right.getType()))
        {
            int[] i = {(int) left.getData(), Integer.parseInt(right.getData().toString().split("\\.")[0])};

            //System.out.println("i[0] = " + i[0]); // Debug
            //System.out.println("i[1] = " + i[1]); // Debug
            //System.out.println("i[0] < i[1] ? " + (i[0] < i[1])); // Debug
            //System.out.println("op < ? " + (operation == Parser.getOperator("<")) + " ["+operation+"] {"+Parser.getOperator("<")+"}"); // Debug
            if (operation == Parser.getOperator("+"))
                return (new Multi('i', i[0] + i[1]));
            if (operation == Parser.getOperator("-"))
                return (new Multi('i', i[0] - i[1]));
            if (operation == Parser.getOperator("*"))
                return (new Multi('i', i[0] * i[1]));
            if (operation == Parser.getOperator("/"))
                return (new Multi('i', i[0] / i[1]));
            if (operation == Parser.getOperator("%"))
                return (new Multi('i', i[0] % i[1]));
            if (operation == Parser.getOperator("!="))
                return (new Multi('b', i[0] != i[1]));
            if (operation == Parser.getOperator("=="))
                return (new Multi('b', i[0] == i[1]));
            if (operation == Parser.getOperator(">"))
                return (new Multi('b', i[0] > i[1]));
            if (operation == Parser.getOperator(">="))
                return (new Multi('b', i[0] >= i[1]));
            if (operation == Parser.getOperator("<"))
                return (new Multi('b', i[0] < i[1]));
            if (operation == Parser.getOperator("<="))
                return (new Multi('b', i[0] <= i[1]));
        }
        else
        {
            return (operationFloat(left, operation, right));
        }
        return (null);
    }

    private static Multi operationDouble(Multi left, int operation, Multi right)
    {
        double[] d = { (double)left.getData(), Double.valueOf(right.getData().toString()) };

        if (operation == Parser.getOperator("+"))
            return (new Multi('d', d[0] + d[1]));
        if (operation == Parser.getOperator("-"))
            return (new Multi('d', d[0] - d[1]));
        if (operation == Parser.getOperator("/"))
            return (new Multi('d', d[0] * d[1]));
        if (operation == Parser.getOperator("*"))
            return (new Multi('d', d[0] / d[1]));
        if (operation == Parser.getOperator("%"))
            return (new Multi('d', d[0] % d[1]));
        if (operation == Parser.getOperator("!="))
            return (new Multi('b', d[0] != d[1]));
        if (operation == Parser.getOperator("=="))
            return (new Multi('b', d[0] == d[1]));
        if (operation == Parser.getOperator(">"))
            return (new Multi('b', d[0] > d[1]));
        if (operation == Parser.getOperator(">="))
            return (new Multi('b', d[0] >= d[1]));
        if (operation == Parser.getOperator("<"))
            return (new Multi('b', d[0] < d[1]));
        if (operation == Parser.getOperator("<="))
            return (new Multi('b', d[0] <= d[1]));
        return (null);
    }

    private static Multi operationLong(Multi left, int operation, Multi right)
    {
        long[] l = { (long)left.getData(), Long.valueOf(right.getData().toString().split("\\.")[0]) };

        if (operation == Parser.getOperator("+"))
            return (new Multi('l', l[0] + l[1]));
        if (operation == Parser.getOperator("-"))
            return (new Multi('l', l[0] - l[1]));
        if (operation == Parser.getOperator("*"))
            return (new Multi('l', l[0] * l[1]));
        if (operation == Parser.getOperator("/"))
            return (new Multi('l', l[0] / l[1]));
        if (operation == Parser.getOperator("%"))
            return (new Multi('l', l[0] % l[1]));
        if (operation == Parser.getOperator("!="))
            return (new Multi('b', l[0] != l[1]));
        if (operation == Parser.getOperator("=="))
            return (new Multi('b', l[0] == l[1]));
        if (operation == Parser.getOperator(">"))
            return (new Multi('b', l[0] > l[1]));
        if (operation == Parser.getOperator(">="))
            return (new Multi('b', l[0] >= l[1]));
        if (operation == Parser.getOperator("<"))
            return (new Multi('b', l[0] < l[1]));
        if (operation == Parser.getOperator("<="))
            return (new Multi('b', l[0] <= l[1]));
        return (null);
    }

    private static Multi operationRow(Multi left, int operation, Multi right)
    {
        Row[] l = { (Row)left.getData(), (Row)right.getData() };
        ArrayList<Multi> tmp;
        Row row;

        row = new Row(l[0].getCol());
        if (operation == Parser.getOperator("+"))
        {
            tmp = l[1].getCol();
            for (int i = 0; i < tmp.size(); i++)
                row.addCol(tmp.get(i));
            return (new Multi('r', row));
        }
        return (new Multi('E', null));
    }

    private static Multi operationDate(Multi left, int operation, Multi right)
    {
        return (new Multi('s', "Table operation not implemented yet"));
    }

    private static Multi operationTable(Multi left, int operation, Multi right)
    {
        /*
        Table[] l = { (Table)left.getData(), (Table)right.getData() };
        ArrayList<Multi> tmp;
        Row row;

        row = new Row(l[0].getCol());
        if (operation == Parser.getOperator("+"))
        {
            tmp = l[1].getCol();
            for (int i = 0; i < tmp.size(); i++)
                row.addCol(tmp.get(i));
            return (new Multi('r', row));
        }
        return (new Multi('E', null));
        */
        return (new Multi('s', "Table operation not implemented yet"));
    }

    private static Multi operationNull(Multi left, int operation, Multi right)
    {
        Multi swp;

        if (left.getType() != 'E' && right.getType() == 'E')
        {
            swp = left;
            left = right;
            right = swp;
        }
        if (operation == Parser.getOperator("==") || operation == Parser.getOperator("!="))
        {
            boolean empty;

            if (left.getType() == 'E')
            {
                empty = right.getData() == null ? true : false;
                //System.out.println("Debug OPERATION NULL : " + (operation == Parser.getOperator("==") ? empty : !empty));
                return (new Multi('b', operation == Parser.getOperator("==") ? empty : !empty));
            }
            empty = left.getData() == null ? true : false;
            return (new Multi('b', operation == Parser.getOperator("==") ? empty : !empty));
        }
        return (null);
    }

    public static Multi operation(Multi left, String op, Multi right, HashMap<String, Multi> stack)
    {
        int operation;

        if (Parser.isEmpty(op))
            return (new Multi('E', null));
        operation = Parser.getOperator(op);
        if (operation == -1)
            return (new Multi('E', null));
            //return (null);
        System.out.println("Expression: op=[" + op + "]"); // Debug
        //if (right == null) // Original
        if (right == null && Parser.isComparaisonOperator(Parser.getOperator(operation)) == -1)
            return (left);
        System.out.println("LeftType :::: " + (left == null ? "null!" : left.getType())); // Debug
        System.out.println("RightType :::: " + (right == null ? "null!" : right.getType())); // Debug
        //System.out.println("isAssignationOperator : [" + Parser.isAssignationOperator(Parser.getOperator(operation)) + "]"); // Debug
        if (Parser.isAssignationOperator(Parser.getOperator(operation)) != -1)
        {

            if (operation == Parser.getOperator("="))
                return (right);
            operation = Parser.getOperator(Parser.getOperator(operation).substring(0, op.length() - 1));
        }
        if (left == null)
        {
            //return (null);
            return (new Multi('E', null));
        }
        if (right.getType() == 'z')
            right = ((FunctionCall)right.getData()).run(stack);
        if (operation == Parser.getOperator("=")) // Equals
            return (new Multi(right.getType(), right.getData()));
        if (left.getType() == 'E' || right.getType() == 'E')
            return (operationNull(left, operation, right));
        if (left.getType() == 's' || right.getType() == 's')
            return (operationString(left, operation, right));
        if (left.getType() == 'b' || right.getType() == 'b')
        {
            System.out.println("Operation boolean"); // Debug
            return (operationBoolean(left, operation, right));
        }
        if (left.getType() == 'r' && right.getType() == 'r')
            return (operationRow(left, operation, right));
        if (left.getType() == 't' && right.getType() == 't')
            return (operationTable(left, operation, right));
        if (left.getType() == 'D')
            return (operationDate(left, operation, right));
        if (Parser.isNumericType(right.getType()) == -1)
            return (null);
        if (left.getType() == 'c')
            return (operationChar(left, operation, right));
        if (left.getType() == 'i')
            return (operationInt(left, operation, right));
        if (left.getType() == 'f')
            return (operationFloat(left, operation, right));
        if (left.getType() == 'd')
            return (operationDouble(left, operation, right));
        if (left.getType() == 'l')
            return (operationLong(left, operation, right));
        //System.out.println("LeftType == " + left.getType()); // Debug
        // Manque Array, Table, Date, Object
        return (null);
    }

    public Multi        result(HashMap<String, Multi> stack)
    {
        char    type;
        Multi   cast;
        Multi   left;
        String  operator;
        Multi   right;

        //System.out.println("::: Expression::Result :::"); // Debug
        if (elements.size() == 0)
            return (new Multi('E', null)); // return (null); <- original
        cast = null;
        operator = "";
        left = elements.get(0);
        if (left.getType() == 'e' || left.getType() == 'T' || left.getType() == 'z')
            left = ((Computable) left.getData()).result(stack);
        // TODO DEBUG BOOLEAN ALGEBRA + NULL VALUES
        for (int i = 0; i < elements.size(); i++) // Debug
            System.out.println("E: " + elements.get(i)); // Debug
        // TODO DEBUG BOOLEAN ALGEBRA + NULL VALUES
        System.out.println("(" + elements.size() + ")-- TYPE :: " + left.getType()); // Debug
        for (int i = 1; i < elements.size(); i++)
        {
            System.out.println("Expression::result LOOP"); // Debug
            right = elements.get(i);
            type = right.getType();
            System.out.println("TYPE :: " + type); // Debug
            if (/*i == 1 && */left.getType() == 'C')
            {
                cast = left;
                left = right; // Todo
                if (left.getType() == 'e' || left.getType() == 'T' || left.getType() == 'z')
                    left = ((Computable)left.getData()).result(stack);
                System.out.println("Get Cast type ::: " + Parser.getCastType((int)cast.getData())); // Debug
                switch (Parser.getCastType((int)cast.getData()))
                {
                    //case 'b': left = operation(right, Parser.getOperator("="), new Multi(type, right.getData()), stack); break;
                    //case 'c': left = operation(right, Parser.getOperator("="), new Multi(type, (char)right.getData()), stack); break;
                    case 'i': cast = operation(right, "=", new Multi('i', Integer.valueOf(right.getData().toString().split("\\.")[0])), stack); break;
                    case 's': cast = operation(right, "=", new Multi('s', "" + right.getData()), stack); break;
                    case 'f': cast = operation(right, "=", new Multi('f', Float.valueOf(right.getData().toString())), stack); break;
                    case 'd': cast = operation(right, "=", new Multi('d', Double.valueOf(right.getData().toString())), stack); break;
                    case 'l': cast = operation(right, "=", new Multi('l', Long.valueOf(right.getData().toString())), stack); break;
                }
                //System.out.println("OUT: [" + right.getData() + "] " + operator + " [" + cast.getData() + "]"); // Debug
                left = operation(left, operator.length() > 0 ? operator : "=", cast, stack);
                //if (elements.size() != 2)
                continue;
            }
            if (type == 'O') {
                operator = (String) right.getData();
                if (Parser.getOperator(operator) == Parser.getOperator("||") && left.getType() == 'b' && (boolean)left.getData()) // Todo TESTS --------------------
                    return (new Multi('b', true)); // Todo TESTS --------------------
                //System.out.println("Operator: ("+operator+")"); // Debug
                continue;
            }
            else
            {
                if (right.getType() == 'C')
                    continue;
                if (right.getType() == 'e' || right.getType() == 'T' || right.getType() == 'z')
                    right = ((Computable)right.getData()).result(stack);
            }
            System.out.println("LEFT[" + (left != null) + "] RIGHT["+(right != null)+"]"); // Debug
            System.out.println(":::Operator: ("+operator+") ["+Parser.getOperator(operator)+"]"); // Debug
            left = operation(left, operator, right, stack);
            System.out.println("OPERATION DONE ; LEFT[" + (left != null) + "]");
            if (left.getType() == 'E') // Null error
                System.out.println("ERROR: operation return NULL value");
            System.out.println("LEFT (" + left.getType() + "): " + left.getData()); // Debug
        }
        System.out.println("Return left: " + left); // Debug
        return (left);
    }

    public String toString()
    {
        return (expr);
    }
}
