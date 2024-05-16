package repl;

import java.util.ArrayList;
import java.util.Scanner;

import ast.Program;
import evaluator.Evaluator;
import lexer.Lexer;
import parser.Parser;

public class REPL {
    final static String PROMPT = ">> ";
    final static String MONKEY_FACE = "            __,__\n" +
    "   .--.  .-\"     \"-.  .--.\n" +
    "  / .. \\/  .-. .-.  \\/ .. \\\n" +
    " | |  '|  /   Y   \\  |'  | |\n" +
    " | \\   \\  \\ 0 | 0 /  /   / |\n" +
    "  \\ '- ,\\.-\"\"\"\"\"\"\"-./, -' /\n" +
    "   ''-' /_   ^ ^   _\\ '-''\n" +
    "       |  \\._   _./  |\n" +
    "       \\   \\ \'~\' /   /\n" +
    "        \'._ '-=-' _.'\n" +
    "           '-----'\n";

    private static void printParserErrors(ArrayList<String> errors) {
        System.out.print(MONKEY_FACE);
        System.out.println("Whoops! We ran into some monkey business here!");
        System.out.println(" parser errors:");
        for (String msg : errors) {
            System.out.printf("\t%s\n", msg);
        }
    }

    public static void Start(){
        Scanner scnr = new Scanner(System.in);

        while (true) {
            System.out.print(PROMPT);
            String line = scnr.nextLine();
            if (line.equals(".quit")) {
                break;
            }

            Lexer l = new Lexer(line);
            Parser p = new Parser(l);
            Program prog = p.parseProgram();
            if (p.errors.size() > 0) {
                printParserErrors(p.errors);
            }

            var evaluated = Evaluator.Eval(prog);
            if (evaluated != null) {
                System.out.printf("%s\n", evaluated.Inspect());
            }
        }

        scnr.close();
    }
}
