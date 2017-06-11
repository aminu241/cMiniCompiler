package util;

import java.util.ArrayList;

public class SymbolEntry {
	private String name;
	private String varType;
	private String Datatype;
	private ArrayList<SymbolEntry> refs;
	// register allocation
	private int reg;
	
	public SymbolEntry(String name, String dataType){
		this.name = name;
		this.Datatype = dataType;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVarType() {
		return varType;
	}
	public void setVarType(String varType) {
		this.varType = varType;
	}
	public String getDatatype() {
		return Datatype;
	}
	public void setDatatype(String datatype) {
		Datatype = datatype;
	}
	public ArrayList<SymbolEntry> getRefs() {
		return refs;
	}
	public void setRefs(ArrayList<SymbolEntry> refs) {
		this.refs = refs;
	}
	public int getReg() {
		return reg;
	}
	public void setReg(int reg) {
		this.reg = reg;
	}
	
}
