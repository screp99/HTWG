// O. Bittel
// 22.02.2017
package dictionary;

import java.util.Iterator;

/**
 * Implementation of the Dictionary interface as AVL tree.
 * <p>
 * The entries are ordered using their natural ordering on the keys, or by a
 * Comparator provided at set creation time, depending on which constructor is
 * used.
 * <p>
 * An iterator for this dictionary is implemented by using the parent node
 * reference.
 * 
 * @param <K> Key.
 * @param <V> Value.
 */
public class BinaryTreeDictionary<K, V> implements Dictionary<K, V> {

	static private class Node<K, V> {
		K key;
		V value;
		int height;
		Node<K, V> left;
		Node<K, V> right;
		Node<K, V> parent;

		Node(K k, V v) {
			key = k;
			value = v;
			height = 0;
			left = null;
			right = null;
			parent = null;
		}
	}

	private Node<K, V> root = null;
	private int size = 0;

	// ...

	/**
	 * Pretty prints the tree
	 */
	public void prettyPrint() {
		printR(0, root);
	}

	private void printR(int level, Node<K, V> p) {
		printLevel(level);
		if (p == null) {
			System.out.println("#");
		} else {
			System.out.println(p.key + " " + p.value + "^" + ((p.parent == null) ? "null" : p.parent.key));
			if (p.left != null || p.right != null) {
				printR(level + 1, p.left);
				printR(level + 1, p.right);
			}
		}
	}

	private static void printLevel(int level) {
		if (level == 0) {
			return;
		}
		for (int i = 0; i < level - 1; i++) {
			System.out.print("   ");
		}
		System.out.print("|__");
	}

	@Override
	public V insert(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V search(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V remove(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
