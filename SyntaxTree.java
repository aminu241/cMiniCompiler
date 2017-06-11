package util;

import internal.LexicalAnalyzer;

public class SyntaxTree {
	private TreeNode root;
	private static String indent = "\t"; 
	
	public SyntaxTree(){
		root = null;
	}
	
	public SyntaxTree(TreeNodeType type){
		this.root = new TreeNode(type);
	}
	
	public static void print(TreeNode root){
		String prefix = "";
		print2(root, prefix);
	}
	
	public static void print2(TreeNode root, String prefix){
		//prefix = prefix.replace("\t", "----");
		Object lexeme = "";
		if(root.getTokenIndex() >= 0){
			lexeme = LexicalAnalyzer.getValue(root.getTokenIndex());
		}
		System.out.print(prefix + "|----" + root.getType() + "(" + lexeme + ")");
		if(root.getReg() >= 0){
			System.out.println(":" + root.getReg());
		}else{
			System.out.println("");
		}
		for(TreeNode c: root.getChildren()){
			print2(c, prefix + indent);
		}
	}
	
	public static void main(String[] args){
		TreeNode e = new TreeNode(TreeNodeType.PROGRAM);
		TreeNode c1 = new TreeNode(TreeNodeType.FUNC);
		TreeNode c2 = new TreeNode(TreeNodeType.FUNC);
		TreeNode c3 = new TreeNode(TreeNodeType.FUNC);
		e.addChild(c1);
		e.addChild(c2);
		e.addChild(c3);
		TreeNode i = new TreeNode(TreeNodeType.FUNC);
		c1.addChild(i);
		TreeNode c4 = new TreeNode(TreeNodeType.FUNC);
		TreeNode c5 = new TreeNode(TreeNodeType.FUNC);
		TreeNode c6 = new TreeNode(TreeNodeType.FUNC);
		TreeNode c7 = new TreeNode(TreeNodeType.FUNC);
		TreeNode c8 = new TreeNode(TreeNodeType.FUNC);
		c3.addChild(c4);
		c3.addChild(c5);
		c3.addChild(c6);
		c6.addChild(c7);
		c4.addChild(c8);
		
		SyntaxTree tree = new SyntaxTree();
		tree.print(e);
	}
}
