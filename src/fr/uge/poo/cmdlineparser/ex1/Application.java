package fr.uge.poo.cmdlineparser.ex1;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    static private class PaintSettings {
        private boolean legacy=false;
        private boolean bordered=true;

        public void setLegacy(boolean legacy) {
            this.legacy = legacy;
        }

        public boolean isLegacy(){
            return legacy;
        }

        public void setBordered(boolean bordered){
            this.bordered=bordered;
        }

        public boolean isBordered(){
            return bordered;
        }

        @Override
        public String toString(){
            return "PaintSettings [ bordered = "+bordered+", legacy = "+ legacy +" ]";
        }
    }

    public static void main(String[] args) {
        var options = new PaintSettings();
        String[] arguments={"-legacy","-no-borders","filename1","filename2"};
        var cmdParser = new CmdLineParser();
        cmdParser.registerOption("-legacy",()->{options.setLegacy(true);});
        cmdParser.registerOption("-no-borders",()->{options.setBordered(true);});
        List<String> result = cmdParser.process(arguments);
        List<Path> files = result.stream().map(Path::of).toList();
        // this code replaces the rest of the application
        files.forEach(System.out::println);
        System.out.println(options.toString());

    }
}
