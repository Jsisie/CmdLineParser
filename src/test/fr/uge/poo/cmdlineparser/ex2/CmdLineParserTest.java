package fr.uge.poo.cmdlineparser.ex2;

import fr.uge.poo.cmdlineparser.ex2.Application.PaintSettings;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class CmdLineParserTest {

    PaintSettings options = new PaintSettings();
    CmdLineParser cmdLineParser = new CmdLineParser();

    @Test
    public void testAddFlagShouldThrowExceptionIfArgumentNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addFlag("test", null)),
                () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addFlag(null, () -> System.out.println("test"))),
                () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addFlag(null, null))
        );
    }

    @Test
    public void testProcessShouldThrowExceptionIfArgumentNull() {
        assertThrows(NullPointerException.class, () -> cmdLineParser.process(null));
    }

    @Test
    public void testProcessBordersShouldBeFalse() {
        String[] arguments = {"-no-borders"};
        cmdLineParser.addFlag("-no-borders", () -> options.setBordered(false));
        cmdLineParser.process(arguments);
        assertFalse(options.isBordered());
    }

    @Test
    public void testProcessLegacyShouldBeTrue() {
        String[] arguments = {"-legacy"};
        cmdLineParser.addFlag("-legacy", () -> options.setLegacy(true));
        cmdLineParser.process(arguments);
        assertTrue(options.isLegacy());
    }

    @Test
    public void testProcessFilesShouldBeReturned() {
        String[] arguments = {"-legacy", "file1", "-no-borders", "file2"};
        cmdLineParser.addFlag("-legacy", () -> options.setLegacy(true));
        cmdLineParser.addFlag("-no-borders", () -> options.setBordered(false));
        var files = cmdLineParser.process(arguments);
        System.out.println(files);
        var list = new ArrayList<String>();
        list.add("file1");
        list.add("file2");
        assertEquals(list, files);
    }

    @Test
    public void testSameOptionCouldNotBeRegisteredTwice() {
        cmdLineParser.addFlag("-legacy", () -> options.setLegacy(true));
        assertThrows(IllegalStateException.class, () -> cmdLineParser.addFlag("-legacy", () -> options.setLegacy(true)));
    }

    @Test
    public void testAddOptionWithOneParameterShouldThrownExceptionIfArgumentIsNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addOptionWithOneParameter(null, __ -> System.out.println("test"))),
                () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addOptionWithOneParameter("test", null)),
                () -> assertThrows(NullPointerException.class, () -> cmdLineParser.addOptionWithOneParameter(null, null))
        );
    }

    @Test
    public void testProcessWithOneArgumentShouldWork() {
        String[] arguments = {"-window-name", "test"};
        cmdLineParser.addOptionWithOneParameter("-window-name", it -> options.setWindowName(it.next()));
        cmdLineParser.process(arguments);
        assertEquals("test", options.getWindowName());
    }
}