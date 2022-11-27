package fr.uge.poo.cmdlineparser.ex1;

import java.util.*;
import java.util.function.Consumer;

public class CmdLineParser {

    private final HashMap<String,Runnable> registeredOptions = new HashMap<>();

    public void registerOption(String option, Runnable process) {
        Objects.requireNonNull(option);
        Objects.requireNonNull(process);
        if(registeredOptions.containsKey(option))
        	throw new IllegalStateException();
        registeredOptions.put(option,process); 
    }


    public List<String> process(String[] arguments) {
        ArrayList<String> files = new ArrayList<>();
        for (String argument : arguments) {
        	var process = registeredOptions.get(argument);
            if (process!=null)
                process.run();
            else
                files.add(argument);
        }
        return files;
    }
}