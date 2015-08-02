package scriptism.compiler;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static scriptism.compiler.Utils.getEscapedString;
import static scriptism.compiler.Utils.getInterpolatedString;

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

    @Test
    public void testGetInterpolatedString() {
        String basicString = "basic string";
        InterpolatedString interpolatedString = getInterpolatedString(basicString);
        assertThat(interpolatedString.getText(), is(equalTo(basicString)));
        assertThat(interpolatedString.getVariables().size(), is(equalTo(0)));
    }

    @Test
    public void testGetInterpolatedStringWithVar() {
        InterpolatedString interpolatedString = getInterpolatedString("string with #{count} vars");
        assertThat(interpolatedString.getText(), is(equalTo("string with %s vars")));
        assertThat(interpolatedString.getVariables().size(), is(equalTo(1)));
        assertThat(interpolatedString.getVariables().get(0), is(equalTo("count")));
    }

    @Test
    public void testGetInterpolatedStringWithMultipleVar() {
        InterpolatedString interpolatedString = getInterpolatedString("string with #{count} vars #{one} two #{three} four");
        assertThat(interpolatedString.getText(), is(equalTo("string with %s vars %s two %s four")));
        assertThat(interpolatedString.getVariables().size(), is(equalTo(3)));
        assertThat(interpolatedString.getVariables().get(0), is(equalTo("count")));
        assertThat(interpolatedString.getVariables().get(1), is(equalTo("one")));
        assertThat(interpolatedString.getVariables().get(2), is(equalTo("three")));
    }

    @Test
    public void testGetInterpolatedStringWithVarAtEnd() {
        InterpolatedString interpolatedString = getInterpolatedString("string with #{count} vars #{one} two #{three}");
        assertThat(interpolatedString.getText(), is(equalTo("string with %s vars %s two %s")));
        assertThat(interpolatedString.getVariables().size(), is(equalTo(3)));
        assertThat(interpolatedString.getVariables().get(0), is(equalTo("count")));
        assertThat(interpolatedString.getVariables().get(1), is(equalTo("one")));
        assertThat(interpolatedString.getVariables().get(2), is(equalTo("three")));
    }
}