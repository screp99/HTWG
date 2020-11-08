package dictionary;

import java.util.Iterator;

public class BinaryTreeDictionary<K extends Comparable<? super K>, V> implements Dictionary<K, V> {

	private Node<K, V> root;
	private V oldValue;
	private int size;

	@Override
	public V insert(K key, V value) {
		this.root = insertR(key, value, root);
		if (root != null)
			root.parent = null;
		return this.oldValue;
	}

	private Node<K, V> insertR(K key, V value, Node<K, V> p) {
		if (p == null) {
			p = new Node<K, V>(new Entry<K, V>(key, value), null, null);
			size++;
			oldValue = null;
		} else if (key.compareTo(p.entry.getKey()) < 0) {
			p.left = insertR(key, value, p.left);
			if (p.left != null)
				p.left.parent = p;
		} else if (key.compareTo(p.entry.getKey()) > 0) {
			p.right = insertR(key, value, p.right);
			if (p.right != null)
				p.right.parent = p;
		} else {
			// Schlüssel bereits vorhanden:
			oldValue = p.entry.getValue();
			p.entry.setValue(value);
		}

		p = balance(p);
		return p;
	}

	@Override
	public V search(K key) {
		return searchR(key, root);
	}

	private V searchR(K key, Node<K, V> p) {
		if (p == null)
			return null;
		else if (key.compareTo(p.entry.getKey()) < 0)
			return searchR(key, p.left);
		else if (key.compareTo(p.entry.getKey()) > 0)
			return searchR(key, p.right);
		else
			return p.entry.getValue();
	}

	@Override
	public V remove(K key) {
		root = removeR(key, root);
		return oldValue;
	}

	private Node<K, V> removeR(K key, Node<K, V> p) {
		if (p == null) {
			oldValue = null;
		} else if (key.compareTo(p.entry.getKey()) < 0)
			p.left = removeR(key, p.left);
		else if (key.compareTo(p.entry.getKey()) > 0)
			p.right = removeR(key, p.right);
		else if (p.left == null || p.right == null) {
			// p muss gelöscht werden
			// und hat ein oder kein Kind:
			oldValue = p.entry.getValue();
			p = (p.left != null) ? p.left : p.right;
			size--;
		} else {
			// p muss gelöscht werden und hat zwei Kinder:
			MinEntry<K, V> min = new MinEntry<K, V>();
			oldValue = p.entry.getValue();
			p.right = getRemMinR(p.right, min);
			oldValue = p.entry.getValue();
			p = new Node<>(new Entry<K, V>(min.key, min.value), p.left, p.right);
			size--;
		}
		p = balance(p);
		return p;
	}

	private Node<K, V> getRemMinR(Node<K, V> p, MinEntry<K, V> min) {
		assert p != null;
		if (p.left == null) {
			min.key = p.entry.getKey();
			min.value = p.entry.getValue();
			p = p.right;
		} else
			p.left = getRemMinR(p.left, min);

		p = balance(p);
		return p;
	}

	@Override
	public int size() {
		return this.size;
	}

	public void prettyPrint() {
		prettyPrintR(root, 0);
	}

	private void prettyPrintR(Node<K, V> p, int depth) {
		for (int x = 0; x < depth; x++)
			System.out.print("  ");

		if (p == null) {
			System.out.print("#\n");
			return;
		}

		System.out.print(String.format("(%s: %s)\n", p.entry.getKey(), p.entry.getValue()));

		prettyPrintR(p.left, depth + 1);
		prettyPrintR(p.right, depth + 1);
	}

	private int getHeight(Node<K, V> p) {
		if (p == null)
			return -1;
		else
			return p.height;
	}

	private int getBalance(Node<K, V> p) {
		if (p == null)
			return 0;
		else
			return getHeight(p.right) - getHeight(p.left);
	}

	private Node<K, V> balance(Node<K, V> p) {
		if (p == null)
			return null;
		p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
		if (getBalance(p) == -2) {
			if (getBalance(p.left) <= 0)
				p = rotateRight(p);
			else
				p = rotateLeftRight(p);
		} else if (getBalance(p) == +2) {
			if (getBalance(p.right) >= 0)
				p = rotateLeft(p);
			else
				p = rotateRightLeft(p);
		}
		return p;
	}

	private Node<K, V> rotateRight(Node<K, V> p) {
		assert p.left != null;
		Node<K, V> q = p.left;
		p.left = q.right;

		if (p.left != null)
			p.left.parent = p;

		q.right = p;

		if (q.right != null)
			q.right.parent = q;

		p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
		q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
		return q;
	}

	private Node<K, V> rotateLeft(Node<K, V> p) {
		assert p.right != null;
		Node<K, V> q = p.right;
		p.right = q.left;

		if (p.right != null)
			p.right.parent = p;

		q.left = p;

		if (q.left != null)
			q.left.parent = q;

		p.height = Math.max(getHeight(p.right), getHeight(p.left)) + 1;
		q.height = Math.max(getHeight(q.right), getHeight(q.left)) + 1;
		return q;

	}

	private Node<K, V> rotateLeftRight(Node<K, V> p) {
		assert p.left != null;
		p.left = rotateLeft(p.left);
		return rotateRight(p);
	}

	private Node<K, V> rotateRightLeft(Node<K, V> p) {
		assert p.right != null;
		p.right = rotateRight(p.right);
		return rotateLeft(p);
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Dictionary.Entry<K, V>>() {
			Node<K, V> currentNode = null;
			int currentCounter = 0;

			@Override
			public boolean hasNext() {
				return this.currentCounter < size() ? true : false;
			}

			@Override
			public Entry<K, V> next() {
				if (this.currentNode == null)
					this.currentNode = leftMostDescendant(root);
				else if (this.currentNode.right != null)
					this.currentNode = leftMostDescendant(this.currentNode.right);
				else
					this.currentNode = parentOfLeftMostAncestor(this.currentNode);
				this.currentCounter++;
				return this.currentNode.entry;
			}

			private Node<K, V> leftMostDescendant(Node<K, V> p) {
				assert p != null;
				while (p.left != null)
					p = p.left;
				return p;
			}

			private Node<K, V> parentOfLeftMostAncestor(Node<K, V> p) {
				assert p != null;
				while (p.parent != null && p.parent.right == p)
					p = p.parent;
				return p.parent; // kann auch null sein
			}
		};
	}

	@SuppressWarnings("hiding")
	private class Node<K extends Comparable<? super K>, V> {
		Entry<K, V> entry;
		Node<K, V> parent;
		Node<K, V> left;
		Node<K, V> right;
		int height;

		public Node(Entry<K, V> entry, Node<K, V> left, Node<K, V> right) {
			this.entry = entry;
			this.left = left;
			this.right = right;
			this.parent = null;
			this.height = 0;
		}
	}

	private static class MinEntry<K, V> {
		private K key;
		private V value;
	}
}
