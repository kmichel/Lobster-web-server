package info.kmichel.lobster;

import info.kmichel.util.Callable;
import info.kmichel.util.Strings;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;

public class MethodHandler implements Handler {
	
	private final Callable callable;
	private final Pattern pattern;
	private final Set<String> allowedMethods;

	public MethodHandler(final Callable callable, final Set<String> allowedMethods, final Pattern pattern) {
		this.callable = callable;
		this.allowedMethods = allowedMethods;
		this.pattern = pattern;
	}

	public boolean handle(final Exchange exchange) {
		final Matcher matcher = pattern.matcher(exchange.getRequestPath());
		if (matcher.matches()) {
			if (allowedMethods.contains(exchange.getRequestMethod())) {
				final Exchange exchangeProxy = new ExchangeProxy(exchange){
					public Matcher getPathMatcher() {
						return matcher;
					}
				};
				try {
					final Object returned = callable.invoke(exchangeProxy);
					if (returned != null) {
						exchange.write(returned.toString());
					}
				} catch (final InstantiationException e) {
					throw new RuntimeException(e);
				} catch (final IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (final InvocationTargetException e) {
					throw new RuntimeException(e.getCause());
				}
				return true;
			} else {
				// This handler matches the pattern but not the method
				// This information should be reused in MethodNotAllowed response code
				return false;
			}
		} else {
			return false;
		}
	}

	public String toString() {
		return Strings.join(allowedMethods, ",") + ": " + pattern;
	}

}
