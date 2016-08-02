package Screens;

import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import bptree.*;

public class SaveBtree {
	public SaveBtree(String att, String tableName) {
		JSONObject top1 = new JSONObject();
		createNodes(top1, att);
		//System.out.println(top1.toJSONString());
		BPlusTreeIndexing.SaveBTree(tableName, att, top1);
	}


	private void createNodes(JSONObject top1, String att) {
		BPlusTreeIndexing btree = GlobalData.AttBTreeIndex.get(att);
		BTreeNode node = btree.getRoot();
		String key = node.printRange();
		top1.put(key, addAllChildNode1(node,att));
		//System.out.println(top1.toString());
	}
	private JSONObject addAllChildNode1(BTreeNode node,String att) {
		
		JSONObject temp = new JSONObject();
		if (!node.IsLeaf()) {
			for (int i = 0; i < node.getKeyCount()+1; i++) {
				String key = node.getChild(i).printRange();
				temp.put(key, addAllChildNode1(node.getChild(i),att));
				//System.out.println(temp.toString());
			}
		} else {
			for (int i = 0; i < node.getKeyCount(); i++) {
				JSONArray j = GlobalData.tableJSonArray.get(GlobalData.AttTableMap.get(att));
				int rn = j.indexOf((JSONObject)node.getLeafValue(i));
				temp.put(node.getNodeKey(i),rn );
				//System.out.println(temp.toString());
				
			}
		}
		return temp;
	}
	
}
