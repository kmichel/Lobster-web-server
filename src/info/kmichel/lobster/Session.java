package info.kmichel.lobster;

import info.kmichel.codecs.HumanBase32;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Session {

	private final static Pattern safeSessionPattern = Pattern.compile("[a-zA-Z0-9]*");

	private final Exchange exchange;
	private String identifier;
	private boolean identifierWasRemoved;

	public Session(final Exchange exchange) {
		this.exchange = exchange;
	}

	/**
	 * Tests whether the exchange is in a session.
	 */
	public boolean isActive() {
		return getIdentifier() != null;
	}

	/**
	 * Stops the current session.
	 */
	public void stop() {
		exchange.setHeader("Set-Cookie", "SID=; Version=1; expires="+getRelativeDate(-60*60)+"; Path=/; HttpOnly");
		identifier = null;
		identifierWasRemoved = true;
	}

	/**
	 * Starts a new session which will finish when the client close its window.
	 */
	public String start() {
		identifier = generateIdentifier();
		exchange.setHeader("Set-Cookie", "SID="+identifier+"; Version=1; Path=/; HttpOnly");
		return identifier;
	}

	/**
	 * Starts a new session which will stop after {@code length} seconds.
	 */
	public String start(final int length) {
		identifier = generateIdentifier();
		exchange.setHeader("Set-Cookie", "SID="+identifier+"; Version=1; expires="+getRelativeDate(length)+"; Path=/; HttpOnly");
		return identifier;
	}

	/**
	 * Change the current session's length to {@code length} seconds. If there's
	 * no running session, creates a new session with given {@code length}.
	 */
	public String extend(final int length) {
		if (isActive()) {
			exchange.setHeader("Set-Cookie", "SID="+identifier+"; Version=1; Max-Age="+getRelativeDate(length)+"; Path=/; HttpOnly");
			return identifier;
		} else {
			return start(length);
		}
	}

	/**
	 * Returns the session identifier, or null if session is not active. Will
	 * also return null if the session identifier found in the cookie is invalid.
	 */
	public String getIdentifier() {
		if (identifier == null && identifierWasRemoved == false) {
			identifier = exchange.getCookies().getFirst("SID");
			// We don't want the client to be tricked into sending a malicious
			// session id which may be sent back to the client using extendSession
			if (identifier != null && !safeSessionPattern.matcher(identifier).matches()) {
				stop();
			}
		}
		return identifier;
	}

	private String generateIdentifier() {
		final byte[] identifier = new byte[20];
		new SecureRandom().nextBytes(identifier);
		return HumanBase32.encode(identifier);
	}

	private String getRelativeDate(final int length) {
		final Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.add(Calendar.SECOND, length);
		final DateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z");
		dateFormat.setCalendar(calendar);
		return dateFormat.format(calendar.getTime());
	}

}
