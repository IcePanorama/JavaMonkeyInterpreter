package repl;

import java.util.Scanner;
import lexer.Lexer;
import token.Token;

public class REPL {
    final static String PROMPT = ">> ";

    public static void Start(){
        Scanner scnr = new Scanner(System.in);

        while (true) {
            System.out.print(PROMPT);
            String line = scnr.nextLine();
            if (line.equals(".quit")) {
                break;
            }

            Lexer l = new Lexer(line);

            for (var tok = l.nextToken(); !tok.type.equals(Token.EOF);
                 tok = l.nextToken()) {
                    System.out.printf("%s\n", tok.toString());
            }
        }

        scnr.close();
    }
}
