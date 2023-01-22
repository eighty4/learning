package eighty4.akimbo.expressions;

public abstract class AkimboExpressionValueListener<T> extends AkimboExpressionsParserBaseListener {

    public abstract T buildValue();

}
