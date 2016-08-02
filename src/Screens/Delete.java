package Screens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Delete {

	String tableName;
	List<WhereClause> whereConditions = new ArrayList<WhereClause>();
	boolean conditionFlag = false;
	HashMap<String, String> conditions = new HashMap<String, String>();
	String conditionOp;
	boolean tablePrimaryKeyInWhere = false;
	HashMap<String, String> tableColumnMap;

	public boolean parse(String sql) {

		// from clause exists
		int fromIndex = (sql.indexOf("FROM") != -1) ? sql.indexOf("FROM") : sql.indexOf("from");

		if (fromIndex == -1) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax,Missing FROM clause", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// where clause exists

		int whereIndex = (sql.indexOf("WHERE") != -1) ? sql.indexOf("WHERE") : sql.indexOf("where");

		// check if from is at position 2
		String[] tokens = sql.split("\\s+");

		// is * present

		if (tokens.length > 2 && !tokens[1].equals("*") && !tokens[1].equalsIgnoreCase("from")) {

			JOptionPane.showMessageDialog(null, "Invalid Syntax, position of from clause is wrong", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;

		} else if (tokens.length > 2 && tokens[2].equals("*") && !tokens[3].equalsIgnoreCase("from")) {

			JOptionPane.showMessageDialog(null, "Invalid Syntax, position of from clause is wrong", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// DELETE FROM correct

		// check if table name is present

		String tableName = "";

		if (whereIndex != -1) {
			tableName = sql.substring(fromIndex, whereIndex).trim().substring(4).trim();
		} else if (tokens.length > 2) {

			tableName = tokens[2];

		} else {
			JOptionPane.showMessageDialog(null, "Invalid Syntax,table name missing", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if ("".equals(tableName.trim())) {

			JOptionPane.showMessageDialog(null, "Invalid Syntax,table name missing", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		this.tableName = tableName;

		// delete from table correct

		// check the where clause

		if (tokens.length > 3 && !"where".equalsIgnoreCase(tokens[3])) {

			JOptionPane.showMessageDialog(null, "Invalid Syntax,Missing WHERE clause", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;

		} else {

			// get the string after where clause
			String condition = sql.substring(whereIndex, sql.length()).trim().substring(5).trim();

			System.out.println("conditions: " + condition);

			if ("".equals(condition)) {

				JOptionPane.showMessageDialog(null, "Invalid Syntax, Missing conditions after WHERE clause", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;

			} else if (condition.startsWith("and") || condition.startsWith("or") || condition.endsWith("and")
					|| condition.endsWith("or")) {

				JOptionPane.showMessageDialog(null, "Invalid Syntax, Missing conditions before/after AND or OR",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;

			} else {

				boolean isValid = fetchWhereClause(condition);

				if (!isValid) {
					JOptionPane.showMessageDialog(null, "Invalid syntax, format of where clause is incorrect", "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}

			}
		}

		// if syntax is correct, check if tableName and columnName exists
		if (!GlobalUtil.validateTableName(this.tableName)) {
			JOptionPane.showMessageDialog(null, "Invalid Syntax: No such table exists", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;

		}

		/*
		 * validate if colName exists or not
		 */
		boolean validColNames = validateColNames();

		if (!validColNames) {
			JOptionPane.showMessageDialog(null, "Invalid Column Name", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		/// if everything is good, delete records
		tableColumnMap = GlobalUtil.getTableColumnMap(this.whereConditions, tableName);

		//deleteRecords();

		return true;

	}

	public boolean validateColNames() {

		List<String> colNames = new ArrayList<String>();

		for (WhereClause clause : this.whereConditions) {
			colNames.add(clause.attribute1);
		}

		if(!GlobalUtil.validateColumnNames(colNames, this.tableName))
			return false;

		return true;

	}

	public boolean fetchWhereClause(String sql) {

		if (sql.indexOf("=", 0) == -1 && sql.indexOf("<", 0) == -1 && sql.indexOf(">", 0) == -1) {
			return false;
		}

		System.out.println("inside fetchWhereClause");

		String[] firstConditionArr = sql.split("\\s+");

		boolean conditionFlag = true;
		boolean andOrFlag = false;

		for (String condition : firstConditionArr) {

			System.out.println(condition);

			if (conditionFlag) {
				char operator;
				String opt;

				if (condition.indexOf("=") != -1) {
					operator = '=';
					opt = "=";
				} else if (condition.indexOf(">") != -1) {
					operator = '>';
					opt = ">";
				} else if (condition.indexOf("<") != -1) {
					operator = '<';
					opt = "<";
				} else {
					return false;
				}

				System.out.println("i am here");
				// split based on operator
				String[] columData = condition.split(opt);

				if (columData.length < 2)
					return false;

				String colName = columData[0];
				String colVal = columData[1];

				if ("".equals(colName.trim()) || "".equals(colVal.trim()))
					return false;

				System.out.println("colName:" + colName);
				System.out.println("colVal:" + colVal);

				if (colName.equalsIgnoreCase(GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase())))
					this.tablePrimaryKeyInWhere = true;

				// conditions.put(colName.trim(), colVal.trim());
				WhereClause where = new WhereClause();
				where.attribute1 = colName;
				where.attribute2 = colVal;
				where.operation = operator;

				this.whereConditions.add(where);

				conditionFlag = false;
				andOrFlag = true;

			} else if (andOrFlag) {

				if (condition.equalsIgnoreCase("and"))
					this.conditionOp = "and";
				else if (condition.equalsIgnoreCase("or"))
					this.conditionOp = "or";
				else {
					return false;
				}

				andOrFlag = false;
				conditionFlag = true;

			}

		}

		return true;
	}
	

	private boolean validateColVal(String val) {

		if (!val.startsWith("'") || !val.endsWith("'"))
			return false;

		return true;

	}

	public void deleteRecords(){
		
		System.out.println("inside delete record");
		
		String primaryKey = GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase());

		BPlusTreeIndexing  bplusTree = GlobalData.AttBTreeIndex.get(primaryKey);

		if(tablePrimaryKeyInWhere && bplusTree != null){
 
			// use b-tree to fetch Json objects
			// if only single where clause then fetch records using B-tree
			if(conditionOp == null){

				// get the json objects from  B-tree
				WhereClause whereClause = this.whereConditions.get(0);			    
				//BPlusTreeIndexing  bplusTree = GlobalData.AttBTreeIndex.get(whereClause.attribute1);	
				String operation = whereClause.operation + "";
				JSONArray jsonArray = bplusTree.qBptree(whereClause.attribute1,operation,Long.valueOf(whereClause.attribute2));
				
				System.out.println(jsonArray);

				if(jsonArray.size() != 0){
					
					int size = jsonArray.size();

					String actualTableName = GlobalData.getTableName(this.tableName);
					JSONArray maintable = GlobalData.tableJSonArray.get(actualTableName);

					//String primaryKey = GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase());

					for(int i=0; i < size; i++){		

						JSONObject temp = (JSONObject) jsonArray.get(i);
						int index =  maintable.indexOf(temp);
						System.out.println("index in MAIN JSON:"+index);
						long keyValue = (Long)temp.get(primaryKey);

						maintable.remove(index);
						
						// remove from btree
						bplusTree.delete(keyValue);
						//System.out.println(bplusTree.search(keyValue).toString());

						System.out.println("Id:"+keyValue+": removed from btree: "+bplusTree.search(keyValue));
						System.out.println("Id:"+(keyValue +1)+":  " + bplusTree.search(keyValue+1)==null);

					}
					
				/*	// delete records in file
					try {

						JSONObject newJson = new JSONObject();
						newJson.put("Records", maintable);

						File file = new File("Data/Records/" + this.tableName + ".json");
						
						if(file.exists()){
							file.delete();
						}
						
						FileWriter fw = null;
						BufferedWriter bw = null;

						fw = new FileWriter(file.getAbsoluteFile());
						bw = new BufferedWriter(fw);

						bw.write(newJson.toJSONString());
						bw.flush();
						bw.close();
						fw.close();


					}catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} */
				}
			}else if("And".equalsIgnoreCase(conditionOp)){
				// get the json array based on primary key and filter them based on other conditions
				
				System.out.println("inside and");
				JSONArray jsonArray = getJSONObjectsBasedOnPrimaryKey(primaryKey);
				
				//System.out.println(jsonArray.toJSONString());
				
				if(jsonArray.size() != 0){

					String actualTableName = GlobalData.getTableName(this.tableName);
					JSONArray maintable = GlobalData.tableJSonArray.get(actualTableName);
	
					// filter further based on other conditions					
					for(int i = 0; i < jsonArray.size(); i++){						
						// check if other conditions match
						JSONObject jsonObject = (JSONObject)jsonArray.get(i);
						boolean match = allConditionsExceptPrimaryKey(jsonObject,primaryKey);

						if(match){								
							//delete the object							
							JSONObject temp = (JSONObject) jsonArray.get(i);
							int index =  maintable.indexOf(temp);
							//System.out.println("index in MAIN JSON:"+index);
							long keyValue = (Long)temp.get(primaryKey);

							maintable.remove(index);							
							// remove from btree
							bplusTree.delete(keyValue);
						}
					}
					// delete from file
					//deleteRecordsFromFile(maintable);
				}				
			}else if("OR".equalsIgnoreCase(this.conditionOp)){
				
				System.out.println("inside or");
                 				
				JSONArray jsonArray = getJSONObjectsBasedOnPrimaryKey(primaryKey);

				String actualTableName = GlobalData.getTableName(this.tableName);
				JSONArray maintable = GlobalData.tableJSonArray.get(actualTableName);
				
			    JSONArray jsonforOR = new JSONArray();
				
				for(int i = 0; i < maintable.size(); i++){
				
				     JSONObject jsonObject = (JSONObject)maintable.get(i);
					  
				     boolean match = eitherConditionsMatchExceptPrimaryKey(jsonObject,primaryKey);
				     
				     if(match){				    	 
				    	 jsonforOR.add(jsonObject);				    	 
				     }
				}
				
				// delete from maintable
				JSONArray finalJSON = mergeTwoJsonArrays(jsonforOR,jsonArray,primaryKey);
				
				for(int j = 0 ; j < finalJSON.size(); j++){
					
					JSONObject temp = (JSONObject) finalJSON.get(j);
					int index =  maintable.indexOf(temp);
				
					long keyValue = (Long)temp.get(primaryKey);

					maintable.remove(index);							
					// remove from btree
					bplusTree.delete(keyValue);
				
				}
				
				// delete from file				
			  //deleteRecordsFromFile(maintable);
							
			}
		}else{
		  
		//	JSONParser parser = new JSONParser();
		
			
			//try {

				/*FileReader f1 = new FileReader("Data/Records/" + this.tableName + ".json");
				Object obj = parser.parse(f1);
				JSONObject json = (JSONObject) obj;
				JSONArray headers = (JSONArray) json.get("Records");
				f1.close();*/

				String actualTableName = GlobalData.getTableName(this.tableName);
				JSONArray headers = GlobalData.tableJSonArray.get(actualTableName);
				
				for (int i = 0; i < headers.size(); i++) {

					JSONObject temp = (JSONObject)headers.get(i);

					if("And".equalsIgnoreCase(conditionOp)){

						System.out.println("conditionOp: "+this.conditionOp);			    	
						boolean result = allConditionsMatch(temp);

						if(result){
							// change here
							headers.remove(i);
							if(bplusTree != null){
								 long keyValue = (Long)temp.get(primaryKey);
								 bplusTree.delete(keyValue);
							}
												
						}	

					}else if("Or".equalsIgnoreCase(conditionOp)){

						System.out.println("conditionOp: "+this.conditionOp);			    	
						boolean result = eitherConditionsMatch(temp);

						if(result){		
							// change here
							headers.remove(i);
							
							if(bplusTree != null){
								 long keyValue = (Long)temp.get(primaryKey);
								 bplusTree.delete(keyValue);
							}	
							
						}

					}else{			    	
						// there is only one condition in the where clause			    	
						boolean result = checkIfConditionMatch(temp);
						if(result){
							// change here
							System.out.println("matched");
							
							headers.remove(i);
							
							if(bplusTree != null){
								 long keyValue = (Long)temp.get(primaryKey);
								 bplusTree.delete(keyValue);
							}
							
						}
					}	
				}

				// write the file back to disk
				/*json.put("Records", headers);
				//System.out.println(json.toJSONString());

				File file = new File("Data/Records/" + tableName + ".json");
				FileWriter fw = null;
				BufferedWriter bw = null;
				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);

				bw.write(json.toJSONString());
				bw.flush();
				fw.close();
				bw.close();			

				System.out.println("data deleted");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}*/		
		}
	}

	public boolean eitherConditionsMatch(JSONObject temp) {

		// iterate through each condition and check
		boolean match = false;

		for (WhereClause whereClause : this.whereConditions) {

			String colName = whereClause.attribute1;
			String colVal = whereClause.attribute2;

			// get table columnName
			String tableColName = tableColumnMap.get(colName);

			Object value = temp.get(tableColName);
			char operator = whereClause.operation;

			if (value != null && !(value instanceof String)) {

				if (operator == '>') {

					System.out.println("Inside > ");
					// System.out.println("Table value: "+value.toString());

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (actualVal.compareTo(searchVal) > 0) {
						match = true;
						break;
					}

				} else if (operator == '<') {

					System.out.println("Inside < ");

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (actualVal.compareTo(searchVal) < 0) {
						match = true;
						break;
					}
				} else if (operator == '=') {

					System.out.println("Inside =");

					System.out.println("col val: " + colVal);

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (searchVal.compareTo(actualVal) == 0) {
						match = true;
						break;
					}
				}
			} else {

				if (value != null && colVal != null) {

					colVal = colVal.substring(1, colVal.length() - 1);

					//System.out.println("after substring: " + colVal);

					if (colVal.equalsIgnoreCase(value.toString())) {

						System.out.println(colVal + " matches " + value);
						match = true;
						break;

					}
				}
			}
		}

		return match;
	}

	public boolean allConditionsMatch(JSONObject temp) {

		boolean allConditionsMatch = true;

		System.out.println("inside allConditionsMatch");

		for (WhereClause whereClause : this.whereConditions) {

			String colName = whereClause.attribute1;
			String colVal = whereClause.attribute2;

			System.out.println("colName:" + colName);

			System.out.println("colVal:" + colVal);

			// get table columnName
			String tableColName = tableColumnMap.get(colName);

			Object value = temp.get(tableColName);
			char operator = whereClause.operation;

			if (value != null && !(value instanceof String)) {

				if (operator == '>') {

					System.out.println("Inside > ");
					System.out.println("Table value: " + value.toString());

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (actualVal.compareTo(searchVal) <= 0) {
						allConditionsMatch = false;
						break;
					}

				} else if (operator == '<') {

					System.out.println("Inside < ");

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (actualVal.compareTo(searchVal) >= 0) {
						allConditionsMatch = false;
						break;
					}

				} else if (operator == '=') {

					System.out.println("Inside =");

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (searchVal.compareTo(actualVal) != 0) {
						allConditionsMatch = false;
						break;
					}
				}
			} else {

				if (value != null && colVal != null) {

					colVal = colVal.substring(1, colVal.length() - 1);
					System.out.println("after substring: " + colVal);

					if (colVal.equalsIgnoreCase(value.toString())) {
						System.out.println(colVal + " matches " + value);
						continue;
					} else {

						allConditionsMatch = false;
						break;
					}

				} else {
					allConditionsMatch = false;
					break;
				}
			}
		}

		return allConditionsMatch;

	}

	public boolean checkIfConditionMatch(JSONObject temp) {

		boolean match = true;

		//System.out.println("inside checkIfConditionMatch");

		for (WhereClause whereClause : this.whereConditions) {

			String colName = whereClause.attribute1;
			String colVal = whereClause.attribute2;

			// get table columnName
			String tableColName = tableColumnMap.get(colName);

			Object value = temp.get(tableColName);
			char operator = whereClause.operation;

			if (value != null && !(value instanceof String)) {

				if (operator == '>') {

					System.out.println("Inside > ");
					System.out.println("Table value: " + value.toString());

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (actualVal.compareTo(searchVal) <= 0) {
						match = false;
						break;
					}

				} else if (operator == '<') {

					System.out.println("Inside < ");

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (actualVal.compareTo(searchVal) >= 0) {
						match = false;
						break;
					}
				} else if (operator == '=') {

					System.out.println("Inside =");

					BigDecimal searchVal = new BigDecimal(colVal);
					BigDecimal actualVal = new BigDecimal(value.toString());

					if (searchVal.compareTo(actualVal) != 0) {
						match = false;
						break;
					} else {
						System.out.println("match = true for " + searchVal + "," + actualVal);
					}
				}
			} else {

				if (value != null && colVal != null) {

					colVal = colVal.substring(1, colVal.length() - 1);

					//System.out.println("after substring: " + colVal);

					if (colVal.equals(value.toString())) {
						match = true;
						System.out.println(colVal + " matches "+ value);
						break;
					} else {

						match = false;
						break;
					}

				} else {
					match = false;
					break;
				}
			}
		}

		return match;

	}

	public boolean deleteInJson(JSONArray jsonArray) {

		int size = jsonArray.size();

		
		String actualTableName = GlobalData.getTableName(this.tableName);
		JSONArray maintable = GlobalData.tableJSonArray.get(actualTableName);

		String primaryKey = GlobalData.tablePrimaryKeyMap.get(this.tableName.toLowerCase());

		for (int i = 0; i < size; i++) {

			JSONObject temp = (JSONObject) jsonArray.get(i);
			int index = maintable.indexOf(temp);
			System.out.println("index in MAIN JSON:" + index);
			long keyValue = (Long) temp.get(primaryKey);

			maintable.remove(index);
		}

		// delete records in file

		try {
			
			JSONObject newJson = new JSONObject();
			newJson.put("Records", maintable);

			File file = new File("Data/Records/" + this.tableName + ".json");
			FileWriter fw = null;
			BufferedWriter bw = null;

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			bw.write(newJson.toJSONString());
			bw.flush();
			bw.close();
			fw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	 public JSONArray getJSONObjectsBasedOnPrimaryKey(String primaryKey){

		WhereClause whereClause = null;

		for(WhereClause where:this.whereConditions){

			if(where.attribute1.equalsIgnoreCase(primaryKey)){

				whereClause = where;
				break;
			}
		}

		// get the json objects based on this primary key
		BPlusTreeIndexing  bplusTree = GlobalData.AttBTreeIndex.get(whereClause.attribute1);	
		String operation = whereClause.operation + "";
		JSONArray jsonArray = bplusTree.qBptree(whereClause.attribute1,operation, Long.valueOf(whereClause.attribute2));

		return jsonArray;   

	}
	
	 
	 public boolean allConditionsExceptPrimaryKey(JSONObject temp,String primaryKey){

			boolean allConditionsMatch = true;

			//String primaryKey = GlobalData.tablePrimaryKeyMap.get(this.tableName);

			System.out.println("inside allConditionsMatch");

			for(WhereClause whereClause: this.whereConditions){

				String colName = whereClause.attribute1;

				if(colName.equalsIgnoreCase(primaryKey))
					continue;

				String colVal = whereClause.attribute2;

				System.out.println("colName:" + colName);

				System.out.println("colVal:" + colVal);

				// get table columnName
				String tableColName = tableColumnMap.get(colName);					 

				Object value = temp.get(tableColName);			 
				char operator = whereClause.operation;

				if(value != null && !(value instanceof String)){

					if(operator == '>'){

						System.out.println("Inside > ");     							 
						System.out.println("Table value: "+value.toString());

						BigDecimal searchVal = new BigDecimal(colVal);
						BigDecimal actualVal = new BigDecimal(value.toString());

						if(actualVal.compareTo(searchVal) <= 0){
							allConditionsMatch = false;
							break;
						}	

					}else if(operator == '<'){

						System.out.println("Inside < ");

						BigDecimal searchVal = new BigDecimal(colVal);
						BigDecimal actualVal = new BigDecimal(value.toString());

						if(actualVal.compareTo(searchVal) >= 0){
							allConditionsMatch = false;
							break;
						}

					}else if(operator == '=') {

						System.out.println("Inside ="); 

						BigDecimal searchVal = new BigDecimal(colVal);
						BigDecimal actualVal = new BigDecimal(value.toString()); 

						if(searchVal.compareTo(actualVal) != 0){
							allConditionsMatch = false;
							break;								
						}		
					}
				}else{		

					if(value != null && colVal != null){	

						colVal = colVal.substring(1, colVal.length()-1);						 
						System.out.println("after substring: "+colVal);

						if(colVal.equals(value.toString())){
							System.out.println(colVal+" matches "+value);
							continue;
						}else{

							allConditionsMatch = false;
							break;
						}

					}else{
						allConditionsMatch = false;
						break;
					}												 
				}				
			}

			return allConditionsMatch;	   

		} 

	 
	 public void deleteRecordsFromFile(JSONArray maintable){
		 
		 try {

				JSONObject newJson = new JSONObject();
				newJson.put("Records", maintable);

				File file = new File("Data/Records/" + this.tableName + ".json");
				
				if(file.exists()){
					file.delete();
				}
				
				FileWriter fw = null;
				BufferedWriter bw = null;

				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);

				bw.write(newJson.toJSONString());
				bw.flush();
				bw.close();
				fw.close();


			}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 
		 
		 System.out.println("Records deleted");
	 
	 }

	 
	 public boolean eitherConditionsMatchExceptPrimaryKey(JSONObject temp, String primaryKey){
		 
		    //iterate through each condition and check
			boolean match = false;

			for (WhereClause whereClause : this.whereConditions) {

				String colName = whereClause.attribute1;
				
				if(colName.equalsIgnoreCase(primaryKey))
					  continue;
				
				String colVal = whereClause.attribute2;

				// get table columnName
				String tableColName = tableColumnMap.get(colName);

				Object value = temp.get(tableColName);
				char operator = whereClause.operation;

				if (value != null && !(value instanceof String)) {

					if (operator == '>') {

						System.out.println("Inside > ");
						// System.out.println("Table value: "+value.toString());

						BigDecimal searchVal = new BigDecimal(colVal);
						BigDecimal actualVal = new BigDecimal(value.toString());

						if (actualVal.compareTo(searchVal) > 0) {
							match = true;
							break;
						}

					} else if (operator == '<') {

						System.out.println("Inside < ");

						BigDecimal searchVal = new BigDecimal(colVal);
						BigDecimal actualVal = new BigDecimal(value.toString());

						if (actualVal.compareTo(searchVal) < 0) {
							match = true;
							break;
						}
					} else if (operator == '=') {

						System.out.println("Inside =");

						System.out.println("col val: " + colVal);

						BigDecimal searchVal = new BigDecimal(colVal);
						BigDecimal actualVal = new BigDecimal(value.toString());

						if (searchVal.compareTo(actualVal) == 0) {
							match = true;
							break;
						}
					}
				} else {

					if (value != null && colVal != null) {

						colVal = colVal.substring(1, colVal.length() - 1);

						System.out.println("after substring: " + colVal);

						if (colVal.equals(value.toString())) {

							System.out.println(colVal + " matches " + value);
							match = true;
							break;

						}
					}
				}
			}

			return match;
		 		 
	 }
	 
	 public JSONArray mergeTwoJsonArrays(JSONArray arr1, JSONArray arr2, String primaryKey){
		 
		 JSONArray headers = new JSONArray();
		 
		 for(int i = 0; i < arr2.size(); i++){
			 
			 JSONObject temp1 = (JSONObject)arr2.get(i);
			 headers.add(temp1);
			 
		 }
		 
		 Iterator<JSONObject> itr1 = arr1.iterator();
		 
			while(itr1.hasNext())
			{
				boolean pre =false;
				JSONObject temp1 = itr1.next();
				
				Iterator<JSONObject> itr2 = arr2.iterator();
				
				while(itr2.hasNext())
				{
					JSONObject temp2 = (JSONObject)itr2.next();
					if(temp1.get(primaryKey) == temp2.get(primaryKey))
					{
						pre=true;
						break;
					}
				}
				if(!pre)
				{
					headers.add(temp1);
					pre=false;
				}
			} 
		 
			return headers;
	 }
	 
}
