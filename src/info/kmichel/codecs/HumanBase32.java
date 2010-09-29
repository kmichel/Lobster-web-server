package info.kmichel.codecs;

/**
 * HumanBase32 is an encoding similar to Base64 or other kinds of Base32,
 * but specifically designed to be more tolerant to human transcription errors.
 * It's case insensitive and similar characters have same values ("o", "O" and
 * "0" are all valid representations of the same value, same thing for the
 * group "i", "I", "l", "L", "1" and the group "u", "U", "v", "V"). Characters
 * others than numbers and letters are allowed and ignored.
 */

import java.io.UnsupportedEncodingException;

public final class HumanBase32 {

	HumanBase32()
			throws InstantiationException {
		throw new InstantiationException("HumanBase32 is a static-only utility class");
	}

	private static final char[] valueToCode = {
		'2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
		'i', 'j', 'k', 'm', 'n', 'o', 'p', 'q',
		'r', 's', 't', 'u', 'w', 'x', 'y', 'z'
	};

	private static byte codeToValue(final char code) {
		switch (code) {
			case '2': return 0;
			case '3': return 1;
			case '4': return 2;
			case '5': return 3;
			case '6': return 4;
			case '7': return 5;
			case '8': return 6;
			case '9': return 7;
			case 'a': case 'A': return 8;
			case 'b': case 'B': return 9;
			case 'c': case 'C': return 10;
			case 'd': case 'D': return 11;
			case 'e': case 'E': return 12;
			case 'f': case 'F': return 13;
			case 'g': case 'G': return 14;
			case 'h': case 'H': return 15;
			case 'i': case 'I': case 'l': case 'L': case '1': return 16;
			case 'j': case 'J': return 17;
			case 'k': case 'K': return 18;
			case 'm': case 'M': return 19;
			case 'n': case 'N': return 20;
			case 'o': case 'O': case '0': return 21;
			case 'p': case 'P': return 22;
			case 'q': case 'Q': return 23;
			case 'r': case 'R': return 24;
			case 's': case 'S': return 25;
			case 't': case 'T': return 26;
			case 'u': case 'U': case 'v': case 'V': return 27;
			case 'w': case 'W': return 28;
			case 'x': case 'X': return 29;
			case 'y': case 'Y': return 30;
			case 'z': case 'Z': return 31;
			default:
				return -1;
		}
	}

	private static int countIgnored(final String data) {
		int ignored = 0;
		for (int i=0; i<data.length(); ++i) {
			if (codeToValue(data.charAt(i)) == -1) {
				ignored++;
			}
		}
		return ignored;
	}

	/**
	 * Decodes an HumanBase32 encoded string to an byte array.
	 * @throws UnsupportedEncodingException If the String is not a valid
	 * HumanBase32 String.
	 */
	public static byte[] decode(final String data)
		throws UnsupportedEncodingException {
		final int dataLength = data.length() - countIgnored(data);
		final int[] lenMap = {0, -1, 1, -1, 2, 3, -1, 4};
		if (lenMap[dataLength % 8] == -1) {
			throw new UnsupportedEncodingException("Invalid input: bad length");
		}
		byte[] buffer = new byte[dataLength / 8 * 5 + lenMap[dataLength % 8]];
		int j = 0;
		int partial = 0;
		int readOffset = 0;
		for (int i = 0; i < dataLength; ++i) {
			final byte value = codeToValue(data.charAt(i+readOffset));
			if (value == -1) {
				readOffset++;
				i--;
				continue;
			}
			switch (i % 8) {
				case 0:
					partial = value << 3;
					break;
				case 1:
					buffer[j++] = (byte)((partial | value >> 2) & 0xff);
					partial = value << 6;
					break;
				case 2:
					partial |= value << 1;
					break;
				case 3:
					buffer[j++] = (byte)((partial | value >> 4) & 0xff);
					partial = value << 4;
					break;
				case 4:
					buffer[j++] = (byte)((partial | value >> 1) & 0xff);
					partial = value << 7;
					break;
				case 5:
					partial |= value << 2;
					break;
				case 6:
					buffer[j++] = (byte)((partial | value >> 3) & 0xff);
					partial = value << 5;
					break;
				case 7:
					buffer[j++] = (byte)((partial | value) & 0xff);
					break;
			}
		}
		if (dataLength % 8 != 0 && (partial&0xff) != 0) {
			throw new UnsupportedEncodingException("Invalid input: bad finish");
		}
		return buffer;
	}

	/**
	 * Encodes a long number to the shortest possible HumanBase32 encoded String.
	 * Note that negative number will all produce strings of 13 characters
	 * because of the leading 1 in binary representation of negative numbers.
	 */
	public static String encode(final long value) {
		final int neededBytes = Long.SIZE / Byte.SIZE - Long.numberOfLeadingZeros(value) / Byte.SIZE;
		final byte[] buffer = new byte[neededBytes];
		for (int i=0; i<neededBytes; ++i) {
			buffer[neededBytes - i - 1] = (byte)(value >> (i*Byte.SIZE));
		}
		return encode(buffer);
	}

	/**
	 * Encodes an int number to the shortest possible HumanBase32 encoded String. 
	 * Note that negative number will all produce strings of 7 characters
	 * because of the leading 1 in binary representation of negative numbers.
	 */
	public static String encode(final int value) {
		final int neededBytes = Integer.SIZE / Byte.SIZE - Integer.numberOfLeadingZeros(value) / Byte.SIZE;
		final byte[] buffer = new byte[neededBytes];
		for (int i=0; i<neededBytes; ++i) {
			buffer[neededBytes - i - 1] = (byte)(value >> (i*Byte.SIZE));
		}
		return encode(buffer);
	}

	/**
	 * Decodes an HumanBase32 encoded String to a long.
	 * @throws UnsupportedEncodingException If the decoded value is too big for a long.
	 */
	public static long decodeLong(final String data)
			throws UnsupportedEncodingException {
		final byte[] decoded = decode(data);
		if (decoded.length > (Long.SIZE / Byte.SIZE)) {
			throw new UnsupportedEncodingException("Too large value");
		}
		long value = 0;
		for (int i=0; i<decoded.length; ++i) {
			value <<= Byte.SIZE;
			value |= decoded[i] & 0xFF;
		}
		return value;
	}

	/**
	 * Decodes an HumanBase32 encoded String to a long.
	 * @throws UnsupportedEncodingException If the decoded value is too big for a long.
	 */
	public static int decodeInt(final String data)
			throws UnsupportedEncodingException {
		final byte[] decoded = decode(data);
		if (decoded.length > (Integer.SIZE / Byte.SIZE)) {
			throw new UnsupportedEncodingException("Too large value");
		}
		int value = 0;
		for (int i=0; i<decoded.length; ++i) {
			value <<= Byte.SIZE;
			value |= decoded[i] & 0xFF;
		}
		return value;
	}

	/**
	 * Encodes a byte array to an HumanBase32 encoded String.
	 */
	public static String encode(final byte[] data) {
		final int[] lenMap = {0, 2, 4, 5, 7};
		char[] buffer = new char[data.length / 5 * 8 + lenMap[data.length % 5]];
		int j = 0;
		int partial = 0;
		for (int i = 0; i < data.length; ++i) {
			switch (i%5) {
				case 0:
					buffer[j++] = valueToCode[(data[i] & 0xff) >> 3];
					partial = ((data[i] << 5) & 0xff) >> 3;
					break;
				case 1:
					buffer[j++] = valueToCode[(partial | (data[i] & 0xff) >> 6) & 0xff];
					buffer[j++] = valueToCode[((data[i] << 2 ) & 0xff) >> 3];
					partial = ((data[i] << 7) & 0xff) >> 3;
					break;
				case 2:
					buffer[j++] = valueToCode[(partial | (data[i] & 0xff) >> 4) & 0xff];
					partial = ((data[i] << 4) & 0xff) >> 3;
					break;
				case 3:
					buffer[j++] = valueToCode[(partial | (data[i] & 0xff) >> 7) & 0xff];
					buffer[j++] = valueToCode[((data[i] << 1) & 0xff) >> 3];
					partial = ((data[i] << 6) & 0xff) >> 3;
					break;
				case 4:
					buffer[j++] = valueToCode[(partial | (data[i] & 0xff) >> 5) & 0xff];
					buffer[j++] = valueToCode[((data[i] << 3) & 0xff) >> 3];
					break;
			}
		}
		if (data.length % 5 != 0) {
			buffer[j++] = valueToCode[partial & 0xff];
		}
		return new String(buffer);
	}


}
