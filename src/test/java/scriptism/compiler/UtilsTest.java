package scriptism.compiler;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static scriptism.compiler.Utils.getEscapedString;

public class UtilsTest {
    @Test
    public void testGetEscapedDoubleQuoteString() {
        String content = "one two three";
        String result = getEscapedString("\"" + content + "\"");
        assertThat(result, is(equalTo(content)));
    }

    @Test
    public void testGetEscapedDoubleQuoteStringWithEmbeddedDoubleQuotes() {
        String result = getEscapedString("\"one \\\"two\\\" three\"");
        assertThat(result, is(equalTo("one \\\"two\\\" three")));
    }

    @Test
    public void testGetEscapedSingleQuoteString() {
        String content = "one two three";
        String result = getEscapedString("'" + content + "'");
        assertThat(result, is(equalTo(content)));
    }

    @Test
    public void testGetEscapedSingleQuoteStringWithEmbeddedDoubleQuotes() {
        String result = getEscapedString("'one \"two\" three'");
        assertThat(result, is(equalTo("one \\\"two\\\" three")));
    }
}