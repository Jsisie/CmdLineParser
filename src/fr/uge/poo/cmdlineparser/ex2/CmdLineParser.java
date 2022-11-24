package fr.uge.poo.cmdlineparser.ex2;

import java.util.*;
import java.util.function.Consumer;

public class CmdLineParser {

	private final HashMap<String,Consumer<Iterator<String>>> registeredParameters = new HashMap<>();

	public void registerOption(String option, Consumer<Iterator<String>> process) {
		Objects.requireNonNull(option);
		Objects.requireNonNull(process);
		if(registeredParameters.containsKey(option)) {
			throw new IllegalStateException();
		}
		registeredParameters.put(option,process); 
	}


	public List<String> process(String[] arguments) {//pour le process sur le registeredparameters while(it.hasNext){arg = it.next;registeredparameters.get();if(consumer!=null}
		ArrayList<String> files = new ArrayList<>();
		var it = List.of(arguments).iterator();
		while(it.hasNext()) {
			var option = it.next();
			var consumer = registeredParameters.get(option);
			if(consumer!=null) {
				consumer.accept(it);
			}
			else {
				files.add(option);
			}
		}
		return files;
	}
	public void registerWithParameter(String option, Consumer<Iterator<String>> operation) {
		Objects.requireNonNull(option);
		Objects.requireNonNull(operation);
		if(registeredParameters.containsKey(option)) {
			throw new IllegalStateException();
		}
		registeredParameters.put(option, operation);
	}
}