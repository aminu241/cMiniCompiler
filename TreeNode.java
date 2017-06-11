package util;

import java.util.ArrayList;

public class TreeNode {
	
	private TreeNodeType type;
	private ArrayList<TreeNode> children;
	private int tokenIndex;
	//symbol table
	private SymbolTable st;
	
	//register allocation
	private int reg;
	
	// default constructor
	public TreeNode(){
		this.type = TreeNodeType.UNKNOWN;
		this.children = new ArrayList<TreeNode>();
		this.setTokenIndex(-1);
		this.reg = -1;
	}
	
	// constructor with type
	public TreeNode(TreeNodeType type){
		this.type = type;
		this.children = new ArrayList<TreeNode>();
		this.reg = -1;
		this.setTokenIndex(-1);
	}
	
	public ArrayList<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}
	public TreeNodeType getType() {
		return type;
	}
	public void setType(TreeNodeType type) {
		this.type = type;
	}
	
	// get a child by index
	public TreeNode getChildByIndex(int index){
		if(index < this.children.size()){
			return this.children.get(index);
		}else{
			System.out.println("TreeNode children index out of bound!");
			System.exit(1);
		}
		return null;
	}
	
	// add a child
	public void addChild(TreeNode c){
		this.children.add(c);
	}

	public int getTokenIndex() {
		return tokenIndex;
	}

	public void setTokenIndex(int tokenIndex) {
		this.tokenIndex = tokenIndex;
	}

	public SymbolTable getSt() {
		return st;
	}

	public void setSt(SymbolTable st) {
		this.st = st;
	}

	public int getReg() {
		return reg;
	}

	public void setReg(int reg) {
		this.reg = reg;
	}
}
