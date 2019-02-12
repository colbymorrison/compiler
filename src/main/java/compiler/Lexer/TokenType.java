package compiler.Lexer;

/**
 * Enum of possible TokenTypes
 */
public enum TokenType {
    PROGRAM, RESULT, IF, INTCONSTANT, COMMA, LBRACKET,
    BEGIN, THEN, REALCONSTANT, SEMICOLON, RBRACKET,
    END, REAL, ELSE, RELOP, COLON, UNARYMINUS, INTEGER,
    VAR, ARRAY, WHILE, MULOP, LPAREN, UNARYPLUS,
    FUNCTION, OF, DO, ADDOP, RPAREN, ENDMARKER,
    PROCEDURE, NOT, IDENTIFIER, ASSIGNOP, DOUBLEDOT, ENDOFFILE,
}