package parser;

import ast.Expression;

interface InfixParseFn {
    Expression call(Expression expression);
}
