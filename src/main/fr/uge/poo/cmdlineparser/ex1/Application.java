package fr.uge.poo.cmdlineparser.ex1;

import java.nio.file.Path;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        var options = new PaintSettings();
        String[] arguments = {"-legacy", "-no-borders", "file1", "file2"};
        var cmdLineParser = new CmdLineParser();

        cmdLineParser.addFlag("-legacy", () -> options.setLegacy(true));
        cmdLineParser.addFlag("-no-borders", () -> options.setBordered(true));

        List<String> result = cmdLineParser.process(arguments);
        List<Path> files = result.stream().map(Path::of).toList();
        files.forEach(System.out::println);
        System.out.println(options);
    }

    static public class PaintSettings {
        private boolean legacy = false;
        private boolean bordered = true;

        public boolean isLegacy() {
            return legacy;
        }

        public void setLegacy(boolean legacy) {
            this.legacy = legacy;
        }

        public boolean isBordered() {
            return bordered;
        }

        public void setBordered(boolean bordered) {
            this.bordered = bordered;
        }

        @Override
        public String toString() {
            return "PaintSettings[bordered = " + bordered + ", legacy = " + legacy + "]";
        }
    }
}
