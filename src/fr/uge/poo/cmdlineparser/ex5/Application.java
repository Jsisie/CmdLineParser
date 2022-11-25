package fr.uge.poo.cmdlineparser.ex5;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        var options = new PaintOptions.PaintOptionsBuilder();
        String[] arguments = {"-legacy", "-no-borders", "-window-name", "filename1", "-border-width", "4", "-min-size", "600", "600", "-remote-server", "Chatavion", "8080"};
        var cmdParser = new CmdLineParser();
        cmdParser.registerWithoutParameter("-legacy", () -> {
            options.setLegacy(true);
        });
        cmdParser.registerWithoutParameter("-no-borders", () -> {
            options.setBordered(true);
        });
        cmdParser.registerWithParameters("-border-width", 1, (argList) -> {
            options.setBorderWidth(Integer.parseInt(argList.get(0)));
        });
        cmdParser.registerWithParameters("-window-name", 1, (argList) -> {
            options.setWindowName(argList.get(0));
        });
        cmdParser.registerWithParameters("-min-size", 2, (argList) -> {
            options.setWindowWidth(Integer.parseInt(argList.get(0)));
            options.setWindowHeight(Integer.parseInt(argList.get(1)));
        });
        cmdParser.registerWithParameters("-remote-server", 2, (argList) -> {
            var hostname = argList.get(0);
            var port = Integer.parseInt(argList.get(1));
            options.setServ(new InetSocketAddress(hostname, port));
        });

        List<String> result = cmdParser.process(arguments);
        var opt = options.build();
        List<Path> files = result.stream().map(Path::of).toList();
        files.forEach(System.out::println);
        System.out.println(opt);
    }
}
