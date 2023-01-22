lexer grammar AkimboExpressionsLexer;

@lexer::members {
    @Override
    public void recover(RecognitionException e) {
        throw new eighty4.akimbo.expressions.MalformedExpressionException(e.getMessage(), e);
    }
}

COMP_REF:          '#';
fragment PropRef:  '$';
TYPE_REF:          '@';
AKIMBO_TYPE_REF:   '@@';

NEW_KEYWORD:       'new';

DOT:               '.';
COMMA:             ',';
LPAREN:            '(';
RPAREN:            ')';
LBRACKET:          '{';
RBRACKET:          '}';
SQUOTE:            '\'';
DQUOTE:            '"';

TRUE_LITERAL:      'true';
FALSE_LITERAL:     'false';
NULL_LITERAL:      'null';

IDENTIFIER:        Letter LetterOrDigit*;

PROPERTY_VALUE:    PropRef LBRACKET PropKey RBRACKET;
ENV_VAR_VALUE:     PropRef LBRACKET EnvVarKey RBRACKET;

fragment PropKey
    : [a-z] [a-z0-9\\-\\.]*;

fragment EnvVarKey
    : [A-Z] [A-Z0-9_]*;

NUMBER:            Digit+;

STRING_CONTENT:    SQUOTE ( Escape | ~[\\"\r\n] )* SQUOTE;

WS: [ \t\n\r] + -> skip;

fragment LetterOrDigit
    : Letter
    | Digit
    ;

fragment Digit
    : [0-9]
    ;

fragment Letter
    : [a-zA-Z$_]
    | ~[\u0000-\u007F\uD800-\uDBFF]
    | [\uD800-\uDBFF] [\uDC00-\uDFFF]
    ;

fragment String
    : ( Escape | . )
    ;

fragment Escape
    : '\\' ( '\'' | '\\' )
    ;