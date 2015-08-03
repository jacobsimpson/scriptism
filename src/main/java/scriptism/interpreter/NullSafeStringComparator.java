package scriptism.interpreter;

import java.util.Comparator;

public class NullSafeStringComparator implements Comparator<String> {
    public static final Comparator<String> COMPARATOR = new NullSafeStringComparator();

    public int compare(String string1, String string2) {
        if (string1 == string2) {
            return 0;
        }
        if (string1 == null) {
            return -1;
        }
        if (string2 == null) {
            return 1;
        }
        return string1.compareTo(string2);
    }
}
