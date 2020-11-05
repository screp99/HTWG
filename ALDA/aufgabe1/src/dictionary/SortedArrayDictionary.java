package dictionary;

import java.util.Arrays;
import java.util.Iterator;

public class SortedArrayDictionary<K extends Comparable<? super K>, V> implements Dictionary<K, V> {

	private static final int DEF_CAPACITY = 16000;
	private int size;
	private Entry<K, V>[] data;

	@SuppressWarnings("unchecked")
	public SortedArrayDictionary() {
		this.size = 0;
		this.data = new Entry[DEF_CAPACITY];
	}

	@Override
	public V insert(K key, V value) {
		int i = searchKey(key);
		// Key already exists
		if (i >= 0) {
			V r = data[i].getValue();
			data[i].setValue(value);
			return r;
		}
		// New entry for key
		if (data.length == size) {
			data = Arrays.copyOf(data, 2 * size);
		}
		int j = this.size - 1;
		while (j >= 0 && key.compareTo(data[j].getKey()) < 0) {
			data[j + 1] = data[j];
			j--;
		}
		data[j + 1] = new Entry<K, V>(key, value);
		size++;
		return null;
	}

	@Override
	public V search(K key) {
		int i = searchKey(key);
		if (i >= 0)
			return data[i].getValue();
		else
			return null;
	}

	@Override
	public V remove(K key) {
		int i = searchKey(key);
		if (i == -1) {
			System.out.println("[INFO] Specified key does not exist in dictionary!");
			return null;
		}
		// Delete entry and close gap
		V r = data[i].getValue();
		for (int j = i; j < size - 1; j++) {
			data[j] = data[j + 1];
		}
		data[--size] = null;
		return r;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Dictionary.Entry<K, V>>() {
			int currentIndex = 0;

			@Override
			public boolean hasNext() {
				return currentIndex < size;
			}

			@Override
			public Entry<K, V> next() {
				Entry<K, V> returnEntry = data[currentIndex++];
				return returnEntry;
			}
		};
	}

	private int searchKey(K key) {
		int li = 0;
		int re = size - 1;
		while (re >= li) {
			int m = (li + re) / 2;
			if (key.compareTo(data[m].getKey()) < 0) {
				re = m - 1;
			} else if (key.compareTo(data[m].getKey()) > 0) {
				li = m + 1;
			} else {
				return m;
			}
		}
		// key nicht gefunden
		return -1;
	}

}
