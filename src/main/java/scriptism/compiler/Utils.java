package scriptism.compiler;

import java.util.ArrayList;
import java.util.List;

import static scriptism.compiler.ParseState.IN_STRING;
import static scriptism.compiler.ParseState.START_VAR;
import static scriptism.compiler.ParseState.VAR;

public class Utils {
    public static String getEscapedString(String text) {
        if (text == null) {
            return "";
        } else if (text.length() == 0) {
            return "";
        } else if (text.startsWith("'")) {
            return text.substring(1, text.length() - 1).replaceAll("\"", "\\\\\"");
        } else {
            return text.substring(1, text.length() - 1);
        }
    }

    public static InterpolatedString getInterpolatedString(String escapedString) {
        if (escapedString == null) return new InterpolatedString(escapedString, null);

        String result = "";
        ParseState state = IN_STRING;
        int fragmentStart = 0;
        List<String> vars = new ArrayList<>();
        for (int i = 0; i < escapedString.length(); i++) {
            if (state == IN_STRING) {
                if (escapedString.charAt(i) == '#') {
                    state = START_VAR;
                }
            } else if (state == START_VAR) {
                if (escapedString.charAt(i) == '{') {
                    state = VAR;
                    result += escapedString.substring(fragmentStart, i - 1);
                    fragmentStart = i + 1;
                } else {
                    state = IN_STRING;
                }
            } else if (state == VAR) {
                if (escapedString.charAt(i) == '}') {
                    state = IN_STRING;
                    result += "%s";
                    vars.add(escapedString.substring(fragmentStart, i));
                    fragmentStart = i + 1;
                }
            }

        }
        result += escapedString.substring(fragmentStart, escapedString.length());
        return new InterpolatedString(result, vars);
    }
}
