parser grammar AkimboExpressionsParser;

options { tokenVocab=AkimboExpressionsLexer; }

@parser::members {
    @Override
    public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException e) {
        throw new eighty4.akimbo.expressions.MalformedExpressionException(msg, e);
    }
}

expression
    : ongoingExpression
    | componentReference
    | instanceCreation
    | propertyValue
    | literalValue
    ;

ongoingExpression
    : typeReference methodOrProperty*
    | instanceCreation methodOrProperty*
    | componentReference methodOrProperty*
    ;

methodOrProperty
    : DOT name=IDENTIFIER arguments?
    ;

instanceCreation
    : NEW_KEYWORD typeReference arguments
    ;

componentReference
    : COMP_REF name=IDENTIFIER
    ;

typeReference
    : akimboTypeReference
    | userTypeReference
    ;

akimboTypeReference
    : AKIMBO_TYPE_REF name=packageQualifiedTypeName
    ;

userTypeReference
    : TYPE_REF name=packageQualifiedTypeName
    ;

packageQualifiedTypeName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

arguments
    : LPAREN argumentList? RPAREN
    ;

argumentList
    : expression (COMMA expression)*
    ;

propertyValue
    : PROPERTY_VALUE
    | ENV_VAR_VALUE
    ;

literalValue
    : stringValue
    | numberValue
    | booleanValue
    | nullValue
    ;

numberValue
    : NUMBER
    ;

stringValue
    : STRING_CONTENT
    ;

booleanValue
    : TRUE_LITERAL
    | FALSE_LITERAL
    ;

nullValue
    : NULL_LITERAL
    ;
