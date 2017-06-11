package internal;

import util.SymbolEntry;
import util.SymbolTable;
import util.SyntaxTree;
import util.TreeNode;
import util.TreeNodeType;

/*-------------

PROGRAM 	--> FUNCLIST
FUNC_LIST 	-->	FUNC FUNC_LIST  | e 
FUNC		--> TYPE ID '(' ARGUMENTS ')' CODE_BLOCK
TYPE		--> 'int' | 'float' | 'char'

ARGUMENTS	-->	e | ARGUMENT | ARG_LIST
ARG_LIST	--> ARGUMENT ',' ARG_LIST | ARGUMENT

ARGUMENT	--> TYPE ID
CODE_BLOCK	--> '{' STMTS '}'
STMTS		--> STMT STMTS | e
STMT		--> ASSIGN_STMT | RETURN_STMT | DECL_STMT
ASSIGN_STMT	--> ID '=' EXPR ';'
RETURN_STMT	--> 'return' EXPR ';'
DECL_STMT	--> TYPE ID ';'

EXPR		--> EXPR '+' TERM | EXPR '-' TERM | TERM

EXPR		--> TERM EXPR'
EXPR'		--> '+' TERM EXPR' | '-' TERM EXPR' | e

TERM		--> TERM '*' FACTOR | TERM '/' FACTOR | FACTOR

TERM		--> FACTOR TERM'
TERM'		--> '*' FACTOR TERM' | '/' FACTOR TERM' | e 

FACTOR		--> '(' EXPR ')' | ID | CONST | FUNC_CALL

FUNC_CALL	--> ID '(' EXPR_LIST ')'
EXPR_LIS	--> EXPR | EXPR | EXPR_LIST | e

 
int main(int argc, char** argv){
	float 6566cab, f; 	// I'm a comment
	if(argc < 2){
		return;
	}
	f = 3.14*2*3;
	printf("hello // world!");
}

PROGRAM => FUNCLIST
		=> FUNC FUNC_LIST
		=> TYPE ID '(' ARGUMENTS ')' CODE_BLOCK FUNC_LIST
		=> int  ID '(' ARGUMENTS ')' CODE_BLOCK FUNC_LIST
		=> int main ( ARGUMENTS  ')' CODE_BLOCK FUNC_LIST
		=> int main ( ARG_LIST ')'  CODE_BLOCK FUNC_LIST
		=> int main ( ARGUMENT ',' ARG_LIST ')'  CODE_BLOCK FUNC_LIST
		=> int main ( ARGUMENT ',' ARGUMENT ')'  CODE_BLOCK FUNC_LIST
		=> int main ( TYPE ID , TYPE ID ) CODE_BLOCK FUNC_LIST
		=> int main ( TYPE ID , TYPE ID ) CODE_BLOCK FUNC_LIST

 
  
  
---------------- */




public class SyntaxAnalyzer {
	public static TreeNode tree = null;
	public void process(String iFile, String oFile){
		System.out.println("Syntax analyzing...");
		
		int num = LexicalAnalyzer.getTokenNum();
		for(int i = 0; i < num ; i++){
			TokenType t = LexicalAnalyzer.getType(i);
			Object o = LexicalAnalyzer.getValue(i);
			System.out.println("Token " + i + ":" + t + "," + o);
		}
		
		TreeNode program = matchProgram();
		tree = program;
		SyntaxTree.print(program);
		program.getSt().print();
	}
	
	int tokenIndex = -1;
	TokenType currTokenType = TokenType.NONE;
	String currLexeme = "";
	
	
	public void getNextToken(){
		tokenIndex++;
		if(tokenIndex >= LexicalAnalyzer.getTokenNum()){
			System.out.println("There is no more token left!");
			System.exit(0);
		}
		
		currTokenType = LexicalAnalyzer.getType(tokenIndex);
		currLexeme = "" + LexicalAnalyzer.getValue(tokenIndex);
	}
	
	public void matchToken(TokenType t, String lexeme){
		boolean isMatch = true;
		if(currTokenType == t){
			if(t == TokenType.PC){
				if(!currLexeme.equals(lexeme)){
					isMatch = false;
				}
			}
		}else{
			isMatch = false;
		}
		if(!isMatch){
			System.out.println("unmatched token found!");
			System.out.println("CurrToken: index = " + tokenIndex +
				", type=" + currTokenType + ", lexeme=" + currLexeme);
			System.out.println("InputToken: type=" + t +
					", lexeme=" + lexeme);
			System.exit(0);
		}else{
			//tokenIndex++;
		}
	}
	
	private SymbolTable currSt = null;
	
	/*PROGRAM 	--> FUNCLIST*/
	public TreeNode matchProgram(){
		System.out.println("start to match program");
		getNextToken();
		
		SymbolTable st = new SymbolTable(null);
		this.currSt = st;
		
		TreeNode funcList = matchFuncList();
		TreeNode program = new TreeNode(TreeNodeType.PROGRAM);
		program.addChild(funcList);
		System.out.println("program matched");
		
		program.setSt(st);
		
		
		return program;
	}
	/*FUNC_LIST 	-->	FUNC FUNC_LIST  | e*/ 
	public TreeNode matchFuncList(){
		if(currTokenType == TokenType.ID){ //FUNC FUNC_LIST
			TreeNode func = matchFunc();
			TreeNode funcList1 = matchFuncList();
			TreeNode funcList2 = new TreeNode(TreeNodeType.FUNC_LIST);
			funcList2.addChild(func);
			if(funcList1 != null){
				funcList2.addChild(funcList1);
			}
			return funcList2;
		}else if(currTokenType == TokenType.TOKEN_END){ //e
			return null;
		}else{
			System.out.println("There is something wrong here!");
		}
		return null;
	}
	
	/* FUNC		--> TYPE ID '(' ARGUMENTS ')' CODE_BLOCK */
	public TreeNode matchFunc(){
		//type
		matchType();
		TreeNode funcType = new TreeNode(TreeNodeType.TYPE);
		funcType.setTokenIndex(tokenIndex);
		getNextToken();
		//name
		matchToken(TokenType.ID, "");
		TreeNode funcName = new TreeNode(TreeNodeType.ID);
		funcName.setTokenIndex(tokenIndex);
		
		//create symbol table
		SymbolTable st = new SymbolTable(currSt);
		currSt.getChildren().add(st);
		currSt = st;
		
		//arguments
		getNextToken();
		matchToken(TokenType.PC, "(");
		getNextToken();
		TreeNode args = matchArguments();
		matchToken(TokenType.PC, ")");
		getNextToken();
		//body
		TreeNode cb = matchCodeBlock();
		TreeNode func = new TreeNode(TreeNodeType.FUNC);
		func.addChild(funcType);
		func.addChild(funcName);
		if(args !=null){
			func.addChild(args);
		}
		func.addChild(cb);
		
		func.setSt(st);
		currSt = st.getTop();
		
		return func;
	}
	
	public void matchType(){
		matchToken(TokenType.ID, "");
	}
	
	
	/*
		ARGUMENTS	-->	e | ARGUMENT ARG_LIST
		
	 */
	
	public TreeNode matchArguments(){
		if(currTokenType == TokenType.PC){
			return null;
		}else{
			TreeNode arg = matchArgument();
			TreeNode argList = matchArgList();
			TreeNode arguments = new TreeNode(TreeNodeType.ARGUMENTS);
			arguments.addChild(arg);
			if(argList != null){
				arguments.addChild(argList);
			}
			return arguments;
		}
	}
	
	/*
	 * ARGUMENT	--> TYPE ID
	 */
	public TreeNode matchArgument(){
		matchType();
		TreeNode argType = new TreeNode(TreeNodeType.TYPE);
		argType.setTokenIndex(tokenIndex);
		getNextToken();
		
		matchToken(TokenType.ID, "");
		TreeNode argName = new TreeNode(TreeNodeType.ID);
		argName.setTokenIndex(tokenIndex);
		getNextToken();
		
		//symbol entry
		SymbolEntry se = new SymbolEntry(
				LexicalAnalyzer.getValue(argName.getTokenIndex()).toString(),
				LexicalAnalyzer.getValue(argType.getTokenIndex()).toString() 
			);
		currSt.insertEntry(se);
		se.setVarType("argument");
		
		TreeNode arg = new TreeNode(TreeNodeType.ARGUMENT);
		arg.addChild(argType);
		arg.addChild(argName);
		return arg;
	}
	/*
	 * ARG_LIST	--> ',' ARGUMENT ARG_LIST | e
	 */
	public TreeNode matchArgList(){
		if(currLexeme.equals(",")){
			matchToken(TokenType.PC, ",");
			getNextToken();
			TreeNode arg = matchArgument();
			TreeNode argList = matchArgList();
			TreeNode argList2 = new TreeNode(TreeNodeType.ARG_LIST);
			argList2.addChild(arg);
			if(argList != null){
				argList2.addChild(argList);
			}
			return argList2;
		}else{
			return null;
		}
	}
	
	
	/*CODE_BLOCK	--> '{' STMTS '}'*/
	public TreeNode matchCodeBlock(){
		matchToken(TokenType.PC, "{");
		getNextToken();
		TreeNode stmts = matchStmts();
		matchToken(TokenType.PC, "}");
		getNextToken();
		TreeNode cb = new TreeNode(TreeNodeType.CODE_BLOCK);
		if(stmts != null){
			cb.addChild(stmts);
		}
		return cb;
	}
	/* STMTS		--> STMT STMTS | e	*/
	public TreeNode matchStmts(){
		if(currTokenType == TokenType.PC &&
				currLexeme.equals("}")){ // e
			return null;
		}else{//STMT STMTS
			TreeNode stmt = matchStmt();
			TreeNode stmts = matchStmts();
			TreeNode stmts2 = new TreeNode(TreeNodeType.STMTS);
			stmts2.addChild(stmt);
			if(stmts != null){
				stmts2.addChild(stmts);
			}
			return stmts2;
		}
	}
	/* STMT		--> ASSIGN_STMT | RETURN_STMT | DECL_STMT */
	public TreeNode matchStmt(){
		TreeNode stmt = null;
		if(currLexeme.equals("return")){//RETURN_STMT
			stmt = matchReturnStmt();
			System.out.println("return statement found!");
		}else if(currLexeme.equals("int") || currLexeme.equals("float")){
			stmt = matchDeclStmt();
			System.out.println("declaration statement found!");
		}else if(currLexeme.equals("if")){
			
		}else if(currLexeme.equals("for")){
			
		}else{	//assignment statement
			stmt = matchAssignStmt();
			System.out.println("assignment statement found!");
		}
		
		return stmt;
	}
	/* RETURN_STMT	--> 'return' EXPR ';'   */
	public TreeNode matchReturnStmt(){
		matchToken(TokenType.ID, "return");
		getNextToken();
		TreeNode expr = matchExpr();
		matchToken(TokenType.PC, ";");
		getNextToken();
		TreeNode rtn = new TreeNode(TreeNodeType.RETURN_STMT);
		rtn.addChild(expr);
		return rtn;
	}
	
	/* DECL_STMT	--> TYPE ID ';'  */
	public TreeNode matchDeclStmt(){
		//type
		matchToken(TokenType.ID, "");
		TreeNode varType = new TreeNode(TreeNodeType.TYPE);
		varType.setTokenIndex(tokenIndex);
		getNextToken();
		//id
		matchToken(TokenType.ID, "");
		TreeNode varName = new TreeNode(TreeNodeType.ID);
		varName.setTokenIndex(tokenIndex);
		getNextToken();
		matchToken(TokenType.PC, ";");
		getNextToken();
		
		//symbol entry
		SymbolEntry se = new SymbolEntry(
				LexicalAnalyzer.getValue(varName.getTokenIndex()).toString(),
				LexicalAnalyzer.getValue(varType.getTokenIndex()).toString());
		currSt.insertEntry(se);
		se.setVarType("local");
	
		
		TreeNode decl = new TreeNode(TreeNodeType.DECL_STMT);
		decl.addChild(varType);
		decl.addChild(varName);
		return decl;
		
	}
	/*  ASSIGN_STMT	--> ID '=' EXPR '; */
	public TreeNode matchAssignStmt(){
		//id
		matchToken(TokenType.ID, "");
		TreeNode id = new TreeNode(TreeNodeType.ID);
		id.setTokenIndex(tokenIndex);
		//=
		getNextToken();
		matchToken(TokenType.OP, "=");
		TreeNode op = new TreeNode(TreeNodeType.OP);
		op.setTokenIndex(tokenIndex);
		//expr
		getNextToken();
		TreeNode expr = matchExpr();
		matchToken(TokenType.PC, ";");
		getNextToken();
		//tree node
		TreeNode assign = new TreeNode(TreeNodeType.ASSIGN_STMT);
		assign.addChild(id);
		assign.addChild(op);
		assign.addChild(expr);
		return assign;
	}
	
	/*
	EXPR		--> TERM EXPR'
	*/ 
	public TreeNode matchExpr(){
		//getNextToken();
		TreeNode term = matchTerm();
		TreeNode expr2 = matchExpr2();
		TreeNode expr = new TreeNode(TreeNodeType.EXPR);
		expr.addChild(term);
		if(expr2 != null){
			expr.addChild(expr2);
		}
		return expr;
	}
	
	/*
	TERM		--> FACTOR TERM'
	*/
	public TreeNode matchTerm(){
		TreeNode factor = matchFactor();
		TreeNode term2 = matchTerm2();
		TreeNode term = new TreeNode(TreeNodeType.TERM);
		term.addChild(factor);
		if(term2 != null){
			term.addChild(term2);
		}
		return term;
	}
	
	/*
	EXPR'		--> '+' TERM EXPR' | '-' TERM EXPR' | e
	*/
	public TreeNode matchExpr2(){
		//getNextToken();
		if(currLexeme.equals("+")){
			matchToken(TokenType.OP, "+");
			TreeNode op = new TreeNode(TreeNodeType.OP);
			op.setTokenIndex(tokenIndex);
			getNextToken();
			
			TreeNode term = matchTerm();
			TreeNode expr2 = matchExpr2();
			TreeNode expr = new TreeNode(TreeNodeType.EXPR2);
			expr.addChild(op);
			expr.addChild(term);
			if(expr2 != null){
				expr.addChild(expr2);
			}
			return expr;
		}else if(currLexeme.equals("-")){
			matchToken(TokenType.OP, "-");
			TreeNode op = new TreeNode(TreeNodeType.OP);
			op.setTokenIndex(tokenIndex);
			getNextToken();
			TreeNode term = matchTerm();
			TreeNode expr2 = matchExpr2();
			TreeNode expr = new TreeNode(TreeNodeType.EXPR2);
			expr.addChild(op);
			expr.addChild(term);
			if(expr2 != null){
				expr.addChild(expr2);
			}
			return expr;
		}else{
			return null;
		}
	}
	
	/*
	TERM'		--> '*' FACTOR TERM' | '/' FACTOR TERM' | e
	*/
	public TreeNode matchTerm2(){
		getNextToken();
		if(currLexeme.equals("*")){
			matchToken(TokenType.OP, "*");
			TreeNode op = new TreeNode(TreeNodeType.OP);
			op.setTokenIndex(tokenIndex);
			getNextToken();
			TreeNode factor = matchFactor();
			TreeNode term2 = matchTerm2();
			TreeNode term = new TreeNode(TreeNodeType.TERM2);
			term.addChild(op);
			term.addChild(factor);
			if(term2 != null){
				term.addChild(term2);
			}
			return term;
		}else if(currLexeme.equals("/")){
			matchToken(TokenType.OP, "/");
			TreeNode op = new TreeNode(TreeNodeType.OP);
			op.setTokenIndex(tokenIndex);
			getNextToken();
			TreeNode factor = matchFactor();
			TreeNode term2 = matchTerm2();
			TreeNode term = new TreeNode(TreeNodeType.TERM2);
			term.addChild(op);
			term.addChild(factor);
			if(term2 != null){
				term.addChild(term2);
			}
			return term;
		}else{
			return null;
		}
	}
	
	/*
	FACTOR		--> '(' EXPR ')' | ID | CONST | FUNC_CALL

	FUNC_CALL	--> ID '(' EXPR_LIST ')'
	EXPR_LIS	--> EXPR | EXPR | EXPR_LIST | E
	 */
	public TreeNode matchFactor(){
		//getNextToken();
		TreeNode factor = new TreeNode(TreeNodeType.FACTOR);
		
		if(currLexeme.equals("(")){
			matchToken(TokenType.PC, "(");
			getNextToken();
			TreeNode expr = matchExpr();
			factor.addChild(expr);
			matchToken(TokenType.PC, ")");
			//getNextToken();
		}else if(currTokenType == TokenType.ID){
			TreeNode id = new TreeNode(TreeNodeType.ID);
			id.setTokenIndex(tokenIndex);
			if(testNextToken("(")){
				TreeNode func = new TreeNode(TreeNodeType.FUNC_CALL);
				func.addChild(id);
				getNextToken();
				matchToken(TokenType.PC, "(");
				getNextToken();
				if(currLexeme.equals(")")){
				}else{
					TreeNode exprs = matchExprList();
					func.addChild(exprs);
				}
				matchToken(TokenType.PC,")");
				factor.addChild(func);
			}else{
				factor.addChild(id);
			}
		}else if(currTokenType == TokenType.I_CONST){
			TreeNode iconst = new TreeNode(TreeNodeType.I_CONST);
			iconst.setTokenIndex(tokenIndex);
			factor.addChild(iconst);
		}else if(currTokenType == TokenType.F_CONST){
			TreeNode fconst = new TreeNode(TreeNodeType.F_CONST);
			fconst.setTokenIndex(tokenIndex);
			factor.addChild(fconst);
		}else{
			System.out.println("unmatched factor!");
		}
		return factor;
	}

	public TreeNode matchExprList() {
		TreeNode exprs = new TreeNode(TreeNodeType.EXPR_LIST);
		TreeNode expr = matchExpr();
		exprs.addChild(expr);
		if(currLexeme.equals(",")){
			matchToken(TokenType.PC,",");
			getNextToken();
			TreeNode exprs2 = matchExprList();
			exprs.addChild(exprs2);
		}
		return exprs;
	}

	public boolean testNextToken(TokenType t) {
		if(tokenIndex + 1 >= LexicalAnalyzer.getTokenNum()){
			System.out.println("There is no more token left!");
			System.exit(0);
		}else{
			if(LexicalAnalyzer.getType(tokenIndex + 1) == t){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	public boolean testNextToken(String l) {
		if(tokenIndex + 1 >= LexicalAnalyzer.getTokenNum()){
			System.out.println("There is no more token left!");
			System.exit(0);
		}else{
			//System.out.println(LexicalAnalyzer.getValue(tokenIndex + 1));
			
			if(("" + LexicalAnalyzer.getValue(tokenIndex + 1).toString().trim()).equals(l)){
				return true;
			}
		}
		return false;
	}
	
}
