package util;

import java.util.ArrayList;

public class SymbolTable {
	private SymbolTable top;
	private ArrayList<SymbolTable> children;
	private ArrayList<SymbolEntry> ses;

	public SymbolTable(){
		this.ses =  new ArrayList<SymbolEntry>();
		this.setChildren(new ArrayList<SymbolTable>());
		this.setTop(null);
	}
	
	public SymbolTable(SymbolTable top){
		this.setTop(top);
		this.ses =  new ArrayList<SymbolEntry>();
		this.setChildren(new ArrayList<SymbolTable>());
	}
	
	public void print(){
		System.out.println("Name----------VarType----------DataType---------Reg-------");
		for(SymbolEntry se: this.ses){
			System.out.println(se.getName() + "\t\t" + se.getVarType() +
					"\t\t" + se.getDatatype() + "\t\t" + se.getReg());
		}
		System.out.println("-------------------------------");
		
		for(SymbolTable st: this.children){
			st.print();
		}
	}
	
	public void insertEntry(SymbolEntry se){
		this.ses.add(se);
	}
	
	public SymbolEntry lookupEntry(String name){
		for(SymbolEntry se: this.ses){
			if(se.getName().equals(name)){
				return se;
			}
		}
		return null;
	}
	
	public SymbolEntry lookupEntry2(String name){
		SymbolTable st = this;
		while(st != null){
			SymbolEntry se = st.lookupEntry(name);
			if(se != null){
				return se;
			}
			st = st.getTop();
		}
		return null;
	}
	
	public SymbolEntry getByIndex(int index){
		if(index < this.ses.size()){
			return this.ses.get(index);
		}else{
			System.out.println("Symbol table out of bound!");
			System.exit(1);
		}
		return null;
	}
	public ArrayList<SymbolEntry> getSes() {
		return ses;
	}

	public void setSes(ArrayList<SymbolEntry> ses) {
		this.ses = ses;
	}

	public SymbolTable getTop() {
		return top;
	}

	public void setTop(SymbolTable top) {
		this.top = top;
	}

	public ArrayList<SymbolTable> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<SymbolTable> children) {
		this.children = children;
	}
	
}
