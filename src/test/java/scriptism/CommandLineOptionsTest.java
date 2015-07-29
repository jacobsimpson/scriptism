package scriptism;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class CommandLineOptionsTest {

    @Test
    public void testNoParameters() {
        Options options = CommandLineOptions.parse();

        assertThat(options.getWriteClassfile(), is(equalTo(false)));
        assertThat(options.getScript(), is(nullValue()));
        assertThat(options.getShouldExit(), is(equalTo(true)));
        assertThat(options.getExitStatus(), is(equalTo(0)));
        assertThat(options.getArgs().length, is(equalTo(0)));
    }

    @Test
    public void testHelp() {
        Options options = CommandLineOptions.parse("--help");

        assertThat(options.getWriteClassfile(), is(equalTo(false)));
        assertThat(options.getScript(), is(nullValue()));
        assertThat(options.getShouldExit(), is(equalTo(true)));
        assertThat(options.getExitStatus(), is(equalTo(0)));
        assertThat(options.getArgs().length, is(equalTo(0)));
    }

    @Test
    public void testScriptNameWithParameters() throws IOException {
        File scriptFile = File.createTempFile("mock-test-file", ".txt");
        scriptFile.deleteOnExit();

        Options options = CommandLineOptions.parse(scriptFile.getAbsolutePath(), "arg1", "-arg2", "arg3");

        assertThat(options.getWriteClassfile(), is(equalTo(false)));
        assertThat(options.getScript(), is(equalTo(scriptFile)));
        assertThat(options.getShouldExit(), is(equalTo(false)));
        assertThat(options.getExitStatus(), is(equalTo(0)));
        assertThat(options.getArgs().length, is(equalTo(3)));
        assertThat(options.getArgs(), is(equalTo(new String[] {"arg1", "-arg2", "arg3"})));
    }

    @Test
    public void testScriptOutputClassfile() throws IOException {
        File scriptFile = File.createTempFile("mock-test-file", ".txt");
        scriptFile.deleteOnExit();

        Options options = CommandLineOptions.parse("--classfile", scriptFile.getAbsolutePath(), "arg1", "-arg2", "arg3");

        assertThat(options.getWriteClassfile(), is(equalTo(true)));
        assertThat(options.getScript(), is(equalTo(scriptFile)));
        assertThat(options.getShouldExit(), is(equalTo(false)));
        assertThat(options.getExitStatus(), is(equalTo(0)));
        assertThat(options.getArgs().length, is(equalTo(3)));
        assertThat(options.getArgs(), is(equalTo(new String[]{"arg1", "-arg2", "arg3"})));
    }
}