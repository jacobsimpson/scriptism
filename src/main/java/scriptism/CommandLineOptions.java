package scriptism;

import java.io.File;
import java.util.Arrays;

public class CommandLineOptions {
    public static Options parse(String... args) {
        Options options = new Options();

        if (args.length == 0) {
            printHelp();
            options.shouldExit(0);
        }

        for (int i = 0; i < args.length; i++) {
            if ("-h".equals(args[i]) || "-help".equals(args[i]) || "--help".equals(args[i])) {
                printHelp();
                options.shouldExit(0);
                return options;
            } else if ("--classfile".equals(args[i])) {
                options.setWriteClassfile(true);
            } else {
                File programFile = new File(args[i]);
                options.setScript(programFile);
                options.setArgs(Arrays.copyOfRange(args, i + 1, args.length));
                if (!programFile.exists()) {
                    System.err.printf("The script file '%s' does not exist.\n", programFile.getAbsolutePath());
                    options.shouldExit(1);
                }
                if (!programFile.canRead()) {
                    System.err.printf("The script file '%s' is not readable.\n", programFile.getAbsolutePath());
                    options.shouldExit(1);
                }
                return options;
            }
        }
        return options;
    }

    private static void printHelp() {
        System.out.println("scriptism [--help] [--classfile] <program file> <arg>*");
        System.out.println();
        System.out.println("    --help          print this message");
        System.out.println("    --classfile     write the Java source file that corresponds to the script.");
        System.out.println();
        System.out.println("    program file    the script file to execute.");
        System.out.println("    arg             the list of arguments to pass to the script.");
        System.out.println();
    }
}
