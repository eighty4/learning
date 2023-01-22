package eighty4.akimbo.expressions;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class AkimboExpression {

    public static AkimboExpression fromString(String expression) {
        AkimboExpressionsLexer lexer = new AkimboExpressionsLexer(CharStreams.fromString(expression));
        AkimboExpressionsParser parser = new AkimboExpressionsParser(new CommonTokenStream(lexer));
        return new AkimboExpression(expression, lexer, parser);
    }

    private final String expression;

    private final AkimboExpressionsLexer lexer;

    private final AkimboExpressionsParser parser;

    private AkimboExpression(String expression, AkimboExpressionsLexer lexer, AkimboExpressionsParser parser) {
        this.expression = expression;
        this.lexer = lexer;
        this.parser = parser;
    }

    public <T> T evaluate(AkimboExpressionsParserVisitor<T> visitor) {
        T result = parser.expression().accept(visitor);
        parser.reset();
        return result;
    }

    public void evaluate(AkimboExpressionsParserListener listener) {
        ParseTreeWalker.DEFAULT.walk(listener, parser.expression());
        parser.reset();
    }

    public <T> T evaluate(AkimboExpressionValueListener<T> listener) {
        ParseTreeWalker.DEFAULT.walk(listener, parser.expression());
        parser.reset();
        return listener.buildValue();
    }

    public String getExpression() {
        return expression;
    }

    AkimboExpressionsLexer getLexer() {
        return lexer;
    }

    AkimboExpressionsParser getParser() {
        return parser;
    }

}
