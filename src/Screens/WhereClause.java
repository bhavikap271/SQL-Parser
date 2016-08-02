package Screens;

public class WhereClause {
	
	String attribute1;
	String attribute2;
	char operation;
	boolean attribute2value;
	boolean bool, valid;
	boolean boolOP;	
	
	public String getAttribute1() {
		return attribute1;
	}
	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}
	public String getAttribute2() {
		return attribute2;
	}
	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}
	public char getOperation() {
		return operation;
	}
	public void setOperation(char operation) {
		this.operation = operation;
	}
	public boolean isAttribute2value() {
		return attribute2value;
	}
	public void setAttribute2value(boolean attribute2value) {
		this.attribute2value = attribute2value;
	}
	public boolean isBool() {
		return bool;
	}
	public void setBool(boolean bool) {
		this.bool = bool;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public boolean isBoolOP() {
		return boolOP;
	}
	public void setBoolOP(boolean boolOP) {
		this.boolOP = boolOP;
	}

}
