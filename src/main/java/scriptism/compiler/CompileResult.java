package scriptism.compiler;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Collections;
import java.util.List;

public class CompileResult {
    private final boolean success;
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;
    private ClassLoader classLoader;

    public CompileResult(boolean success, List<Diagnostic<? extends JavaFileObject>> diagnostics, ClassLoader classLoader) {
        this.success = success;
        this.diagnostics = Collections.unmodifiableList(diagnostics);
        this.classLoader = classLoader;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnostics;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
