package fr.uge.poo.cmdlineparser.ex6;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class CmdLineParser {
    private final OptionsManager optionsManager = new OptionsManager();
    private final LoggerObserver loggerObserver = new LoggerObserver();
    private final DocOptionsManagerObserver docOptionsManagerObserver = new DocOptionsManagerObserver();
    private final ConflictOptionsManagerObserver conflictOptionsManagerObserver = new ConflictOptionsManagerObserver();
    private final RequiredOptionsManagerObserver requiredOptionsManagerObserver = new RequiredOptionsManagerObserver();

    private static boolean isOption(String arg) {
        return arg.startsWith("-");
    }
    public CmdLineParser() {
        optionsManager.addObserver(loggerObserver);
        optionsManager.addObserver(docOptionsManagerObserver);
        optionsManager.addObserver(conflictOptionsManagerObserver);
        optionsManager.addObserver(requiredOptionsManagerObserver);
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
        optionsManager.finishProcess();
        return files;
    }

    private void processTokenOpt(String name, Iterator<String> it) {
        var opt = optionsManager.processOption(name).orElseThrow(() -> new IllegalArgumentException("'" + name + "' is not an option"));
        var params = getParameters(it, opt.nbParameters);
        try {
            opt.action.accept(params);
        } catch (Exception e) {
            throw new IllegalStateException("Error while applying option on parameters");
        }
    }

    public void addOption(Option opt) {
        Objects.requireNonNull(opt);
        if (optionsManager.processOption(opt.name).isPresent())
            throw new IllegalStateException("The map already contains this option");
        optionsManager.register(opt);
    }

    public void addFlag(String name, Runnable action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        optionsManager.register(new Option(name, 0, arg -> action.run()));
    }

    public void addOptionWithOneParameter(String name, Consumer<List<String>> action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        optionsManager.register(new Option(name, 0, action));
    }


    public void registerWithParameters(String name, int nbParameters, Consumer<List<String>> action) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(action);
        if (nbParameters < 0)
            throw new IllegalArgumentException("The number of parameters should be greater than or equals to 0");
        optionsManager.register(new Option(name, nbParameters, action::accept));
    }

    public void usage() {
        if (optionsManager.byName.isEmpty()) {
            System.out.println("No options have been registered yet");
            return;
        }
        var listName = new ArrayList<>(optionsManager.byName.values().stream().map(opt -> opt.name).distinct().toList());
        Collections.sort(listName);
        System.out.println("List of the options registered :");
        for (var name : listName) {
            var opt = optionsManager.processOption(name).orElseThrow(IllegalArgumentException::new);
            System.out.print(" " + name);
            if (opt.doc != null)
                System.out.print(", \"" + opt.doc + "\"");
            System.out.print("\n");
        }
    }

    interface OptionsManagerObserver {

        void onRegisteredOption(OptionsManager optionsManager, Option option);

        void onProcessedOption(OptionsManager optionsManager, Option option);

        void onFinishedProcess(OptionsManager optionsManager);
    }

    private static class OptionsManager {

        private final HashMap<String, Option> byName = new HashMap<>();
        private final Set<OptionsManagerObserver> observers = new HashSet<>();

        /**
         * Register the option with all its possible names
         *
         * @param option
         */
        void register(Option option) {
            register(option.name, option);
            for (var alias : option.aliases) {
                register(alias, option);
            }
        }


        private void register(String name, Option option) {
            if (byName.containsKey(name))
                throw new IllegalStateException("Option " + name + " is already registered.");
            observers.forEach(o -> o.onRegisteredOption(this, option));
            byName.put(name, option);
        }

        /**
         * This method is called to signal that an option is encountered during
         * a command line process
         *
         * @param optionName
         * @return the corresponding object option if it exists
         */
        Optional<Option> processOption(String optionName) {
            Objects.requireNonNull(optionName);
            var option = byName.get(optionName);
            if (option != null)
                observers.forEach(o -> o.onProcessedOption(this, option));
            return Optional.ofNullable(option);
        }

        /**
         * This method is called to signal the method process of the CmdLineParser is finished
         */
        void finishProcess() {
            observers.forEach(o -> o.onFinishedProcess(this));
        }

        void addObserver(OptionsManagerObserver observer) {
            Objects.requireNonNull(observer);
            observers.add(observer);
        }
    }

    static public class Option {

        private final String name;
        private final int nbParameters;
        private final Consumer<List<String>> action;
        public Set<String> conflicts = new HashSet<>();
        private boolean isRequired;
        private String doc;
        private Set<String> aliases = new HashSet<>();

        private Option(OptionsBuilder optionsBuilder) {
            this.name = optionsBuilder.name;
            this.nbParameters = optionsBuilder.nbParameters;
            this.action = optionsBuilder.action;
            this.isRequired = optionsBuilder.isRequired;
            this.doc = optionsBuilder.doc;
            this.aliases = optionsBuilder.aliases;
            this.conflicts = optionsBuilder.conflicts;
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

        @Override
        public String toString() {
            return "\"" + name + "\"";
        }

        public static class OptionsBuilder {
            private final HashSet<String> aliases = new HashSet<>();
            public Set<String> conflicts = new HashSet<>();
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

            public OptionsBuilder(String name, int nbParameters) {
                Objects.requireNonNull(name);
                if (nbParameters < 0)
                    throw new IllegalStateException("nbParameters must be higher than 0");
                this.name = name;
                this.nbParameters = nbParameters;
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

            public OptionsBuilder setConsumer(Consumer<List<String>> action) {
                Objects.requireNonNull(action);
                this.action = action;
                return this;
            }

            public OptionsBuilder setIntConsumer(IntConsumer action) {
                Objects.requireNonNull(action);
                this.action = acc -> action.accept(Integer.parseInt(acc.get(0)));
                return this;
            }

            public OptionsBuilder setBiConsumerIntegerToInteger(BiConsumer<Integer, Integer> action) {
                Objects.requireNonNull(action);
                this.action = acc -> action.accept(Integer.parseInt(acc.get(0)), Integer.parseInt(acc.get(1)));
                return this;
            }

            public OptionsBuilder setBiConsumerInetSocketAddress(BiConsumer<String, Integer> action) {
                Objects.requireNonNull(action);
                this.action = acc -> action.accept(acc.get(0), Integer.parseInt(acc.get(1)));
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

            public OptionsBuilder conflictWith(String opt) {
                Objects.requireNonNull(opt);
                if(isOption(opt))
                    conflicts.add(opt);
                return this;
            }
        }
    }

    class RequiredOptionsManagerObserver implements OptionsManagerObserver {
        private final Set<String> missingRequiredOptions = new HashSet<>();

        @Override
        public void onRegisteredOption(OptionsManager optionsManager, Option option) {
            Objects.requireNonNull(optionsManager);
            Objects.requireNonNull(option);
            if(option.isRequired) {
                missingRequiredOptions.add(option.name);
                missingRequiredOptions.addAll(option.aliases);
            }
        }

        @Override
        public void onProcessedOption(OptionsManager optionsManager, Option option) {
            Objects.requireNonNull(optionsManager);
            Objects.requireNonNull(option);
            if(option.isRequired) {
                missingRequiredOptions.remove(option.name);
                missingRequiredOptions.removeAll(option.aliases);
            }
        }

        @Override
        public void onFinishedProcess(OptionsManager optionsManager) {
            Objects.requireNonNull(optionsManager);
            if(missingRequiredOptions.size() != 0)
                throw new IllegalStateException("A required option has not been used");
        }
    }

    class DocOptionsManagerObserver implements OptionsManagerObserver {
        private final Map<String, String> options = new HashMap<>();

        @Override
        public void onRegisteredOption(OptionsManager optionsManager, Option option) {
            Objects.requireNonNull(option);
            Objects.requireNonNull(optionsManager);
            options.put(option.name, option.doc);
        }

        @Override
        public void onProcessedOption(OptionsManager optionsManager, Option option) {
            // Nothing
        }

        @Override
        public void onFinishedProcess(OptionsManager optionsManager) {
            // Nothing
        }

        void usage() {
            options.keySet().stream().sorted().forEach(option -> {
                var doc = options.get(option);
                if(doc != null)
                    System.out.println(option + " " + doc);
                else
                    System.out.println(option);
            });
        }
    }

    class ConflictOptionsManagerObserver implements OptionsManagerObserver {
        private final Map<String, List<String>> conflicts = new HashMap<>();
        private final Set<String> seenOptions = new HashSet<>();

        @Override
        public void onRegisteredOption(OptionsManager optionsManager, Option option) {
            Objects.requireNonNull(optionsManager);
            Objects.requireNonNull(option);
            var blackList = new ArrayList<>(option.conflicts);
            conflicts.put(option.name, blackList);
            option.aliases.forEach(a -> conflicts.put(a, blackList));
        }

        @Override
        public void onProcessedOption(OptionsManager optionsManager, Option option) {
            Objects.requireNonNull(optionsManager);
            Objects.requireNonNull(option);
            var conflictWith = conflicts.get(option.name);
            conflictWith.stream()
                    .filter(seenOptions::contains)
                    .findAny()
                    .ifPresent(opt -> {
                        throw new IllegalStateException("Option " + opt + " is in conflict with previously seen options");
                    });
            seenOptions.add(option.name);
            seenOptions.addAll(option.aliases);
        }

        @Override
        public void onFinishedProcess(OptionsManager optionsManager) {
            Objects.requireNonNull(optionsManager);
            for(var seen:seenOptions) {
                var inConflictWith = conflicts.get(seen);
                for(var name : inConflictWith) {
                    if(seenOptions.contains(name))
                        throw new IllegalStateException(seen+" is processed with "+name);
                }
            }
        }
    }

    class LoggerObserver implements OptionsManagerObserver {

        @Override
        public void onRegisteredOption(OptionsManager optionsManager, Option option) {
            System.out.println("Option " + option + " is registered");
        }

        @Override
        public void onProcessedOption(OptionsManager optionsManager, Option option) {
            System.out.println("Option " + option + " is processed");
        }

        @Override
        public void onFinishedProcess(OptionsManager optionsManager) {
            System.out.println("Process method is finished");
        }
    }
}