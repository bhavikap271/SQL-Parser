package bptree;

import org.json.simple.JSONArray;

/**
 * A B+ tree Since the structures and behaviors between internal node and
 * external node are different, so there are two different classes for each kind
 * of node.
 * 
 * @param <TKey>
 *            the data type of the key
 * @param <TValue>
 *            the data type of the value
 */
public class BTree<TKey extends Comparable<TKey>, TValue> {
	private BTreeNode<TKey> root;

	public BTree() {
		this.root = new BTreeLeafNode<TKey, TValue>();
	}

	public BTreeNode<TKey> getRoot(){
		return this.root;
	}
	
	/**
	 * Insert a new key and its associated value into the B+ tree.
	 */
	public void insert(TKey key, TValue value) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		leaf.insertKey(key, value);

		if (leaf.isOverflow()) {
			BTreeNode<TKey> n = leaf.dealOverflow();
			if (n != null)
				this.root = n;
		}
	}

	/**
	 * Search a key value on the tree and return its associated value.
	 */
	public TValue search(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

		int index = leaf.search(key);
		return (index == -1) ? null : leaf.getValue(index);
	}

	/**
	 * Delete a key and its associated value from the tree.
	 */
	public void delete(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

		if (leaf.delete(key) && leaf.isUnderflow()) {
			BTreeNode<TKey> n = leaf.dealUnderflow();
			if (n != null)
				this.root = n;
		}
	}

	/**
	 * Search the leaf node which should contain the specified key
	 */
	@SuppressWarnings("unchecked")
	private BTreeLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
		BTreeNode<TKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TKey>) node).getChild(node.search(key));
		}

		return (BTreeLeafNode<TKey, TValue>) node;
	}

	public void printbtree() {
		BTreeNode<TKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TKey>) node).getChild(0);
		}

		while (true) {
			for (int i = 0; i < node.getKeyCount(); i++)
				  System.out.println(node.getKey(i));
			if (node.rightSibling == null)
				break;
			node = node.rightSibling;
		}
	}

	public int getTotalKeyNumbers() {
		int total = 0;
		BTreeNode<TKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TKey>) node).getChild(0);
		}

		while (true) {
			for (int i = 0; i < node.getKeyCount(); i++)
				total++;
			if (node.rightSibling == null)
				break;
			node = node.rightSibling;
		}
		return total;
	}

	public JSONArray SearchFrom(String op, TKey value) {
		JSONArray result = new JSONArray();
		if (op.equals(">")) {
			BTreeLeafNode<TKey, TValue> leafnode = this.findLeafNodeShouldContainKey(value);
			int index = -1;
			for (int i = 0; i < leafnode.keyCount; i++) {
				int cmp = leafnode.getKey(i).compareTo(value);
				if (cmp == 0) {
					index = i + 1;
					break;
				} else if (cmp > 0) {
					index = i;
					break;
				}
			}

			while (index < leafnode.keyCount && index > -1) {
				result.add(leafnode.getValue(index));
				index++;
			}

			while (leafnode.rightSibling != null) {
				leafnode = (BTreeLeafNode<TKey, TValue>) leafnode.rightSibling;
				for (int i = 0; i < leafnode.keyCount; i++)
					result.add(leafnode.getValue(i));
			}

		}

		if (op .equals("<") ) {
			BTreeLeafNode<TKey, TValue> leafnode = this.findLeafNodeShouldContainKey(value);
			int index = -1;
			for (int i = leafnode.keyCount - 1; i > -1; i--) {
				int cmp = leafnode.getKey(i).compareTo(value);
				if (cmp == 0) {
					index = i - 1;
					break;
				} else if (cmp < 0) {
					index = i;
					break;
				}
			}

			while (index < leafnode.keyCount && index > -1) {
				result.add(leafnode.getValue(index));
				index--;
			}

			while (leafnode.leftSibling != null) {
				leafnode = (BTreeLeafNode<TKey, TValue>) leafnode.leftSibling;
				for (int i = leafnode.keyCount - 1; i > -1; i--)
					result.add(leafnode.getValue(i));
			}
		}
		return result;
	}

}
