package scriptism.interpreter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.reflect.MethodUtils;
import scriptism.compiler.CompileResult;
import scriptism.compiler.InMemoryJavaCompiler;
import scriptism.compiler.SourceCodeGenerationVisitor;
import scriptism.grammar.ScriptismLexer;
import scriptism.grammar.ScriptismParser;

import javax.tools.Diagnostic;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

public class Interpreter {
    public static PrintStream err = System.err;
    public static PrintStream out = System.out;

    public static int execute(Options options) throws IOException,
                                                      NoSuchMethodException,
                                                      IllegalAccessException,
                                                      InvocationTargetException,
                                                      ClassNotFoundException {
        // The process of compiling Scriptism to an executable format is
        // basically a two step process. First, lex and parse the input file.
        // Second, transform it to byte code and load it into memory.

        // Step 1: Lex and parse the input file.  The lexer and parser are
        // generated by ANTLR. To see the grammar, have a look at the .g4 file.
        // If you find some of the Scriptism* classes are missing or out of
        // date, that is because those classes are generated from the .g4 file
        // from the ANTLR tool.
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(options.getScript()));
        ScriptismLexer lexer = new ScriptismLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ScriptismParser parser = new ScriptismParser(tokens);
        ErrorListener errorListener = new ErrorListener();
        parser.addErrorListener(errorListener);

        // The .program() method is the root node of the grammar specified in
        // the .g4 file.
        ParseTree tree = parser.program();

        if (errorListener.isErrorFound()) {
            return 1;
        }

        // Step 2: Use the generated ANTLR visitor to generate byte code.
        // Parsed computer programs are stored in a tree structure, commonly
        // called an AST. A visitor for the tree will receive callbacks at each
        // node of the tree, giving it a chance to see what information the
        // parser found at that node and take some action at that time. The
        // visitor will generate Java source code and compile that using the
        // JavaCompiler interface of the JDK.
        final String className = options.getScriptName();
        SourceCodeGenerationVisitor visitor = new SourceCodeGenerationVisitor(className);
        visitor.visit(tree);

        CompileResult compile = InMemoryJavaCompiler.compile(className, visitor.getResult());

        if (options.getWriteClassfile()) {
            ClassLoaderUtils.writeSourceToFile(className, visitor);
        }

        if (compile.isSuccess()) {
            try {
                Class<?> classDefinition = Class.forName(className, true, compile.getClassLoader());
                MethodUtils.invokeExactStaticMethod(classDefinition,
                        "main",
                        new Object[]{options.getArgs()},
                        new Class<?>[]{String[].class});
            } catch (ClassNotFoundException e) {
                err.println("Class not found: " + e);
            } catch (NoSuchMethodException e) {
                err.println("No such method: " + e);
            } catch (IllegalAccessException e) {
                err.println("Illegal access: " + e);
            } catch (InvocationTargetException e) {
                err.println("Invocation target: " + e);
            }
        } else {
            for (Diagnostic diagnostic : compile.getDiagnostics()) {
                out.println(diagnostic.getCode());
                out.println(diagnostic.getKind());
                out.println(diagnostic.getPosition());
                out.println(diagnostic.getStartPosition());
                out.println(diagnostic.getEndPosition());
                out.println(diagnostic.getSource());
                out.println(diagnostic.getMessage(null));
                out.printf("%s: At position %s: %s\n",
                        diagnostic.getKind(),
                        diagnostic.getPosition(),
                        diagnostic.getMessage(null));
            }
            return 1;
        }
        return 0;
    }
}
