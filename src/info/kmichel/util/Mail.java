package info.kmichel.util;

import java.text.ParseException;
import java.util.regex.Pattern;
import java.util.Locale;

/**
 * Does not handle :
 * - comments
 * - quoted string
 * - FWS
 * - IP as a domain part
 *
 * Stored value is lowercased to avoid duplicate (it's not conformant !)
 */
public class Mail {

	private final static Pattern charsThatRequireQuoting = Pattern.compile("[\\x00-\\x20\\(\\)<>\\[\\]:;@\\\\,\"]");

	private final String value;

	public Mail(final String mail) throws ParseException {
		if (checkContent(mail)) {
			this.value = mail.toLowerCase(Locale.ENGLISH);
		} else {
			throw new ParseException("Invalid email format", 0);
		}
	}

	public String getDomain() {
		// This will always work since we validated in the constructor
		return value.split("@", -1)[1];
	}

	public String toString() {
		return value;
	}

	private boolean checkContent(final String mail) {
		// http://www.rfc-editor.org/errata_search.php?rfc=3696
		if (mail.length() > 256) return false;
		final String[] parts = mail.split("@", -1);
		return
			(parts.length == 2)
			&& checkLocal(parts[0])
			&& checkDomain(parts[1]);
	}

	private boolean checkLocal(final String local) {
		if (local.length() > 64) return false;
		final String[] parts = local.split("\\.", -1);
		if (parts.length < 1) return false;
		for (final String part: parts) {
			if (
				// Detect dot a beginning, consecutive dots, dot at end
				part.length() == 0
				// http://tools.ietf.org/html/rfc3696#section-3
				|| charsThatRequireQuoting.matcher(part).find() ) {
				return false;
			}
		}
		return true;
	}

	private boolean checkDomain(final String domain) {
		// http://tools.ietf.org/html/rfc1123#section-6.1.3.5
		if (domain.length() > 255) return false;
		final String[] parts = domain.split("\\.", -1);
		// Must have at least a dot
		if (parts.length < 2) return false;
		for (final String part: parts) {
			if (
				// Detect dot a beginning, consecutive dots, dot at end
				part.length() == 0
				// http://tools.ietf.org/html/rfc1123#section-6.1.3.5
				// Each label can be up to 63 octets
				|| part.length() > 63
				// http://tools.ietf.org/html/rfc3696#section-2
				// It is not permitted to have an hyphen at the beginning or end of
				// a label
				|| part.startsWith("-")
				|| part.endsWith("-")
				// http://tools.ietf.org/html/rfc3696#section-3
				|| charsThatRequireQuoting.matcher(part).find() ) {
				return false;
			}
		}
		// TLD can't be all-numeric
		if (parts[parts.length-1].matches("\\d+")) {
			return false;
		}
		return true;
	}

}
