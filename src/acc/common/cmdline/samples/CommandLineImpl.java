package acc.common.cmdline.samples;

import acc.common.cmdline.annotation.*;

import java.util.Arrays;

/**
 * The class contains implementations of all commands supported by program.
 */
public class CommandLineImpl {
    @Name(name = "global", shortName = "g", description = "Global parameter available to all commands")
    public String globalParameter = null;

    @DefaultCommand
    @Name(name = "default", shortName = "d", description = "Default command with simple action")
    public void defaultCommand() {
        System.out.printf("globalParameter=%s%n", this.globalParameter);
        System.out.println("Default Command");
    }

    @Name(name = "simple", shortName = "s", description = "Simple command with simple action")
    public void simpleCommand() {
        System.out.printf("globalParameter=%s%n", this.globalParameter);
        System.out.println("Simple Command");
    }

    @Name(name = "complex", shortName = "c", description = "Complex command with parameters")
    public void complexCommand(
            @Name(name = "param1", shortName = "p1", description = "First parameter")
            @LongRange(min = 10, max = 100)
            int param1,

            @Required
            @Name(name = "param2", shortName = "p2", description = "Second parameter")
            @Regex("a[0-9]+")
            String param2,

            @DefaultValue("true")
            @Name(name = "param3", shortName = "p3", description = "Third parameter")
            boolean param3) {
        System.out.printf("globalParameter=%s%n", this.globalParameter);
        System.out.printf("Complex Command: %d, %s, %b%n", param1, param2, param3);
    }

    @Name(name = "array", shortName = "a", description = "Complex command with array parameter")
    public void complexCommandWithArrayParameter(
            @Name(name = "param1", shortName = "p1", description = "First parameter")
            @LongRange(min = 10, max = 100)
            int[] param1) {
        System.out.printf("globalParameter=%s%n", this.globalParameter);
        System.out.printf("Complex Command with array parameter: %d, %s%n", param1.length, Arrays.toString(param1));
    }

    @Name(name = "array2", shortName = "ua", description = "Complex command with unnamed array parameter")
    public void complexCommandWithUnnamedArrayParameter(
            @Unnamed(description = "First parameter")
            @LongRange(min = 10, max = 100)
            int[] param1) {
        System.out.printf("globalParameter=%s%n", this.globalParameter);
        System.out.printf("Complex Command with array parameter: %d, %s%n", param1.length, Arrays.toString(param1));
    }

    @Name(name = "unnamed", shortName = "u", description = "Complex command with unnamed parameters")
    public void complexCommandWithUnnamedParameters(
            @Name(name = "param1", shortName = "p1", description = "First parameter")
            @LongRange(min = 10, max = 100)
            int param1,

            @Required
            @Unnamed(description = "Second parameter")
            @Regex("a[0-9]+")
            String param2,

            @DefaultValue("true")
            @Unnamed(description = "Third parameter")
            boolean param3) {
        System.out.printf("globalParameter=%s%n", this.globalParameter);
        System.out.printf("Complex Command with unnamed parameters: %d, %s, %b%n", param1, param2, param3);
    }

    @Help
    @Name(name = "help", shortName = "h", description = "Help command displaying the usage information")
    public void help(
            @Name(name = "help", description = "Help text")
            String help) {
        System.out.println(help);
    }
}
