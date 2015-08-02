package scriptism.compiler;

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

}
