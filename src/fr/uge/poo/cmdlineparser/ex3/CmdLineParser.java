package fr.uge.poo.cmdlineparser.ex3;

import java.util.*;
import java.util.function.Consumer;

public class CmdLineParser {

    private final HashMap<String, Consumer<Iterator<String>>> registeredParameters = new HashMap<>();

    public void registerOption(String option, Consumer<Iterator<String>> process) {
        Objects.requireNonNull(option);
        Objects.requireNonNull(process);
        if (registeredParameters.containsKey(option))
            throw new IllegalStateException();
        registeredParameters.put(option, process);
    }

    public List<String> process(String[] arguments) {
        var files = new ArrayList<String>();
        var it = List.of(arguments).iterator();
        while (it.hasNext()) {
            var option = it.next();
            var consumer = registeredParameters.get(option);
            if (consumer != null)
                consumer.accept(it);
            else
                files.add(option);
        }
        return files;
    }

    public void registerWithParameter(String option, Consumer<Iterator<String>> operation) {
        Objects.requireNonNull(option);
        Objects.requireNonNull(operation);
        if (registeredParameters.containsKey(option))
            throw new IllegalStateException();
        registeredParameters.put(option, operation);
    }
}