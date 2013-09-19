package acc.common.cmdline.samples;

import acc.common.cmdline.annotation.*;

/**
 * The class contains implementations of all commands supported by program.
 */
public class CommandLineImpl {
    @Name(name = "global", shortName = "g", description = "Global parameter available to all commands")
    @Regex("a[0-9]+")
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
            @Name(name = "value1", description = "Value1 description")
            @LongRange(min = 10, max = 100)
            int value1,

            @Required
            @Name(name = "value2", shortName = "v2", description = "Value2 description")
            String value2,

            @DefaultValue("true")
            @Name(name = "value3", description = "Value3 description")
            boolean value3) {
        System.out.printf("globalParameter=%s%n", this.globalParameter);
        System.out.printf("Complex Command: %d, %s, %b%n", value1, value2, value3);
    }

    @Help
    @Name(name = "help", shortName = "h", description = "Help command displaying the usage information")
    public void help(
            @Name(name = "help", description = "Help text")
            String help) {
        System.out.println(help);
    }
}
