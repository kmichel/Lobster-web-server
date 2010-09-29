package info.kmichel.lobster;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;

public abstract class ExchangeProxy implements Exchange {

	private final Exchange exchange;

	public ExchangeProxy(final Exchange exchange) {
		this.exchange = exchange;
	}
	
	public CookieJar getCookies() {
		return exchange.getCookies();
	}

	public String toString() {
		return exchange.toString();
	}

	public String getRequestMethod() {
		return exchange.getRequestMethod();
	}

	public String getRequestPath() {
		return exchange.getRequestPath();
	}

	public Matcher getPathMatcher() {
		return exchange.getPathMatcher();
	}

	public Arguments getPost() {
		return exchange.getPost();
	}

	public Arguments getQuery() {
		return exchange.getQuery();
	}

	public String getRequestHeader(final String key) {
		return exchange.getRequestHeader(key);
	}

	public void setHeader(final String key, final String value) {
		exchange.setHeader(key, value);
	}

	public void unsetHeader(final String key) {
		exchange.unsetHeader(key);
	}

	public void setResponseCode(final ResponseCode responseCode) {
		exchange.setResponseCode(responseCode);
	}

	public Writer getWriter() throws IOException {
		return exchange.getWriter();
	}

	public void write(final CharSequence... sequences) {
		exchange.write(sequences);
	}

	public void flush() {
		exchange.flush();
	}

	public void finish() throws IOException {
		exchange.finish();
	}

}
