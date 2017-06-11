package internal;
import java.util.Hashtable;
import java.util.Stack;

import util.SymbolEntry;
import util.SymbolTable;
import util.SyntaxTree;
import util.TreeNode;
import util.TreeNodeType;

public class CodeGenerator {
	boolean inFuncCall;
	int argCnt;
	private Stack<Integer> regStack;
	public CodeGenerator(){
		regStack = new Stack<Integer>();
		this.regStack.push(8);
		this.regStack.push(9);
		this.regStack.push(10);
		this.regStack.push(11);
		this.regStack.push(12);
		this.regStack.push(13);
		this.regStack.push(14);
		this.regStack.push(15);
		this.regStack.push(16);
		this.regStack.push(17);
		this.regStack.push(18);
		this.regStack.push(19);
		this.regStack.push(20);
		this.regStack.push(21);
		this.regStack.push(22);
		this.regStack.push(23);
		this.regStack.push(24);
		this.regStack.push(25);
	}
	//public HashTable used 
	public void process(String iFile, String oFile){
		
		System.out.println("Code generating...");
		//;
		Stack<Integer> usedRegs = new Stack<Integer>();
		allocateReg(SyntaxAnalyzer.tree,null,usedRegs);
		
		SyntaxTree.print(SyntaxAnalyzer.tree);
		SyntaxAnalyzer.tree.getSt().print();
		String code = "";
		code += genHeader(iFile);
		code += genDataSection();
		code += genTextSection();
		
		System.out.println(code);
	}
	private boolean hasReg(){
		return !this.regStack.empty();
	}
	private String getRegName(int reg){
		if(reg == 2){
			return "$v0";
		}else if(reg == 4){
			return "$a0";
		}else if(reg == 5){
			return "$a1";
		}else if(reg == 6){
			return "$a2";
		}else if(reg == 7){
			return "$a3";
		}else if(reg == 8){
			return "$t0";
		}else if(reg == 9){
			return "$t1";
		}else if(reg == 10){
			return "$t2";
		}else if(reg == 11){
			return "$t3";
		}else if(reg == 12){
			return "$t4";
		}else if(reg == 13){
			return "$t5";
		}else if(reg == 14){
			return "$t6";
		}else if(reg == 15){
			return "$t7";
		}else if(reg == 16){
			return "$s0";
		}else if(reg == 17){
			return "$s1";
		}else if(reg == 18){
			return "$s2";
		}else if(reg == 19){
			return "$s3";
		}else if(reg == 20){
			return "$s4";
		}else if(reg == 21){
			return "$s5";
		}else if(reg == 22){
			return "$s6";
		}else if(reg == 23){
			return "$s7";
		}else if(reg == 24){
			return "$t8";
		}else if(reg == 25){
			return "$t9";
		}else{
		return "UnKown";
		}
		
	}
	private int getFreeReg(){
		if(this.regStack.empty()){
			System.out.println("There is no more register available");
			System.exit(1);
		}
		return this.regStack.pop();
	}
	
	private void freeRegs(Stack<Integer> usedRegs){
		while(!usedRegs.empty()){
			this.regStack.push(usedRegs.pop());
		}
	}
		
	public void allocateReg(TreeNode root, SymbolTable st, Stack<Integer> usedRegs) {
		Stack<Integer> subUsedRegs = new Stack<Integer>();
		
		TreeNodeType type = root.getType();
		if(type == TreeNodeType.PROGRAM){
			st = root.getSt();
			for(SymbolEntry se : st.getSes()){
				int reg = this.getFreeReg();
				se.setReg(reg);
			}
			for(TreeNode c : root.getChildren()){
				allocateReg(c,st,subUsedRegs);
			}	
		}else if(type == TreeNodeType.FUNC){
			st = root.getSt();
			int argIndex = 4;
			for(SymbolEntry se : st.getSes()){
				if(se.getVarType().equals("local")){
					int reg = this.getFreeReg();
					se.setReg(reg);
				}else{
					se.setReg(argIndex);
					argIndex++;
				}
			}
			allocateReg(root.getChildByIndex(root.getChildren().size() -1),st,subUsedRegs);
		}else if(type == TreeNodeType.ID){
			String name = "" + LexicalAnalyzer.getValue(root.getTokenIndex());
			SymbolEntry se = st.lookupEntry(name);
			if(se == null){
				System.out.println("Symbol " + name + " is not found in the Symbol table!");
			}else{
				root.setReg(se.getReg());
			}
		}else if(type == TreeNodeType.I_CONST){
			int reg = this.getFreeReg();
			root.setReg(reg);
			usedRegs.push(reg);
		}else if(type == TreeNodeType.FUNC_CALL){
			root.setReg(2);
			allocateReg(root.getChildByIndex(1) , st, subUsedRegs);
		}else if(root.getChildren().size() == 1){
			allocateReg(root.getChildByIndex(0), st, subUsedRegs);
			root.setReg(root.getChildByIndex(0).getReg());
		}else{
			for(TreeNode c : root.getChildren()){
				allocateReg(c,st,subUsedRegs);
			}
			if(type == TreeNodeType.EXPR2 || type == TreeNodeType.EXPR || 
					type == TreeNodeType.TERM || type == TreeNodeType.TERM2){
				int reg = this.getFreeReg();
				root.setReg(reg);
			}
		}
	}
	
	
	public void initRegPool(){
		 regStack.push(5);
		 regStack.push(6);
	}
	
	public void allocReg(TreeNode root){
		TreeNodeType type = root.getType();
		if(type == TreeNodeType.I_CONST || 
				type == TreeNodeType.ID || 
				type == TreeNodeType.TERM2 ||
				type == TreeNodeType.EXPR2){
			
			int reg = getFreeReg();
			root.setReg(reg);
		}
	}
	
	public String genHeader(String iFile){
		String code = "";
		
		code += "\n";
		code += "# Code generated feom MiniCCompiler \n";
		code += "# input file:" + iFile + "\n";
		code += "# Please do not change this file! \n ";
		code += "\n";
		
		return code;
	}
	
	public String genDataSection(){
		String code = "";
		
		code += "\t.data\n\n";
		
		return code;
	}
	
	public String genTextSection() {
		String code = "";
		
		code += "\t.text\n\n";
		
		code += genCode(SyntaxAnalyzer.tree);
		
		return code;
	}
	
	private Stack<TreeNode> oprStack = new Stack<TreeNode>();
	private Hashtable<Integer,String> usedRegs = new Hashtable<Integer,String>(); 
	
	public String genCode(TreeNode root){
		String code = "";
		
		TreeNodeType type = root.getType();
		if(type == TreeNodeType.I_CONST){
			code += "\tli " + this.getRegName(root.getReg()) + ",";
			String value = "" + LexicalAnalyzer.getValue(root.getTokenIndex());
			usedRegs.put(root.getTokenIndex(), this.getRegName(root.getReg()));

			code += value + "\n";
		}
		else if( type == TreeNodeType.FUNC){
			int index = root.getChildByIndex(1).getTokenIndex();
			String funcName = "" + LexicalAnalyzer.getValue(index);
			code += "__" + funcName + ":\n";
			code += genCode(root.getChildByIndex(root.getChildren().size() - 1));
			
		}else if(type == TreeNodeType.ASSIGN_STMT){
			
			code += genCode(root.getChildByIndex(2));
			int reg1 = root.getChildByIndex(0).getReg();
			int reg2 = root.getChildByIndex(2).getReg();
			code += "\tmove " + this.getRegName(reg1) + ", "+ this.getRegName(reg2) + "\n";

			
		}else if(type == TreeNodeType.EXPR){
			
			if(root.getChildren().size() == 1){
				code += genCode(root.getChildByIndex(0));
			}else{
				TreeNode term = root.getChildByIndex(0);
				TreeNode expr2 = root.getChildByIndex(1);
				this.oprStack.push(term);
				code += genCode(term);
				code += genCode(expr2);
				code += "\tmove " + this.getRegName(root.getReg()) + ", " 
				+ this.getRegName(expr2.getReg()) + "\n";
			}
			
		}else if(type == TreeNodeType.EXPR2){
			TreeNode term1 = this.oprStack.pop();
			TreeNode term2 = root.getChildByIndex(1);
			code += genCode(term2);
			
			int index = root.getChildByIndex(0).getTokenIndex();
			String opr = "" + LexicalAnalyzer.getValue(index);
			if(opr.equals("+")){
				code += "\tadd ";
			}else if(opr.equals("-")){
				code += "\tsub ";	
			}
			code += " " + getRegName(root.getReg()) + ", " +
						  getRegName(term1.getReg()) + ", " +
						  getRegName(term2.getReg()) + "\n";
			this.oprStack.push(root);
			if(root.getChildren().size() >= 3){
				code += genCode(root.getChildByIndex(2));
			}
		}else if(type == TreeNodeType.TERM){
			TreeNode factor = root.getChildByIndex(0);
			code += genCode(factor);
			
			if(root.getChildren().size() >= 2){
				TreeNode term2 = root.getChildByIndex(1);
				this.oprStack.push(factor);
				code += genCode(term2);
			}
			
		}else if(type == TreeNodeType.TERM2){
			TreeNode factor1 = this.oprStack.pop();
			TreeNode factor2 = root.getChildByIndex(1);
			code += genCode(factor2);
			
			int index = root.getChildByIndex(0).getTokenIndex();
			String opr = "" + LexicalAnalyzer.getValue(index);
			if(opr.equals("*")){
				code += "\tmul ";
			}else if(opr.equals("/")){
				code += "\tdiv ";
			}
			
			code += " " + getRegName(root.getReg()) + ", "
					+ getRegName(factor1.getReg()) + ", "
					+ getRegName(factor2.getReg()) + "\n";
			this.oprStack.push(root);
			if(root.getChildren().size() >= 3){
				this.oprStack.push(root);
				code += genCode(root.getChildByIndex(2));
			}
		}else if(type == TreeNodeType.RETURN_STMT){
			code += genCode(root.getChildByIndex(0));
			code += "\tmove $v0, " + getRegName(root.getReg()) + "\n";
			code += "\tjr $ra\n";
		}else if(type == TreeNodeType.FUNC_CALL){
			 inFuncCall = true;
			 argCnt = 0;
			if(root.getChildren().size() >= 2){
				code += genCode(root.getChildByIndex(1));
			}
			String func = "" + LexicalAnalyzer.getValue(root.getChildByIndex(0).getTokenIndex());
			code += "\tjal __" + func + "\n";
			inFuncCall = false;
			
		}else if(type == TreeNodeType.EXPR_LIST){
			code += genCode(root.getChildByIndex(0));
			code += "\tmove $a" + argCnt + ", " + getRegName(root.getChildByIndex(0).getReg()) + "\n";
			argCnt++;
			if(root.getChildren().size() >= 2){
				code += genCode(root.getChildByIndex(1));
			}
		}
	
		else{
			for(TreeNode c: root.getChildren()){
				code += genCode(c);
			}
		}
		
		if(root.getType()==TreeNodeType.FUNC)
		code += "\n\n";
		return code;
	}
}
