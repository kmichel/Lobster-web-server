package info.kmichel.lobster.front;

import info.kmichel.util.Escaper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.antlr.stringtemplate.AttributeRenderer;

public class HTMLRenderer implements AttributeRenderer {

	private final Escaper escaper;
	private final Escaper javascriptEscaper;
	private final Map<String, String> forbiddens;

	public HTMLRenderer() {
		// We escape "too much" for html attributes and the two rcdata tags
		// (textarea and title) but even if it's not an optimal encoding it's
		// not a wrong one.
		escaper = new Escaper();
		escaper.add('<', "&lt;");
		escaper.add('>', "&gt;");
		escaper.add('"', "&quot;");
		escaper.add('\'', "&#039;");
		escaper.add('&', "&amp;");
		javascriptEscaper = new Escaper();
		javascriptEscaper.add('\'', "\\\'");
		javascriptEscaper.add('"', "\\\"");
		javascriptEscaper.add('\\', "\\");
		// Those are not strictly needed but cost not much and lead to more
		// readable code.
		javascriptEscaper.add('\b', "\\b");
		javascriptEscaper.add('\f', "\\f");
		javascriptEscaper.add('\n', "\\n");
		javascriptEscaper.add('\r', "\\r");
		javascriptEscaper.add('\t', "\\t");
		// This is not strictly needed, but avoids check for '</script'
		javascriptEscaper.add('<', "\\u003C");
		forbiddens = new HashMap<String, String>();
		forbiddens.put("cdata", "]]>");
		// This is too restrictive, we should check for the character just after
        // the tag name, if it's not " ", "/", ">", "\t", "\n" or "\f" then it's ok
		forbiddens.put("script:code", "</script");
		forbiddens.put("style", "</style");
		forbiddens.put("xmp", "</xmp");
		forbiddens.put("iframe", "</iframe");
		forbiddens.put("noembed", "</noembed");
		forbiddens.put("noframes", "</noframes");
		// This does not check for those two html comment rules :
		// - must not start with >
		// - must not start with -
		// But since we don't know where we are in the comment,
		// we will assume we are surrounded with other comment string.
		forbiddens.put("comment", "--");
	}

	public String toString(final Object object) {
		return escaper.escape(object.toString());
	}

	public String toString(final Object object, final String format) {
		final String input = object.toString();
		final String forbidden = forbiddens.get(format);
		if (forbidden != null) {
			if (input.toLowerCase().contains(forbidden)) {
				// SNIP !
				return "";
			} else {
				return input;
			}
		} else if (format.equals("script:string")) {
			return javascriptEscaper.escape(input);
		} else if (format.equals("url")) {
			final String encoded;
			try {
				encoded = URLEncoder.encode(input, "UTF-8");
			} catch (final UnsupportedEncodingException e) {
				// Every implementation of the Java platform is required to support
				// the following standard charsets: [...], UTF-8, [...]
				throw new RuntimeException(e);
			}
			return escaper.escape(encoded);
		} else if (format.equals("default")) {
			return escaper.escape(input);
		} else {
			throw new IllegalArgumentException("Unknown format: "+format);
		}
	}

}
