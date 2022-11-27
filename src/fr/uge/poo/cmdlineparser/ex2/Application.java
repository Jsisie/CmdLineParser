package fr.uge.poo.cmdlineparser.ex2;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    static private class PaintSettings {
        private boolean legacy=false;
        private boolean bordered=true;
        private int borderWidth = 0;
        private String windowName;

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

        public void setBorderWidth(int borderWidth) {
			this.borderWidth = borderWidth;
		}

		@Override
        public String toString(){
            return "PaintSettings [ bordered = "+bordered+", legacy = "+ legacy+" "+borderWidth+" "+windowName+" ]";
        }

		public void setBorderName(String borderName) {
			this.windowName = borderName;
		}
    }

    public static void main(String[] args) {
        var options = new PaintSettings();
        String[] arguments={"-legacy","-no-borders","-window-name","filename1","-border-width","4"};

        var cmdParser = new CmdLineParser();

        cmdParser.registerOption("-legacy", __ -> options.setLegacy(true));
        cmdParser.registerOption("-no-borders", __ -> options.setBordered(true));
        cmdParser.registerOption("-border-width", (it) -> {
        	if(!it.hasNext())
                throw new IllegalStateException("you gave no arguments");
        	options.setBorderWidth(Integer.parseInt(it.next()));
        	});
        cmdParser.registerOption("-window-name", (it) -> {
        	if(!it.hasNext())
                throw new IllegalStateException("you gave no arguments");
        	options.setBorderName(it.next());
        	});

        List<String> result = cmdParser.process(arguments);
        List<Path> files = result.stream().map(Path::of).toList();
        files.forEach(System.out::println);
        System.out.println(options);

    }
}
