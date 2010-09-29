package info.kmichel.util;

import java.util.TreeMap;
import java.util.Map;

public class Escaper {

	private final Map<Character, String> dictionary;

	public Escaper() {
		dictionary = new TreeMap<Character, String>();
	}

	public void add(final Character escaped, final String escaping) {
		dictionary.put(escaped, escaping);
	}

	public String escape(final String input) {
		final StringBuilder builder = new StringBuilder();
		int marker = 0;
		for (int i=0; i<input.length(); ++i) {
			char character = input.charAt(i);
			final String escaped = dictionary.get(character);
			if (escaped != null) {
				builder.append(input, marker, i);
				marker = i+1;
				builder.append(escaped);
			}
		}
		builder.append(input, marker, input.length());
		return builder.toString();
	}

}
