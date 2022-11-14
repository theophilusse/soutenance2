package com.example.exojavafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * The type Parser.
 */
public class Parser {

    public static String[] insertLines(String[] in, int index, String[] insert) // Not tested
    {
        int         k;
        String[]    ret;

        if (isEmpty(in))
            return (isEmpty(insert) ? null : insert);
        if (isEmpty(insert) || index < 0 || index >= in.length)
            return (in);
        ret = new String[in.length + insert.length];
        k = 0;
        if (index == 0)
        {
            for (int i = 0; i < insert.length; i++)
                ret[k++] = insert[i];
            for (int i = 0; i < in.length; i++)
                ret[k++] = in[i];
        }
        else if (index >= in.length - 1)
        {
            for (int i = 0; i < in.length; i++)
                ret[k++] = in[i];
            for (int i = 0; i < insert.length; i++)
                ret[k++] = insert[i];
        }
        else
        {
            for (int i = 0; i < index; i++)
                ret[k++] = in[i];
            for (int i = 0; i < insert.length; i++)
                ret[k++] = insert[i];
            for (int i = index; i < in.length; i++)
                ret[k++] = in[i];
        }
        return (ret);
    }

    public static String[] replaceLine(String[] in, int index, String[] insert)
    {
        int         k;
        String[]    ret;

        if (isEmpty(in))
            return (isEmpty(insert) ? null : insert);
        if (isEmpty(insert) || index < 0 || index >= in.length)
            return (in);
        ret = new String[in.length + insert.length - 1];
        k = 0;
        if (index <= 0)
        {
            for (int i = 0; i < insert.length; i++)
                ret[k++] = insert[i];
            for (int i = 1; i < in.length; i++)
                ret[k++] = in[i];
        }
        else if (index >= in.length - 1)
        {
            for (int i = 0; i < in.length - 1; i++)
                ret[k++] = in[i];
            for (int i = 0; i < insert.length; i++)
                ret[k++] = insert[i];
        }
        else
        {
            for (int i = 0; i < index; i++)
                ret[k++] = in[i];
            for (int i = 0; i < insert.length; i++)
                ret[k++] = insert[i];
            for (int i = index + 1; i < in.length; i++)
                ret[k++] = in[i];
        }
        return (ret);
    }

    public static String reverseString(String s)
    {
        String ret;

        if (isEmpty(s))
            return (null);
        if (s.length() == 1)
            return (s);
        ret = "";
        for (int i = s.length() - 1; i >= 0; i--)
            ret += s.charAt(i);
        return (ret);
    }
    public static String dupChar(char c, int n)
    {
        String      ret;

        ret = "";
        n = Math.abs(n);
        while (n-- != 0)
            ret += c;
        return (ret);
    }

    public static boolean isEmpty(String s)
    {
        return (s == null || s.length() == 0);
    }

    public static boolean isEmpty(Multi e)
    {
        return (e == null || e.getType() == 'E');
    }

    public static boolean isEmpty(String[] s)
    {
        return (s == null || s.length == 0);
    }

    public static boolean isEmptyLine(String s)
    {
        if (isEmpty(s))
            return (true);
        for (int i = 0; i < s.length() && s.charAt(i) != '\n'; i++)
            if (s.charAt(i) != ' ' && s.charAt(i) != '\t')
                return (false);
        return (true);
    }

    /**
     * Teste si la valeur d'un char est comprise entre 0 et 9
     *
     * @param c the c
     * @return the boolean
     */
    public static boolean isDigit(char c)
    {
        return (c >= '0' && c <= '9');
    }

    public static boolean isLowercase(char c)
    {
        return (c >= 'a' && c <= 'z');
    }

    public static boolean isUppercase(char c)
    {
        return (c >= 'A' && c <= 'Z');
    }

    public static boolean isAlphabetic(char c)
    {
        if (isLowercase(c) || isUppercase(c))
            return (true);
        return (false);
    }

    public static boolean isAlphaNum(char c)
    {
        if (isAlphabetic(c) || isDigit(c))
            return (true);
        return (false);
    }

    public static boolean isBlank(char c)
    {
        return (c <= ' ' || c == '\t' || c == '\n' || c == '\r');
    }

    public static boolean isPrintable(char c)
    {
        return (c > ' ' && c != '\t' && c != '\n' && c != '\r');
    }

    public static boolean isVisible(char c)
    {
        return (c > ' ');
    }

    public static boolean isSeparator(char c)
    {
        return (c == ' ' || c == '\t' || c == '\r' || c == '\n');
    }

    public static int isAritmeticOperator(String c)
    {
        return (listIndexOperator(aritmeticOperator, c));
    }

    public static int isComparaisonOperator(String c)
    {
        return (listIndexOperator(comparaisonOperator, c));
    }

    public static int isLogicalOperator(String c)
    {
        return (listIndexOperator(logicalOperator, c));
    }

    public static int isBinaryOperator(String c)
    {
        return (listIndexOperator(binaryOperator, c));
    }

    public static int isAssignationOperator(String c)
    {
        return (listIndexOperator(assignationOperator, c));
    }

    public static int isScopeDelimiter(String c)
    {
        return (listIndex(scopeDelimiter, c));
    }

    public static int isStringDelimiter(char c)
    {
        String s;

        s = "" + c;
        return (listIndex(stringDelimiter, s));
    }

    public static int isElementDelimiter(String c)
    {
        return (listIndex(elementDelimiter, c));
    }

    public static boolean isDelimiter(String c)
    {
        return (listIndex(stringDelimiter, c) != -1 ||
                listIndex(scopeDelimiter, c) != -1 ||
                listIndex(elementDelimiter, c) != -1);
    }

    public static boolean isEscape(String c)
    {
        return (c.equals("\\"));
    }

    public static boolean isVariable(String c)
    {
        if (isEmpty(c))
            return (false);
        c = c.charAt(0) == '-' ? c.substring(1) : c;
        if (isEmpty(c) || isDigit(c.charAt(0)))
            return (false);
        if (c.length() == 1 && (isAlphabetic(c.charAt(0)) || c.charAt(0) == '_'))
            return (true);
        for (int i = 0; i < c.length(); i++)
            if (!isAlphaNum(c.charAt(i)) && c.charAt(i) != '_')
                return (false);
        return (true);
    }

    public static int isPointer(String c)
    {
        if (c == null || c.length() < 3)
            return (-1);
        if (!isVariable(c.substring(1)))
            return (-1);
        return (listIndex(pointer, c.substring(0, 1)));
    }

    public boolean isReserved(String c)
    {
        return (isNativeFunction(c) != -1 || isVerb(c) != -1);
    }

    public static String getNativeFunction(int function)
    {
        return (function < 0 || function >= nativeFunction.length ? "" : nativeFunction[function]);
    }

    public static int isNativeFunction(String c)
    {
        return (listIndexStrict(nativeFunction, c));
    }

    public static String getNativeMethod(int method)
    {
        return (method < 0 || method >= nativeMethod.length ? "" : nativeMethod[method]);
    }

    public static int isNativeMethod(String c)
    {
        return (listIndexStrict(nativeMethod, c));
    }

    public static int isVerb(String c)
    {
        return (listIndexStrict(verb, c));
    }

    public static int isCast(String c)
    {
        if (isEmpty(c))
            return (-1);
        for (int i = 0; i < cast.length; i++)
            if (cast[i].length() <= c.length() && cast[i].equals(c.substring(0, cast[i].length())))
                return (i);
        return (-1);
    }

    public static char getCastType(int c)
    {
        if (c < 0 || c > cast.length)
            return ('\0');
        return (cast[c].toLowerCase().charAt(0)); // May cause bug for other types
        /*
        switch (cast[c])
        {
            case "(bool)": return ('b');
            case "(char)": return ('c');
            case "(int)": return ('i');
            case "(String)": return ('s');
            case "(float)": return ('f');
            case "(double)": return ('d');
            case "(long)": return ('l');
        }
        return ('\0');
        */
    }

    public static String copyOperator(String c)
    {
        int from;
        int to;

        if (isEmpty(c))
            return (null);
        from = 0;
        while (from < c.length() && isBlank(c.charAt(from)))
            from++;
        to = from + 1;
        while (to < c.length() && isOperator("" + c.charAt(to)))
            to++;
        return (to < from || to >= c.length() ? null : c.substring(from, to));
    }

    public static String copyExpression(String s)
    {
        int from;
        int k;
        char c;

        if (isEmpty(s))
            return (null);
        from = 0;
        while (from < s.length() && isBlank(s.charAt(from)))
            from++;
        if (from >= s.length())
            return (null);
        k = 0;
        for (int i = 0; i < s.length(); i++)
        {
            c = s.charAt(i);
            if (c == '(') {
                if (++k == 1)
                    from = i;
            }
            else if (c == ')') {
                if (--k <= 0)
                    //return (i - 1 < 0 || from + 1 > i - 1 ? "" : s.substring(from + 1, i - 1));
                    return (i - 1 < 0 || from + 1 > i - 1 ? "" : s.substring(from + 1, i));
            }
        }
        return (null);
    }

    public static ArrayList<Multi> parameterList(String parameters, HashMap<String, Multi> stack)
    {
        //public FunctionCall(String call, String parameters, HashMap<String, Multi> stack)
        ArrayList<Multi> param;
        String   buf;
        int      from;
        int      offset;

        if (stack == null)
            return (null);
        param = new ArrayList<Multi>();
        from = 0;
        System.out.println("PARAM :: [" + parameters + "]"); // Debug
        if (!Parser.isEmpty(parameters))
            for (int i = 0; i < parameters.length(); i++)
            {
                buf = copyVariable(parameters.substring(i));
                System.out.println("PARAM <- [" + parameters.substring(i) + "]"); // Debug
                System.out.println("VAR   -- [" + buf + "]"); // Debug
                if (i < parameters.length() && (parameters.charAt(i) == '(' || parameters.charAt(i) == ',' || i + 1 >= parameters.length()))
                {
                    if (parameters.charAt(i) == '(')
                    {
                        offset = Parser.closingOffset('(', ')', parameters.substring(i));
                        if (offset == -1)
                            return (null);
                        i += offset;
                        System.out.println("PARAM PARENTHESIS :: [" + parameters.substring(i) + "]"); // Debug
                    }
                    if (parameters.charAt(i) == ',' || i + 1 >= parameters.length())
                    {
                        System.out.println("PARAM ADD :: [" + parameters.substring(from, i + (i + 1 >= parameters.length() ? 1 : 0)) + "]"); // Debug
                        param.add(new Expression(parameters.substring(from, i + (i + 1 >= parameters.length() ? 1 : 0)), stack).result(stack));
                    }
                    from = i + 1;
                    while (from < parameters.length() && Parser.isBlank(parameters.charAt(from)))
                        from++;
                    i = from - 1;
                }
                System.out.println("PARAM -> [" + parameters.substring(i) + "]");
            }
        return (param);
    }

    public static boolean isOperator(String c)
    {
        return (isAritmeticOperator(c) != -1 ||
                isAssignationOperator(c) != -1 ||
                isBinaryOperator(c) != -1 ||
                isComparaisonOperator(c) != -1 ||
                isLogicalOperator(c) != -1);
    }

    public static int           getOperator(String operator)
    {
        int             i;
        int             k;

        k = 0;
        i = -1;
        while (++i < aritmeticOperator.length)
            if (operator.equals(aritmeticOperator[i]))
                return (i);
        k += i;
        i = -1;
        while (++i < assignationOperator.length)
            if (operator.equals(assignationOperator[i]))
                return (k + i);
        k += i;
        i = -1;
        while (++i < binaryOperator.length)
            if (operator.equals(binaryOperator[i]))
                return (k + i);
        k += i;
        i = -1;
        while (++i < comparaisonOperator.length)
            if (operator.equals(comparaisonOperator[i]))
                return (k + i);
        k += i;
        i = -1;
        while (++i < logicalOperator.length)
            if (operator.equals(logicalOperator[i]))
                return (k + i);
        return (-1);
    }

    public static String           getOperator(int operator)
    {
        int offset;

        if (operator < 0)
            return (null);
        if (operator < aritmeticOperator.length)
            return (aritmeticOperator[operator]);
        if (operator < aritmeticOperator.length + assignationOperator.length)
            return (assignationOperator[operator - aritmeticOperator.length]);
        if (operator < aritmeticOperator.length + assignationOperator.length + binaryOperator.length)
            return (binaryOperator[operator - (aritmeticOperator.length + assignationOperator.length)]);
        if (operator < aritmeticOperator.length + assignationOperator.length + binaryOperator.length + comparaisonOperator.length)
            return (comparaisonOperator[operator - (aritmeticOperator.length + assignationOperator.length + binaryOperator.length)]);
        if (operator < aritmeticOperator.length + assignationOperator.length + binaryOperator.length + comparaisonOperator.length + logicalOperator.length)
            return (logicalOperator[operator - (aritmeticOperator.length + assignationOperator.length + binaryOperator.length + comparaisonOperator.length)]);
        return (null);
    }

    public static String     getSuffix(int sfx)
    {
        if (sfx < 0 || sfx >= suffix.length)
            return ("");
        return (suffix[sfx]);
    }

    public static int     getSuffix(String c)
    {
        int i;

        i = 0;
        while (i < c.length() && (isAlphaNum(c.charAt(i)) || c.charAt(i) == '('))
        {
            if (c.charAt(i) == '(')
                i += copyExpression(c).length() + 2;
            else
                i++;
        }
        return (listIndex(suffix, c.substring(i)));
    }

    public static boolean isRealNumber(char c)
    {
        return (c == 'f' || c == 'd');
    }

    public static boolean isWholeNumber(char c)
    {
        return (c == 'c' || c == 'i' || c == 'l');
    }

    public static int isNumericType(char c)
    {
        return (listIndex(numericType, c));
    }

    static char[]   numericType = {
        'c', 'i', 'd', 'f', 'l'
    };

    static String[] cast = {
            "bool",       // 0
            "char",       // 1
            "int",        // 2
            "String",     // 3
            "float",      // 4
            "double",     // 5
            "long"        // 6
    };

    static String[] nativeMethod = {
            "type",
            "length",
            "width",
            "split",
            "trim",
            "uppercase",
            "lowercase",
            "capitalize",
            "substring",
            "escape",
            "replace",
            "replaceAll",
            "replaceFirst",
            "get",
            "set",
            "add",
            "remove",
            "pow", //
            "min",
            "max",
            "avg",
            "sqrt",
            "abs",
            "floor",
            "ceil",
            "clamp",
            "sort",
            "loadPage",
            "close",
            "getByXPath",
            "getNodeName",
            "getText",
            "getAttribute"
    };

    static String[] nativeFunction = {
            "shell",
            "read",
            "write",
            "free",
            "rand",
            "sqrt",
            "declared",
            "print",
            "println",
            "pause",
            "any",
            "script",         // meh
    };
    static String[] verb = {
            "if",           // 1
            "else",
            "elseif",
            "?",
            ":",
            "break",
            "continue",
            "func",
            "endfunc",
            "return",
            "while",
            "wend",
            "label",
            "goto",
            "exit"
    };

    static String[] pointer = {
            "*", // meh
            "#", // meh
            "$",
            "@",
            "~" // meh
    };

    static String[] suffix = {
            "++",
            "--",
            "."
    };

    static String[] elementDelimiter = {
            ";",
            ":",
            "::",
            ",",
            "."
    };
    static String[] stringDelimiter = {
            "'",
            "\""
    };

    static String[] scopeDelimiter = {
            "(",
            ")",
            "[",
            "]",
            "{",
            "}",
            "<",
            ">"
    };
    static String[] assignationOperator = {
            "++",
            "--",
            "=",
            "+=",
            "-=",
            "*=",
            "/=",
            "%=",
            "|=",
            "&=",
            "^=",
            "<<=",
            ">>="
    };
    static String[] aritmeticOperator = {
            "=",
            "+",
            "+=",
            "++",
            "-",
            "--",
            "-=",
            "*",
            "*=",
            "/",
            "/=",
            "%",
            "%="
    };

    static String[] binaryOperator = {
            "!",
            "|",
            "|=",
            "&",
            "&=",
            "^",
            "^=",
            "<<",
            "<<=",
            ">>",
            ">>="
    };
    static String[] comparaisonOperator = {
            ">",
            "<",
            "<=",
            ">=",
            "!=",
            "=="
    };
    static String[] logicalOperator = {
            "&&",
            "||"
    };

    public static int listIndex(char[] list, char c)
    {
        if (list == null)
            return (-1);
        for (int i = 0; i < list.length; i++)
            if (list[i] == c)
                return (i);
        return (-1);
    }

    public static int listIndexOperator(String[] list, String c)
    {
        if (list == null)
            return (-1);
        for (int i = 0; i < list.length; i++)
            if (c.length() == list[i].length() && list[i].equals(c))
                return (i);
        return (-1);
    }

    public static int listIndexStrict(String[] list, String c)
    {
        if (list == null)
            return (-1);
        for (int i = 0; i < list.length; i++)
            if (c.length() >= list[i].length() && list[i].equals(c.substring(0, list[i].length())) &&
                    (list[i].length() == c.length() ||
                            (!isAlphaNum(c.charAt(list[i].length())) && c.charAt(list[i].length()) != '_')))
                return (i);
        return (-1);
    }

    public static int listIndex(String[] list, String c)
    {
        if (list == null)
            return (-1);
        for (int i = 0; i < list.length; i++)
            if (c.length() >= list[i].length() && list[i].equals(c.substring(0, list[i].length())))
                return (i);
        return (-1);
    }

    public static char getNumericType(int type)
    {
        if (type < 0 || type > numericType.length)
            return ('\0');
        return (numericType[type]);
    }

    public static char getNumericType(String s)
    {
        int i;
        boolean dot;

        if (isEmpty(s))
            return ('\0');
        s = s.trim();
        i = 0;
        if (s.charAt(0) == '-')
            i++;
        if (!isDigit(s.charAt(i)))
            return ('\0');
        dot = false;
        while (++i < s.length() && (isDigit(s.charAt(i)) || s.charAt(i) == '.'))
            if (s.charAt(i) == '.')
            {
                if (dot)
                    return ('\0');
                dot = true;
            }
        if (i == s.length() || isNumericType(s.charAt(i)) != '\0')
            return (dot ? 'd' : 'i');
        else
        {
            if (s.charAt(i) == 'd')
                return ('d');
            if (s.charAt(i) == 'f')
                return ('f');
            if (!dot && s.charAt(i) == 'l')
                return ('l');
            if (!dot && s.charAt(i) == 'c')
                return ('c');
        }
        return ('\0');
    }

    public static String skipWord(String s, int wordIndex)
    {
        int i;

        i = 0;
        while (wordIndex > 0) {
            while (i < s.length() && s.charAt(i) == ' ')
                i++;
            if (i == s.length())
                return (null);
            while (i < s.length() && s.charAt(i) != ' ')
                i++;
            if (i == s.length())
                return (null);
            wordIndex--;
        }
        return (s.substring(i));
    }

    public static int firstOccurence(String[] l, int i, int step, String occ)
    {
        if (isEmpty(l) || isEmpty(occ) || i < 0 || step == 0)
            return (-1);
        while (i >= 0 && i < l.length)
            if (l[i].trim().split("[ \\t]")[0].equals(occ))
                return (i);
            else
                i += step;
        return (-1);
    }

    public static int startScope(String[] l, int i, String open, String close)
    {
        int k;

        if (isEmpty(l) || isEmpty(open) || isEmpty(close) || i < 0)
            return (-1);
        k = 0;
        while (i >= 0)
        {
            //System.out.println("startScope: (k=" + k + ") " + l[i]); // Debug
            if (l[i].trim().split("[ \\t]")[0].equals(open) && ++k >= 0)
            {
                //System.out.println("startScope: Return: " + l[i]); // Debug
                return (i);
            }
            else if (l[i].trim().equals(close))
                k--;
            i--;
        }
        return (-1);
    }

    public static int endScope(String[] l, int i, String open, String close)
    {
        int k;

        if (isEmpty(l) || isEmpty(open) || isEmpty(close) || i < 0)
            return (-1);
        k = 0;
        while (i < l.length)
        {
            if (l[i].trim().split("[ \\t]")[0].equals(open))
                k++;
            //else if (l[i].trim().split("[ \\t]")[0].equals(close) && --k <= 0)
            else if (l[i].trim().equals(close) && --k <= 0)
                return (i);
            i++;
        }
        //System.out.println("RETURN::: ERROR"); // Debug
        return (-1);
    }

    public static int gotoElse(String[] l, int i)
    {
        if (isEmpty(l) || i < 0)
            return (-1);
        if (l[i].equals("else"))
            i++;
        while (i < l.length)
        {
            if (l[i].trim().split("[ \\t]")[0].equals("if"))
                i += endScope(l, i + 1, "if", "endif");
            else if (l[i].trim().split("[ \\t]")[0].equals("else"))
                return (i);
            i++;
        }
        return (-1);
    }

    public static int controlWhile(String[] l, int eip, HashMap<String, Multi> stack)
    {
        Multi tmp;

        if (l == null || l.length == 0 || eip < 0 || stack == null)
            return (-1);
        tmp = new Expression(Parser.skipWord(l[eip], 1), stack).result(stack);
        if (tmp == null || tmp.getType() != 'b')
            return (-1);
        if ((boolean)tmp.getData() == false) {
            //System.out.println("WHILE"); // Debug
            eip = Parser.endScope(l, eip, "while", "wend");
            //System.out.println("Jumping to: [" + l[i] + "]"); // Debug
        }
        return (eip);
    }

    public static int controlElseIf(String[] l, int eip, HashMap<String, Multi> stack)
    {
        Multi tmp;

        if (l == null || l.length == 0 || eip < 0 || eip >= l.length || stack == null)
            return (-1);
        tmp = new Expression(Parser.skipWord(l[eip], 2), stack).result(stack);
        if (tmp == null || tmp.getType() != 'b')
            return (-1);
        if ((boolean)tmp.getData() == false)
        {
            eip = Parser.gotoElse(l, eip);
            if (eip < 0)
            {
                eip = Parser.endScope(l, eip, "if", "endif");
                if (eip < 0)
                    return (-1);
            }
        }
        return (eip);
    }

    public static int controlIf(String[] l, int eip, HashMap<String, Multi> stack)
    {
        Multi tmp;
        int ip;

        if (l == null || l.length == 0 || eip < 0 || eip >= l.length || stack == null)
            return (-1);
        tmp = new Expression(Parser.skipWord(l[eip], 1), stack).result(stack);
        if (tmp == null || tmp.getType() != 'b')
            return (-1);
        ip = eip;
        if ((boolean)tmp.getData() == false)
        {
            ip = Parser.gotoElse(l, eip);
            if (ip < 0)
            {
                ip = Parser.endScope(l, eip, "if", "endif");
                if (ip < 0)
                    return (-1);
            }
        }
        return (ip);
    }

    public static int closingOffset(char open, char close, String s)
    {
        char            c;
        int             k;
        int             len;

        if (s == null)
            return (-1);
        len = s.length();
        if (len == 0)
            return (-1);
        k = 0;
        //System.out.println("Closing Ofsset: " + s); // Debug
        for (int i = 0; i < len; i++)
        {
            c = s.charAt(i);
            //System.out.println("Debuggg c: [" + c + "]");
            if (isStringDelimiter(c) != -1)
                while (++i < len && (s.charAt(i) != c || (s.charAt(i) == c && s.charAt(i - 1) != '\\'))) ;
            else if (close == c && --k <= 0)
            {
                //System.out.println("Debuggg return");
                return (i);
            }
            else if (open == c)
                k++;
        }
        return (len - 1);
    }

    public static String        stringFormatConversion(String s)
    {
        String ret;
        char   c;

        if (isEmpty(s))
            return ("");
        ret = "";
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '\\')
            {
                i++;
                if (i == s.length())
                    break;
                c = s.charAt(i);
                switch (c) {
                    case '0': c = '\0'; break;
                    case 'a': c = (char) 7; break;
                    case 'b': c = '\b'; break;
                    case 't': c = '\t'; break;
                    case 'n': c = '\n'; break;
                    case 'v': c = (char) 11; break;
                    case 'f': c = '\f'; break;
                    case 'r': c = '\r';
                }
                ret += c;
            }
            else
                ret += s.charAt(i);
        return (ret);
    }
    public static String        escape(String s)
    {
        String ret;
        char c;

        if (Parser.isEmpty(s))
            return ("");
        ret = "";
        for (int i = 0; i < s.length(); i++)
        {
            c = s.charAt(i);
            if (c == '\'' || c == '"')
                ret += '\\';
            ret += c;
        }
        return (ret);
    }

    public static String        parseString(String s)
    {
        return (parseStringFormat(s, true));
    }

    public static String        parseStringFormat(String s, boolean format)
    {
        String          ret;
        int             to;
        int             len;
        int             delimiter;
        int             from;

        if (s == null)
            return ("");
        len = s.length();
        if (len == 0)
            return ("");
        from = 0;
        while (from < len && isStringDelimiter(s.charAt(from)) == -1)
            from++;
        to = from + 1;
        if (to >= len)
            return ("");
        delimiter = isStringDelimiter(s.charAt(from));
        while (to < len && (isStringDelimiter(s.charAt(to)) != delimiter || (to - 1 > 0 && s.charAt(to - 1) == '\\')))
            to++;
        if (from + 1 > to - 1)
            return ("");
        return (format ? stringFormatConversion(s.substring(from + 1, to)) : s.substring(from + 1, to));
    }

    public static int               skipBlank(String s, int index)
    {
        if (s == null || s.length() == 0 || index < 0)
            return (-1);
        while (index < s.length() && Parser.isBlank(s.charAt(index)))
            index++;
        if (index == s.length())
            return (-1);
        return (index);
    }

    public static boolean          isFunctionCall(String line) // Todo
    {
        int k;

        if (isEmpty(line))
            return (false);
        line = line.trim();
        k = 0;
        while (k < line.length() && Parser.isAlphaNum(line.charAt(k)))
            k++;
        return (k < line.length() && (line.charAt(k) == '.' || line.charAt(k) == '('));
    }

    public static String[]         isTernary(String expression)
    {
        boolean         symLeft;
        boolean         symRight;
        String[]        ret;
        int             from;

        if (Parser.isEmpty(expression))
            return (null);
        symLeft = false;
        symRight = false;
        for (int i = 0; i < expression.length(); i++)
        {
            if (isStringDelimiter(expression.charAt(i)) != -1)
                i += parseStringFormat(expression.substring(i), false).length() + 2;
            else if (expression.charAt(i) == '(')
                i += copyExpression(expression.substring(i)).length() + 2;
            else if (expression.charAt(i) == '?') {
                if (symLeft)
                    return (null);
                symLeft = true;
            }
            else if (expression.charAt(i) == ':') {
                if (symRight)
                    return (null);
                symRight = true;
            }
        }
        if (!symLeft || !symRight)
            return (null);
        from = 0;
        ret = new String[3];
        symLeft = false;
        symRight = false;
        for (int i = 0; i < expression.length(); i++)
        {
            if (isStringDelimiter(expression.charAt(i)) != -1)
                i += parseStringFormat(expression.substring(i), false).length() + 2;
            else if (expression.charAt(i) == '(')
                i += copyExpression(expression.substring(i)).length() + 2;
            else if (expression.charAt(i) == '?')
            {
                if (symLeft)
                    return (null);
                symLeft = true;
                ret[0] = expression.substring(from, i - 1).trim();
                from = i + 1;
                if (from >= expression.length())
                    return (null);
            }
            else if (expression.charAt(i) == ':')
            {
                if (symRight)
                    return (null);
                symRight = true;
                ret[1] = expression.substring(from, i - 1).trim();
                from = i + 1;
                if (from >= expression.length())
                    return (null);
            }
        }
        ret[2] = expression.substring(from).trim();
        return (ret);
    }

    public static String           copyNumber(String s)
    {
        int         i;
        int         from;
        boolean     dot;

        i = 0;
        while (i < s.length() && isBlank(s.charAt(i)))
            i++;
        if (i == s.length())
            return (null);
        from = i;
        if (s.charAt(i) == '-')
            i++;
        if (!isDigit(s.charAt(i)))
            return (null);
        dot = false;
        while (++i < s.length() && (isDigit(s.charAt(i)) || s.charAt(i) == '.'))
            if (s.charAt(i) == '.') {
                if (dot)
                    return (null);
                dot = true;
            }
        //System.out.println("Debug COPY NUMBER : [" + s.substring(from, s.length()) + "]"); // Debug
        //return (s.substring(from, i - (i < s.length() && isNumericType(s.charAt(i)) != -1 ? 0 : 1)));
        return (s.substring(from, i));
    }

    public static String           copyParam(String s)
    {
        int from, to;

        if (isEmpty(s))
            return ("");
        from = 0;
        while (from < s.length() && isBlank(s.charAt(from)))
            from++;
        while (from < s.length() && s.charAt(from) != '(')
            from++;
        to = from + closingOffset('(', ')', s.substring(from));
        from++;
        if (to < from)
            return ("");
        if (to >= s.length())
            to = s.length() - 1;
        return (s.substring(from, to));
    }

    public static String            copyWordDumb(String s)
    {
        String word;

        if (s == null || s.length() == 0)
            return (null);
        word = "";
        for (int i = 0; i < s.length(); i++)
            if (!isBlank(s.charAt(i)))
                word += s.charAt(i);
            else
                break; // Test
        return (word);
    }

    public static String            copyWord(String s)
    {
        String word;

        if (s == null || s.length() == 0)
            return (null);
        word = "";
        for (int i = 0; i < s.length(); i++)
            if (isAlphaNum(s.charAt(i)) || s.charAt(i) == '_') // || s.charAt(i) == '.')
                word += s.charAt(i);
            else
                break; // Test
        return (word);
    }

    public static String copyVariable(String s)
    {
        int from;
        int i;

        if (isEmpty(s))
            return (null);
        i = 0;
        while (i < s.length() && isBlank(s.charAt(i)))
            i++;
        if (i == s.length() || !isAlphabetic(s.charAt(i)))
            return (null);
        from = i;
        i++;
        while (i < s.length() && (isAlphaNum(s.charAt(i)) || s.charAt(i) == '_'))
            i++;
        return (s.substring(from, i));
    }

    /**
     * Converti une String au format int
     *
     * @param s the s
     * @return the int
     */
    public static int parseInt(String s)
    {
        boolean     sign;
        boolean     digit;
        String      num;
        int         ret;

        if (s == null || s.length() == 0)
            return (0);
        sign = true;
        digit = true;
        num = "";
        ret = -1;
        for (int i = 0; i < s.length(); i++)
            if (isDigit(s.charAt(i)) || (sign && s.charAt(i) == '-')) {
                if (sign && s.charAt(i) == '-' && i + 1 < s.length() && isDigit(s.charAt(i + 1)))
                {
                    sign = false;
                    continue;
                }
                if (digit)
                    digit = false;
                num += s.charAt(i);
            } else if (!digit) break;
        try { ret = Integer.parseInt(num) * (!sign ? -1 : 1); } catch (Exception e) { return (0); }
        return (ret);
    }

    /**
     * Converti une String au format double (positif)
     *
     * @param s the s
     * @return the double
     */
    public static double parsePositiveDouble(String s) {
        boolean     digit;
        boolean     dot;
        String      num;
        double      ret;

        if (s == null || s.length() == 0)
            return (-1);
        digit = true;
        dot = true;
        num = "";
        ret = -1;
        for (int i = 0; i < s.length(); i++)
            if (isDigit(s.charAt(i)) || (dot && s.charAt(i) == '.')) {
                if (digit)
                    digit = false;
                num += s.charAt(i);
                if (s.charAt(i) == '.')
                    dot = false;
            } else if (!digit) break;
        try { ret = Double.parseDouble(num); } catch (Exception e) { return (-1); }
        return (ret);
    }

    public static void displayHashMap(HashMap m)
    {
        Iterator<Multi> d;
        Iterator<Set>   k;
        Multi           v;

        System.out.println("----------------------- map");
        if (m == null) {
            System.out.println("NULL");
            return ;
        }
        k = m.keySet().iterator();
        d = m.values().iterator();
        if (!k.hasNext())
            System.out.println("Empty");
        else
            while (k.hasNext()) {
                if (d.hasNext()) {
                    v = d.next();
                    if (v == null)
                        System.out.println("k: " + k.next() + " d: NULL");
                    else
                        System.out.println("k: " + k.next() + " d(" + v.getType() + "): " + v.toString());
                }
                else
                    System.out.println("k: " + k.next() + " d: " + "END");
            }
        System.out.println("----------------------- map");
    }

}
