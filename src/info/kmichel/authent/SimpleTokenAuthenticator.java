package info.kmichel.authent;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTokenAuthenticator implements TokenAuthenticator {

	private final ConcurrentMap<String, Integer> bindings;

	public SimpleTokenAuthenticator() {
		bindings = new ConcurrentHashMap<String, Integer>();
	}

	public Integer authenticate(final String tokenIdentifier) {
		if (tokenIdentifier == null) {
			return null;
		} else {
			return bindings.get(tokenIdentifier);
		}
	}

	public void associate(final Integer userIdentifier, final String tokenIdentifier) {
		bindings.put(tokenIdentifier, userIdentifier);
	}

	public void revoke(final String tokenIdentifier) {
		bindings.remove(tokenIdentifier);
	}

	public Collection<String> fetchTokens(final Integer userIdentifier, final Collection<String> tokenIdentifiers) {
		// XXX not thread coherent
		for (final Map.Entry<String, Integer> entry: bindings.entrySet()) {
			if (entry.getValue().equals(userIdentifier)) {
				tokenIdentifiers.add(entry.getKey());
			}
		}
		return tokenIdentifiers;
	}

}
