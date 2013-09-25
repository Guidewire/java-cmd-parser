package acc.common.cmdline.samples;

import acc.common.cmdline.CmdException;
import acc.common.cmdline.CmdParser;

/**
 * Sample program using cmd-parser library.
 */
public class Program {
    public static void main(String[] args) {
        //CmdParser.dispatchArgs(args, new CommandLineImpl());

        try {
            CmdParser cmdParser = new CmdParser(new CommandLineImpl());
            cmdParser.dispatch(args);
        }
        catch (CmdException e) {
            System.err.printf("Invalid command: %s %n", e.getMessage());
            System.err.printf("ErrorCode=%s, Argument=%s%n", e.getErrorCode().toString(), e.getInvalidArgument());
        }
    }
}
