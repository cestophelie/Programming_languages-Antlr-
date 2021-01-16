import java.lang.*;
import java.util.*;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.IntStream;

class Kotlin2JavaVisitor extends KotlinBaseVisitor<String>{
	String useName = " ";	
	StringBuilder fileIs = new StringBuilder();
	public Kotlin2JavaVisitor(String fname){ 
		useName = fname;
		fileIs = new StringBuilder(fname);
		useName = fname.substring(0,useName.lastIndexOf("."));
	}

	public StringBuffer javaStr  = new StringBuffer();
	int paramNumFlag = 0, noArgument = 0, mainFlag = 0, mainFlag1 = 0;
	int paren = 0,objFlag = 0, innerMethod = 0, innerConst = 0;
	int forFlag = 0, returnFlag = 0, methodCount = 0,mainMethodCount = 0,strFlag = 0;
	int listFlag = 0, paramNumFlag2 = 0, listEnd = 0;
	int layer = 0;

	@Override
	public String visitStart(KotlinParser.StartContext ctx){
		javaStr.append("class ");
                javaStr.append(useName);
		javaStr.append("{\r\n");
//		System.out.println("visitStart : "+ctx.getChild(1).toString());
		return visitChildren(ctx);
	}
	@Override
	public String visitTopLevel(KotlinParser.TopLevelContext ctx){	
//		System.out.println("visitTopLevel : "+ctx.getChild(0).toString());
		return visitChildren(ctx);
//		return visit(ctx.getChild(0));
	}
	
	@Override
        public String visitMethodEnd(KotlinParser.MethodEndContext ctx){
		//javaStr.append(";");
		if(innerMethod==1){
			javaStr.append("\r\n}\r\n}");
			innerMethod = 0;
			innerConst = 1;
		}
		else{
			javaStr.append("\r\n}");
		}
                
//              System.out.println("visitTopLevel : "+ctx.getChild(0).toString());
                return visitChildren(ctx);
        }


	@Override public String visitMethod(KotlinParser.MethodContext ctx) { 
//		System.out.println("visitMethod : "+ctx.getChild(0));
		String methodName = ctx.getChild(1).toString();
		//general method
		if(ctx.getChild(0).toString().equals("fun")){
			//main method
	                if(ctx.getChild(1).getChild(0).toString().equals("main")){
                	        javaStr.append("\r\npublic static void ");
				mainFlag = 1;
				innerMethod = 1;mainMethodCount = 1;
               		}

			//RETURN TYPE
			if(!ctx.getChild(1).getChild(0).toString().equals("main")){
//				methodCount++;//COUNTING THE NUMBER OF INNER METHOD
				if(innerMethod==0){
					javaStr.append("public static ");
						
					if(!ctx.getChild(2).getChild(1).getChild(0).getChild(2).getChild(0).toString().equals("Any")){
						if(!ctx.getChild(2).getChild(1).getChild(0).getChild(2).getChild(0).toString().equals("String")){
							javaStr.append(ctx.getChild(2).getChild(1).getChild(0).getChild(2).getChild(0).toString().toLowerCase());
						}
						else{
							javaStr.append("String");
						}
	
					}
					else{
						String objIs = ctx.getChild(4).getChild(0).toString();
						if(objIs.equals("Int")){
							javaStr.append("Integer");
						}
					}
					javaStr.append(" ");
				}
				else {
					javaStr.append("class Inner{\r\n");
					innerMethod = 1;
					String objIs = ctx.getChild(4).getChild(0).toString();
					if(objIs.equals("Int")){
                                              javaStr.append("Integer");javaStr.append(" ");
                                        }
	
				}		
			}
		}
		
		return visitChildren(ctx);
	}
	@Override
        public String visitParameters(KotlinParser.ParametersContext ctx){
	//	int childNum = ctx.getChildCount();
//		System.out.println("visitParameters");
	//	System.out.println("CHILD NUMMMM : "+childNum);	
	/*	if(childNum != 3){
			paramNumFlag = childNum/2;

		}
		if(childNum == 3){
			noArgument = 1;
			
		}*/
               // System.out.println("visitParameters : "+ctx.getText());
		javaStr.append("(");
		if(mainFlag == 1){
                	javaStr.append("String[] args)");
        	        mainFlag = 0; mainFlag1 = 1; 
	        }
		
                return visitChildren(ctx);
        }

	 @Override
        public String visitParam(KotlinParser.ParamContext ctx){
		int childNum = ctx.getChildCount();
		if(childNum != 1){
                        paramNumFlag = childNum/2;
			paramNumFlag2 = paramNumFlag+1;

                }
                if(childNum == 1){ 
                        noArgument = 1;
                 
                }
	
		return visitChildren(ctx);
	}
	
	@Override
        public String visitFinalParam(KotlinParser.FinalParamContext ctx){
	//	System.out.println("visitFinalParam");
		
		 //if the DATATYPE IS 'ANY' OBJ TYPE
                if(ctx.getChild(2).getChild(0).toString().equals("Any")){
                        javaStr.append("Object ");
			javaStr.append(ctx.getChild(0).getChild(0).toString());	
			javaStr.append(")");
                }
		//primitive datatype
		if(!ctx.getChild(2).getChild(0).toString().equals("Any")){
			if(!ctx.getChild(2).getChild(0).toString().equals("String")){
                                        javaStr.append(ctx.getChild(2).getChild(0).toString().toLowerCase());
					javaStr.append(" ");javaStr.append(ctx.getChild(0).getChild(0).toString());
				//	javaStr.append(")");
                        }
                        else {
                                        javaStr.append("String ");
					javaStr.append(ctx.getChild(0).getChild(0).toString());
					javaStr.append(")");
                        }
			
//counting the numbers of parameters
			if(noArgument != 1){
			//	javaStr.append(" ");
				
				if(paramNumFlag == 0){
					javaStr.append(")");
				}
				else{
					javaStr.append(",");paramNumFlag--;
				}
			}	
		}
		
		//javaStr.append(")");
//		System.out.println("STRING F : "+javaStr.toString());
                return visitChildren(ctx);
        }


	@Override public String visitMethodName(KotlinParser.MethodNameContext ctx) {
//                System.out.println("visitMethodName : "+ctx.getChild(0));
	/*	if(mainMethodCount == 0){
			methodCount++;//counting the number of methods except "MAIN"
		}*/
		if((ctx.getChild(0).toString().equals("println"))){
			javaStr.append("System.out.println");
			if(innerConst==1){
				javaStr.append("new Inner().");
			}
			paren = 1;
		}
		else if((ctx.getChild(0).toString().equals("print"))){
                        javaStr.append("System.out.print");
			 if(innerConst==1){
                                javaStr.append("new Inner().");
                        }

			paren = 1;
                }
		else{
			javaStr.append(ctx.getChild(0));
		}
		
//		System.out.println("STRING : "+javaStr.toString());
                return visitChildren(ctx);
        }
	
	@Override public String visitMethodCallBody(KotlinParser.MethodCallBodyContext ctx){
		methodCount++;
		if(ctx.getChildCount() == 0){
		//	javaStr.append("()");
		}
/*		else{
			javaStr.append("(");

                javaStr.append(ctx.getText());
                if (paren == 1){
                        javaStr.append(");\r\n");
                        paren = 0;
                }

		}*/
		return visitChildren(ctx);
	}
	@Override public String visitMethodCallEnd(KotlinParser.MethodCallEndContext ctx){
	//	javaStr.append("COUNT");
	//	javaStr.append(Integer.toString(methodCount));
		if(methodCount==2){
			if(innerMethod==0){
	//			javaStr.append("methodCall;");
				javaStr.append("cnt);\r\n");
				methodCount--;
			}
			
		}
		else if(methodCount>2){	
//			javaStr.append("2)");
			methodCount--;
		}
		else {
			if(strFlag==0){
			javaStr.append(";");//methodCall
		//	 javaStr.append("cnt);\r\n");
                       methodCount--;
			}
			else{
				javaStr.append(";");
			}
		}
	//	strFlag=0;
		
		return visitChildren(ctx);
}
///////////////////////////////////////////////////////
//collection



///////////////////////////////////////////////////////
//When switch
	@Override public String visitWhenSwitch(KotlinParser.WhenSwitchContext ctx){
		returnFlag = 1;
                javaStr.append("switch(");javaStr.append(ctx.getChild(2).getChild(0).toString());javaStr.append("){\r\n"); 
                return visitChildren(ctx);
	}
	@Override public String visitWhenBody(KotlinParser.WhenBodyContext ctx){
/*		javaStr.append("case ");javaStr.append(ctx.getChild(0).toString());
		javaStr.append(" : ");*/
		
		if(!ctx.getChild(0).toString().equals("else")){
			javaStr.append("case ");javaStr.append(ctx.getChild(0).toString());
	                javaStr.append(" : ");
			javaStr.append(ctx.getChild(2).getChild(0).getText().toString());javaStr.append(";\r\n");
		}
		else {
			javaStr.append("default :");javaStr.append(ctx.getChild(2).getChild(0).getText().toString());javaStr.append(";\r\n");
		}
//		returnFlag = 0;
                return visitChildren(ctx);
        }
	@Override public String visitWhenEnd(KotlinParser.WhenEndContext ctx){
                javaStr.append("\r\n}");
                return visitChildren(ctx);
        }



//IF CONDITION
	@Override public String visitIfCondition(KotlinParser.IfConditionContext ctx){
//		System.out.println("visitIfCondition");
		javaStr.append("if(");
					
		return visitChildren(ctx);
	}
	@Override public String visitTypeCheck(KotlinParser.TypeCheckContext ctx){
		javaStr.append(ctx.getChild(0).getChild(0));
		javaStr.append(" instanceof ");
		javaStr.append(ctx.getChild(2).getChild(0));javaStr.append(")");
		javaStr.append("\r\n");
		objFlag = 1;
		return visitChildren(ctx);
	}

//FOR LOOP
	@Override public String visitForLoop(KotlinParser.ForLoopContext ctx){
		javaStr.append("for(");
		forFlag = 1;	
		//javaStr.append();
		return visitChildren(ctx);
	}
	@Override public String visitForEnd(KotlinParser.ForEndContext ctx){
                javaStr.append("\r\n}\r\n");
                //javaStr.append();
                return visitChildren(ctx);
        }

	@Override public String visitForCondition(KotlinParser.ForConditionContext ctx){
		String dataType = " ";
		dataType = ctx.getParent().getParent().getParent().getParent().getChild(0).getChild(5).getChild(0).getChild(0).toString();
		if(dataType.contains("\"")){
			javaStr.append("String ");
		}
		else{
			javaStr.append(dataType);
			javaStr.append("Integer ");
		}
		javaStr.append(ctx.getChild(0).getChild(0).toString());
		javaStr.append(" : ");
		javaStr.append(ctx.getChild(2).getChild(0).toString());
		javaStr.append(") {\r\n");
		return visitChildren(ctx);
	}
	@Override public String visitForRange(KotlinParser.ForRangeContext ctx){
            //    javaStr.append("for");
		String variable = " ";
		if(ctx.getChildCount()==5){
			variable = ctx.getChild(0).getChild(0).toString();
			javaStr.append("int ");
			javaStr.append(variable);javaStr.append("=");
			javaStr.append(ctx.getChild(2).getText());
			javaStr.append(";");javaStr.append(variable);
			javaStr.append("<=");javaStr.append(ctx.getChild(4).getText().toString());
			javaStr.append(";");javaStr.append(variable);
			javaStr.append("++");
		}
		else {
			variable = ctx.getChild(0).getChild(0).toString();
			javaStr.append("int ");
			javaStr.append(variable);
			javaStr.append("=");
			javaStr.append(ctx.getChild(2).getText().toString());javaStr.append(";");
			javaStr.append(variable);javaStr.append(">=");
			javaStr.append(ctx.getChild(4).getText().toString());
			javaStr.append(";");
			javaStr.append(variable);
			javaStr.append("=");javaStr.append(variable);
			javaStr.append("-");javaStr.append(ctx.getChild(6).getText().toString());	
		}
                javaStr.append("){\r\n");
                return visitChildren(ctx);
        }


//RETURN
	@Override public String visitReturnStmt(KotlinParser.ReturnStmtContext ctx) {
//		System.out.println("visitReturnStmt");
		if(returnFlag!=1){
		javaStr.append(ctx.getChild(0));javaStr.append(" ");}
                return visitChildren(ctx); }

	@Override public String visitVariable(KotlinParser.VariableContext ctx) {
		if(forFlag==0){
	                javaStr.append(ctx.getChild(0));javaStr.append(";");
		}
                return visitChildren(ctx); }

	@Override public String visitObjCall(KotlinParser.ObjCallContext ctx) {
                if(objFlag == 1){
			javaStr.append("((");
			javaStr.append(ctx.getParent().getParent().getParent().getChild(2).getChild(0).getChild(0).getChild(2).getChild(0).toString());
			javaStr.append(")");
			javaStr.append(ctx.getChild(0).getChild(0).toString());
			javaStr.append(").");
			javaStr.append(ctx.getChild(2).getChild(0));
			javaStr.append("()");
			objFlag = 0;
		}
		else{
			//
			javaStr.append(ctx.getText());
		}
		javaStr.append(";");
                return visitChildren(ctx); }

	@Override public String visitParamOperationLayer(KotlinParser.ParamOperationLayerContext ctx) {
           //     System.out.println("visitParamOPerationLayer");
                javaStr.append(ctx.getText());javaStr.append(";");//nope
       //         System.out.println("String final : "+javaStr.toString());
                return visitChildren(ctx); }

//BODY
	@Override public String visitBody(KotlinParser.BodyContext ctx) { 
//		System.out.println("visitBody");
		javaStr.append("{\r\n");

		return visitChildren(ctx);
	}

	@Override public String visitImplicitBody(KotlinParser.ImplicitBodyContext ctx) {

                javaStr.append("{\r\n");
		javaStr.append("return ");

                return visit(ctx.getChild(1)); 
	}
	
	@Override public String visitCallParam(KotlinParser.CallParamContext ctx) {
//		System.out.println("visitCallParam");
               // javaStr.append("(");
	
		javaStr.append(ctx.getText());
		if (paren == 1){
		//	javaStr.append(")");
			paren = 0;
		}
		return visitChildren(ctx);
	}

	 @Override public String visitCallBodyStart(KotlinParser.CallBodyStartContext ctx) {
		layer++;
		javaStr.append("(");
		return visitChildren(ctx);
	}
	 @Override public String visitCallBodyEnd(KotlinParser.CallBodyEndContext ctx) {
		layer--;
		if(layer==0){
			javaStr.append(");");
		}
		else{
		javaStr.append(")");}
                return visitChildren(ctx);
        }


	@Override public String visitBlock(KotlinParser.BlockContext ctx) {
//		System.out.println("visitBLock : ");
		

		return visitChildren(ctx);
	}
	@Override public String visitVarName(KotlinParser.VarNameContext ctx) {
//		System.out.println("visitVarName");
                return visitChildren(ctx); }
	
	@Override public String visitDataType(KotlinParser.DataTypeContext ctx) { 
//		System.out.println("visitDataType : ");
		return visitChildren(ctx); }

	@Override public String visitStringy(KotlinParser.StringyContext ctx) {
		strFlag = 1;
 //              System.out.println("visitStringy : ");

		javaStr.append(ctx.getChild(0).getText());
//		javaStr.append(")");

	
//print the following on the last one time
		if(listFlag == 0){//number of COMMA to print
			listEnd=1;
			//javaStr.append("h);\r\n");
		}
		else{
			javaStr.append(",");listFlag--;
		}
                return visitChildren(ctx);
	}

	@Override public String visitListElem(KotlinParser.ListElemContext ctx) {
		listFlag = ctx.getChildCount()/2;//number of COMMAS needed
//		javaStr.append(Integer.toString(listFlag));
		return visitChildren(ctx);
	}	

//-------------------------------VARIABLES-----------------
		public String typeCheck(KotlinParser.AssnContext ctx){
			String dataType = " ";
//			System.out.println("------------------------YAYA");
			
			return "quoi";
		}
	@Override public String visitAssnEnd(KotlinParser.AssnEndContext ctx) {
		if(listEnd==1){
			javaStr.append(");");
			listEnd= 0;
		}
		listFlag = 0;
		strFlag = 0;
		return visitChildren(ctx);
	}	
	
	@Override public String visitAssn(KotlinParser.AssnContext ctx) {

//all of this when it is not listOf
	if(!ctx.getChild(3).toString().equals("listOf")){
///////////////
//		System.out.println("visitAssn : "+ctx.getChild(0));
                if(ctx.getChild(0).toString().equals("val")){
//			System.out.println("ASSN?? : ");
			javaStr.append("final ");
			if(ctx.getChild(2).toString().equals(":")){
				javaStr.append(ctx.getChild(3).getChild(0).toString().toLowerCase());javaStr.append(" ");
				javaStr.append(ctx.getChild(1).getChild(0));
				javaStr.append("=");
				javaStr.append(ctx.getChild(5).getChild(0).getChild(0));javaStr.append(";\r\n");
			}
			else {
				String dataType = ctx.getChild(3).getText();
				if(dataType.contains(".")){
					javaStr.append("double ");
				}
				else{
					javaStr.append("int ");
				}	
			
				javaStr.append(ctx.getChild(1).getChild(0));
				javaStr.append(" = ");
				javaStr.append(ctx.getChild(3).getText());javaStr.append(";\r\n");
			}	
			
		}
		else if(ctx.getChild(0).toString().equals("var")){
			//do nothing?
                                if(ctx.getChild(3).getText().contains(".")){
                                        javaStr.append("double ");
                                }
                                else{
                                        javaStr.append("int ");
                                }

                                javaStr.append(ctx.getChild(1).getChild(0));
                                javaStr.append(" = ");
                                javaStr.append(ctx.getChild(3).getText());javaStr.append(";\r\n");

				
		}
		else{//ASSIGNING AFTER DECLARATION
			javaStr.append(ctx.getChild(0).getChild(0));javaStr.append(" ");
			javaStr.append(ctx.getChild(1).getText());javaStr.append(" ");
			javaStr.append(ctx.getChild(2).getText());javaStr.append(";\r\n");

		}
	}
	else{

//LIST OF CASE
		javaStr.insert(0,"import java.util.*;\r\n");
		javaStr.append("List<");
		if(ctx.getChild(5).getChild(0).getChild(0).toString().contains("\"")){
			javaStr.append("String>");//strFlag = 0;
		}
		else{
			javaStr.append("Integer>");
		}
		javaStr.append(ctx.getChild(1).getChild(0).toString());
		javaStr.append("= List.of(");
		
	}
                return visitChildren(ctx); }


//VAR DECLARATION
	@Override public String visitVariableDeclare(KotlinParser.VariableDeclareContext ctx) {
//                System.out.println("VAR DECLARATION");
		
		if(ctx.getChild(0).toString().equals("val")){
			javaStr.append("final ");
			javaStr.append(ctx.getChild(3).getChild(0).toString().toLowerCase());javaStr.append(" ");
			
			javaStr.append(ctx.getChild(1).getChild(0));javaStr.append(";\r\n");
			
		}
		else{
			//here when variable is var type
			
		}
                return visitChildren(ctx); }

	}

public class Kotlin2Java{
//-------------------------------------
        public static void main(String[] args) throws IOException{
	//	System.out.println(args[1]);
		String fileName = args[1];
//		System.out.println("HEREEE : "+fileName);
		StringBuffer sb = new StringBuffer();
		
		if(args.length == 0){
			System.out.println("No input file received");
			System.exit(0);
		}
		File file = new File(args[0]);

		if(file.exists()){
			BufferedReader br = new BufferedReader(new FileReader(file));
			while(br.ready()){
				sb.append(br.readLine());
				sb.append("\n");	
			}
		

		KotlinLexer lexer = new KotlinLexer(CharStreams.fromString(sb.toString()));		
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                KotlinParser parser = new KotlinParser(tokens);
	
		ParseTree tree = parser.start();

		Kotlin2JavaVisitor visited = new Kotlin2JavaVisitor(fileName);

		String visitor = visited.visit(tree);

		br.close();
		
		try{
			File file_2 = new File(args[1]);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file_2));

			if(file.isFile() && file_2.canWrite()){
				bufferedWriter.write(visited.javaStr.toString());
				bufferedWriter.close();
			}
		}catch (IOException e){
			System.out.println(e);
		}
		
	}
        }
}

