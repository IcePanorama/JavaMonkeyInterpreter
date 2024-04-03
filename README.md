# Java Monkey Interpreter

A tree-walking interpreter for [the Monkey programming language](https://monkeylang.org/), written in Java.

## Features
+ Read-Eval-Print-Loop (REPL) environment for typing in code
+ Lexer for tokenization
+ Pratt/Recursive descent parser for syntactic analysis.
+ Abstract syntax tree (AST) for code representation and evaluation. 

All of the interpreter's features were created from scratch, without the use of any outside libraries or frameworks.

## Installation & Usage
Installation can be completed by simply cloning the repository:

```bash
git clone https://github.com/IcePanorama/JavaMonkeyInterpreter
```

You can then run the interpreter using the following commands:

```bash
cd JavaMonkeyInterpreter/
java -cp target/classes com.monkeyinterpreter.Main
```

To quit the interpreter, type `.quit`.

## Acknowledgements

This project was developed using the book, ["Writing An Interpreter In Go" by Thorsten Ball](https://interpreterbook.com/). As the name implies, Ball's original project was written entirely in Go and I'm converting everything to Java.
