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
            |   printlnStatement
            |   declarationStatement
            |   assignmentStatement
            |   ifStatement
        )*
    ;

block
    : '{' statements '}'
    ;

booleanExpression
    :   atom COMPARISON_OPERATOR atom
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

printlnStatement
    :   PRINTLN ( STRING | IDENTIFIER )?
    ;

declarationStatement
    :   VAR variableDeclarationStatement (',' variableDeclarationStatement)*
    ;

variableDeclarationStatement
    :   IDENTIFIER 'as' TYPE
    |   IDENTIFIER '=' STRING
    |   IDENTIFIER '=' INTEGER
    |   IDENTIFIER '=' DOUBLE
    ;

assignmentStatement
    :   IDENTIFIER '=' STRING
    |   IDENTIFIER '=' INTEGER
    |   IDENTIFIER '=' DOUBLE
    ;

atom
    :   IDENTIFIER
    |   STRING
    |   INTEGER
    |   DOUBLE
    ;

TYPE
    :   'Int'
    |   'String'
    |   'Double'
    ;

PRINT
    :   'print'
    ;

PRINTLN
    :   'println'
    ;

VAR
    :   'var'
    ;

STRING
    :   '"' ~('\"')* '"'
    |   '\'' ~('\'')* '\''
    ;

COMPARISON_OPERATOR
    :   ( '<' | '<=' | '==' | '!=' | '>=' | '>' )
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