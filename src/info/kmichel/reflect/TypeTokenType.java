package info.kmichel.reflect;

import java.util.regex.Pattern;

public enum TypeTokenType {

	IDENTIFIER("[a-zA-Z][a-zA-Z0-9]*"),
	DOT("\\."),
	COMMA(","),
	AMPERSAND("&"),
	EXTENDS_KEYWORD("\\? +extends"),
	SUPER_KEYWORD("\\? +super"),
	ANGLE_BRACKET_OPEN("<"),
	ANGLE_BRACKET_CLOSE(">"),
	SQUARE_BRACKET_PAIR("\\[ *\\]"),
	EOF("$"),
	INVALID_TOKEN(".+");

	final Pattern pattern;

	TypeTokenType(final String pattern) {
		this.pattern = Pattern.compile(" *("+pattern+")");
	}

}
