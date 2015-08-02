package scriptism;

import scriptism.interpreter.Interpreter;
import scriptism.interpreter.Options;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String... args) throws IOException,
                                                   NoSuchMethodException,
                                                   IllegalAccessException,
                                                   InvocationTargetException,
                                                   ClassNotFoundException {

        Options options = CommandLineOptions.parse(args);
        if (options.getShouldExit()) {
            System.exit(options.getExitStatus());
        }

        System.exit(Interpreter.execute(options));
    }
}
