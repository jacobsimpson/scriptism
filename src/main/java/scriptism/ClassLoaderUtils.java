package scriptism;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ClassLoaderUtils {
    public static Class loadClass(String className, byte[] b)
            throws ClassNotFoundException,
                   NoSuchMethodException,
                   InvocationTargetException,
                   IllegalAccessException {
        Class clazz = null;
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class cls = Class.forName("java.lang.ClassLoader");
        java.lang.reflect.Method method =
                cls.getDeclaredMethod("defineClass",
                        String.class,
                        byte[].class,
                        int.class,
                        int.class);

        // Java's defineClass method is protected by default,  so
        // unprotect it so we can call it.
        method.setAccessible(true);
        try {
            Object[] args = new Object[]{className, b, 0, b.length};
            clazz = (Class)method.invoke(loader, args);
        } finally {
            method.setAccessible(false);
        }
        return clazz;
    }

    public static void writeClassToFile(String className, ByteCodeGenerationVisitor visitor) {
        // Write the byte code to an output file. The produced class file
        // should be executable with javac.
        File classfile = new File(className + ".class");
        try (FileOutputStream fos = new FileOutputStream(classfile)) {
            fos.write(visitor.getResult());
        } catch (IOException e) {
            System.out.printf("Unable to write the file '%s'. %s\n", classfile.getAbsolutePath(), e.getMessage());
        }
    }
}
