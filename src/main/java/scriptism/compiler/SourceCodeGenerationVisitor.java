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

    private static class Atom {
        private final VarType type;
        private final boolean literal;
        private final String value;

        public Atom(VarType type, boolean literal, String value) {
            this.type = type;
            this.literal = literal;
            this.value = value;
        }

        public VarType getType() {
            return type;
        }

        public boolean isLiteral() {
            return literal;
        }

        public String getValue() {
            return value;
        }
    }

    private Atom getAtom(ScriptismParser.AtomContext atom) {
        if (atom.IDENTIFIER() != null) {
            String varName = atom.IDENTIFIER().getText();

            if (!variables.containsKey(varName)) {
                throw new RuntimeException(format("The variable '%s' is not defined.", varName));
            }
            return new Atom(variables.get(varName), false, varName);
        } else if (atom.DOUBLE() != null) {
            return new Atom(VarType.DOUBLE, true, atom.DOUBLE().getText());
        } else if (atom.INTEGER() != null) {
            return new Atom(VarType.INT, true, atom.getText());
        } else if (atom.STRING() != null) {
            return new Atom(VarType.STRING, true, atom.getText());
        }
        return null;
    }

    @Override
    public Integer visitBooleanExpression(ScriptismParser.BooleanExpressionContext ctx) {
        Atom atom1 = getAtom(ctx.atom(0));
        Atom atom2 = getAtom(ctx.atom(1));

        if (atom1 == null || atom2 == null) {
            throw new RuntimeException("Unknown type.");
        }
        
        if (atom1.getType() == VarType.STRING) {
            if (atom2.getType() == VarType.STRING) {
                compareStrings(atom1, atom2, ctx.COMPARISON_OPERATOR().getText());
            } else {
                throw new RuntimeException(format("Can not compare %s to %s on line %s.",
                        atom1.getType(),
                        atom2.getType(),
                        ctx.getStart().getLine()));
            }
        } else if (atom1.getType() == VarType.INT) {
            if (atom2.getType() == VarType.INT) {
                compareNumerics(atom1, atom2, ctx.COMPARISON_OPERATOR().getText());
            } else {
                throw new RuntimeException(format("Can not compare %s to %s on line %s.",
                        atom1.getType(),
                        atom2.getType(),
                        ctx.getStart().getLine()));
            }
        } else if (atom1.getType() == VarType.DOUBLE) {
            if (atom2.getType() == VarType.DOUBLE) {
                compareNumerics(atom1, atom2, ctx.COMPARISON_OPERATOR().getText());
            } else {
                throw new RuntimeException(format("Can not compare %s to %s on line %s.",
                        atom1.getType(),
                        atom2.getType(),
                        ctx.getStart().getLine()));
            }
        }
        return visitChildren(ctx);
    }

    private void compareNumerics(Atom atom1, Atom atom2, String comparisonOperator) {
        if ("<".equals(comparisonOperator)) {
            out.printf(" %s < %s ", getAtomString(atom1), getAtomString(atom2));
        } else if ("<=".equals(comparisonOperator)) {
            out.printf(" %s <= %s ", getAtomString(atom1), getAtomString(atom2));
        } else if ("==".equals(comparisonOperator)) {
            out.printf(" %s == %s ", getAtomString(atom1), getAtomString(atom2));
        } else if ("!=".equals(comparisonOperator)) {
            out.printf(" %s != %s ", getAtomString(atom1), getAtomString(atom2));
        } else if (">=".equals(comparisonOperator)) {
            out.printf(" %s >= %s ", getAtomString(atom1), getAtomString(atom2));
        } else if (">".equals(comparisonOperator)) {
            out.printf(" %s > %s ", getAtomString(atom1), getAtomString(atom2));
        }
    }

    private String getAtomString(Atom atom) {
        if (atom.getType() == VarType.STRING && atom.isLiteral()) {
            return formattedString(atom.getValue());
        }
        return atom.getValue();
    }

    private void compareStrings(Atom atom1, Atom atom2, String comparisonOperator) {
        if ("<".equals(comparisonOperator)) {
            imports.add("java.util.Objects");
            imports.add("scriptism.interpreter.NullSafeStringComparator");
            out.printf(" Objects.compare(%s, %s, NullSafeStringComparator.COMPARATOR) < 0",
                    getAtomString(atom1), getAtomString(atom2));
        } else if ("<=".equals(comparisonOperator)) {
            imports.add("java.util.Objects");
            imports.add("scriptism.interpreter.NullSafeStringComparator");
            out.printf(" Objects.compare(%s, %s, NullSafeStringComparator.COMPARATOR) <= 0",
                    getAtomString(atom1), getAtomString(atom2));
        } else if ("==".equals(comparisonOperator)) {
            imports.add("java.util.Objects");
            out.printf(" Objects.equals(%s, %s) ", getAtomString(atom1), getAtomString(atom2));
        } else if ("!=".equals(comparisonOperator)) {
            imports.add("java.util.Objects");
            out.printf(" !Objects.equals(%s, %s) ", getAtomString(atom1), getAtomString(atom2));
        } else if (">=".equals(comparisonOperator)) {
            imports.add("java.util.Objects");
            imports.add("scriptism.interpreter.NullSafeStringComparator");
            out.printf(" Objects.compare(%s, %s, NullSafeStringComparator.COMPARATOR) >= 0",
                    getAtomString(atom1), getAtomString(atom2));
        } else if (">".equals(comparisonOperator)) {
            imports.add("java.util.Objects");
            imports.add("scriptism.interpreter.NullSafeStringComparator");
            out.printf(" Objects.compare(%s, %s, NullSafeStringComparator.COMPARATOR) > 0",
                    getAtomString(atom1), getAtomString(atom2));
        }
    }

    @Override
    public Integer visitAssignmentStatement(@NotNull ScriptismParser.AssignmentStatementContext ctx) {
        if (ctx.STRING() != null) {
            out.printf("    %s = %s;\n", ctx.IDENTIFIER().getText(), formattedString(ctx.STRING().getText()));
        } else if (ctx.INTEGER() != null) {
            out.printf("    %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.INTEGER().getText());
        } else if (ctx.DOUBLE() != null) {
            out.printf("    %s = %s;\n", ctx.IDENTIFIER().getText(), ctx.DOUBLE().getText());
        }
        return visitChildren(ctx);
    }

    @Override
    public Integer visitVariableDeclarationStatement(ScriptismParser.VariableDeclarationStatementContext ctx) {
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
                out.printf("    String %s = %s;\n", ctx.IDENTIFIER().getText(), formattedString(ctx.STRING().getText()));
            } else {
                throw new RuntimeException(format("The value of the variable '%s' can not be identified.", ctx.IDENTIFIER().getText()));
            }
        }
        return visitChildren(ctx);
    }

    @Override
    public Integer visitPrintStatement(@NotNull ScriptismParser.PrintStatementContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            out.println("    out.print(" + ctx.IDENTIFIER().getText() + ");");
        } else if (ctx.STRING() != null) {
            out.println("    out.print(" + formattedString(ctx.STRING().getText()) + ");");
        } else {
            out.println("    out.print();");
        }
        return visitChildren(ctx);
    }

    @Override
    public Integer visitPrintlnStatement(ScriptismParser.PrintlnStatementContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            out.println("    out.println(" + ctx.IDENTIFIER().getText() + ");");
        } else if (ctx.STRING() != null) {
            out.println("    out.println(" + formattedString(ctx.STRING().getText()) + ");");
        } else {
            out.println("    out.println();");
        }
        return visitChildren(ctx);
    }

    private String formattedString(String text) {
        String escapedString = getEscapedString(text);
        InterpolatedString interpolatedString = getInterpolatedString(escapedString);
        if (interpolatedString.getVariables().size() == 0) {
            return "\"" + escapedString + "\"";
        } else {
            staticImports.add("java.lang.String.format");
            return format("format(\"%s\", %s)",
                    interpolatedString.getText(),
                    StringUtils.join(interpolatedString.getVariables().toArray(), ','));
        }
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
