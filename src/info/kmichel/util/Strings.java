package info.kmichel.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Iterator;

/**
 * Toolbox class similar to {@link java.util.Collections} or
 * {@link java.util.Arrays} dedicated to {@link java.lang.String} manipulations
 * not present in the standard library.
 *
 * @author Michel Kevin
 */
public final class Strings {

	Strings()
			throws InstantiationException {
		throw new InstantiationException("Strings is a static-only utility class");
	}

	/**
	 * Join all object's toString in parts, in the order of the collection,
	 * with a separator between each element.
	 *
	 * @param parts The collection of objects, won't be modified in any way by
	 * this function.
	 * @param separator The separator between each element, null is allowed and
	 * equivalent to the empty string.
	 * @return The joined string.
	 */
	public static <T> String join(final Collection<T> parts, final String separator) {
		if (parts.isEmpty()) {
			return "";
		}
		final Iterator<T> iter = parts.iterator();
		final StringBuilder buffer = new StringBuilder(iter.next().toString());
		while (iter.hasNext()) {
			if (separator != null) {
				buffer.append(separator);
			}
			buffer.append(iter.next().toString());
		}
		return buffer.toString();
	}

	/**
	 * Read the full content of an input stream and return it as a String.
	 * Conversion is done using utf-8 encoding.
	 * @param inputStream The source input stream.
	 * @return The content of the stream as a string.
	 * @throws IOException 
	 */
	public static String consume(final InputStream inputStream) throws IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		while (true) {
			final int readVal = inputStream.read();
			if (readVal == -1) {
				break;
			}
			outputStream.write(readVal);
		}
		return new String(outputStream.toByteArray(), "utf-8");
	}

	public static String random(final int length) {
		final String table = "abcdefghjkmnpqrstuvwxyz23456789ABCDEFGHJKLMNPQSRTUVWXYZ";
		String value = "";
		final SecureRandom random = new SecureRandom();
		for (int i=0; i<length; ++i) {
			value += table.charAt(random.nextInt(table.length()));
		}
		return value;
	}
}	
