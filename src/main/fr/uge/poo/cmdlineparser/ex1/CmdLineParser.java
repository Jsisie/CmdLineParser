package fr.uge.poo.cmdlineparser.ex1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CmdLineParser {

    private final HashMap<String, Runnable> registeredOptions = new HashMap<>();

    public void addFlag(String name, Runnable action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        if (registeredOptions.containsKey(name))
            throw new IllegalStateException("The option has already been registered");
        registeredOptions.put(name, action);
    }

    public List<String> process(String[] arguments) {
        Objects.requireNonNull(arguments);
        ArrayList<String> files = new ArrayList<>();
        for (String argument : arguments) {
            var process = registeredOptions.get(argument);
            if (process != null)
                process.run();
            else
                files.add(argument);
        }
        return files;
    }
}