package info.kmichel.reflect;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

public class TypeParser {

	private final Iterator<TypeToken> tokens;
	private TypeToken token;

	private TypeParser(final Iterable<TypeToken> tokens) {
		this.tokens = tokens.iterator();
	}

	public static Type parse(final Iterable<TypeToken> tokens) throws TypeNotFoundException {
		final TypeParser parser = new TypeParser(tokens);
		parser.consume();
		final Type type = parser.parseType(true);
		if (parser.token.type != TypeTokenType.EOF) {
			throw new TypeNotFoundException("unexpected token: "+parser.tokens.next());
		}
		return type;
	}

	private void consume() throws TypeNotFoundException {
		if (tokens.hasNext()) {
			token = tokens.next();
		} else {
			throw new TypeNotFoundException("unexpected end of tokens");
		}
	}

	private Type parseType(final boolean allow_wildcards) throws TypeNotFoundException {
		if (allow_wildcards) {
			switch (token.type) {
				case EXTENDS_KEYWORD:
					consume();
					return new SimpleWildcardType(parseTypeList(TypeTokenType.AMPERSAND, false), new Type[0]);
				case SUPER_KEYWORD:
					consume();
					return new SimpleWildcardType(new Type[0], parseTypeList(TypeTokenType.AMPERSAND, false));
				default:
			}
		}

		final Class<?> rawType = parseRawType();

		if (token.type == TypeTokenType.ANGLE_BRACKET_OPEN) {
			consume();
			final Type[] parameters = parseTypeList(TypeTokenType.COMMA, true);
			Type type;
			try {
				type = new SimpleParameterizedType(rawType, parameters);
			} catch (final IllegalArgumentException e) {
				throw new TypeNotFoundException("can't create parameterized type", e);
			}
			if (token.type == TypeTokenType.ANGLE_BRACKET_CLOSE) {
				consume();
			} else {
				throw new TypeNotFoundException("unexpected token: "+token);
			}

			while (token.type == TypeTokenType.SQUARE_BRACKET_PAIR) {
				consume();
				type = new SimpleGenericArrayType(type);
			}
			return type;
		} else {
			return rawType;
		}
	}

	private Class<?> parseRawType() throws TypeNotFoundException {
		final StringBuilder builder = new StringBuilder();
		if (token.type == TypeTokenType.IDENTIFIER) {
			builder.append(token.value);
			consume();
		} else {
			throw new TypeNotFoundException("unexpected token: "+token);
		}
		while (token.type == TypeTokenType.DOT) {
			builder.append('.');
			consume();
			if (token.type == TypeTokenType.IDENTIFIER) {
				builder.append(token.value);
				consume();
			} else {
				throw new TypeNotFoundException("unexpected token: "+token);
			}
		}
		if (token.type == TypeTokenType.SQUARE_BRACKET_PAIR) {
			builder.insert(0, 'L');
			while (token.type == TypeTokenType.SQUARE_BRACKET_PAIR) {
				builder.insert(0, '[');
				consume();
			}
			builder.append(';');
		}
		try {
			return Class.forName(builder.toString());
		} catch (final ClassNotFoundException e) {
			throw new TypeNotFoundException("raw class not found: "+builder.toString(), e);
		}
	}

	private Type[] parseTypeList(
			final TypeTokenType separator,
			final boolean allow_wildcards)
			throws TypeNotFoundException {
		final Collection<Type> types = new ArrayList<Type>();
		types.add(parseType(allow_wildcards));
		while (token.type == separator) {
			consume();
			types.add(parseType(allow_wildcards));
		}
		return types.toArray(new Type[types.size()]);
	}

}
