package info.kmichel.lobster.wrappers;

import info.kmichel.lobster.Exchange;
import info.kmichel.lobster.Session;
import info.kmichel.lobster.Redirection;
import java.util.List;

public abstract class ExchangeWrappers {

	private ExchangeWrappers() {}

	public static String fromPath(final Exchange exchange, final FromPath annotation) {
		return exchange.getPathMatcher().group(annotation.value());
	}

	public static Session fromSession(final Exchange exchange) {
		return new Session(exchange);
	}

	public static String firstFromPost(final Exchange exchange, final FromPost annotation) {
		return exchange.getPost().getFirst(annotation.value());
	}

	public static String firstFromQuery(final Exchange exchange, final FromQuery annotation) {
		return exchange.getQuery().getFirst(annotation.value());
	}

	public static List<String> fromPost(final Exchange exchange, final FromPost annotation) {
		return exchange.getPost().get(annotation.value());
	}

	public static List<String> fromQuery(final Exchange exchange, final FromQuery annotation) {
		return exchange.getQuery().get(annotation.value());
	}

	public static Redirection redirection(final Exchange exchange) {
		return new Redirection(exchange);
	}
}
