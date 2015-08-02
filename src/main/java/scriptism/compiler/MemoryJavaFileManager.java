package scriptism.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final Map<String, MemoryJavaFileObject> classDefinitions = new HashMap<>();

    public MemoryJavaFileManager(StandardJavaFileManager standardFileManager) {
        super(standardFileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        MemoryJavaFileObject memoryJavaFileObject = new MemoryJavaFileObject(className, kind);
        classDefinitions.put(className, memoryJavaFileObject);
        return memoryJavaFileObject;
    }

    public Map<String, byte[]> getClassDefinitions() {
        Map<String, byte[]> definitions = new HashMap<>();
        for (Map.Entry<String, MemoryJavaFileObject> entry : classDefinitions.entrySet()) {
            definitions.put(entry.getKey(), entry.getValue().getBytes());
        }
        return definitions;
    }
}
