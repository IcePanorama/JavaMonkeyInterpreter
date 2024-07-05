package com.monkeyinterpreter;

import repl.REPL;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello! Welcome to the Monkey programming language!");
        System.out.println("Feel free to type in commands.");
        System.out.println("Type .quit to quit.");
        REPL.start();
    }
}
