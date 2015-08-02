package scriptism;

import scriptism.compiler.SourceCodeGenerationVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ClassLoaderUtils {
    public static void writeSourceToFile(String className, SourceCodeGenerationVisitor visitor) {
        // Write the byte code to an output file. The produced class file
        // should be executable with javac.
        File classfile = new File(className + ".java");
        try (PrintStream ps = new PrintStream(new FileOutputStream(classfile))) {
            ps.print(visitor.getResult());
        } catch (IOException e) {
            System.out.printf("Unable to write the file '%s'. %s\n", classfile.getAbsolutePath(), e.getMessage());
        }
    }
}
