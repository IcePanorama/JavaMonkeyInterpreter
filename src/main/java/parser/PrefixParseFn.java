package parser;

import ast.Expression;

interface PrefixParseFn {
    Expression call();
}
