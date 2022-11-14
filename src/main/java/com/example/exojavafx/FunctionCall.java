package com.example.exojavafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;

public class FunctionCall implements Computable {
    String call;
    ArrayList<Multi> parameters;

    public FunctionCall(String call, String parameters, HashMap<String, Multi> stack)
    {
        int      from;
        int      offset;

        if (Parser.isEmpty(call) || stack == null)
            return ;
        this.call = call;
        this.parameters = Parser.parameterList(parameters, stack);
    }

    public Multi run(HashMap<String, Multi> stack)
    {
        return (result(stack));
    }

    public Multi result(HashMap<String, Multi> stack)
    {
        Multi                   def;
        FunctionDefinition      foo;
        String                  key;
        Iterator                iter;
        HashMap<String, Multi>  local;

        if (stack == null)
            return (null);
        def = stack.get(call);
        if (def == null || def.getType() != 'Z')
            return (null);
        foo = (FunctionDefinition)def.getData();
        // System.out.println("Parameters: (" + parameters.size() + "){" + parameters + "}");
        if (foo == null || foo.getSym().size() != parameters.size())
            return (null); // Wrong argument number error
        /*
        for (int i = 0; i < parameters.size(); i++)
            if (stack.get(foo.getSym.get(i)) != null)
                return (null); // Redefinition error
        */
        local = new HashMap<String, Multi>();
        for (int i = 0; i < parameters.size(); i++) // Build variable scope
        {
            //System.out.println("local.put(" + foo.getSym().get(i) + ", " + new Multi(parameters.get(i).getType(), parameters.get(i).getData()) + ")"); // Debug
            local.put(foo.getSym().get(i), new Multi(parameters.get(i).getType(), parameters.get(i).getData()));
        }
        iter = stack.keySet().iterator();
        while (iter.hasNext()) // Add fonction definition
        {
            key = (String)iter.next();
            if (Parser.isEmpty(key))
                continue;
            def = stack.get(key);
            if (def == null)
                continue;
            if (def.getType() == 'Z')
                local.put(key, def);
        }
        //def = foo.script.run(stack); // Run function
        def = foo.getScript().result(local); // Run function
        if (def == null || def.getType() == 'K')
            return (null); // Reserved
        /*
        for (int i = 0; i < parameters.size(); i++) // Restore variable scope
            stack.remove(foo.getSym.get(i));
        */
        return (def); // Return value
    }

    public String toString()
    {
        String ret;

        ret = call + "(";
        for (int i = 0; i < parameters.size() - 1; i++)
            if (parameters.get(i) != null) // TODO Test
                ret += parameters.get(i).toString() + ", ";
        if (parameters.size() > 1)
            if (parameters.get(parameters.size() - 1) != null) // TODO Test
                ret += parameters.get(parameters.size() - 1).toString();
        return (ret + ")");
    }
}
