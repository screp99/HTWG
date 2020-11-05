package dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

enum Language {
	GERMAN, ENGLISH
}

public class Measurement {

	private static final int WORD_COUNT = 16000;
	private static final String SOURCE = "./src/resources/dtengl.txt";

	// private constructor to prevent instantiation of library
	private Measurement() {
	}

	public static void main(String[] args) throws Exception {
		List<Dictionary<String, String>> dictionaries = new ArrayList<>();
		dictionaries.add(new SortedArrayDictionary<>());
		dictionaries.add(new HashDictionary<>(3));
		dictionaries.add(new BinaryTreeDictionary<>());
		for (Dictionary<String, String> dictionary : dictionaries) {
			System.out.println("\nActual dictionary: " + (dictionary.getClass().toString().split("dictionary.")[1]));
			System.out.println("Word count: " + WORD_COUNT);
			List<String> wordlist = new ArrayList<>(WORD_COUNT);
			measureInsertTime(dictionary);
			fillWordlist(wordlist, Language.GERMAN);
			measureSearchTime(wordlist, dictionary);
			measureNonFindSearchTime(wordlist, dictionary);
		}
	}

	private static void fillWordlist(List<String> wordlist, Language lang) throws Exception {
		try (BufferedReader reader = new BufferedReader(new FileReader(SOURCE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (lang == Language.GERMAN)
					wordlist.add(line.split(" ")[0]);
				else
					wordlist.add(line.split(" ")[1]);
			}
			reader.close();
		}
	}

	private static void measureInsertTime(Dictionary<String, String> dict) throws Exception {
		try (BufferedReader reader = new BufferedReader(new FileReader(SOURCE))) {
			String line;
			int counter = 0;
			long startTime = System.nanoTime();
			while ((line = reader.readLine()) != null && counter < WORD_COUNT) {
				String[] words = line.split(" ");
				dict.insert(words[0], words[1]);
				counter++;
			}
			long stopTime = System.nanoTime();
			System.out.println("InsertTime: " + ((stopTime - startTime) / 1_000_000) + " ms");
			reader.close();
		}
	}

	private static void measureSearchTime(List<String> germanWordlist, Dictionary<String, String> dict)
			throws Exception {
		Iterator<String> iter = germanWordlist.iterator();
		int counter = 0;
		long startTime = System.nanoTime();
		while (iter.hasNext() && counter < WORD_COUNT) {
			dict.search(iter.next());
			counter++;
		}
		long stopTime = System.nanoTime();
		System.out.println("FindTime: " + ((stopTime - startTime) / 1_000_000) + " ms");
	}

	private static void measureNonFindSearchTime(List<String> wordlist, Dictionary<String, String> dict)
			throws Exception {
		Iterator<String> iterator = wordlist.iterator();
		int counter = 0;
		long startTime = System.nanoTime();
		while (iterator.hasNext() && counter < WORD_COUNT) {
			dict.search((String) iterator.next());
			counter++;
		}
		long stopTime = System.nanoTime();
		System.out.println("NonFindTime: " + ((stopTime - startTime) / 1_000_000) + " ms");
	}
}
