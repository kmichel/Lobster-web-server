package info.kmichel.authent;

import info.kmichel.babel.Babel;
import info.kmichel.babel.BabelDataSource;
import java.util.Collection;

public class BabelTokenAuthenticator implements TokenAuthenticator {
	
	private final BabelDataSource babelDataSource;
	private final String tableName;

	public BabelTokenAuthenticator(final BabelDataSource babelDataSource, final String tableName) {
		this.babelDataSource = babelDataSource;
		this.tableName = tableName;
	}

	public Integer authenticate(final String tokenIdentifier) {
		return babelDataSource.getConnection().queryFirst(
			Babel.fromField("account", Integer.class),
			"select account from "+tableName+" where token =  ?",
			tokenIdentifier);
	}

	public void associate(final Integer userIdentifier, final String tokenIdentifier) {
		babelDataSource.getConnection().update(
			"insert into "+tableName+" (account, token) "+
			"values (?, ?)",
			userIdentifier, tokenIdentifier);
	}

	public void revoke(final String tokenIdentifier) {
		babelDataSource.getConnection().update(
			"delete from "+tableName+" where token = ? ",
			tokenIdentifier);
	}

	public Collection<String> fetchTokens(final Integer userIdentifier, final Collection<String> tokenIdentifiers) {
		return babelDataSource.getConnection().query(
			tokenIdentifiers,
			Babel.fromField("token", String.class),
			"select token from "+tableName+" where account = ?",
			userIdentifier);
	}

}
