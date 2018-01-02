import com.patrickfeltes.interpreter.ast.Expr;
import com.patrickfeltes.interpreter.tokens.Token;
import com.patrickfeltes.interpreter.tokens.TokenType;

public class AstPrinter implements Expr.Visitor<String> {

    public static void main(String[] args) {
        Expr expression = new Expr.Unary(new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Binary(new Expr.Literal(123), new Token(TokenType.MUL, "*", null, 1), new Expr.Literal(123)));
        System.out.println(new AstPrinter().printAst(expression));
    }

    public String printAst(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return "(" + expr.operator.lexeme + " " + expr.left.accept(this) + " " + expr.right.accept(this) + ")";
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return "(" + "grouping" + expr.accept(this) + ")";
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return "(" + expr.operator.lexeme + " " + expr.right.accept(this);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return null;
    }
}
