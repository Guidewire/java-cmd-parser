package acc.common.cmdline.samples;

import acc.common.cmdline.CmdParser;

/**
 * Sample program using cmd-parser library.
 */
public class Program {
    public static void main(String[] args) {
        CmdParser.dispatchArgs(args, new CommandLineImpl());
    }
}
