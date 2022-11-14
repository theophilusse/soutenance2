package com.example.exojavafx;

import java.util.HashMap;

public class TernaryExpression implements Computable {

    private Expression comp, rTrue, rFalse;

    public TernaryExpression(Expression comp, Expression left, Expression right)
    {
        this.comp = comp;
        rTrue = left;
        rFalse = right;
    }

    public Multi result(HashMap<String, Multi> stack)
    {
        Multi tmp;

        if (stack == null)
            return (null);
        tmp = comp.result(stack);
        if (tmp == null || tmp.getType() != 'b')
            return (null);
        return ((boolean)tmp.getData() ? rTrue.result(stack) : rFalse.result(stack));
    }

    public String toString()
    {
        return ("(" + comp.toString() + " ? " + rTrue.toString() + " : " + rFalse.toString() + ")");
    }
}
