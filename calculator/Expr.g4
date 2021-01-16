grammar Expr;

prog : (assn ';' NEWLINE? | expr ';' NEWLINE?)*;
expr : expr ('-'|'+') expr | expr ('*'|'/') expr | '('expr')' | num | ID;

assn : ID '=' num;

num : ('+'|'-') num | unsigned_num;
unsigned_num : INT | REAL;
NEWLINE : [\r\n]+ -> skip;
INT : [0-9]+;
REAL : [0-9]+'.'[0-9]*;
ID : [a-zA-Z]+ ;
WS : [ \t\r\n]+ -> skip ;
