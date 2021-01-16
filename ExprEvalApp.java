import java.lang.*;
import java.util.*;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
//import java.util.HashMap;


class EvalListener extends ExprBaseListener{
public int assnCheck = 0;
public String var = "";
public double val = 0;
public int cnt = 1;
public int exprCheck = 0;
public int ready = 0;
public int numCheck = 0;
public String numAppend = "";
public int numCk = 0;
public int assnNum = 0;

// hash-map for variables' integer value for assignment
HashMap<String, Double> map = new HashMap<String, Double>();
HashMap<String, Integer> prior = new HashMap<String,Integer>();

// stack for expression tree evaluation
Stack<String> evalStack= new Stack<String>();
Stack<String> stack1 = new Stack<String>();

LinkedList<String> list = new LinkedList<String>();
LinkedList<String> postfix = new LinkedList<String>();
LinkedList<String> last = new LinkedList<String>();


@Override
public void enterExpr(ExprParser.ExprContext ctx){
	exprCheck = 1;
	//checking if it is either assn statement or expr statement
}

@Override
public void exitExpr(ExprParser.ExprContext ctx) {
	
//	exprCheck = 0;
}

@Override public void enterAssn(ExprParser.AssnContext ctx){
	assnCheck = 1;//when entering Assn, change the 'Check' variable value

}

@Override
public void exitAssn(ExprParser.AssnContext ctx){
	assnCheck = 0;//when exitting Assn, init to the original values
	var = "";
	val = 0;
	assnNum = 0;
	cnt = 1;
}
@Override
public void enterNum(ExprParser.NumContext ctx){
	numCheck = 1;
	numCk++;
	if((assnNum == 0)&&(assnCheck == 1)){
		assnNum = 1;
	}

	if((assnCheck==1)&&(cnt==3)){
		val = Double.parseDouble(ctx.getText());
		map.put(var,val);
		cnt++;
	}
	
}
@Override
public void exitNum(ExprParser.NumContext ctx){
	numAppend = ctx.getText();
	if((numCk==0)&&(assnCheck!=1)){
		evalStack.push(numAppend);
	}
	numCk=0;
}
@Override
public void exitUnsigned_num(ExprParser.Unsigned_numContext ctx){
	numCheck = 0;
	if((numCk==1)&&(assnCheck!=1)){
		evalStack.push(ctx.getText());	
	}
}

//////////////////////
//function deciding the priority of the operator
public void calculator(){
	String backup = "";
	int oprBackup = 0;
	
	int takeTurn = 0;
	String lastStr = "";
	int listSize = 0;
	String operator = "";
	int result = 0;
	int idx = 0;
	String postBackup = "";
	int count = 0;
	int lastCheck = 0;

	prior.put("+",1);prior.put("-",1);
	prior.put("*",2);prior.put("/",2);
	prior.put("(",3);prior.put(")",4);

	while(!stack1.empty()){
		backup = stack1.pop();
		if(prior.get(backup)!=null){
			//operator
			oprBackup = prior.get(backup);
			//calculating rules
			//'(' >> push in stack
			if(oprBackup==3){
				list.add(backup);
			}
			//')' >> pop till meeting '('
			if(oprBackup==4){
				listSize = list.size();
				while(prior.get(list.peekLast())!=3){
					postfix.add(list.removeLast());
				
				}
				list.removeLast();
			}
			// + - * / >> comparing the priorities
			if(oprBackup==2){
				list.add(backup);
				takeTurn = 0;
			}
			if(oprBackup==1){
				if(!list.isEmpty()){
					if(prior.get(list.peekLast())==2){
						//URGENT!!WHAT IF THERE ARE TWO LEVEL 2 OPERATIONS?
						postfix.add(list.removeLast());
						list.add(backup);
					}
					else{
						list.add(backup);
					}
				}
				if(list.isEmpty()){
					list.add(backup);
				}
				takeTurn = 0;
			}	
			//numbers going into the POSTFIX directly

		}
		if(prior.get(backup)==null){
			if(takeTurn == 0){
				//numbers
				postfix.add(backup);
				takeTurn = 1;
			}
		}

	}
	//out of while point
	while(!list.isEmpty()){
		postfix.add(list.removeLast());
		count = 1;
	}
	if(count ==1){
		while(!postfix.isEmpty()){
			last.add(postfix.remove());
		}
		
	}
	
/////////////ACTUAL CALCULATING
	while(last.size()>1){
		for(int i = 0; i<last.size();i++){
			if(prior.get(last.get(i))!=null){	
				switch (last.get(i)){
				case "*":
                                        postBackup = Double.toString(Double.parseDouble(last.get(i-2))*Double.parseDouble(last.get(i-1)));
                                        last.remove(i-2);
                                        last.remove(i-2);
                                        last.remove(i-2);
			                last.add(i-2,postBackup);
					lastCheck=1;
					i = 0;
					break;
				
				case "/":
                                        postBackup = Double.toString(Double.parseDouble(last.get(i-2))/Double.parseDouble(last.get(i-1)));
                                        last.remove(i-2);
                                        last.remove(i-2);
                                        last.remove(i-2);
			                last.add(i-2,postBackup);
					lastCheck=1;
					i = 0;
					break;
				case "+":
                                        postBackup = Double.toString(Double.parseDouble(last.get(i-2))+Double.parseDouble(last.get(i-1)));
                                        last.remove(i-2);
                                        last.remove(i-2);
                                        last.remove(i-2);
			                last.add(i-2,postBackup);
					lastCheck=1;
					i = 0;
					break;
				case "-":
                                        postBackup = Double.toString(Double.parseDouble(last.get(i-2))-Double.parseDouble(last.get(i-1)));
                                        last.remove(i-2);
                                        last.remove(i-2);
                                        last.remove(i-2);
                                        last.add(i-2,postBackup);
					lastCheck=1;
					i = 0;
					break;

				default:
				//	System.out.println("default");
					break;
				}
			}
		}

	}
	count = 0;
	ready = 0;
	if(!postBackup.equals("")){
		System.out.println(postBackup);
	}

	if((lastCheck==1)&&(last.size()==1)){
		last.pop();//to terminate the program. exit while loop		
		lastCheck=0;
	}
}
///////////////////////
@Override
public void visitTerminal(TerminalNode node) {
	String semiColon = ";";
	String nl = "\r\n";
	String cmp = "";
	if((assnCheck == 1)&&(cnt==1)){
		var = node.getText();
		cnt++;
		return;
	}
	if((assnCheck ==1)&&(cnt==2)){	
		cnt++;
		return;
	}
	
	//when it is not assn case, but expr case
	if((exprCheck == 1)&&(assnCheck==0)){
		if(map.get(node.getText())!=null){//variable hashmap
			evalStack.push(Double.toString(map.get(node.getText())));
		}
		if((map.get(node.getText())==null)&&(assnCheck==0)){
			if(!(node.getText().equals(semiColon))){
				switch(numCheck){
				case 0 : 
					evalStack.push(node.getText());
					break;
				default : 
					break;
				}
			}
		}
		
	}
	if(node.getText().equals(semiColon)){
		while(!evalStack.empty()){
			stack1.push(evalStack.pop());
			
        	}
		if(evalStack.empty()){
			ready = 1;
			
		}
		if(ready==1){
			calculator();
			ready = 0;
		}
	}
	return;
}
}



public class ExprEvalApp{
	public static void main(String[] args) throws IOException{
		// Get lexer
		ExprLexer lexer= new ExprLexer(CharStreams.fromStream(System.in));
		// Get a list of matched tokens
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// Pass tokens to parser
		ExprParser parser = new ExprParser(tokens);

		// Walk parse-tree and attach our listener
		ParseTreeWalker walker = new ParseTreeWalker();
		EvalListener listener = new EvalListener();
			
		// walk from the root of parse tree
		walker.walk(listener, parser.prog());
		
	}
}

