package scriptism.compiler;

import java.util.Collections;
import java.util.List;

public class InterpolatedString {
    private final String text;
    private final List<String> variables;

    public InterpolatedString(String text, List<String> variables) {
        this.text = text;
        if (variables == null) {
            this.variables = Collections.emptyList();
        } else {
            this.variables = variables;
        }
    }

    public String getText() {
        return text;
    }

    public List<String> getVariables() {
        return variables;
    }
}
