package fr.uge.poo.cmdlineparser.ex3;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

public class Application {


    public static void main(String[] args) {
        var options = new PaintSettings.PaintOptionsBuilder();
        String[] arguments = {"-legacy", "-no-borders", "-window-name", "filename1", "-border-width", "4", "-min-size", "600", "600", "-remote-server", "Chatavion", "8080"};
        var cmdParser = new CmdLineParser();

        cmdParser.addFlag("-legacy", () -> options.setLegacy(true));
        cmdParser.addFlag("-no-borders", () -> options.setBordered(true));
        cmdParser.addOptionWithOneParameter("-border-width", (it) -> {
            if (!it.hasNext())
                throw new IllegalStateException("no arguments passed");
            options.setBorderWidth(Integer.parseInt(it.next()));
        });
        cmdParser.addOptionWithOneParameter("-window-name", (it) -> {
            if (!it.hasNext())
                throw new IllegalStateException("no arguments passed");
            options.setWindowName(it.next());
        });
        cmdParser.addOptionWithOneParameter("-min-size", (it) -> {
            if (!it.hasNext())
                throw new IllegalStateException("no arguments passed");
            options.setWindowWidth(Integer.parseInt(it.next()));
            if (!it.hasNext())
                throw new IllegalStateException("no arguments passed");
            options.setWindowHeight(Integer.parseInt(it.next()));
        });
        cmdParser.addOptionWithOneParameter("-remote-server", (it) -> {
            if (!it.hasNext())
                throw new IllegalStateException("no arguments passed");
            var hostname = it.next();
            if (!it.hasNext())
                throw new IllegalStateException("no arguments passed");
            var port = Integer.parseInt(it.next());
            options.setSocketAddress(new InetSocketAddress(hostname, port));
        });

        List<String> result = cmdParser.process(arguments);
        var opt = options.build();
        List<Path> files = result.stream().map(Path::of).toList();
        files.forEach(System.out::println);
        System.out.println(opt);
    }
}