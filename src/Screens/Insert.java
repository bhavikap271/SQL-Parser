package Screens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Insert {

	private String tableName;
	private List<String> columns;
	private List<String> values;
	private boolean columnsPresent = false;

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public void parse(String sql) {

		// statement = statement.toLowerCase();

		/*
		 * if(tokens.length != 5 && tokens.length != 6){
		 * JOptionPane.showMessageDialog(null, "Invalid INSERT Statement",
		 * "Error", JOptionPane.ERROR_MESSAGE); return; }
		 */

		String[] tokens = sql.split("\\s+");

		int intoIndex = (sql.indexOf("INTO") != -1) ? sql.indexOf("INTO") : sql.indexOf("into");
		int valueIndex = (sql.indexOf("VALUES") != -1) ? sql.indexOf("VALUES") : sql.indexOf("values");

		if (intoIndex == -1) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax,Missing INTO clause", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (valueIndex == -1) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax,Missing VALUES clause", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// if reached here , means into and values are present;
		// check if at correct position
		if (!tokens[1].equalsIgnoreCase("INTO")) {

			JOptionPane.showMessageDialog(null, "Invalid location of INTO clause", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// INSERT INTO correct

		// check if columnNames present
		int firstBracketIndex = sql.indexOf("(");

		if (firstBracketIndex != -1 && firstBracketIndex < valueIndex) {
			this.columnsPresent = true;
		}

		// check if the brackets are balanced
		boolean bracketsBalanced = balancedBrackets(sql);

		if (!bracketsBalanced) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax: invalid brackets", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int closingBracketIndex = sql.indexOf(")", firstBracketIndex);

		System.out.println("columns Present:" + this.columnsPresent);

		// System.out.println("token4: "+tokens[4]);

		// check if values is at position 4 or 5
		if (!columnsPresent && tokens.length > 3 && !tokens[3].equalsIgnoreCase("values")) {
			JOptionPane.showMessageDialog(null, "Invalid position of VALUES clause", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// fetch tableName

		String tableName = "";

		System.out.println("columnsPresent" + columnsPresent);

		if (columnsPresent) {
			tableName = sql.substring(intoIndex, firstBracketIndex).trim().substring(4).trim();
		} else
			tableName = sql.substring(intoIndex, valueIndex).trim().substring(4).trim();

		if ("".equals(tableName)) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax,Missing tableName", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			System.out.println("TableName: " + tableName);
			this.tableName = tableName;
		}

		if (!GlobalUtil.validateTableName(this.tableName)) {
			// System.out.println("Table Name not valid");
			JOptionPane.showMessageDialog(null, "No such table exists", "Error", JOptionPane.ERROR_MESSAGE);
			return;

		}

		// fetch columnNames
		if (columnsPresent) {

			int begin = sql.indexOf(this.tableName) + tableName.length();

			String columnData = sql.substring(begin, valueIndex).trim();
			System.out.println("column Data: " + columnData);

			// check if each column has singleQuote around it
			if (!columnData.startsWith("(") || !columnData.endsWith(")")) {
				JOptionPane.showMessageDialog(null, "Invalid Syntax: Column Names not surrounded with ()", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			} else {

				int startIndex = columnData.indexOf("(");
				int endIndex = columnData.indexOf(")");
				String columnNames = columnData.substring(startIndex + 1, endIndex).trim();

				if ("".equals(columnNames)) {
					JOptionPane.showMessageDialog(null, "Invalid Syntax: Column Names missing", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else if (columnNames.startsWith(",") || columnNames.endsWith(",")) {

					JOptionPane.showMessageDialog(null, "Invalid Syntax : Comma at the beginning/end of col names",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;

				} else {

					String[] colNames = columnNames.split(",\\s*");
					this.columns = new ArrayList<String>();
					for (String name : colNames) {
						columns.add(name.trim());
					}
				}
			}

			System.out.println("SQL col names: " + this.columns);
			
			// check if trying to insert in primary key
			String tableKey = GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase());
			
			for(String name: this.columns){
				
				  if(tableKey.equalsIgnoreCase(name)){
					  JOptionPane.showMessageDialog(null, "Cannot insert dtaa in primary key", "Error", JOptionPane.ERROR_MESSAGE);
					  return;
				  }				
			}
			

			// check if values is present in correct location:

			String valueString = sql.substring(closingBracketIndex + 1, sql.indexOf("(", closingBracketIndex)).trim();

			System.out.println("value String: " + valueString);

			if (!valueString.equalsIgnoreCase("values")) {
				JOptionPane.showMessageDialog(null, "Missing VALUES clause", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// fetch columnValues
		String colValues = "";

		if (valueIndex + 6 == sql.length()) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax: Column Values Missing", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else
			colValues = sql.substring(valueIndex, sql.length()).trim().substring(7).trim();

		System.out.println("ColValues: " + colValues);


		if (!colValues.startsWith("(") || !colValues.endsWith(")")) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax: Invalid Format of Column Values", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else {

			int startIndex = colValues.indexOf("(");
			int endIndex = colValues.indexOf(")");

			this.values = new ArrayList<String>();

			String val = colValues.substring(startIndex + 1, endIndex).trim();

			if ("".equals(val)) {

				JOptionPane.showMessageDialog(null, "Invalid Syntax: Column Values missing", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;

			} else if (val.startsWith(",") || val.endsWith(",")) {
				JOptionPane.showMessageDialog(null, "Invalid Syntax : Comma at the beginning/end of col values",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// check if val is a comma separated string
			// System.out.println("val: "+val);
			String[] columnValues = val.split(",");

			boolean flag = true;

			/*
			 * for(String value : columnValues){ //System.out.println("value: "
			 * +value); if(!validateColVal(value.trim())){ flag = false; break;
			 * } }
			 */

			if (!flag) {
				JOptionPane.showMessageDialog(null, "Invalid Syntax: Missing single quotes in column values", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				for (String name : columnValues) {
					values.add(name.trim());
				}
			}

			// check if columNamesList and ValueList size matches
			if (this.columnsPresent && this.values != null && this.columns.size() != this.values.size()) {
				JOptionPane.showMessageDialog(null, "Invalid Syntax: Mismatch between data and columnNames size",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// once syntax is correct, check if table name and all columnNames
			// exists
			boolean isValid = validateSemantics();

			if (!isValid) {
				System.out.println("invalid semantics");
				return;
			} else {
				// save the record in the database
				// need to do this.
				boolean result = insertRecord();

				if (result) {
					JOptionPane.showMessageDialog(null, "Record Inserted Successfully", "Message",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				} else
					return;
			}
		}
	}

	private boolean insertRecord() {

		System.out.println("inside insert record");
		JSONObject newJson = new JSONObject();

		if (!this.columnsPresent) {

			HashMap<String, String> columnMap = GlobalUtil.fetchColumnNames(this.tableName);
			ArrayList<String> columnNames = GlobalUtil.fetchOnlyColumnNames(this.tableName);

			if (columnNames.size() < this.values.size()) {
				JOptionPane.showMessageDialog(null, "Mismatch between no of columns in table and values size", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			// check if all the varchar columns have single quotes
			int j = 0;
			for(String val : this.values){
				
				String name = columnNames.get(j);
				
				String dataType = GlobalUtil.getDataType(this.tableName, name);
				
				if("VARCHAR".equals(dataType)){
					
					 if(!val.startsWith("'") || !val.endsWith("'")){
						 JOptionPane.showMessageDialog(null, "Invalid Syntax: Missing single quote for column : "+(j+1), "Error",JOptionPane.ERROR_MESSAGE);
							return false; 
					 }
				}
				
				j++;
			}
			

			// ArrayList<String> colName = new ArrayList<String>(columnNames);
			int i = 0;

			for (String val : this.values) {

				String name = columnNames.get(i);
				// check if dataType matches
				// System.out.println("colName: "+name);
				if (!GlobalUtil.validateDataType(columnMap.get(name), val)) {
					JOptionPane.showMessageDialog(null, "Invalid Syntax: Mismatch between dataType and Value", "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}else {

					String dataType = GlobalUtil.getDataType(this.tableName, name);

					if ("VARCHAR".equals(dataType))
						newJson.put(name, val.substring(1, val.length() - 1));
					else if ("INT".equals(dataType)) {
						newJson.put(name, Long.valueOf(val));
					}else if("FLOAT".equals(dataType)){
						newJson.put(name, Float.valueOf(val));	
					}

				}
				i++;
			}

			// add data for primary key

			// get last primaryKey value from json record
			long lastKeyId = getLastPrimaryKey(this.tableName);
			String primaryKey = GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase());
			newJson.put(primaryKey, lastKeyId + 1);

			// update the b-tree:

			BPlusTreeIndexing btree = GlobalData.AttBTreeIndex.get(primaryKey);

			if (btree != null) {
				//System.out.println(this.tableName);
				String tnm = GlobalData.getTableName(this.tableName);
				JSONArray maintable = GlobalData.tableJSonArray.get(tnm);
				System.out.println(maintable.size());
				maintable.add(newJson);
				btree.insert(lastKeyId + 1, maintable.get(maintable.size() - 1));
			}

		} else {

			int i = 0;

			HashMap<String, String> columnMap = GlobalUtil.fetchColumnDataType(this.tableName);

			// check if all the varchar columns have single quotes
			int j = 0;
			for(String val : this.values){

				String name = this.columns.get(j);

				String dataType = GlobalUtil.getDataType(this.tableName, name);

				if("VARCHAR".equals(dataType)){

					if(!val.startsWith("'") || !val.endsWith("'")){
						JOptionPane.showMessageDialog(null, "Invalid Syntax: Missing single quote for column : "+(j+1), "Error",
								JOptionPane.ERROR_MESSAGE);
						return false; 
					}
				}

				j++;
			}

			// get the tablecolumnMap
			HashMap<String, String> tableColumnMap = GlobalUtil.getTableColumnNameMap(this.columns, tableName);

			for (String name : this.columns) {

				String val = this.values.get(i);
				// check if dataType matches

				String tableName = tableColumnMap.get(name);

				if (!GlobalUtil.validateDataType(columnMap.get(name.toLowerCase()), val)) {
					JOptionPane.showMessageDialog(null, "Invalid DataType: Mismatch between dataType and Value",
							"Error", JOptionPane.ERROR_MESSAGE);
					return false;
				} else {
					// remove single quotes from string

					String dataType = GlobalUtil.getDataType(this.tableName, name);

					if ("VARCHAR".equals(dataType))
						newJson.put(tableName, val.substring(1, val.length() - 1));
					else if ("INT".equals(dataType)) {
						newJson.put(tableName, Long.valueOf(val));
					}else if("FLOAT".equals(dataType)){
						newJson.put(tableName, Float.valueOf(val));	
					}
				}

				i++;
			}

			// add data for primary key
			// get last primaryKey value from json record
			long lastKeyId = getLastPrimaryKey(this.tableName);
			String primaryKey = GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase());
			newJson.put(primaryKey, lastKeyId + 1);

			// update the b-tree:

			BPlusTreeIndexing btree = GlobalData.AttBTreeIndex.get(primaryKey);

			if (btree != null) {
				String tnm = GlobalData.getTableName(this.tableName);
				JSONArray maintable = GlobalData.tableJSonArray.get(tnm);
				maintable.add(newJson);
				btree.insert(lastKeyId + 1, maintable.get(maintable.size() - 1));
			}
		}

		// save the json in the table.json file.
		//saveRecordInFile(newJson);
		return true;

	}

	private long getLastPrimaryKey(String tableName) {

		JSONParser parser = new JSONParser();

		long lastKeyId = 0;

		ArrayList<Long> keyData = new ArrayList<Long>();

		try {

			FileReader f1 = new FileReader("Data/Records/" + this.tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json1 = (JSONObject) obj;
			JSONArray headers = (JSONArray) json1.get("Records");

			int size = headers.size();

			// get the last record from json

			//System.out.println("Json size:" + size);

			if (size != 0) {

				for (int i = 0; i < size; i++) {

					JSONObject temp = (JSONObject) headers.get(i);
					// get the last primary key value
					//System.out.println();
					long keyValue = (Long) temp.get(GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase()));
					// lastKeyId = size;
					keyData.add(keyValue);
				}

				// sort the data
				Collections.sort(keyData, new Comparator<Long>() {
					public int compare(Long o1, Long o2) {
						return o2.compareTo(o1);
					}
				});

				// fetch the max value

				lastKeyId = keyData.get(0);

				System.out.println("lastKeyId" + lastKeyId);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lastKeyId;

	}

	private void saveRecordInFile(JSONObject newJson) {

		JSONParser parser = new JSONParser();
		try {

			FileReader f1 = new FileReader("Data/Records/" + this.tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json1 = (JSONObject) obj;
			// System.out.println(json1.toJSONString());
			try {

				JSONArray headers = (JSONArray) json1.get("Records");
				headers.add(newJson);
				json1.put("Records", headers);

			} catch (ClassCastException e) {
				JSONArray JA = new JSONArray();
				JA.add(newJson);
				json1.put("Records", JA);
			}

			// System.out.println(json1.toJSONString());
			File file = new File("Data/Records/" + this.tableName + ".json");
			FileWriter fw = null;
			BufferedWriter bw = null;
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			bw.write(json1.toJSONString());
			bw.flush();
			bw.close();
			f1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean validateSemantics() {

		if (!GlobalUtil.validateTableName(this.tableName)) {
			// System.out.println("Table Name not valid");
			JOptionPane.showMessageDialog(null, "Invalid Syntax: No such table exists", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;

		} else {
			// if table exists
			// check if columnNames are valid
			if (this.columnsPresent) {

				if (!GlobalUtil.validateColumnNames(this.columns, this.tableName)) {
					// System.out.println("Column Name not valid");
					JOptionPane.showMessageDialog(null, "Column Name invalid", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}

		return true;
	}

	private boolean balancedBrackets(String sql) {

		Stack<Character> stack = new Stack<Character>();

		for (int i = 0; i < sql.length(); i++) {

			if (sql.charAt(i) == '(')
				stack.push('(');

			else if (sql.charAt(i) == ')') {
				if (stack.isEmpty())
					return false;
				if (stack.pop() != '(')
					return false;
			} else if (sql.charAt(i) == '{' || sql.charAt(i) == '}' || sql.charAt(i) == '[' || sql.charAt(i) == ']')
				return false;

		}

		return stack.isEmpty();

	}

	private boolean validateColVal(String name) {

		if (!name.startsWith("'") || !name.endsWith("'"))
			return false;

		return true;

	}

}
