package fr.uge.poo.cmdlineparser.ex6;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

public class Application {

    public static CmdLineParser createCmdLineParser(PaintSettings.PaintSettingsBuilder optionsBuilder) {
        var cmdLineParser = new CmdLineParser();
        cmdLineParser.addOption(
                new CmdLineParser.Option.OptionsBuilder("-legacy", 0, __ -> {
                    optionsBuilder.setLegacy(true);
                }).addAliases("-l", "-lg").build()
        );
        cmdLineParser.addOption(
                new CmdLineParser.Option.OptionsBuilder("-no-borders", 0, __ -> {
                    optionsBuilder.setBordered(true);
                }).doc("Set border to the drawing window").build()
        );
        cmdLineParser.addOption(
                new CmdLineParser.Option.OptionsBuilder("-border-width", 1, (argList) -> {
                    optionsBuilder.setBorderWidth(Integer.parseInt(argList.get(0)));
                }).build()
        );
        cmdLineParser.addOption(
                new CmdLineParser.Option.OptionsBuilder("-window-name", 1, (argList) -> {
                    optionsBuilder.setWindowName(argList.get(0));
                }).isRequired().doc("Set the name of the graphic window").build()
        );
        cmdLineParser.addOption(
                new CmdLineParser.Option.OptionsBuilder("-min-size", 2, (argList) -> {
                    optionsBuilder.setWindowWidth(Integer.parseInt(argList.get(0)));
                    optionsBuilder.setWindowHeight(Integer.parseInt(argList.get(1)));
                }).isRequired().build()
        );
        cmdLineParser.addOption(
                new CmdLineParser.Option.OptionsBuilder("-remote-server", 2, (argList) -> {
                    var hostname = argList.get(0);
                    var port = Integer.parseInt(argList.get(1));
                    optionsBuilder.setServ(new InetSocketAddress(hostname, port));
                }).build()
        );
        return cmdLineParser;
    }

    public static void main(String[] args) {
        var optionsBuilder = new PaintSettings.PaintSettingsBuilder();
        String[] arguments = {"-l", "-no-borders", "-window-name", "filename1", "-border-width", "4", "-min-size", "600", "600", "-remote-server", "Chatavion", "8080"};

        var cmdLineParser = createCmdLineParser(optionsBuilder);

        List<String> result = cmdLineParser.process(arguments);
        var opt = optionsBuilder.build();
        List<Path> files = result.stream().map(Path::of).toList();
        files.forEach(System.out::println);
        System.out.println(opt);
    }
}
