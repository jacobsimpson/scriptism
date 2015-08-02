package scriptism.compiler;

import org.antlr.v4.runtime.misc.NotNull;
import scriptism.grammar.ScriptismBaseVisitor;
import scriptism.grammar.ScriptismParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class SourceCodeGenerationVisitor extends ScriptismBaseVisitor<Integer> {
    private final String className;
    private Set<String> variables = new HashSet<>();
    private final StringWriter writer = new StringWriter();
    private final PrintWriter out = new PrintWriter(writer);

    public SourceCodeGenerationVisitor(String className) {
        this.className = className;
    }

    @Override
    public Integer visitAssignmentStatement(@NotNull ScriptismParser.AssignmentStatementContext ctx) {
        out.printf("    %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.INTEGER().getText());
        return visitChildren(ctx);
    }

    @Override
    public Integer visitDeclarationStatement(@NotNull ScriptismParser.DeclarationStatementContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        if (variables.contains(varName)) {
            throw new RuntimeException(format("The variable '%s' is already defined.", varName));
        }
        variables.add(varName);
        out.printf("    int %s;\n", ctx.IDENTIFIER().getText());
        return visitChildren(ctx);
    }

    @Override
    public Integer visitPrintStatement(@NotNull ScriptismParser.PrintStatementContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            out.println("    System.out.println(" + ctx.IDENTIFIER().getText() + ");");
        } else if (ctx.STRING() != null) {
            out.println("    System.out.println(\"" + ctx.STRING().getText() + "\");");
        } else {
            out.println("    System.out.println();");
        }
        return visitChildren(ctx);
    }

    @Override
    public Integer visitProgram(@NotNull ScriptismParser.ProgramContext ctx) {
        out.printf("public class %s {\n", className);
        out.println("  public static void main(String... args) {");

        Integer result = visitChildren(ctx);

        out.println("  }");
        out.println("}");
        out.close();
        return result;
    }

    public String getResult() {
        return writer.toString();
    }
}
