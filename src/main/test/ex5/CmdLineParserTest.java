package test.ex5;

import fr.uge.poo.cmdlineparser.ex5.CmdLineParser;
import fr.uge.poo.cmdlineparser.ex5.PaintSettings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
        public void testRegisterWithoutParameterNull() {
            assertAll(
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.registerWithoutParameter("test", null)),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.registerWithoutParameter(null, () -> System.out.println("test"))),
                    () -> assertThrows(NullPointerException.class, () -> cmdLineParser.registerWithoutParameter(null, null))
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
            cmdLineParser.registerWithoutParameter("-no-borders", () -> optionsBuilder.setBordered(true));
            cmdLineParser.registerWithParameters("-window-name", 1, (argList) -> optionsBuilder.setWindowName(argList.get(0)));
            cmdLineParser.process(arguments);
            var opt = optionsBuilder.build();
            assertEquals("PaintOptions[bordered = true, bordered-width = 10, legacy = false, serv = null, window-name = Test, window-width = 555, window-height = 555]",
                    opt.toString()
            );
        }

        @Test
        public void processUnregisteredOptionShouldThrowException() {
            String[] arguments = {"-min-size", "555", "555", "-unregisteredOption"};
            cmdLineParser.registerWithParameters("-min-size", 2, (argList) -> {
                optionsBuilder.setWindowWidth(Integer.parseInt(argList.get(0)));
                optionsBuilder.setWindowHeight(Integer.parseInt(argList.get(1)));
            });
            assertThrows(IllegalArgumentException.class, () -> cmdLineParser.process(arguments));
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


}