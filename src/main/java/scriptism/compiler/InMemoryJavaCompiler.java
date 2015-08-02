package scriptism.compiler;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.util.Arrays;

public class InMemoryJavaCompiler {
    public static CompileResult compile(String className, String javaSource) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(standardFileManager);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaFileObject file = new MemoryJavaFileObject(className, javaSource);

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();
        return new CompileResult(success,
                diagnostics.getDiagnostics(),
                new MemoryClassLoader(fileManager.getClassDefinitions()));
    }
}
