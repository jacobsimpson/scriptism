package scriptism.interpreter;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InterpreterTest {
    @Test
    public void testHelloWorld() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/hello-world.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(), is(equalTo("hello world\n")));
    }

    @Test
    public void testIfStatement() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/if-statement.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(),
                is(equalTo("Before if-1. Compare x(3) == y(4)\n" +
                        "x != y\n" +
                        "Before if-2. Compare s1(s-value) == s2(s-value)\n" +
                        "s1 == s2\n" +
                        "Before if-3.\n" +
                        "i1 == i1\n")));
    }

    @Test
    public void testInterpolatedString() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/interpolated_string.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(),
                is(equalTo("This is the number 3.\n" +
                        "Your name is joseph and you live here.\n")));
    }

    @Test
    public void testCompareInterpolatedString() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/compare_interpolated_string.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(),
                is(equalTo("Interpolations are equal.\n")));
    }
}
