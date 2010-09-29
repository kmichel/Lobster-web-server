package info.kmichel.lobster;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;

/**
 * An {@code Exchange} represents a single request from a client, it contains
 * all informations needed to identify the request and methods needed to
 * reply to the client.
 *
 * @see Server
 *
 * @author Michel Kevin
 */
public interface Exchange {

	CookieJar getCookies();

	/**
	 * @return a single-line String containing the request method, the request
	 * path and the response code.
	 */
	String toString();

	/**
	 * @return the http method.
	 */
	String getRequestMethod();

	/**
	 * @return the queried path, without any query string, port or host.
	 */
	String getRequestPath();

	/**
	 * @return the matcher used for the path pattern matching.
	 */
	Matcher getPathMatcher();

	/**
	 * @return all arguments sent as POST-data in the request body.
	 */
	Arguments getPost();

	/**
	 * @return all arguments sent as query string in the request URI.
	 */
	Arguments getQuery();

	/**
	 * @return the value of the first occurrence of the request header with the
	 * given key.
	 */
	String getRequestHeader(final String key);

	/**
	 * Sets a response header, if used twice with the same key, header will be
	 * overwrited, not duplicated.
	 */
	void setHeader(final String key, final String value);

	/**
	 * Unsets a response header.
	 */
	void unsetHeader(final String key);

	/**
	 * Sets the response code, this can be at any time and many times as long as
	 * you don't finish or flush the exchange.
	 */
	void setResponseCode(final ResponseCode responseCode);

	/**
	 * @return a {@link java.io.Writer} in which you can append content, effect will be similar
	 * to {@link #write(CharSequence...)}.
	 */
	Writer getWriter() throws IOException;

	/**
	 * Push content to the client in the response body. The content won't be sent
	 * immediately unless you use {@link #flush()}.
	 */
	void write(final CharSequence... sequences);

	/**
	 * Force flushing data to the client, this also forces using chuncked
	 * transfert encoding since we don't know yet the size of the response body.
	 */
	void flush();

	/**
	 * Finishes the exchange, use only once and don't use the exchange after
	 * this.
	 */
	void finish() throws IOException;

}
