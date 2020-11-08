package dictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HashDictionary<K, V> implements Dictionary<K, V> {

	private LinkedList<Entry<K, V>> tab[];
	private int size;
	private static final int LOAD_FACTOR = 2;

	@SuppressWarnings("unchecked")
	public HashDictionary(int capacity) {
		if (!isPrime(capacity)) {
			System.out.println("[WARN] Specified capacity is not a prime!");
		}
		this.tab = new LinkedList[capacity];
	}

	@Override
	public V insert(K key, V value) {
		int hashAddress = getHashAddress(key);
		if (search(key) == null) {
			if (!isLoadFactorOk()) {
				doubleCapacity();
				hashAddress = getHashAddress(key);
			}
			if (tab[hashAddress] == null) {
				tab[hashAddress] = new LinkedList<Entry<K, V>>();
			}
			tab[hashAddress].add(new Entry<K, V>(key, value));
			size++;
		} else {
			for (var entry : tab[hashAddress]) {
				if (entry.getKey().equals(key)) {
					return entry.setValue(value);
				}
			}
		}
		return null;

	}

	@Override
	public V search(K key) {
		int hashCode = getHashAddress(key);
		if (tab[hashCode] != null) {
			for (var entry : tab[hashCode]) {
				if (entry.getKey().equals(key))
					return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public V remove(K key) {
		if (search(key) != null) {
			int hashCode = getHashAddress(key);
			for (int i = 0; i < tab[hashCode].size(); i++) {
				if (tab[hashCode].get(i).getKey().equals(key)) {
					size--;
					return tab[hashCode].remove(i).getValue();
				}
			}
		}
		return null;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new Iterator<Dictionary.Entry<K, V>>() {
			int tabIndex = 0;
			int listIndex = 0;

			@Override
			public boolean hasNext() {
				if (tabIndex < tab.length) {
					if (tab[tabIndex] == null) {
						tabIndex++;
						return this.hasNext();
					}
					if (listIndex < tab[tabIndex].size())
						return true;
				}
				return false;
			}

			@Override
			public Entry<K, V> next() {
				Entry<K, V> entry = tab[tabIndex].get(listIndex++);
				if (tab[tabIndex].size() == listIndex) {
					tabIndex++;
					listIndex = 0;
				}
				return entry;
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void doubleCapacity() {
		List<Entry<K, V>> entries = new ArrayList<>(this.size);
		for (var v : this)
			entries.add(v);
		this.tab = new LinkedList[calculateNewPrime(this.tab.length)];
		for (var v : entries)
			this.insert(v.getKey(), v.getValue());
	}

	private boolean isLoadFactorOk() {
		return (this.size / this.tab.length > LOAD_FACTOR) ? false : true;
	}

	private int getHashAddress(K key) {
		int adr = key.hashCode();
		if (adr < 0)
			adr = -adr;
		return adr % tab.length;
	}

	private int calculateNewPrime(int oldPrime) {
		int newPrime = oldPrime * 2;
		while (!isPrime(newPrime))
			newPrime++;
		return newPrime;
	}

	private boolean isPrime(int number) {
		if (number <= 1)
			return false;
		if (number <= 3)
			return true;
		if (number % 2 == 0 || number % 3 == 0)
			return false;

		for (int i = 5; i * i <= number; i = i + 6)
			if (number % i == 0 || number % (i + 2) == 0)
				return false;

		return true;
	}
}
