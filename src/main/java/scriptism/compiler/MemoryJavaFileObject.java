package scriptism.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class MemoryJavaFileObject extends SimpleJavaFileObject {
    private String code;
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public MemoryJavaFileObject(String name, String code) {
        this(name, Kind.SOURCE);
        this.code = code;
    }

    public MemoryJavaFileObject(String name, Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return byteArrayOutputStream;
    }

    public byte[] getBytes() {
        return byteArrayOutputStream.toByteArray();
    }
}