package info.kmichel.authent;

import info.kmichel.babel.Babel;
import info.kmichel.babel.BabelDataSource;
import info.kmichel.babel.BabelConnection;
import info.kmichel.codecs.HumanBase32;
import info.kmichel.util.Strings;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class BabelLoginAuthenticator implements LoginAuthenticator {

	private final BabelDataSource babelDataSource;
	private final static Charset charset = Charset.forName("utf-8");

	public BabelLoginAuthenticator(final BabelDataSource babelDataSource) {
		this.babelDataSource = babelDataSource;
	}

	public Integer authenticate(final Integer account, final String password) {
		final AuthenticationResponse response = babelDataSource.getConnection().queryFirst(
			Babel.fromRow(AuthenticationResponse.class),
			"select account, salt, salted_password_hash from authentication where account = ?",
			account);
		if (response == null) {
			return null;
		} else {
			final String saltedPasswordHash = digest(response.getSalt(), password);
			if (saltedPasswordHash.equals(response.getSaltedPasswordHash())) {
				return account;
			} else {
				return null;
			}
		}
	}

	public void setPassword(final Integer account, final String password) {
		// XXX missing transactional behavior
		final BabelConnection connection = babelDataSource.getConnection();
		final String salt = Strings.random(10);
		connection.update(
			"delete from authentication where account = ?",
			account);
		connection.update(
			"insert into authentication(account, salt, salted_password_hash) "+
			"values (?, ?, ?)",
			account, salt, digest(salt, password));
	}

	private String digest(final String... pieces) {
		try {
			final MessageDigest message = MessageDigest.getInstance("SHA-1");
			for (final String piece: pieces) {
				message.update(piece.getBytes(charset));
			}
			return HumanBase32.encode(message.digest());
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
