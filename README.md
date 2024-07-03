# Java Monkey Interpreter

A tree-walking interpreter for [the Monkey programming language](https://monkeylang.org/), written in Java.

As of July 3rd, 2024, the interpreter is fully functional, supporting "mathematical expressions, variable bindings, functions and the application of those functions, conditionals, return statements, and advanced concepts like higher-order functions and closures" (Ball 197) as well as 5 datatypes: integers, booleans, strings, arrays, and hashes.

## Features
+ Read-Eval-Print-Loop (REPL) environment for typing in code
+ Lexer for tokenization
+ Pratt/Recursive descent parser for syntactic analysis.
+ Abstract syntax tree (AST) for code representation and evaluation. 

All of the interpreter's features were created from scratch, without the use of any outside libraries or frameworks.

## Installation & Usage
First, verify that you have both java and maven installed on your machine.

```bash
java --version
mvn --version
```

After that, installation can be completed by simply cloning the repository on to your local machine:

```bash
git clone https://github.com/IcePanorama/JavaMonkeyInterpreter
```

You can then run the interpreter using the following commands:

```bash
cd JavaMonkeyInterpreter/
mvn package
java -cp target/classes com.monkeyinterpreter.Main
```

To quit the interpreter, type `.quit`.

## Acknowledgements

This project was developed using the book, ["Writing An Interpreter In Go" by Thorsten Ball](https://interpreterbook.com/). As the name implies, Ball's original project was written entirely in Go and I converted everything to Java.

As of July 3rd, 2024, I'm finished with the book, although I still plan on cleaning the project up some before I'll truly consider it "complete". After that, I'll likely add some more features to the language on my own before moving onto the Thorsten Ball's next book, ["Writing An Interpreter In Go: The Lost Chapter - A Macro System For Monkey"](https://interpreterbook.com/lost/).