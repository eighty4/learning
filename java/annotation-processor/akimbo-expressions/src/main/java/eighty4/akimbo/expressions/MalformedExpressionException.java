package eighty4.akimbo.expressions;

import org.antlr.v4.runtime.RecognitionException;

public class MalformedExpressionException extends RuntimeException {

    public MalformedExpressionException(String message, RecognitionException e) {
        super(message, e);
    }
}
