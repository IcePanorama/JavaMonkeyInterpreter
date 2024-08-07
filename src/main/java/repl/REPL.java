package repl;

import java.util.ArrayList;
import java.util.Scanner;

import ast.Program;
import evaluator.Evaluator;
import lexer.Lexer;
import monkeyobject.Environment;
import monkeyobject.MonkeyObject;
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

    // Should this just be main?
    public static void start(){
        Scanner scnr = new Scanner(System.in);
        Environment env = new Environment();

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

            MonkeyObject evaluated = Evaluator.eval(prog, env);
            if (evaluated != null) {
                System.out.printf("%s\n", evaluated.Inspect());
            }
        }

        scnr.close();
    }
}
