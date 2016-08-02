package Screens;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bptree.BTree;

public class GlobalData {

	static List<String> allTables;
	static HashMap<String, String> tablePrimaryKeyMap;
	static HashMap<String, JSONArray> tableJSonArray = new HashMap<String, JSONArray>();
	static HashMap<String, BPlusTreeIndexing> AttBTreeIndex = new HashMap<String, BPlusTreeIndexing>();
	static HashMap<String, String> AttTableMap = new HashMap<String, String>();

	public static void addAttTableMap(String tableName) throws Exception {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("Data/Metadata/" + tableName + ".json"));
			JSONObject json = (JSONObject) obj;
			JSONArray headers = (JSONArray) json.get("headers");

			for (int i = 0; i < headers.size(); i++) {
				JSONObject curr = (JSONObject) headers.get(i);
				AttTableMap.put((String) curr.get("Column Name"), tableName);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void initAttTableMap() throws Exception {
		for (String tableName : GlobalData.allTables) {
			addAttTableMap(tableName);
		}
	}
	public static String getTableName(String name) {

		boolean tableExists = false;

		// check if tableName exists
		Iterator<String> itr = GlobalData.allTables.iterator();
		String tnm=null;
		while (itr.hasNext()) {
			tnm = itr.next();
			if (name.equalsIgnoreCase(tnm)) {
				break;
			}
		}

		return tnm;

	}

	public static void initAttBTreeIndex() throws Exception {
		for (String tableName : GlobalData.allTables) {
			GlobalData.addAttBTreeIndex(tableName, GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase()));
		}
	}

	public static void initTableJSonArray() throws Exception {
		for (String tableName : GlobalData.allTables) {
			tableJSonArray.put(tableName, readJSonFile(tableName));
		}
	}

	public static void addAttBTreeIndex(String table_name, String att) throws Exception {
		AttBTreeIndex.put(att, new BPlusTreeIndexing(table_name, att));
	}

	public static JSONArray readJSonFile(String tablename) {
		JSONParser parser = new JSONParser();
		JSONArray headers = new JSONArray();
		try {
			Object obj = parser.parse(new FileReader("Data/Records/" + tablename + ".json"));
			JSONObject json = (JSONObject) obj;
			headers = (JSONArray) json.get("Records");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return headers;
	}

	public static void addTableJSonArray(String tableName) throws Exception {
		tableJSonArray.put(tableName, readJSonFile(tableName));
	}

	public static void initTableArray() throws Exception {
		allTables = new ArrayList<String>();
		String fileName = "Data/TableIndex.txt";

		// This will reference one line at a time
		String line = null;

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				allTables.add(line);
				System.out.println(line);
			}

			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		}
	}

	public static void initprimaryKey() throws Exception {
		tablePrimaryKeyMap = new HashMap<>();
		Iterator<String> itr = allTables.iterator();
		JSONParser parser = new JSONParser();
		while (itr.hasNext()) {
			String tnm = (itr.next()).toString();

			FileReader f1 = new FileReader("Data/Metadata/" + tnm + ".json");

			Object obj = parser.parse(f1);
			JSONObject json = (JSONObject) obj;

			JSONArray headers = (JSONArray) json.get("headers");
			int size = headers.size();

			for (int i = 0; i < size; i++) {
				Object temp = parser.parse(headers.get(i).toString());
				JSONObject temp1 = (JSONObject) temp;
				if ((boolean) temp1.get("Key")) {
					String keynm = (String) temp1.get("Column Name");

					tablePrimaryKeyMap.put(tnm.toLowerCase(), keynm);
					// System.out.println("tablePrimaryKeyMap:
					// "+tablePrimaryKeyMap.get(tnm));
				}

			}

			f1.close();
		}
	}

	public static void updateTableFile() throws Exception {
		File file = new File("Data/TableIndex.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		fw = new FileWriter(file.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		String tables = "";
		Iterator<String> itr = allTables.iterator();
		while (itr.hasNext()) {
			tables += (itr.next()).toString() + "\n";
		}
		System.out.println(tables);
		bw.write(tables);
		bw.flush();
		bw.close();
		initprimaryKey();

	}

	public static void deleteTableFile(String tnm) throws Exception {
		System.out.println(tnm);
		allTables.remove(tnm);
		updateTableFile();
		File file1 = new File("Data/Metadata/" + tnm + ".json");
		boolean f = file1.delete();
		System.out.println(f);
		File file2 = new File("Data/Records/" + tnm + ".json");

		file2.delete();
		initprimaryKey();
	}

}
