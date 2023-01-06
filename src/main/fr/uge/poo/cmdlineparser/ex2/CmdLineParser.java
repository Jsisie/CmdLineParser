package fr.uge.poo.cmdlineparser.ex2;

import java.util.*;
import java.util.function.Consumer;

public class CmdLineParser {

	private final HashMap<String,Consumer<Iterator<String>>> registeredOptions = new HashMap<>();

    public void addFlag(String name, Runnable action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        if (registeredOptions.containsKey(name))
            throw new IllegalStateException("The option has already been registered");
        registeredOptions.put(name, __ -> action.run());
    }

	public void addOptionWithOneParameter(String name, Consumer<Iterator<String>> action) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(action);
		if(registeredOptions.containsKey(name))
			throw new IllegalStateException("The option has already been registered");
		registeredOptions.put(name,action);
	}

	public List<String> process(String[] arguments) {
		ArrayList<String> files = new ArrayList<>();
		var it = List.of(arguments).iterator();
		while(it.hasNext()) {
			var option = it.next();
			var consumer = registeredOptions.get(option);
			if(consumer != null)
				consumer.accept(it);
			else
				files.add(option);
		}
		return files;
	}
}