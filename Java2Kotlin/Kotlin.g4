grammar Kotlin;

//PARSING RULES
start : pre topLevel* end;

topLevel : method | classDeclare | COMMENT;
end : ;
//METHOD
method : modifier? FUN methodName parameters (COLON dataType returnTypeCheck?)? (body | implicitBody) methodEnd;
methodEnd : ;

//method rules
body : LEFTCURLY block RIGHTCURLY;
implicitBody : ASSNEQUAL elseBody; 
elseBody : (assn | expr | declaration | COMMENT | returnStmt | breakStmt | methodCall | objCall)+;
block : (assn | expr | declaration | COMMENT | returnStmt | breakStmt | method)+;
forBlock : (assn | declaration | method)+;
breakStmt : BREAK;//method eliminated from block
returnTypeCheck : '?';

//MODIFIER
modifier : ABSTRACT | OVERRIDE | PRIVATE | PUBLIC | PROTECTED;


//ASSIGNMENT
//stmt : assn;
assn : (VAL|VAR) varName COLON dataType methodCall? ASSNEQUAL (num | methodCall | objCall | booleanExpr)
	| varName assnOperator (num | methodCall) 
	| (VAL|VAR) varName ASSNEQUAL (num | methodCall | paramOperations) 
	| varName assnOperator (varName | paramOperations)
	| (VAL | VAR) varName ASSNEQUAL (LISTOF | SETOF) LEFTROUND listElem RIGHTROUND assnEnd
	| variable
	| modifier? (VAL | VAR) methodName COLON dataType methodCall ASSNEQUAL (varName | objCall | methodCall | booleanExpr)
	;//check this. declaring and then RE-ASSIGNING

assnEnd : ;

//IMPORT	
pre : ((PACKAGE | IMPORT) string)*;

listElem : stringy (COMMA stringy)* | varName (COMMA varName)*;
//assnFuncValue : call 


//METHOD PARTS(PARAMETERS)
parameters : LEFTROUND (param?) RIGHTROUND;
param :  finalParam(COMMA finalParam)*;
finalParam : varName COLON dataType;


//EXPRESSIONS
expr : conditionExpr | loopExpr | whenExpr | paramOperations | collections;
loopExpr : forLoop | whileLoop;
booleanExpr : (varName | objCall | methodCall) compareOperator (varName | objCall | methodCall);


//WHEN EXPRESSION
whenExpr : whenSwitch | whenList;
whenList : WHEN LEFTCURLY (STRINGTRY IN varName '->' block)* RIGHTCURLY;
whenSwitch : WHEN LEFTROUND varName RIGHTROUND LEFTCURLY (whenBody)* RIGHTCURLY whenEnd;
whenEnd : ;



whenBody : (num | STRINGTRY | IS (dataType | string*) | NOT_IS (dataType | string*) | ELSE)  '->' whenBodyResult;
whenBodyResult : STRINGTRY | num | returnStmt;


//LOOP EXPRESSION
//forLoop : FOR LEFTROUND (forCondition | forRange) RIGHTROUND LEFTCURLY (block)? RIGHTCURLY forEnd;
forLoop : FOR LEFTROUND (forCondition | forRange) RIGHTROUND LEFTCURLY (forBlock)? RIGHTCURLY forEnd;


forEnd : ;

forCondition : (varName | objCall) IN (varName | objCall);
forRange : variable IN num ('..' | DOWNTO) num (STEP num)? | ;
whileLoop : WHILE LEFTROUND whileCondition RIGHTROUND LEFTCURLY (block)? RIGHTCURLY;
whileCondition : ((variable | num | objCall) compareOperator (variable | num | objCall)) | (TRUE | FALSE | num | variable | NULL)+;


//IF CONDITION EXPRESSION
conditionExpr :ifCondition | elseCondition;
ifCondition : IF LEFTROUND manyConditions RIGHTROUND
		LEFTCURLY? (block)? RIGHTCURLY?;

manyConditions : condition(conditionOperator condition)*
		|(NOT | TRUE | FALSE | num | variable | NULL)+ 
		| range 
		;
elseCondition : ELSE (LEFTCURLY)? (elseBody)?(RIGHTCURLY)?;

condition : (LEFTROUND)? (variable | num | objCall) compareOperator? (variable | num | objCall) (RIGHTROUND)? | typeCheck;
conditionOperator : (AND | OR);

//RETURN STMT
//returnStmt : RETURN (variable | NULL | objCall | methodCall | paramOperations)*;


//CALL
objCall : varName '.' (methodCall | varName);


//TYPECHECK
typeCheck : (varName (IS | NOT_IS) dataType);

//RANGES
range : (num | string) (IN | NOTIN) (num | operators | string)+ RANGING (num | operators | string)+ | string+ NOTIN string+;
operators : PLUS | MINUS | DIV | MUL | DPLUS | DMINUS;

//COLLECTIONS
collections : varName collectionBody+;
collectionBody : DOT methodName LEFTCURLY (varName | objCall | methodCall) RIGHTCURLY;

//COMMENT
COMMENT : ('//'~('\r'|'\n')* '\r'? '\n'
	| '/*' .*? '*/');//->skip;//-> channel(HIDDEN);


//CLASS
classDeclare : modifier? (CLASS | INTERFACE) methodName LEFTROUND? classFinalParams* RIGHTROUND? implement? LEFTCURLY classBody RIGHTCURLY;

classParams : (VAL | VAR) varName (COLON varName '<' dataType '>') | (VAL | VAR)varName COLON dataType;
classFinalParams : classParams (COMMA classParams)* | COMMENT;
classBody : block;
implement : COLON implementElem(COMMA implementElem)*;
implementElem : (varName | LISTOF) (LEFTROUND classConstructorParam(COMMA classConstructorParam)*  RIGHTROUND)?;
classConstructorParam : varName | methodCall | objCall;


//DECLARATION
//METHODCALL
declaration : methodCall | objCall | methodDeclare | variableDeclare;//methodDeclaration is different from method. no curly brackets


methodCall : methodName LEFTROUND callBodyStart methodCallBody callBodyEnd RIGHTROUND methodCallEnd
	| LISTOF manyString
;//CAN I USE THE SAME RULE?
manyString : stringy (COMMA stringy)*;
methodCallEnd : ;
callBodyStart : ;
callBodyEnd : ;

methodCallBody : callParam | stringy | | paramOperations | many?;
many : (STRINGTRY | methodCall)(COMMA (STRINGTRY | methodCall))*;
methodDeclare : modifier? FUN methodName LEFTROUND RIGHTROUND COLON dataType;
variableDeclare : (VAL | VAR) varName COLON dataType;
stringy : STRINGTRY;

//OPERATIONS
paramOperations : LEFTROUND? paramOperationLayer RIGHTROUND?;
paramOperationLayer : LEFTROUND? (varName | num | objCall | methodCall) simpleOperators (varName | num | objCall | methodCall | paramOperations) RIGHTROUND?
	| LEFTROUND? (DPLUS | DMINUS)? varName (DPLUS | DMINUS)?;

callParam : (varName|num | objCall)(COMMA (varName|num | objCall))*;
STRINGTRY : QUOTE .*? QUOTE;
simpleOperators : PLUS | MINUS | DIV | MUL;


//RETURN
returnStmt : RETURN (variable | NULL | objCall | STRINGTRY)?;

variable : (DPLUS | DMINUS)? (ID | num | '_' | NULL)+ (DPLUS | DMINUS)?;
dataType : ID | NULL;
varName : (DPLUS | DMINUS)? (ID | num | '_' | NULL)+ (DPLUS | DMINUS)?;
methodName : (ID | num | '_')+;
string : (ID | num | '_' | '.' | '!' | '$' | '\'' | '*')+;
//realString : string(WS string)*;
assnOperator : ASSNEQUAL | PLUSEQUAL | MINUSEQUAL | DIVEQUAL | MULEQUAL;
compareOperator : EQUAL | NOTEQUAL | BIGGER | SMALLER | EQBIGGER | EQSMALLER;
//variable declaration

//from here is the keywords
FUN : 'fun';
CLASS : 'class';
INTERFACE : 'interface';
IMPORT : 'import';
PACKAGE : 'package';
VAR : 'var';
VAL : 'val';
LISTOF : 'listOf';
SETOF : 'setOf';
LEFTCURLY : '{';
RIGHTCURLY : '}';
LEFTROUND : '(';
RIGHTROUND : ')';

//LOOP
FOR : 'for';
WHILE : 'while';

IN :'in';
NOTIN : '!in';
WHEN : 'when';
STEP : 'step';
DOWNTO : 'downTo';
NULL : 'null';
IS : 'is';
NOT_IS : '!is';
RETURN : 'return';

IF : 'if';
ELSE : 'else';
BREAK : 'break';

COMMA : ',';
DOT : '.';
COLON : ':';
SEMICOLON : ';';
QUOTE : '"';
//SQUOTE : ''';
RANGING : '..';

//OPERATORS
PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
DPLUS : '++';
DMINUS : '--';
PLUSEQUAL : '+=';
MINUSEQUAL : '-=';
MULEQUAL : '*=';
DIVEQUAL : '/=';
EQUAL : '==';
NOTEQUAL : '!=';
BIGGER : '>';
SMALLER : '<';
EQBIGGER : '>=';
EQSMALLER : '<=';
ASSNEQUAL : '=';
AND : '&&';
OR : '||';
NOT : '!';
TRUE : 'true';
FALSE : 'false';


//access moidifiers
PUBLIC : 'public';
PRIVATE : 'private';
PROTECTED : 'protected';

//other modifiers
OVERRIDE : 'override';
ABSTRACT : 'abstract';
                        


num : ('+'|'-') num | unsigned_num;
unsigned_num : INT | REAL;
ID : [a-zA-Z]+;

INT : [0-9]+;
REAL : [0-9]+('.'[0-9]+)*;
WS : [ \t\r\n]+ -> skip;
NEWLINE : [\r\n]+ -> skip;
