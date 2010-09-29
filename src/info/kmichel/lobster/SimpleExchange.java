package info.kmichel.lobster;

import com.sun.net.httpserver.HttpExchange;
import info.kmichel.util.Strings;
import info.kmichel.codecs.HumanBase32;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.mail.internet.HeaderTokenizer;
import javax.mail.internet.ParseException;
import org.apache.log4j.Logger;

/**
 * An {@code Exchange} represents a single request from a client, it contains
 * all informations needed to identify the request and methods needed to
 * reply to the client.
 *
 * @see Server
 *
 * @author Michel Kevin
 */
public class SimpleExchange implements Exchange {

	private final static Logger logger = Logger.getLogger("info.kmichel.lobster.SimpleExchange");

	private final HttpExchange exchange;
	private ResponseCode responseCode;
	private ByteArrayOutputStream rawStream;
	private GZIPOutputStream compressedStream;
	private Writer writer;
	private Arguments postArguments;
	private Arguments queryArguments;
	private CookieJar cookies;

	public SimpleExchange(final HttpExchange exchange) {
		this.exchange = exchange;
		this.responseCode = ResponseCode.OK;
		this.rawStream = null;
		this.compressedStream = null;
		this.writer = null;
		this.postArguments = null;
		this.queryArguments = null;
		this.cookies = null;
	}

	public CookieJar getCookies() {
		if (cookies == null) {
			cookies = CookieJar.createFromHeader(getRequestHeader("Cookie"));
		}
		return cookies;
	}

	public String toString() {
		return getRequestHeader("X-Real-IP")+" - - ["+new Date()+"] \""+getRequestMethod()+":"+getRequestPath()+"\" "+responseCode.getCode()+" -";
	}

	public String getRequestMethod() {
		return exchange.getRequestMethod();
	}

	public String getRequestPath() {
		return exchange.getRequestURI().getPath();
	}

	public Matcher getPathMatcher() {
		return null;
	}

	/**
	 * Strictly parse content type header, correctly ignoring possible embedded
	 * attributes or comments.
	 *
	 * @return The content type as a String, or {@code null} if the header is
	 * absent or invalid.
	 */
	private String getContentType() {
		try {
			final String rawContentType = getRequestHeader("Content-Type");
			final HeaderTokenizer tokenizer = new HeaderTokenizer(rawContentType, HeaderTokenizer.MIME);
			final HeaderTokenizer.Token type = tokenizer.next();
			final HeaderTokenizer.Token separator = tokenizer.next();
			final HeaderTokenizer.Token subtype = tokenizer.next();
			if (type.getType() == HeaderTokenizer.Token.ATOM
					&& separator.getType() == '/'
					&& subtype.getType() == HeaderTokenizer.Token.ATOM) {
				return type.getValue() + "/" + subtype.getValue();
			}
			return null;
		} catch (final ParseException e) {
			return null;
		}
	}

	public Arguments getPost() {
		if (postArguments == null) {
			if ("application/x-www-form-urlencoded".equals(getContentType())) {
				try {
					postArguments = Arguments.fromURLEncoded(Strings.consume(exchange.getRequestBody()));
				} catch (final IOException e) {
					throw new WrappedExchangeException(e);
				}
			} else if ("multipart/form-data".equals(getContentType())) {
				logger.warn("Got POST data as multipart/form-data, parser not implemented");
				postArguments = new Arguments();
			} else {
				postArguments = new Arguments();
			}
		}
		return postArguments;
	}

	public Arguments getQuery() {
		if (queryArguments == null) {
			final String rawQuery = exchange.getRequestURI().getRawQuery();
			if (rawQuery != null) {
				queryArguments = Arguments.fromURLEncoded(rawQuery);
			} else {
				queryArguments = new Arguments();
			}
		}
		return queryArguments;
	}

	public String getRequestHeader(final String key) {
		return exchange.getRequestHeaders().getFirst(key);
	}

	public void setHeader(final String key, final String value) {
		if (exchange.getResponseCode() != -1) {
			throw new RuntimeException("too late for headers !");
		}
		exchange.getResponseHeaders().set(key, value);
	}

	public void unsetHeader(final String key) {
		exchange.getResponseHeaders().remove(key);
	}

	public void setResponseCode(final ResponseCode responseCode) {
		if (exchange.getResponseCode() != -1) {
			throw new RuntimeException("too late for setting response code !");
		}
		this.responseCode = responseCode;
	}

	// XXX: This does not handle wildcard in Accept-Encoding header
	private final static Pattern qValuePattern = Pattern.compile("(?:0(?:\\.\\d{0,3})?)|(?:1(?:\\.0{0,3})?)");
	private boolean acceptEncoding(final String encoding) {
		final String rawAccept = getRequestHeader("Accept-Encoding");
		try {
			final HeaderTokenizer tokenizer = new HeaderTokenizer(rawAccept, HeaderTokenizer.MIME);
			while (true) {
				final HeaderTokenizer.Token name = tokenizer.next();
				if (name.getType() == HeaderTokenizer.Token.EOF) {
					return false;
				}
				if (name.getType() != HeaderTokenizer.Token.ATOM) {
					throw new ParseException("Expected atom");
				}
				HeaderTokenizer.Token separator = tokenizer.next();
				final float qValue;
				if (separator.getType() == ';') {
					final HeaderTokenizer.Token q = tokenizer.next();
					if (q.getType() != HeaderTokenizer.Token.ATOM
						|| !q.getValue().equals("q")) {
						throw new ParseException("Expected atom named 'q' ");
					}
					final HeaderTokenizer.Token equal = tokenizer.next();
					if (equal.getType() != '=') {
						throw new ParseException("Expected separator '=' ");
					}
					final HeaderTokenizer.Token value = tokenizer.next();
					if (value.getType() != HeaderTokenizer.Token.ATOM
						|| !qValuePattern.matcher(value.getValue()).matches()) {
						throw new ParseException("Expected float value between 0 and 1 with at most three decimals");
					}
					qValue = Float.valueOf(value.getValue());
					separator = tokenizer.next();
				} else {
					qValue = 1.0f;
				}
				if (separator.getType() == ',' || separator.getType() == HeaderTokenizer.Token.EOF) {
					if (name.getValue().equals(encoding) && qValue != 0.0f) {
						return true;
					} else if (separator.getType() == HeaderTokenizer.Token.EOF) {
						return false;
					}
				}
			}
		} catch (final ParseException e) {
			return false;
		}
	}

	public Writer getWriter() throws IOException {
		if (writer == null) {
			assert(rawStream == null);
			assert(compressedStream == null);
			rawStream = new ByteArrayOutputStream();
			if (acceptEncoding("gzip")) {
				compressedStream = new GZIPOutputStream(rawStream);
				setHeader("Content-Encoding", "gzip");
				// XXX this may cause caching problem on some old proxy and IE <= 6
				setHeader("Vary", "Accept-Encoding");
				writer = new OutputStreamWriter(compressedStream, "UTF-8");
			} else {
				writer = new OutputStreamWriter(rawStream, "UTF-8");
			}
		}
		return writer;
	}

	public void write(final CharSequence... sequences) {
		try {
			final Writer writer = getWriter();
			for (final CharSequence sequence: sequences) {
				writer.append(sequence);
			}
		} catch (final IOException e) {
			throw new WrappedExchangeException(e);
		}
	}

	public void flush() {
		try {
			writer.flush();
			if (compressedStream != null) {
				compressedStream.flush();
			}
			exchange.sendResponseHeaders(responseCode.getCode(), /*chuncked*/ 0);
			rawStream.writeTo(exchange.getResponseBody());
			rawStream.reset();
		} catch (final IOException e) {
			throw new WrappedExchangeException(e);
		}
	}

	public void finish() throws IOException {
		if (writer != null) {
			assert(rawStream != null);
			writer.flush();
			if (compressedStream != null) {
				compressedStream.finish();
			}
			if (exchange.getResponseCode() == -1) {
				// We can't send 0 as size, which means chuncked transert, we must
				// send -1 instead.
				final int sentSize = rawStream.size() != 0 ? rawStream.size() : -1;
				exchange.sendResponseHeaders(responseCode.getCode(), sentSize);
			}
			rawStream.writeTo(exchange.getResponseBody());
			rawStream.reset();
		} else {
			exchange.sendResponseHeaders(responseCode.getCode(), /*empty*/ -1);
			exchange.getResponseBody();
		}
		exchange.close();
	}

}
