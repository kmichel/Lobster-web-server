package info.kmichel.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeToken {

	public final TypeTokenType type;
	public final String value;
	public final int start;

	public TypeToken(final TypeTokenType type, final String value, final int start) {
		this.type = type;
		this.value = value;
		this.start = start;
	}

	static public Iterable<TypeToken> tokenize(final CharSequence sequence) {
		final Collection<TypeToken> tokens = new ArrayList<TypeToken>();
		final Matcher matcher = Pattern.compile("").matcher(sequence);
		while (matcher.pattern() != TypeTokenType.EOF.pattern) {
			for (final TypeTokenType type : TypeTokenType.values()) {
				matcher.usePattern(type.pattern);
				if (matcher.lookingAt()) {
					tokens.add(new TypeToken(type, matcher.group(1), matcher.start(1)));
					matcher.region(matcher.end(), sequence.length());
					break;
				}
			}
		}
		return tokens;
	}

	@Override
	public String toString() {
		return type.toString().toLowerCase(Locale.ROOT)+"@"+start;
	}

}
