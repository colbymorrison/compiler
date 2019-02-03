package project.Lexer;

public class Token<T> {
    private TokenType type;
    private T value = null;

    public Token(TokenType tokenType){
        this.type = tokenType;
    }

    public Token(TokenType tokenType, T value){
        this.type = tokenType;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String toString(){
        // Make print in same format as testfiles for easy testing
        String val;
        if(value == null){
            val = "None";
        }
        else{
            val = "'"+value+"'";
        }
        return "['"+type+"', "+val+"]";
    }

//    // Method to create new tokens, sets value to right type based on tokentype
//    public static Token createToken(TokenType t){
//       if(t == TokenType.IDENTIFIER)
//           return new Token<String>(t);
//       else if(t == TokenType.RELOP || t == TokenType.MULOP || t == TokenType.ADDOP)
//           return new Token<Integer>(t);
//       else
//           return new Token<>(t);
//    }
}