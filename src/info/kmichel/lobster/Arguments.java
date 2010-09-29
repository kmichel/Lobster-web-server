package info.kmichel.lobster;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * A container class for HTTP request arguments, used both for {@code GET} and
 * {@code POST} requests. Such object can be obtained using
 * {@link Exchange#getQuery()} and {@link Exchange#getPost()}.
 *
 * <p>For each key, you can find zero or more values, ordered by insertion
 * order. Insertion order is order of appearance when using the parsing static
 * method factories {@link #fromMime(InputStream)} and
 * {@link #fromURLEncoded(byte[])}.
 *
 * @see Exchange
 *
 * @author Michel Kevin
 */
public class Arguments {

	private final Map<String, List<String>> data;
	
	/**
	 * Creates an empty Arguments object.
	 */
	public Arguments() {
		data = new HashMap<String, List<String>>();
	}

	/**
	 * Creates an Arguments object filled by parsing the subset of the mime
	 * format defined for POST data in HTTP request bodies.
	 * Not yet implemented.
	 */
	public static Arguments fromMime(final InputStream inputStream) {
		throw new UnsupportedOperationException("mime data decoding not yet implemented");
	}

	/**
	 * Creates an Arguments object filled by parsing the raw {@code content}
	 * data encoded using the URL encoding defined for HTTP query strings and
	 * POST data in HTTP request bodies.
	 *
	 * @see java.net.URLDecoder
	 */
	public static Arguments fromURLEncoded(final String content) {
		final Arguments ret = new Arguments();
		for (final String pair: content.split("&")) {
			final String[] keyValue = pair.split("=");
			if (keyValue.length == 1 || keyValue.length == 2 ) {
				try {
					final String key = URLDecoder.decode(keyValue[0], "UTF-8");
					final String value;
					if (keyValue.length == 2) {
						value = URLDecoder.decode(keyValue[1], "UTF-8");
					} else {
						value = "";
					}
					ret.put(key, value);
				} catch (final UnsupportedEncodingException e) {
					throw new RuntimeException("Invalid URLEncoded data", e);
				}
			} else {
				throw new RuntimeException("Invalid URLEncoded data");
			}
		}
		return ret;
	}

	/**
	 * Encodes the full arguments content to an URL encoded String.
	 *
	 * @see java.net.URLEncoder
	 */
	public String toURLEncoded() {
		final StringBuilder builder = new StringBuilder();
		boolean isFirstEntry = true;
		for (final Map.Entry<String, List<String>> entry: data.entrySet()) {
			for (final String value: entry.getValue()) {
				if (!isFirstEntry) {
					builder.append("&");
				} else {
					isFirstEntry = false;
				}
				try {
					builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
					builder.append("=");
					builder.append(URLEncoder.encode(value, "UTF-8"));
				} catch (final UnsupportedEncodingException e) {
					// Can't happen
					throw new RuntimeException(e);
				}
			}
		}
		return builder.toString();
	}

	/**
	 * Adds a value associated with the given key. Previous values are kept,
	 * added value is appended to the previous values list.
	 */
	public void put(final String key, final String value) {
		final List<String> valueList = data.get(key);
		if (valueList != null) {
			valueList.add(value);
		} else {
			final List<String> newValueList = new ArrayList<String>();
			newValueList.add(value);
			data.put(key, newValueList);
		}
	}

	/**
	 * @return {@code true} if the {@code Arguments} object contains at least one
	 * value for the given key.
	 */
	public Boolean hasKey(final String key) {
		return data.containsKey(key);
	}

	/**
	 * @return {@code true} if the {@code Arguments} object contains at least one
	 * value for each of the given key.
	 */
	public Boolean hasKeys(final String... keys) {
		for (final String key: keys) {
			if (!hasKey(key)) return false;
		}
		return true;
	}

	/**
	 * @return an ordered immutable list of all values stored with the given key.
	 */
	public List<String> get(final String key) {
		final List<String> value = data.get(key) ;
		if (value == null) {
			return Collections.unmodifiableList(new ArrayList<String>());
		}
		return Collections.unmodifiableList(value);
	}

	/**
	 * @return the first value stored with the given key or {@code defaultValue} if no
	 * value is associated with the given key.
	 */
	public String getFirst(final String key, final String defaultValue) {
		final List<String> value = data.get(key);
		if (value == null || value.size() == 0) {
			return defaultValue;
		}
		return value.get(0);
	}

	/**
	 * @return the first value stored with the given key or {@code null} if no
	 * value is associated with the given key.
	 */
	public String getFirst(final String key) {
		return getFirst(key, null);
	}

}
