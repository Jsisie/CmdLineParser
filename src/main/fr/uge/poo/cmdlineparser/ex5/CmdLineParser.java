package fr.uge.poo.cmdlineparser.ex5;

import java.util.*;
import java.util.function.Consumer;

public class CmdLineParser {

    private final HashSet<String> requiredOptionsSet = new HashSet<>();
    private final HashSet<String> seenOptionsSet = new HashSet<>();
    private final HashMap<String, Option> optionsMap = new HashMap<>();

    private static boolean isOption(String arg) {
        return arg.startsWith("-");
    }

    private static List<String> getParameters(Iterator<String> it, int nbParameters) {
        var paramList = new ArrayList<String>();
        for (int i = 0; i < nbParameters; i++) {
            if (it.hasNext())
                paramList.add(it.next());
            else
                throw new IllegalArgumentException("The number of parameters and number of parameters given should be equals");
        }
        return paramList;
    }

    public List<String> process(String[] arguments) {
        Objects.requireNonNull(arguments);
        ArrayList<String> files = new ArrayList<>();
        var it = List.of(arguments).iterator();
        while (it.hasNext()) {
            var arg = it.next();
            if (!isOption(arg)) {
                files.add(arg);
                continue;
            }
            processTokenOpt(arg, it);
        }
        requiredOptions();
        return files;
    }

    private void requiredOptions() {
        var missingRequiredOptions = new HashSet<>(requiredOptionsSet);
        missingRequiredOptions.removeAll(seenOptionsSet);
        if (missingRequiredOptions.size() != 0)
            throw new IllegalArgumentException("Options are missing");
    }

    private void processTokenOpt(String arg, Iterator<String> it) {
        var opt = optionsMap.get(arg);
        if (opt == null)
            throw new IllegalArgumentException(arg + " is not an option");
        seenOptionsSet.add(arg);
        var params = getParameters(it, opt.nbParameters);
        try {
            opt.action.accept(params);
        } catch (Exception e) {
            throw new IllegalStateException("Error while applying option on parameters");
        }
    }

    public void addOption(Option opt) {
        Objects.requireNonNull(opt);
        if (optionsMap.containsKey(opt.name))
            throw new IllegalStateException("The map already contains this option");
        optionsMap.put(opt.name, opt);
        registerAlias(opt);
        if (opt.isRequired) {
            requiredOptionsSet.add(opt.name);
            requiredOptionsSet.addAll(opt.aliases);
        }
    }

    private void registerAlias(Option opt) {
        for (var alias : opt.aliases)
            optionsMap.put(alias, opt);
    }

    private void registerOption(String name, Option opt) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(opt);
        if (optionsMap.containsKey(name))
            throw new IllegalStateException("The map already contains this option");
        optionsMap.put(name, opt);
        if (opt.isRequired)
            requiredOptionsSet.add(opt.name);
    }

    public void registerWithoutParameter(String name, Runnable action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        registerOption(name, new Option(name, 0, arg -> action.run()));
    }

    public void registerWithParameters(String name, int nbParameters, Consumer<List<String>> action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        if (nbParameters < 0)
            throw new IllegalArgumentException("The number of parameters should be greater than or equals to 0");
        registerOption(name, new Option(name, nbParameters, action::accept));
    }

    public void usage() {
        if(optionsMap.isEmpty()) {
            System.out.println("No options have been registered yet");
            return;
        }
        var listName = new ArrayList<>(optionsMap.values().stream().map(opt -> opt.name).distinct().toList());
        Collections.sort(listName);
        System.out.println("List of the options registered :");
        for (var name : listName) {
            var opt = optionsMap.get(name);
            System.out.print(" " + name);
            if (opt.doc != null)
                System.out.print(", \"" + optionsMap.get(name).doc + "\"");
            System.out.print("\n");
        }
    }

    static public class Option {

        private final String name;
        private final int nbParameters;
        private final Consumer<List<String>> action;
        private boolean isRequired;
        private String doc;
        private HashSet<String> aliases;

        private Option(OptionsBuilder optionsBuilder) {
            this.name = optionsBuilder.name;
            this.nbParameters = optionsBuilder.nbParameters;
            this.action = optionsBuilder.action;
            this.isRequired = optionsBuilder.isRequired;
            this.doc = optionsBuilder.doc;
            this.aliases = optionsBuilder.aliases;
        }

        public Option(String name, int nbParameters, Consumer<List<String>> action) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(action);
            if (nbParameters < 0)
                throw new IllegalStateException("nbParameters must be higher than 0");
            this.name = name;
            this.nbParameters = nbParameters;
            this.action = action;
        }

        public static class OptionsBuilder {
            private final HashSet<String> aliases = new HashSet<>();
            private Consumer<List<String>> action;
            private String name;
            private int nbParameters;
            private boolean isRequired;
            private String doc;

            public OptionsBuilder(String name, int nbParameters, Consumer<List<String>> action) {
                Objects.requireNonNull(name);
                Objects.requireNonNull(action);
                if (nbParameters < 0)
                    throw new IllegalStateException("nbParameters must be higher than 0");
                this.name = name;
                this.nbParameters = nbParameters;
                this.action = action;
            }

            public OptionsBuilder setName(String name) {
                Objects.requireNonNull(name);
                this.name = name;
                return this;
            }

            public OptionsBuilder setNbParameters(int nbParameters) {
                if (nbParameters < 0)
                    throw new IllegalStateException("nbParameters must be higher than 0");
                this.nbParameters = nbParameters;
                return this;
            }

            public OptionsBuilder setAction(Consumer<List<String>> action) {
                Objects.requireNonNull(action);
                this.action = action;
                return this;
            }

            public OptionsBuilder isRequired() {
                isRequired = true;
                return this;
            }

            public OptionsBuilder doc(String doc) {
                Objects.requireNonNull(doc);
                this.doc = doc;
                return this;
            }

            public OptionsBuilder addAliases(String... names) {
                Objects.requireNonNull(names);
                aliases.addAll(Arrays.asList(names));
                return this;
            }

            public Option build() {
                return new Option(this);
            }
        }
    }
}