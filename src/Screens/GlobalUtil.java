package Screens;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GlobalUtil {

	public static boolean validateTableName(String name) {

		boolean tableExists = false;

		// check if tableName exists
		Iterator<String> itr = GlobalData.allTables.iterator();

		while (itr.hasNext()) {
			if (name.equalsIgnoreCase(itr.next())) {
				tableExists = true;
			}
		}

		return tableExists;

	}

	public static String GetAttType(String name) {
		String tablename = GlobalData.AttTableMap.get(name);
		String datatype = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("Data/Metadata/" + tablename + ".json"));
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {
				JSONObject curr = (JSONObject) headers.get(i);
				String temp = (String) curr.get("Column Name");
				if (temp.equalsIgnoreCase(name)) {
					return ((String) curr.get("Data Type"));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return datatype;
	}

	public static JSONObject concat2jobj(JSONObject jobj1, JSONObject jobj2) {
		JSONObject jobj_new = new JSONObject();
		Set<String> keys1 = jobj1.keySet();
		for (String key : keys1) {
			jobj_new.put(key, jobj1.get(key));
		}
		Set<String> keys2 = jobj2.keySet();
		for (String key : keys2) {
			jobj_new.put(key, jobj2.get(key));
		}

		return jobj_new;
	}

	public static JSONArray intersection2jsa(JSONArray jsa1, JSONArray jsa2, String tablename) {
		String pk = GlobalData.tablePrimaryKeyMap.get(tablename.toLowerCase());
		JSONArray jsa_new = new JSONArray();
		String type = GlobalUtil.GetAttType(pk);

		if (jsa1.size() <= jsa2.size()) {
			BPlusTreeIndexing tree = new BPlusTreeIndexing(type, pk, jsa2);
			for (int i = 0; i < jsa1.size(); i++) {
				JSONObject temp = (JSONObject) jsa1.get(i);
				if (type.equals("INT")) {
					long key = (Long) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				} else if (type.equals("FLOAT")) {
					float key = (float) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				} else {
					String key = (String) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				}
			}
		} else {
			BPlusTreeIndexing tree = new BPlusTreeIndexing(type, pk, jsa1);
			for (int i = 0; i < jsa2.size(); i++) {
				JSONObject temp = (JSONObject) jsa2.get(i);
				if (type.equals("INT")) {
					long key = (Long) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));

				} else if (type.equals("FLOAT")) {
					float key = (float) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				} else {
					String key = (String) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				}
			}
		}
		return jsa_new;
	}

	
	public static JSONArray union2jsa(JSONArray jsa1, JSONArray jsa2, String tablename) {
		String pk = GlobalData.tablePrimaryKeyMap.get(tablename.toLowerCase());
		JSONArray jsa_new = new JSONArray();
		String type = GlobalUtil.GetAttType(pk);

		if (jsa1.size() <= jsa2.size()) {
			BPlusTreeIndexing tree = new BPlusTreeIndexing(type, pk, jsa2);
			for (int i = 0; i < jsa1.size(); i++) {
				JSONObject temp = (JSONObject) jsa1.get(i);
				if (type.equals("INT")) {
					long key = (Long) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				} else if (type.equals("FLOAT")) {
					float key = (float) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				} else {
					String key = (String) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				}
			}
		} else {
			BPlusTreeIndexing tree = new BPlusTreeIndexing(type, pk, jsa1);
			for (int i = 0; i < jsa2.size(); i++) {
				JSONObject temp = (JSONObject) jsa2.get(i);
				if (type.equals("INT")) {
					long key = (Long) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));

				} else if (type.equals("FLOAT")) {
					float key = (float) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				} else {
					String key = (String) temp.get(pk);
					if (tree.search(key) != null)
						jsa_new.add(tree.search(key));
				}
			}
		}
		return jsa_new;
	}
	
	public static boolean validateColumnNames(List<String> columnNamesList, String tableName) {

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}

		JSONParser parser = new JSONParser();
		ArrayList<String> columnNames = new ArrayList<String>();
		boolean isValidColName = true;

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				columnNames.add(columnName);
			}

			// check if each column in the sql exists in the columnList
			for (String colName : columnNamesList) {
				boolean found = false;
				for (String tableCol : columnNames) {

					if (tableCol.equalsIgnoreCase(colName)) {
						found = true;
						break;
					}
				}

				if (!found) {
					isValidColName = false;
					break;
				} else
					continue;
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isValidColName;

	}

	/**
	 * To get column Names from table mapping with column names in insert query
	 * 
	 * @param conditions
	 * @param tableName
	 * @return
	 */
	public static HashMap<String, String> getTableColumnMap(List<WhereClause> conditions, String tableName) {

		HashMap<String, String> tableNameMap = new HashMap<String, String>();

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}

		JSONParser parser = new JSONParser();
		ArrayList<String> columnNames = new ArrayList<String>();
		boolean isValidColName = true;

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				columnNames.add(columnName);
			}

			// check if each column in the sql exists in the columnList
			for (WhereClause where : conditions) {
				boolean found = false;
				String colName = where.attribute1;
				for (String tableCol : columnNames) {

					if (tableCol.equalsIgnoreCase(colName)) {
						found = true;
						tableNameMap.put(colName, tableCol);
						break;
					}
				}

				if (!found) {
					isValidColName = false;
					break;
				} else
					continue;
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tableNameMap;
	}

	public static HashMap<String, String> getTableColumnNameMap(List<String> conditions, String tableName) {

		HashMap<String, String> tableNameMap = new HashMap<String, String>();

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}

		JSONParser parser = new JSONParser();
		ArrayList<String> columnNames = new ArrayList<String>();
		boolean isValidColName = true;

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				columnNames.add(columnName);
			}

			// check if each column in the sql exists in the columnList
			for (String sqlName : conditions) {
				boolean found = false;

				for (String tableCol : columnNames) {

					if (tableCol.equalsIgnoreCase(sqlName)) {
						found = true;
						tableNameMap.put(sqlName, tableCol);
						break;
					}
				}

				if (!found) {
					isValidColName = false;
					break;
				} else
					continue;
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tableNameMap;
	}

	public static boolean validateColumnNames(String colnm, String tableName) {

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}
		JSONParser parser = new JSONParser();
		ArrayList<String> columnNames = new ArrayList<String>();

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				columnNames.add(columnName);

			}

			// check if each in the sql exists in the columnList
			// if(!columnNames.contains(colnm)){
			//
			// return false;
			//
			// }
			Iterator<String> itr1 = columnNames.iterator();
			while (itr1.hasNext()) {
				if (itr1.next().equalsIgnoreCase(colnm)) {
					return true;
				}
			}

			return false;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static HashMap<String, String> fetchColumnNames(String tableName) {

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();

		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}
		JSONParser parser = new JSONParser();
		HashMap<String, String> columnDetailMap = new HashMap<String, String>();

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				String dataType = (String) temp1.get("Data Type");

				// System.out.println("Table primary key:
				// "+GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase()));

				if (!columnName.toLowerCase()
						.equalsIgnoreCase(GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase())))
					columnDetailMap.put(columnName, dataType);

			}

			f1.close();
			// check if each in the sql exists in the columnList
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return columnDetailMap;

	}

	public static String getDataType(String tableName, String att) {
		FileReader f1;
		String dataType = null;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}
		JSONParser parser = new JSONParser();
		HashMap<String, String> columnDetailMap = new HashMap<String, String>();

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				if (att.equalsIgnoreCase(((String) temp1.get("Column Name")))) {
					dataType = (String) temp1.get("Data Type");
					break;
				}

			}

			f1.close();

			// check if each in the sql exists in the columnList
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataType;

	}

	public static boolean validateDataType(String dataType, String val) {

		switch (dataType) {
		case "INT":
			try {
				int value = Integer.parseInt(val);
			} catch (Exception e) {
				return false;
			}
			break;
		case "VARCHAR":
			break;
		case "FLOAT":
			try {
				float value = Float.parseFloat(val);
			} catch (Exception e) {
				return false;
			}
			break;
		}
		return true;
	}

	public static HashMap<String, String> fetchColumnDataType(String tableName) {

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}

		JSONParser parser = new JSONParser();
		HashMap<String, String> columnDetailMap = new HashMap<String, String>();

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				String dataType = (String) temp1.get("Data Type");

				if (!columnName.toLowerCase()
						.equalsIgnoreCase(GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase())))
					columnDetailMap.put(columnName.toLowerCase(), dataType);
			}

			f1.close();

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return columnDetailMap;

	}

	public static ArrayList<String> fetchOnlyColumnNames(String tableName) {

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}

		JSONParser parser = new JSONParser();

		ArrayList<String> columnNames = new ArrayList<String>();

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				String dataType = (String) temp1.get("Data Type");

				if (!columnName.toLowerCase()
						.equalsIgnoreCase(GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase())))
					columnNames.add(columnName);
			}

			f1.close();

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return columnNames;

	}

	public static String getTableColumnName(String tableName, String attrName) {

		FileReader f1;
		Iterator<String> itr = GlobalData.allTables.iterator();
		while (itr.hasNext()) {
			String tnm = itr.next();
			if (tableName.equalsIgnoreCase(tnm)) {
				tableName = tnm;
				break;
			}
		}

		JSONParser parser = new JSONParser();

		ArrayList<String> columnNames = new ArrayList<String>();

		try {

			f1 = new FileReader("Data/MetaData/" + tableName + ".json");
			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {

				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;

				String columnName = (String) temp1.get("Column Name");
				columnNames.add(columnName);
			}

			// check if each column in the sql exists in the columnList
			for (String name : columnNames) {
				// boolean found = false;
				if (attrName.equalsIgnoreCase(name)) {
					return name;
				}
			}

			f1.close();

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	
	
	

}
