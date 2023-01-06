package fr.uge.poo.cmdlineparser.ex6;

import fr.uge.poo.cmdlineparser.ex6.CmdLineParser.Option.OptionsBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class CmdLineParserTest {
    private final CmdLineParser cmdLineParser = new CmdLineParser();
    private final PaintSettings.PaintSettingsBuilder optionsBuilder = new PaintSettings.PaintSettingsBuilder();

    @Nested
    public class registerTest {
        @Test
        public void testAddOptionNull() {
            assertThrows(NullPointerException.class, () -> cmdLineParser.addOption(null));
        }

        @Test
        public void testAddFlagShouldThrowExceptionIfArgumentNull() {
            assertAll(
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addFlag("test", null)),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addFlag(null, () -> System.out.println("test"))),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addFlag(null, null))
            );
        }

        @Test
        public void testRegisterWithOneParameterNull() {
            assertAll(
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addOptionWithOneParameter("test", null)),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addOptionWithOneParameter(null, __ -> System.out.println("test"))),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addOptionWithOneParameter(null, null))
            );
        }

        @Test
        public void testRegisterWithParameterNull() {
            assertAll(
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.registerWithParameters("test", 0, null)),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.registerWithParameters(null, 0, __ -> System.out.println("test"))),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.registerWithParameters(null, 0, null))
            );
        }

        @Test
        public void testRegisterWithParameterNbParametersIsNegative() {
            assertThrows(IllegalArgumentException.class, () -> cmdLineParser.registerWithParameters("test", -1, __ -> System.out.println("test")));
        }

        @Test
        public void testRegisterWithParameterNbParametersInArgumentShouldBeEqualsToNbParametersGiven() {
            String[] arguments = {"-window-name"};
            cmdLineParser.registerWithParameters("-window-name", 1, (argList) -> optionsBuilder.setWindowName(argList.get(0)));
            assertThrows(IllegalArgumentException.class, () -> cmdLineParser.process(arguments));
        }

        @Test
        public void testRegisterWithParameterNbParametersInArgumentShouldBeEqualsToNbParametersGiven2() {
            String[] arguments = {"-remote-server", "Chatavion", "8080"};
            cmdLineParser.registerWithParameters("-remote-server", 3, (argList) -> optionsBuilder.setServ(new InetSocketAddress(
                    argList.get(0),
                    Integer.parseInt(argList.get(1))
            )));
            assertThrows(IllegalArgumentException.class, () -> cmdLineParser.process(arguments));
        }
    }

    @Nested
    public class processTest {
        @Test
        public void testProcessNull() {
            assertThrows(NullPointerException.class, () -> cmdLineParser.process(null));
        }

        @Test
        public void testProcessShouldWorkEasy() {
            String[] arguments = {"-min-size", "555", "555"};
            cmdLineParser.registerWithParameters("-min-size", 2, (argList) -> {
                optionsBuilder.setWindowWidth(Integer.parseInt(argList.get(0)));
                optionsBuilder.setWindowHeight(Integer.parseInt(argList.get(1)));
            });
            cmdLineParser.process(arguments);
            var opt = optionsBuilder.setWindowName("test").build();
            assertEquals("PaintOptions[bordered = false, bordered-width = 10, legacy = false, serv = null, window-name = test, window-width = 555, window-height = 555]",
                    opt.toString()
            );
        }

        @Test
        public void testProcessShouldWorkComplex() {
            String[] arguments = {"-min-size", "555", "555", "-window-name", "Test", "-no-borders"};
            cmdLineParser.registerWithParameters("-min-size", 2, (argList) -> {
                optionsBuilder.setWindowWidth(Integer.parseInt(argList.get(0)));
                optionsBuilder.setWindowHeight(Integer.parseInt(argList.get(1)));
            });
            cmdLineParser.addFlag("-no-borders", () -> optionsBuilder.setBordered(true));
            cmdLineParser.registerWithParameters("-window-name", 1, (argList) -> optionsBuilder.setWindowName(argList.get(0)));
            cmdLineParser.process(arguments);
            var opt = optionsBuilder.build();
            assertEquals("PaintOptions[bordered = true, bordered-width = 10, legacy = false, serv = null, window-name = Test, window-width = 555, window-height = 555]",
                    opt.toString()
            );
        }

        @Test
        public void testProcessUnregisteredOptionShouldThrowException() {
            String[] arguments = {"-min-size", "555", "555", "-unregisteredOption"};
            cmdLineParser.registerWithParameters("-min-size", 2, (argList) -> {
                optionsBuilder.setWindowWidth(Integer.parseInt(argList.get(0)));
                optionsBuilder.setWindowHeight(Integer.parseInt(argList.get(1)));
            });
            assertThrows(IllegalArgumentException.class, () -> cmdLineParser.process(arguments));
        }

        @Test
        public void testMissingRequiredOptionShouldThrowException() {
            String[] arguments = {""};
            cmdLineParser.addOption(
                    new CmdLineParser.Option.OptionsBuilder("-legacy", 0, __ -> optionsBuilder.setLegacy(true)).isRequired().build()
            );
            cmdLineParser.registerWithParameters("-window-name", 1, (argList) -> optionsBuilder.setWindowName(argList.get(0)));
            assertThrows(IllegalStateException.class, () -> cmdLineParser.process(arguments));
        }

        @Test
        public void testDocShouldBePrinted() {
            cmdLineParser.addOption(
                    new CmdLineParser.Option.OptionsBuilder("-legacy", 0, __ -> optionsBuilder.setLegacy(true)).doc("Test doc for -legacy").build()
            );
            cmdLineParser.registerWithParameters("-window-name", 1, (argList) -> optionsBuilder.setWindowName(argList.get(0)));
            cmdLineParser.usage();
        }
    }

    @Nested
    public class aliasesTest {

        public void setCmdLineParserUp() {
            cmdLineParser.addOption(
                    new CmdLineParser.Option.OptionsBuilder("-legacy", 0, __ -> optionsBuilder.setLegacy(true)).addAliases("-lgcy").build()
            );
            cmdLineParser.registerWithParameters("-window-name", 1, (argList) -> optionsBuilder.setWindowName(argList.get(0)));
        }

        @Test
        public void testNullAliasShouldThrowAnException() {
            String[] arguments = {"-window-name", "test", "-lgcy"};
            setCmdLineParserUp();
            assertThrows(NullPointerException.class, () -> cmdLineParser.addOption(
                    new CmdLineParser.Option.OptionsBuilder("-legacy", 0, __ -> optionsBuilder.setLegacy(true)).addAliases(null).build()
            ));
        }

        @Test
        public void testAliasShouldWork() {
            String[] arguments = {"-window-name", "test", "-lgcy"};
            setCmdLineParserUp();
            cmdLineParser.process(arguments);
            var opt = optionsBuilder.build();
            assertEquals("PaintOptions[bordered = false, bordered-width = 10, legacy = true, serv = null, window-name = test, window-width = 500, window-height = 500]",
                    opt.toString()
            );
        }

        @Test
        public void testAliasShouldWork2() {
            String[] arguments = {"-window-name", "test", "-legacy"};
            setCmdLineParserUp();
            cmdLineParser.process(arguments);
            var opt = optionsBuilder.build();
            assertEquals("PaintOptions[bordered = false, bordered-width = 10, legacy = true, serv = null, window-name = test, window-width = 500, window-height = 500]",
                    opt.toString()
            );
        }

        @Test
        public void testAliasShouldNotWork() {
            String[] arguments = {"-window-name", "test", "-l"};
            setCmdLineParserUp();
            assertThrows(IllegalArgumentException.class, () -> cmdLineParser.process(arguments));
        }
    }

    // DON'T KNOW HOW TO TEST THIS
    @Nested
    public class usageTest {
        @Test
        public void testMapEmpty() {
            cmdLineParser.usage();

        }
    }

    @Nested
    public class newSetConsumerTest {
        @Test
        public void testSetConsumerNull() {
            var opt = new fr.uge.poo.cmdlineparser.ex5.CmdLineParser.Option.OptionsBuilder("test", 1);
            assertThrows(NullPointerException.class, () -> opt.setConsumer(null));
        }

        @Test
        public void testSetBiConsumerIntegerToIntegerNull() {
            var opt = new fr.uge.poo.cmdlineparser.ex5.CmdLineParser.Option.OptionsBuilder("test", 1);
            assertThrows(NullPointerException.class, () -> opt.setBiConsumerIntegerToInteger(null));
        }

        @Test
        public void testSetBiConsumerInetSocketAddressNull() {
            var opt = new fr.uge.poo.cmdlineparser.ex5.CmdLineParser.Option.OptionsBuilder("test", 1);
            assertThrows(NullPointerException.class, () -> opt.setBiConsumerInetSocketAddress(null));
        }

        @Test
        public void testSetConsumerShouldWorkEasy() {
            String[] arguments = {"-window-name", "test"};
            var windowNameOption = new CmdLineParser.Option.OptionsBuilder("-window-name", 1);
            windowNameOption.setConsumer((argList) -> optionsBuilder.setWindowName(argList.get(0)));
            cmdLineParser.addOption(windowNameOption.build());
            cmdLineParser.process(arguments);
            var opt = optionsBuilder.build();
            assertEquals("PaintOptions[bordered = false, bordered-width = 10, legacy = false, serv = null, window-name = test, window-width = 500, window-height = 500]",
                    opt.toString());
        }

        @Test
        public void testSetBiConsumerIntegerToIntegerShouldWork() {
            // TODO
        }

        @Test
        public void testSetBiConsumerInetSocketAddressShouldWork() {
            String[] arguments = {"-window-name", "test", "-remote-server", "test", "8080"};
            var remoteServerOption = new CmdLineParser.Option.OptionsBuilder("-remote-server", 2);
            remoteServerOption.setBiConsumerInetSocketAddress((hostName, port) -> optionsBuilder.setServ(new InetSocketAddress(hostName, port)));
            cmdLineParser.registerWithParameters("-window-name", 1, (argList) -> optionsBuilder.setWindowName(argList.get(0)));
            cmdLineParser.addOption(remoteServerOption.build());
            cmdLineParser.process(arguments);
            var opt = optionsBuilder.build();
            assertEquals("PaintOptions[bordered = false, bordered-width = 10, legacy = false, serv = test/<unresolved>:8080, window-name = test, window-width = 500, window-height = 500]",
                    opt.toString());
        }
    }

    @Nested
    public class OptionManagerTest {
        @Test
        public void processRequiredOption() {
            var cmdParser = new CmdLineParser();
            var option = new OptionsBuilder("-test", 0, l -> {
            }).isRequired().build();
            cmdParser.addOption(option);
            cmdParser.addFlag("-test1", () -> {
            });
            String[] arguments = {"-test1", "a", "b"};
            assertThrows(IllegalStateException.class, () -> {
                cmdParser.process(arguments);
            });
        }

        @Test
        public void processConflicts() {
            var cmdParser = new CmdLineParser();
            var option = new OptionsBuilder("-test", 0, l -> {
            }).conflictWith("-test1").build();
            cmdParser.addOption(option);
            var option2 = new OptionsBuilder("-test1", 0, l -> {
            }).build();
            cmdParser.addOption(option2);
            String[] arguments = {"-test", "-test1"};
            assertThrows(IllegalStateException.class, () -> {
                cmdParser.process(arguments);
            });
        }

        @Test
        public void processConflicts2() {
            var cmdParser = new CmdLineParser();
            var option = new OptionsBuilder("-test", 0, l -> {
            }).conflictWith("-test1").build();
            cmdParser.addOption(option);
            var option2 = new OptionsBuilder("-test1", 0, l -> {
            }).build();
            cmdParser.addOption(option2);
            String[] arguments = {"-test1", "-test"};
            assertThrows(IllegalStateException.class, () -> {
                cmdParser.process(arguments);
            });
        }

        @Test
        public void processConflictsAndAliases() {
            var cmdParser = new CmdLineParser();
            var option = new OptionsBuilder("-test", 0, l -> {
            }).conflictWith("-test2").build();
            cmdParser.addOption(option);
            var option2 = new OptionsBuilder("-test1", 0, l -> {
            }).addAliases("-test2").build();
            cmdParser.addOption(option2);
            String[] arguments = {"-test1", "-test"};
            assertThrows(IllegalStateException.class, () -> {
                cmdParser.process(arguments);
            });
        }

        @Test
        public void processConflictsAndAliases2() {
            var cmdParser = new CmdLineParser();
            var option = new OptionsBuilder("-test", 0, l -> {
            }).conflictWith("-test2").build();
            cmdParser.addOption(option);
            var option2 = new OptionsBuilder("-test1", 0, l -> {
            }).addAliases("-test2").build();
            cmdParser.addOption(option2);
            String[] arguments = {"-test", "-test1"};
            assertThrows(IllegalStateException.class, () -> {
                cmdParser.process(arguments);
            });
        }
    }
}