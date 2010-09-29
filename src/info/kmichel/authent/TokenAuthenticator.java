package info.kmichel.authent;

import java.util.Collection;

public interface TokenAuthenticator {

	/**
	 * Returns the user identifier associated with this token identifier,
	 * or null if this token is not bound to any user.
	 */
	Integer authenticate(final String tokenIdentifier);

	/**
	 * Associate a token with some user, a token can be associated to only
	 * one user, but an user can have more than one token.
	 */
	void associate(final Integer userIdentifier, final String tokenIdentifier);

	/**
	 * Revokes a token, dissociating the user from it.
	 */
	void revoke(final String tokenIdentifier);

	/**
	 * Get the list of valid tokens for a given user name.
	 */
	Collection<String> fetchTokens(final Integer userIdentifier, final Collection<String> tokenIdentifiers);

}
