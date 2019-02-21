package compiler.Parser;

import compiler.Exception.CompilerError;
import compiler.Exception.ParserError;
import compiler.Lexer.Lexer;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Parser {
    private final String EPSILON = "*E*";
    private final List<Token> errors = new ArrayList<>();
    private final List<String> productions = new ArrayList<>();
    private final HashMap<List<String>, Integer> parseTbl = new HashMap<>();
    private final Stack<String> stack = new Stack<>();
    private final boolean debug;
    private final Lexer lexer;

    /**
     * Constructor for the Parser
     *
     * @param lexer the lexer which the parser will get tokens from
     * @param debug weather to print out stack contents at each token or not
     */
    public Parser(Lexer lexer, boolean debug) {
        this.lexer = lexer;
        this.debug = debug;
        stack.push(TokenType.ENDOFFILE.toString());
        stack.push("<Goal>");
        try {
            initTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Implements the parsing algorithm which ensures that the stream of tokens
     * Conforms to the grammar for the Vascal language.
     *
     * @throws CompilerError
     */
    public void parse() throws CompilerError {
        Token input = lexer.getNextToken();
        // Get the next token, if it's EOF, stop
        while (input.getType() != TokenType.ENDOFFILE) {
            // Get the type as a string as the stack is of Strings
            String inType = input.getType().name();
            String top = stack.pop();
            // If the top of the stack a terminal (i.e. it matches a TokenType value),
            // then the input token must be that terminal
            if (Stream.of(TokenType.values()).anyMatch(x -> x.name().equals(top))) {
                if (!inType.equals(top))
                    panicMode(input);
                else if (debug)
                    dumpStack(top, input, "");
                input = lexer.getNextToken();
            } else {
                // If it's a non-terminal, consult the parse table
                List<String> pair = Arrays.asList(top, inType);
                // Is the token, non-terminal pair in the parse table?
                if (parseTbl.containsKey(pair)) {
                    // The index of the production rule is the entry in the parse table
                    Integer prodIdx = parseTbl.get(pair);
                    // Check for epsilons
                    if (prodIdx < 0)
                        continue;
                    // Get the production rule
                    String[] rules = productions.get(prodIdx).split(" ");
                    if (!rules[0].equals(EPSILON)) {
                        // Push rules in reverse order
                        for (int i = rules.length - 1; i >= 0; i--)
                            stack.push(rules[i]);
                        if (debug)
                            dumpStack(top, input, Arrays.toString(rules));
                    }
                } else {
                    panicMode(input);
                    input = lexer.getNextToken();
                }
            }
        }
        // If there were errors during parsing, throw them
        if (!errors.isEmpty())
            throw new ParserError(errors);
    }

    /**
     * Implements "Panic Mode" error recovery, which skips to the next semicolon
     * In the event of an error.
     *
     * @param token the token that did not match the grammar
     * @throws ParserError
     */
    private void panicMode(Token token) throws ParserError {
        // Add the token to the list of error token
        errors.add(token);

        // Get tokens until a semicolon or the end of the file is reached
        do {
            token = lexer.getNextToken();
        } while (token.getType() != TokenType.SEMICOLON && token.getType() != TokenType.ENDOFFILE);

        // If its the end of the file, nothing we can do, throw the errors
        if (token.getType() == TokenType.ENDOFFILE)
            throw new ParserError(errors);

            // Otherwise, continue parsing by adding correct rules back to stack
        else {
            while (!stack.isEmpty() && !stack.peek().equals("<statement-list-tail>"))
                stack.pop();

            stack.push("SEMICOLON");
            stack.push("<statement>");
        }
    }

    /**
     * Reads the parseTable and grammar from disk
     *
     * @throws IOException
     */
    private void initTables() throws IOException {
        Path path = Paths.get("src", "main", "resources");
        productions.add(null);
        // Read the grammar, empty lines are epsilons
        try (Stream<String> stream = Files.lines(path.resolve("grammar.txt"))) {
            stream.forEach(line -> {
                if (line.isEmpty())
                    productions.add(EPSILON);
                else
                    productions.add(line);
            });
        }

        // Read the parseTable
        try (BufferedReader reader = new BufferedReader(new FileReader(path.resolve("parsetable.txt").toString()))) {
            // The first line is the non terminals
            String line = reader.readLine();
            String[] nTerms = line.split(",");

            while ((line = reader.readLine()) != null) {
                // For each line, the terminal is the first column
                String[] idxs = line.split(",");
                String term = idxs[0];
                // Read through the values (indexes into productions array) on that line
                for (int j = 1; j < idxs.length; j++) {
                    int idx = Integer.parseInt(idxs[j]);
                    // For the non-error values, add to the parseTable the nonTerminal,terminal pair
                    // Mapping to the index
                    if (idx != 999) {
                        parseTbl.put(Arrays.asList(nTerms[j], term.toUpperCase()), idx);
                    }
                }
            }
        }
    }

    /**
     * Print out relevant debug info
     *
     * @param top   top of the stack
     * @param token that caused the exception
     * @param push  what (if anything) to push onto stack
     */
    private void dumpStack(String top, Token token, String push) {
        String out = "";
        out += "Stack: " + stack + "\nPopped " + top + " with token " + token.getType() +
                "at " + token.getCol() + ":" + token.getRow() + "\n";
        if (push.isEmpty())
            out += "Match! \n";
        else
            out += "Pushing " + push + " \n";
        System.out.println(out);
    }

}
