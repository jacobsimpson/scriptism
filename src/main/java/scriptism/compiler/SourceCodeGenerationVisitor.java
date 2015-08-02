package scriptism.compiler;

import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang3.StringUtils;
import scriptism.grammar.ScriptismBaseVisitor;
import scriptism.grammar.ScriptismParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static scriptism.compiler.Utils.getEscapedString;
import static scriptism.compiler.Utils.getInterpolatedString;

public class SourceCodeGenerationVisitor extends ScriptismBaseVisitor<Integer> {
    private final String className;
    private final Map<String, VarType> variables = new HashMap<>();
    private final StringWriter writer = new StringWriter();
    private final PrintWriter out = new PrintWriter(writer);
    private final Set<String> staticImports = newHashSet("scriptism.interpreter.Interpreter.out");
    private final Set<String> imports = newHashSet();

    public SourceCodeGenerationVisitor(String className) {
        this.className = className;
    }

    @Override
    public Integer visitIfStatement(ScriptismParser.IfStatementContext ctx) {
        out.print("    if ");
        return visitChildren(ctx);
    }

    @Override
    public Integer visitIfExpression(ScriptismParser.IfExpressionContext ctx) {
        out.print("(");
        Integer result = visitChildren(ctx);
        out.print(")");
        return result;
    }

    @Override
    public Integer visitElseStatement(ScriptismParser.ElseStatementContext ctx) {
        out.print(" else ");
        return visitChildren(ctx);
    }

    @Override
    public Integer visitBlock(ScriptismParser.BlockContext ctx) {
        out.println("{");
        Integer result = visitChildren(ctx);
        out.println("}");
        return result;
    }

    @Override
    public Integer visitBooleanExpression(ScriptismParser.BooleanExpressionContext ctx) {
        if (ctx.IDENTIFIER(0) != null && ctx.IDENTIFIER(1) != null) {
            String varName1 = ctx.IDENTIFIER(0).getText();
            String varName2 = ctx.IDENTIFIER(1).getText();
            if (!variables.containsKey(varName1)) {
                throw new RuntimeException(format("The variable '%s' is not defined.", varName1));
            }
            if (!variables.containsKey(varName2)) {
                throw new RuntimeException(format("The variable '%s' is not defined.", varName2));
            }
            if (variables.get(varName1) == VarType.STRING) {
                if (variables.get(varName2) == VarType.STRING) {
                    imports.add("java.util.Objects");
                    out.printf("Objects.equals(%s, %s)", ctx.IDENTIFIER(0).getText(), ctx.IDENTIFIER(1).getText());
                } else {
                    throw new RuntimeException(format("Can not compare %s to %s on line %s.",
                            variables.get(varName1),
                            variables.get(varName2),
                            ctx.getStart().getLine()));
                }
            } else if (variables.get(varName1) == VarType.INT) {
                if (variables.get(varName2) == VarType.INT) {
                    out.printf("%s == %s", ctx.IDENTIFIER(0).getText(), ctx.IDENTIFIER(1).getText());
                } else {
                    throw new RuntimeException(format("Can not compare %s to %s on line %s.",
                            variables.get(varName1),
                            variables.get(varName2),
                            ctx.getStart().getLine()));
                }
            } else if (variables.get(varName1) == VarType.DOUBLE) {
                if (variables.get(varName2) == VarType.DOUBLE) {
                    out.printf("%s == %s", ctx.IDENTIFIER(0).getText(), ctx.IDENTIFIER(1).getText());
                } else {
                    throw new RuntimeException(format("Can not compare %s to %s on line %s.",
                            variables.get(varName1),
                            variables.get(varName2),
                            ctx.getStart().getLine()));
                }
            }
        } else {
            throw new RuntimeException(format("On line %s, there are not two variables to compare.", ctx.getStart().getLine()));
        }
        return visitChildren(ctx);
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
        if (variables.containsKey(varName)) {
            throw new RuntimeException(format("The variable '%s' is already defined.", varName));
        }
        if (ctx.TYPE() != null) {
            if ("Int".equals(ctx.TYPE().getText())) {
                variables.put(varName, VarType.INT);
                out.printf("    int %s;\n", ctx.IDENTIFIER().getText());
            } else if ("Double".equals(ctx.TYPE().getText())) {
                variables.put(varName, VarType.DOUBLE);
                out.printf("    double %s;\n", ctx.IDENTIFIER().getText());
            } else if ("String".equals(ctx.TYPE().getText())) {
                variables.put(varName, VarType.STRING);
                out.printf("    String %s;\n", ctx.IDENTIFIER().getText());
            } else {
                throw new RuntimeException(format("Unknown type '%s'.", ctx.IDENTIFIER().getText()));
            }
        } else {
            if (ctx.INTEGER() != null) {
                variables.put(varName, VarType.INT);
                out.printf("    int %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.INTEGER().getText());
            } else if (ctx.DOUBLE() != null) {
                variables.put(varName, VarType.DOUBLE);
                out.printf("    double %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.DOUBLE().getText());
            } else if (ctx.STRING() != null) {
                variables.put(varName, VarType.STRING);
                String escapedString = getEscapedString(ctx.STRING().getText());
                InterpolatedString interpolatedString = getInterpolatedString(escapedString);
                if (interpolatedString.getVariables().size() == 0) {
                    out.printf("    String %s = \"%s\";\n", ctx.IDENTIFIER().getText(), escapedString);
                } else {
                    staticImports.add("java.lang.String.format");
                    out.printf("    String %s = format(\"%s\", %s);\n",
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
            out.println("    out.println(" + ctx.IDENTIFIER().getText() + ");");
        } else if (ctx.STRING() != null) {
            String escapedString = getEscapedString(ctx.STRING().getText());
            InterpolatedString interpolatedString = getInterpolatedString(escapedString);
            if (interpolatedString.getVariables().size() == 0) {
                out.println("    out.println(\"" + escapedString + "\");");
            } else {
                out.printf("    out.printf(\"%s\\n\", %s);\n",
                        interpolatedString.getText(),
                        StringUtils.join(interpolatedString.getVariables().toArray(), ','));
            }
        } else {
            out.println("    out.println();");
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
