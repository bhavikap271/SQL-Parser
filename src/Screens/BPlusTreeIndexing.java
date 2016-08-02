package Screens;

import bptree.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BPlusTreeIndexing extends BTree {
	JSONParser parser = new JSONParser();

	public BPlusTreeIndexing(String table_name, String att) {
		String type = GlobalUtil.GetAttType(att);

		JSONArray headers = GlobalData.tableJSonArray.get(table_name);
		for (int i = 0; i < headers.size(); i++) {
			JSONObject currJson;
			currJson = (JSONObject) headers.get(i);
			if (type.equals("INT")) {
				long key = (Long) currJson.get(att);
				this.insert(key, headers.get(i));
			} else if (type.equals("FLOAT")) {
				float key = (float) currJson.get(att);
				this.insert(key, headers.get(i));
			} else {
				String key = (String) currJson.get(att);
				this.insert(key, headers.get(i));
			}
		}
	}

	public BPlusTreeIndexing(String type, String att, JSONArray headers) {
		for (int i = 0; i < headers.size(); i++) {
			JSONObject currJson;
			currJson = (JSONObject) headers.get(i);
			if (type.equals("INT")) {
				long key = (Long) currJson.get(att);
				this.insert(key, headers.get(i));
			} else if (type.equals("FLOAT")) {
				float key = (float) currJson.get(att);
				this.insert(key, headers.get(i));
			} else {
				String key = (String) currJson.get(att);
				this.insert(key, headers.get(i));
			}
		}
	}

	public BTree GetBPlusTreeIndexing(JSONArray headers, String table_name, String att) {
		BTree tree = new BTree();
		return tree;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray qBptree(String att1, String op, Long value) {
		JSONArray result = new JSONArray();
		BPlusTreeIndexing tree = GlobalData.AttBTreeIndex.get(att1);
		// tree.printbtree();
		if (op.equals("=")) {
			if (tree.search(value) != null)
				result.add(tree.search(value));
		}

		if (op.equals(">") || op.equals("<")) {
			return tree.SearchFrom(op, value);
		}

		// if (op.equals(">=")) {
		// if (tree.search(value) != null)
		// result.add(tree.search(value));
		//
		// JSONArray range = tree.SearchFrom(">", value);
		// for (int i = 0; i < range.size(); i++)
		// result.add(range.get(i));
		// }
		// if (op.equals("<=")) {
		// if (tree.search(value) != null)
		// result.add(tree.search(value));
		//
		// JSONArray range = tree.SearchFrom("<", value);
		// for (int i = 0; i < range.size(); i++)
		// result.add(range.get(i));
		// }

		return result;
	}

	// variant
	public static JSONArray qBptree(Long value, String op, String att1) {
		return qBptree(att1, op, value);
	}

	public static JSONArray qBptree(String table1, String att1, String op, String table2, String att2) {
		JSONArray result = new JSONArray();
		BPlusTreeIndexing tree1 = GlobalData.AttBTreeIndex.get(att1);
		BPlusTreeIndexing tree2 = GlobalData.AttBTreeIndex.get(att2);
		JSONArray jsa1 = GlobalData.tableJSonArray.get(table1);
		JSONArray jsa2 = GlobalData.tableJSonArray.get(table2);
		if (tree1 != null && tree2 != null) {
			if (op.equals("=")) {
				if (jsa1.size() <= jsa2.size()) {
					for (int i = 0; i < jsa1.size(); i++) {
						JSONObject temp1 = (JSONObject) jsa1.get(i);
						JSONObject temp2 = (JSONObject) tree2.search((long) temp1.get(att1));
						if (temp2 != null)
							result.add(GlobalUtil.concat2jobj(temp1, temp2));
					}
				} else {
					for (int i = 0; i < jsa2.size(); i++) {
						JSONObject temp2 = (JSONObject) jsa2.get(i);
						JSONObject temp1 = (JSONObject) tree1.search((long) temp2.get(att2));
						if (temp1 != null)
							result.add(GlobalUtil.concat2jobj(temp1, temp2));
					}
				}
			}
		}
		if (tree1 != null) {
			for (int i = 0; i < jsa2.size(); i++) {
				JSONObject temp2 = (JSONObject) jsa2.get(i);
				JSONObject temp1 = (JSONObject) tree1.search((long) temp2.get(att2));
				if (temp1 != null)
					result.add(GlobalUtil.concat2jobj(temp1, temp2));
			}
		}
		if (tree2 != null) {
			for (int i = 0; i < jsa1.size(); i++) {
				JSONObject temp1 = (JSONObject) jsa1.get(i);
				JSONObject temp2 = (JSONObject) tree2.search((long) temp1.get(att1));
				if (temp2 != null)
					result.add(GlobalUtil.concat2jobj(temp1, temp2));
			}
		}

		return result;
	}

	public void SaveBTree(JSONArray headers, BTree tree, String tableName, String att) {
		try {

			JSONObject temp;
			File file = new File("Data/Index/" + tableName + "_" + att + ".json");
			FileWriter fw = null;
			BufferedWriter bw = null;

			try {

				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);

			} catch (IOException e1) {

				e1.printStackTrace();
			}

			String records = "{\"index\":[";

			records = records.substring(0, records.length() - 1);
			records += "]}";
			System.out.print(records);

			try {

				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);

			} catch (IOException e1) {

				e1.printStackTrace();
			}

			try {
				bw.write(records);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				bw.flush();
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} finally {
		}

	}

	// public BTree LoadBTree(JSONArray headers, String tableName, String att) {
	// try {
	// // load the index file
	// FileReader f1 = new FileReader("Data/Index/" + att + ".json");
	// Object obj = parser.parse(f1);
	// JSONObject json = (JSONObject) obj;
	// JSONArray index = (JSONArray) json.get("index");
	//
	// if (index.size() != headers.size()) {
	// JOptionPane.showMessageDialog(null, "records number from index file and
	// data file not match!", "Error",
	// JOptionPane.ERROR_MESSAGE);
	// }
	//
	// // read the key value of index file
	// Object[] db = new Object[index.size()];
	// for (int i = 0; i < index.size(); i++) {
	// JSONObject currJson = (JSONObject) parser.parse(index.get(i).toString());
	// // !!!!!!!!!!!!!!!!!!
	// // This cast may have problem
	// db[i] = currJson.get(att);
	// System.out.println("test!!" + db[i]);
	// }
	//
	// // get the tuple address from the row number in the index file
	// JSONObject[] tupleaddress = new JSONObject[index.size()];
	// for (int i = 0; i < index.size(); i++) {
	// JSONObject currJson = (JSONObject) parser.parse(index.get(i).toString());
	// // we need this weird cast to convert long to int
	// Long temp = (Long) currJson.get("row #");
	// int j = temp.intValue();
	// // !!!!!!!!!!!!!!!!!!
	// // This cast may have problem
	// tupleaddress[i] = (JSONObject) headers.get(j);
	// }
	//
	// recman = RecordManagerFactory.createRecordManager(TABLE_NAME, props);
	// tree = BTree.createInstance(recman, new StringComparator());
	// recman.setNamedObject(att, tree.getRecid());
	//
	// System.out.println("Created a new empty BTree");
	// System.out.println();
	// for (int i = 0; i < db.length; i++) {
	// System.out.println("Insert: " + db[i]);
	// tree.insert(db[i], tupleaddress[i], false);
	// }
	//
	// } catch (Exception except) {
	// except.printStackTrace();
	// }
	// return tree;
	// }
}