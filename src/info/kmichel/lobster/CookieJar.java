package info.kmichel.lobster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.HeaderTokenizer;
import javax.mail.internet.ParseException;

public class CookieJar {

	private final Map<String, String> properties;
	private final Map<String, List<Cookie>> cookies;
	private Cookie lastCookie;

	public CookieJar() {
		properties = new HashMap<String, String>();
		cookies = new HashMap<String, List<Cookie>>();
	}

	public void setProperty(final String name, final String value) {
		properties.put(name, value);
	}

	public String getProperty(final String name) {
		return properties.get(name);
	}

	public void add(final Cookie cookie) {
		lastCookie = cookie;
		if (cookies.get(cookie.getName()) == null) {
			cookies.put(cookie.getName(), new ArrayList<Cookie>());
		}
		cookies.get(cookie.getName()).add(cookie);
	}

	public String getFirst(final String name) {
		final List<Cookie> candidates = cookies.get(name);
		if (candidates == null || candidates.size() == 0) {
			return null;
		} else {
			return candidates.get(0).getValue();
		}
	}

	// XXX: this incorrectly allows "," between cookie value and it's properties
	public static CookieJar createFromHeader(final String header) {
		final CookieJar cookieJar = new CookieJar();
		final HeaderTokenizer tokenizer = new HeaderTokenizer(header, HeaderTokenizer.MIME);
		try {
			while (true) {
				final HeaderTokenizer.Token name = tokenizer.next();
				if (name.getType() == HeaderTokenizer.Token.EOF) {
					return cookieJar;
				} else if (name.getType() != HeaderTokenizer.Token.ATOM) {
					throw new RuntimeException("Unexpected token: "+name.getValue());
				}
				HeaderTokenizer.Token separator = tokenizer.next();
				if (separator.getType() == '=') {
					final HeaderTokenizer.Token value = tokenizer.next();
					if (
						value.getType() == HeaderTokenizer.Token.EOF
						|| value.getType() == HeaderTokenizer.Token.ATOM
						|| value.getType() == HeaderTokenizer.Token.QUOTEDSTRING) {
						// Note that if token is EOF, we are inserting null
						cookieJar.pushPair(name.getValue(), value.getValue());
					} else {
						throw new RuntimeException(
							"Unexpected token: "+value.getValue());
					}
					separator = tokenizer.next();
				} else {
					cookieJar.pushPair(name.getValue(), "");
				}

				if (separator.getType() == HeaderTokenizer.Token.EOF) {
					return cookieJar;
				} else if (separator.getType() != ';' && separator.getType() != ',') {
					throw new RuntimeException("Unexpected token: "+separator.getValue());
				}
			}
		} catch (final ParseException e) {
			throw new RuntimeException("Parse error", e);
		}
	}

	private void pushPair(final String name, final String value) {
		if (name.startsWith("$")) {
			if (lastCookie == null) {
				setProperty(name.substring(1), value);
			} else {
				lastCookie.setProperty(name.substring(1), value);
			}
		} else {
			add(new Cookie(name, value));
		}
	}

}
