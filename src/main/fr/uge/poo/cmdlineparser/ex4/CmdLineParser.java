package fr.uge.poo.cmdlineparser.ex4;

import java.util.*;
import java.util.function.Consumer;

public class CmdLineParser {

    private final HashSet<String> requiredOpt = new HashSet<>();
    private final HashSet<String> seenOptions = new HashSet<>();
    private final HashMap<String, Option> options = new HashMap<>();

    private static boolean isOption(String arg) {
        return arg.startsWith("-");
    }

    private static List<String> getParameters(Iterator<String> it, int nbParameters) {
        var paramList = new ArrayList<String>();
        for (int i = 0; i < nbParameters; i++) {
            if (it.hasNext())
                paramList.add(it.next());
            else
                throw new IllegalStateException("reach the end of iterator");
        }
        return paramList;
    }

    public void registerOption(String name, Option opt) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(opt);
        if (options.containsKey(name))
            throw new IllegalStateException();
        options.put(name, opt);
        if (opt.required)
            requiredOpt.add(opt.name);
    }

    public List<String> process(String[] arguments) {
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
        var missingRequiredOptions = new HashSet<>(requiredOpt);
        missingRequiredOptions.removeAll(seenOptions);
        if (missingRequiredOptions.size() != 0)
            throw new IllegalArgumentException("Options are missing");
    }

    public void registerWithoutParameter(String name, Runnable action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        registerOption(name, new Option(name, 0, arg -> action.run()));
    }

    public void registerWithParameters(String option, int nbParameters, Consumer<List<String>> operation) {
        Objects.requireNonNull(option);
        Objects.requireNonNull(operation);
        if (options.containsKey(option))
            throw new IllegalStateException();
        registerOption(option, new Option(option, nbParameters, params -> {
            if (params.size() != nbParameters)
                throw new IllegalArgumentException("Option one argument");
            operation.accept(params);
        }));
    }

    void processTokenOpt(String arg, Iterator<String> it) {
        var opt = options.get(arg);
        if (opt == null)
            throw new IllegalArgumentException(arg + "is not an option");
        seenOptions.add(arg);
        var params = getParameters(it, opt.nbParameters);
        try {
            opt.action.accept(params);
        } catch (Exception e) {
            throw new IllegalStateException("Error while applying option on parameters");
        }
    }

    static public class Option {

        private final String name;
        private final int nbParameters;
        private final Consumer<List<String>> action;
        private final boolean required;

        private Option(OptionBuilder b) {
            this.name = b.name;
            this.nbParameters = b.nbParameters;
            this.action = b.action;
            this.required = b.required;
        }

        private Option(String name, int nbParameters, Consumer<List<String>> action) {
            this.name = name;
            this.nbParameters = nbParameters;
            this.action = action;
            this.required = false;
        }

        static public class OptionBuilder {
            private final Consumer<List<String>> action;
            private final String name;
            private final int nbParameters;
            private boolean required = false;

            public OptionBuilder(String name, int nbParameters, Consumer<List<String>> action) {
                Objects.requireNonNull(name);
                Objects.requireNonNull(action);
                if (nbParameters < 0)
                    throw new IllegalStateException("nbParameters must be higher than 0");
                this.name = name;
                this.nbParameters = nbParameters;
                this.action = action;
            }

            public OptionBuilder optional() {
                this.required = false;
                return this;
            }

            public OptionBuilder required() {
                this.required = true;
                return this;
            }
        }
    }
}