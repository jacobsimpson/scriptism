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
    public void hello_world() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/hello_world.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(), is(equalTo("hello world\n")));
    }

    @Test
    public void if_statement() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/if_statement.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(),
                is(equalTo("Integer Comparisons\n" +
                        "==================\n" +
                        "Is x(=3) <  y(=4) yes\n" +
                        "Is x(=3) <= y(=4) yes\n" +
                        "Is x(=3) == y(=4) no\n" +
                        "Is x(=3) != y(=4) yes\n" +
                        "Is x(=3) >= y(=4) no\n" +
                        "Is x(=3) >  y(=4) no\n" +
                        "\n" +
                        "String Comparisons\n" +
                        "==================\n" +
                        "Is s1(=s-value30) <  s2(=s-value2) no\n" +
                        "Is s1(=s-value30) <= s2(=s-value2) no\n" +
                        "Is s1(=s-value30) == s2(=s-value2) no\n" +
                        "Is s1(=s-value30) != s2(=s-value2) yes\n" +
                        "Is s1(=s-value30) >= s2(=s-value2) yes\n" +
                        "Is s1(=s-value30) >  s2(=s-value2) yes\n")));
    }

    @Test
    public void interpolated_string() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/interpolated_string.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(),
                is(equalTo("The number 3.\n" +
                        "Your name is joseph and you live here.\n" +
                        "One last time joseph\n")));
    }

    @Test
    public void compare_interpolated_string() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/compare_interpolated_string.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(),
                is(equalTo("Interpolations are equal.\n")));
    }

    @Test
    public void print_statement() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IOException {
        Options options = new Options().withScript(new File("scripts/print_statement.tsm"));
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        Interpreter.out = new PrintStream(capture);

        Interpreter.execute(options);

        assertThat(capture.toString(),
                is(equalTo("one two three\n" +
                        "four five six\n")));
    }
}
