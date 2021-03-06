package compiler.Parser;

import compiler.Exception.*;
import compiler.Lexer.Lexer;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;
import compiler.SemanticAction.SemanticAction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class implements a Top-Down parser for the Vascal Language.
 * It uses tokens created from a Lexer and calls the appropriate Semantic Actions.
 */
public class Parser
{
    private final String EPSILON = "*E*";
    private final List<Token> errors = new ArrayList<>(); // List of error messages found during parsing
    private final List<String> productions = new ArrayList<>();
    private final HashMap<List<String>, Integer> parseTbl = new HashMap<>();
    private final Stack<String> stack = new Stack<>();
    private final SemanticAction action = new SemanticAction();
    private final boolean debug;
    private final Lexer lexer;
    private Token prevToken;

    /**
     * Constructor for the Parser
     *
     * @param lexer the lexer which the parser will get tokens from
     * @param debug weather to print debug information at each token or not
     */
    public Parser(Lexer lexer, boolean debug)
    {
        this.lexer = lexer;
        this.debug = debug;
        stack.push(TokenType.ENDOFFILE.toString());
        stack.push("<Goal>");
        try
        {
            InitTables();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Implements the parsing algorithm which ensures that the stream of tokens
     * Conforms to the grammar for the Vascal language.
     *
     * @throws ParserError when we've reached the end of the file and if any errors occured.
     */
    public String Parse() throws LexerError, ParserError, SymbolTableError, SemanticError
    {
        Token input = lexer.GetNextToken();

        // If the next token is EOF, stop
        while (!stack.isEmpty())
        {
            // Get the token's type as a string as the stack is of Strings
            String inType = input.GetType().name();
            String top = stack.pop();
            // If the top of the stack a terminal (i.e. it matches a TokenType value),
            // then the input token must be that terminal
            if (Stream.of(TokenType.values()).anyMatch(x -> x.name().equals(top)))
            {
                if (!inType.equals(top))
                    PanicMode();
                else if (debug)
                    DumpStack(top, input, "");
                // If the stack is empty after matching, we're done
                if (stack.isEmpty())
                    break;
                prevToken = input;
                input = lexer.GetNextToken();
                // Top of the stack is a non-terminal
            }
            else if (top.charAt(0) == '<')
            {
                List<String> pair = Arrays.asList(top, inType);
                // Is the <top of stack, input token> pair in the parse table?
                if (parseTbl.containsKey(pair))
                {
                    // The index of the production rule is the entry in the parse table for the pair
                    Integer prodIdx = parseTbl.get(pair);
                    // Check for epsilons
                    if (prodIdx < 0)
                        continue;
                    // Get the production rule
                    String[] rules = productions.get(prodIdx).split(" ");
                    if (!rules[0].equals(EPSILON))
                    {
                        // Push rules in reverse order
                        for (int i = rules.length - 1; i >= 0; i--)
                            stack.push(rules[i]);
                        if (debug)
                            DumpStack(top, input, Arrays.toString(rules));
                    }
                }
                else
                {
                    PanicMode();
                    input = lexer.GetNextToken();
                }
            }
            // Otherwise its a semantic action
            else
            {
                action.Execute(Integer.parseInt(top.substring(1)), input, prevToken);
                if (debug)
                    DumpStack(top, input, "");
            }
        }
        // If there were errors during parsing, throw them
        if (!errors.isEmpty())
            ThrowErrors();
        // Otherwise get the generated intermediate code
        return action.GetInterCode();
    }

    /**
     * Implements "Panic Mode" error recovery, which skips to the next semicolon
     * In the event of an error.
     *
     * @throws ParserError if the end of file is reached
     */
    private void PanicMode() throws LexerError, ParserError
    {
        // Add the row and col of previous token to the list of error tokens
        errors.add(prevToken);

        Token token;
        // Get tokens until a semicolon or the end of the file is reached
        do
        {
            token = lexer.GetNextToken();
        } while (token.GetType() != TokenType.SEMICOLON && token.GetType() != TokenType.ENDOFFILE);

        // If its the end of the file, nothing we can do, throw the errors
        if (token.GetType() == TokenType.ENDOFFILE)
            ThrowErrors();

            // Otherwise, continue parsing by adding correct rules back to stack
        else
        {
            while (!stack.isEmpty() && !stack.peek().equals("<statement-list-tail>"))
                stack.pop();

            stack.push("SEMICOLON");
            stack.push("<statement>");
        }
    }

    /**
     * Reads the parseTable and grammar from disk
     */
    private void InitTables() throws IOException
    {
        Path path = Paths.get("src", "main", "resources");
        productions.add(null);
        // Read the grammar, empty lines are epsilons
        try (Stream<String> stream = Files.lines(path.resolve("grammar_aug.txt")))
        {
            stream.forEach(line ->
            {
                if (line.isEmpty())
                    productions.add(EPSILON);
                else
                    productions.add(line);
            });
        }

        // Read the parseTable
        try (BufferedReader reader = new BufferedReader(new FileReader(path.resolve("parsetable.txt").toString())))
        {
            // The first line is the non terminals
            String line = reader.readLine();
            String[] nTerms = line.split(",");

            while ((line = reader.readLine()) != null)
            {
                // For each line, the terminal is the first column
                String[] idxs = line.split(",");
                String term = idxs[0];
                // Read through the values (indexes into productions array) on that line
                for (int j = 1; j < idxs.length; j++)
                {
                    int idx = Integer.parseInt(idxs[j]);
                    // For the non-error values, map the <nonTerminal,terminal> pair to the production index
                    if (idx != 999)
                        parseTbl.put(Arrays.asList(nTerms[j], term.toUpperCase()), idx);
                }
            }
        }
    }

    /**
     * Throws list of errors
     */
    private void ThrowErrors() throws ParserError
    {
        StringBuilder builder = new StringBuilder("\n");
        for (Token error : errors)
        {
            builder.append("Syntax error");
            builder.append(" at line ").append(error.GetRow()).append(" column ").append(error.GetCol());
        }
        throw new ParserError(builder.toString());
    }

    /**
     * Print out relevant debug info
     *
     * @param top   top of the stack
     * @param token token from the lexer
     * @param push  what (if anything) to push onto stack
     */
    private void DumpStack(String top, Token token, String push)
    {
        String out = "";
        out += "Popped " + top + " with token ";
        TokenType type = token.GetType();
        if (type == TokenType.IDENTIFIER)
            out += type + ", " + token.GetValue().toString().toLowerCase();
        else
            out += type;
        out += " at " + token.GetRow() + ":" + token.GetCol() + "\n";
        if (push.isEmpty())
        {
            if (top.charAt(0) == '#')
                out += "Semantic Action " + top.substring(1) + "\n" +
                        "Semantic Stack " + action.GetStack() + "\n";
            else
                out += "Match! \n";
        }
        else
            out += "Pushing " + push + " \n";
        out += "Stack: " + stack + "\n";
        System.out.print(out);
        System.out.println("Global Table: " + action.GetGlobalTable());
        System.out.println("Local Table: " + action.GetLocalTable());
        System.out.println("Constant Table: " + action.GetConstantTable() + "\n");
    }
}
