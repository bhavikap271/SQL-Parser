package Screens;

public class Table {

	private String name;
	
	private String alias;
	
	
	
	public Table(String tableName) {
		this.name = tableName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	
	
}
