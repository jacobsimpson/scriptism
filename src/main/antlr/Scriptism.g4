grammar Scriptism;

options
{
    language = Java;
}

@header {
    package scriptism.grammar;
}

program
    :   statements
    ;

statements
    :   (
                COMMENT
            |   printStatement
            |   declarationStatement
            |   assignmentStatement
            |   ifStatement
        )*
    ;

block
    : '{' statements '}'
    ;

booleanExpression
    :   IDENTIFIER '==' IDENTIFIER
    ;

ifExpression
    :   booleanExpression
    ;

ifStatement
    :   'if' ifExpression block
    |   'if' ifExpression block elseStatement
    ;

elseStatement
    :   'else' block
    |   'else' ifStatement
    ;

printStatement
    :   PRINT ( STRING | IDENTIFIER )?
    ;

declarationStatement
    :   VAR IDENTIFIER 'as' TYPE
    |   VAR IDENTIFIER '=' STRING
    |   VAR IDENTIFIER '=' INTEGER
    |   VAR IDENTIFIER '=' DOUBLE
    ;

assignmentStatement
    :   IDENTIFIER '=' STRING
    |   IDENTIFIER '=' INTEGER
    |   IDENTIFIER '=' DOUBLE
    ;

TYPE
    :   'Int'
    |   'String'
    |   'Double'
    ;

PRINT
    :   'print'
    ;

VAR
    :   'var'
    ;

STRING
    :   '"' ~('\"')* '"'
    |   '\'' ~('\'')* '\''
    ;

IDENTIFIER
    :   LETTER (DIGIT | LETTER)*
    ;

INTEGER
    :   DIGIT+
    ;

DOUBLE
    :   '.' DIGIT+
    |   DIGIT+ '.'
    |   DIGIT+ '.' DIGIT+
    ;

COMMENT
    :  '#' ~( '\r' | '\n' )* ( '\r' | '\n' )
    ;

WS  :   [ \t\r\n]+ -> skip
    ;

fragment DIGIT         : '0' .. '9';
fragment LETTER        : 'a' .. 'z' | 'A' .. 'Z';