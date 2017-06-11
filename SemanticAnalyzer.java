package internal;

import util.SymbolEntry;
import util.SymbolTable;
import util.TreeNode;
import util.TreeNodeType;

public class SemanticAnalyzer {
	public void process(String iFile, String oFile){
		System.out.println("Semantic analyzing...");
		checkUndecl(SyntaxAnalyzer.tree,null);
		checkReDecl(SyntaxAnalyzer.tree,null);
		interpret(SyntaxAnalyzer.tree);
	}
	
	public void checkUndecl(TreeNode root, SymbolTable st ){
		if(root.getType() == TreeNodeType.PROGRAM){
			st = root.getSt();
		}else if(root.getType() == TreeNodeType.FUNC){
			st = root.getSt();
		}
		if(root.getType() == TreeNodeType.ID){
			String name = "" + LexicalAnalyzer.getValue(root.getTokenIndex());
			SymbolEntry se = st.lookupEntry(name);
			
			if(se == null && st.getTop() != null){
				se = st.getTop().lookupEntry(name);
			}
			if(se == null){
				System.out.println("Symbol " + name + " is not declared");
			}
		}
		if(root.getType() != TreeNodeType.DECL_STMT)
		for(TreeNode c : root.getChildren()){
			checkUndecl(c,st);
		}
		
	}

	void checkReDecl(TreeNode root,SymbolTable st){
		
		
		if(root.getType() == TreeNodeType.PROGRAM){
			st = root.getSt();
		//get the symbol table for function 
		}else if(root.getType() == TreeNodeType.FUNC){
			st = root.getSt();
		}

		if(root.getType() == TreeNodeType.PROGRAM ||
				root.getType() == TreeNodeType.FUNC){
			for(int i = 0; i < st.getSes().size(); i++){
				SymbolEntry se1 = st.getByIndex(i);
				for(int j = 0; j<i; j++){
					SymbolEntry se2 = st.getByIndex(j);
					if(se1.getName().equals(se2.getName())){
						System.out.println("Symbol Redefined: " + se1.getName() + "," + se2.getName());;
					}
				}
			}
			
		}
		for(TreeNode c : root.getChildren()){
			checkReDecl(c,st);
		}
	}
	public int interpret(TreeNode root){
		int value = 0;
		if(root.getType() == TreeNodeType.ASSIGN_STMT){
			value = interpret(root.getChildByIndex(2));
			System.out.println("result = " + value);
			
		}else if(root.getType() == TreeNodeType.I_CONST){
			String lexeme = "" + LexicalAnalyzer.getValue(root.getTokenIndex());
			value = Integer.parseInt(lexeme);
		}else if(root.getType() == TreeNodeType.TERM2){
			value = interpret(root.getChildByIndex(1));
		}else if(root.getType() == TreeNodeType.TERM){
			if(root.getChildren().size() > 1){
			int tokenIndex = root.getChildByIndex(1).getChildByIndex(0).getTokenIndex();
			String op = "" + LexicalAnalyzer.getValue(tokenIndex);
			
			int lvalue = interpret(root.getChildByIndex(0));
			int rvalue = interpret(root.getChildByIndex(1));
			
			if(op.equals("*")){
				value = lvalue * rvalue;
			}else if(op.equals("/")){
				value = lvalue / rvalue;
			}
			}else{
				value = interpret(root.getChildByIndex(0));
			}
		}else if(root.getType() == TreeNodeType.EXPR2){
			value = interpret(root.getChildByIndex(1));
		}else if(root.getType() == TreeNodeType.EXPR){
			if(root.getChildren().size() > 1){
			int tokenIndex = root.getChildByIndex(1).getChildByIndex(0).getTokenIndex();
			String op = "" + LexicalAnalyzer.getValue(tokenIndex);
			
			int lvalue = interpret(root.getChildByIndex(0));
			int rvalue = interpret(root.getChildByIndex(1));
			
			if(op.equals("+")){
				value = lvalue + rvalue;
			}else if(op.equals("-")){
				value = lvalue - rvalue;
			}
			}else{
				value = interpret(root.getChildByIndex(0));
			}
		}else if(root.getType() == TreeNodeType.FUNC){
			value = interpret(root.getChildByIndex(2));
			
		}else if(root.getType() == TreeNodeType.STMTS){
			for(TreeNode c : root.getChildren()){
				interpret(c);
			}
		}else{
			if(root.getChildren().size() == 1){
				value = interpret(root.getChildByIndex(0));
			}else{
				
			}
		}
		
		return value;
	}
}
