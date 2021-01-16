import java.lang.*;
import java.util.*;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.HashMap;

class EvalListener extends ExprBaseListener{
// hash-map for variables' integer value for assignment
Map<String, Integer> vars= new HashMap<String, Integer>();

// stack for expression tree evaluation
Stack<Integer> evalStack= new Stack<Integer>();

@Override
public void exitProg(ExprParser.ProgContext ctx) {
	System.out.println("exitProg: "); 	
}


@Override
public void exitExpr(ExprParser.ExprContext ctx) {
	System.out.println("exitExpr: ");
}


@Override
public void visitTerminal(TerminalNode node) {
	System.out.println("Terminal: " + node.getText());
	//coding direction : stack	
	
	}

}

public class ExprEvalApp{
	public static void main(String[] args) throws IOException{
		System.out.println("** Expression Evalw/ antlr-listener **");
		// Get lexer
		ExprLexer lexer= new ExprLexer(CharStreams.fromStream(System.in));
		System.out.println("lexer has been created");				
		// Get a list of matched tokens
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		System.out.println("tokens created");

		// Pass tokens to parser
		ExprParser parser = new ExprParser(tokens);
		System.out.println("parser created");

		// Walk parse-tree and attach our listener
		ParseTreeWalker walker = new ParseTreeWalker();
		EvalListener listener = new EvalListener();
		System.out.println("walker and listener both");

		// walk from the root of parse tree
		walker.walk(listener, parser.prog());

		System.out.println("wow");
	
	}
}

