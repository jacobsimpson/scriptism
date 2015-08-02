package scriptism.compiler;

import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang3.StringUtils;
import scriptism.grammar.ScriptismBaseVisitor;
import scriptism.grammar.ScriptismParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static scriptism.compiler.Utils.getEscapedString;
import static scriptism.compiler.Utils.getInterpolatedString;

public class SourceCodeGenerationVisitor extends ScriptismBaseVisitor<Integer> {
    private final String className;
    private final Set<String> variables = new HashSet<>();
    private final StringWriter writer = new StringWriter();
    private final PrintWriter out = new PrintWriter(writer);
    private final Set<String> staticImports = new HashSet<>();
    private final Set<String> imports = new HashSet<>();

    public SourceCodeGenerationVisitor(String className) {
        this.className = className;
    }

    @Override
    public Integer visitAssignmentStatement(@NotNull ScriptismParser.AssignmentStatementContext ctx) {
        if (ctx.STRING() != null) {
            out.printf("    %s = \"%s\";\n", ctx.IDENTIFIER().getText(), getEscapedString(ctx.STRING().getText()));
        } else if (ctx.INTEGER() != null) {
            out.printf("    %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.INTEGER().getText());
        } else if (ctx.DOUBLE() != null) {
            out.printf("    %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.DOUBLE().getText());
        }
        return visitChildren(ctx);
    }

    @Override
    public Integer visitDeclarationStatement(@NotNull ScriptismParser.DeclarationStatementContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        if (variables.contains(varName)) {
            throw new RuntimeException(format("The variable '%s' is already defined.", varName));
        }
        variables.add(varName);
        if (ctx.TYPE() != null) {
            if ("Int".equals(ctx.TYPE().getText())) {
                out.printf("    int %s;\n", ctx.IDENTIFIER().getText());
            } else if ("Double".equals(ctx.TYPE().getText())) {
                out.printf("    double %s;\n", ctx.IDENTIFIER().getText());
            } else if ("String".equals(ctx.TYPE().getText())) {
                out.printf("    String %s;\n", ctx.IDENTIFIER().getText());
            } else {
                throw new RuntimeException(format("Unknown type '%s'.", ctx.IDENTIFIER().getText()));
            }
        } else {
            if (ctx.INTEGER() != null) {
                out.printf("    int %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.INTEGER().getText());
            } else if (ctx.DOUBLE() != null) {
                out.printf("    double %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.DOUBLE().getText());
            } else if (ctx.STRING() != null) {
                String escapedString = getEscapedString(ctx.STRING().getText());
                InterpolatedString interpolatedString = getInterpolatedString(escapedString);
                if (interpolatedString.getVariables().size() == 0) {
                    out.printf("    String %s = \"%s\";\n", ctx.IDENTIFIER().getText(), escapedString);
                } else {
                    staticImports.add("java.lang.String.format");
                    out.printf("    String %s = format(\"%s\\n\", %s);\n",
                            ctx.IDENTIFIER().getText(),
                            interpolatedString.getText(),
                            StringUtils.join(interpolatedString.getVariables().toArray(), ','));
                }
            } else {
                throw new RuntimeException(format("The value of the variable '%s' can not be identified.", ctx.IDENTIFIER().getText()));
            }
        }
        return visitChildren(ctx);
    }

    @Override
    public Integer visitPrintStatement(@NotNull ScriptismParser.PrintStatementContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            out.println("    System.out.println(" + ctx.IDENTIFIER().getText() + ");");
        } else if (ctx.STRING() != null) {
            String escapedString = getEscapedString(ctx.STRING().getText());
            InterpolatedString interpolatedString = getInterpolatedString(escapedString);
            if (interpolatedString.getVariables().size() == 0) {
                out.println("    System.out.println(\"" + escapedString + "\");");
            } else {
                out.printf("    System.out.printf(\"%s\\n\", %s);\n",
                        interpolatedString.getText(),
                        StringUtils.join(interpolatedString.getVariables().toArray(), ','));
            }
        } else {
            out.println("    System.out.println();");
        }
        return visitChildren(ctx);
    }

    @Override
    public Integer visitProgram(@NotNull ScriptismParser.ProgramContext ctx) {
        out.println();
        out.printf("public class %s {\n", className);
        out.println("  public static void main(String... args) {");

        Integer result = visitChildren(ctx);

        out.println("  }");
        out.println("}");
        out.close();
        return result;
    }

    public String getResult() {
        String importSection = "\n// Static Imports.\n";

        for (String staticImport : staticImports) {
            importSection += "import static " + staticImport + ";\n";
        }

        importSection += "\n// Regular imports.\n";

        for (String importClass : imports) {
            importSection += "import " + importClass + ";\n";
        }
        importSection += "\n";


        return importSection + writer.toString();
    }
}
