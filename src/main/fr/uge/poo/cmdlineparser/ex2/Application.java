package fr.uge.poo.cmdlineparser.ex2;

import java.nio.file.Path;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        var options = new PaintSettings();
        String[] arguments = {"-legacy", "-no-borders", "-window-name", "window", "-border-width", "6"};

        var cmdParser = new CmdLineParser();

        cmdParser.addOptionWithOneParameter("-legacy", __ -> options.setLegacy(true));
        cmdParser.addOptionWithOneParameter("-no-borders", __ -> options.setBordered(true));
        cmdParser.addOptionWithOneParameter("-border-width", (it) -> {
            if (!it.hasNext())
                throw new IllegalStateException("you gave no arguments");
            options.setBorderWidth(Integer.parseInt(it.next()));
        });
        cmdParser.addOptionWithOneParameter("-window-name", (it) -> {
            if (!it.hasNext())
                throw new IllegalStateException("you gave no arguments");
            options.setWindowName(it.next());
        });

        List<String> result = cmdParser.process(arguments);
        List<Path> files = result.stream().map(Path::of).toList();
        files.forEach(System.out::println);
        System.out.println(options);

    }

    static public class PaintSettings {
        private boolean legacy = false;
        private boolean bordered = true;
        private int borderWidth = 0;
        private String windowName;

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

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }

        public int getBorderWidth() {
            return borderWidth;
        }

        public String getWindowName() {
            return windowName;
        }

        public void setWindowName(String borderName) {
            this.windowName = borderName;
        }

        @Override
        public String toString() {
            return "PaintSettings[bordered = " + bordered + ", legacy = " + legacy + " " + borderWidth + " " + windowName + "]";
        }
    }
}
