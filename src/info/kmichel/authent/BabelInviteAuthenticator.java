package info.kmichel.authent;

import info.kmichel.babel.Babel;
import info.kmichel.babel.BabelDataSource;

public class BabelInviteAuthenticator implements InviteAuthenticator {

	private final BabelDataSource babelDataSource;
	private final int maxInvites;

	public BabelInviteAuthenticator(final BabelDataSource babelDataSource, final int maxInvites) {
		this.babelDataSource = babelDataSource;
		this.maxInvites = maxInvites;
	}

	public Integer authenticate(final String mail, final String friendCode) {
		return babelDataSource.getConnection().queryFirst(
			Babel.fromField("account", Integer.class),
			"select account from invite where friend_code = ? and mail = ?",
			friendCode, mail);
	}

	public void add(final Integer account, final String mail, final String friendCode) {
		babelDataSource.getConnection().update(
			"insert into invite (account, mail, friend_code) "+
			"values (?, ?, ?)",
			account, mail, friendCode);
	}
	
	public boolean hasInvited(final Integer account, final String mail) {
		return 0L != babelDataSource.getConnection().queryFirst(
			Babel.fromField("invited", Long.class),
			"select count(*) as invited from invite where account = ? and mail = ?",
			account, mail);
	}
	
	public int invitesLeft(final Integer account) {
		return maxInvites - babelDataSource.getConnection().queryFirst(
			Babel.fromField("invited", Long.class),
			"select count(*) as invited from invite where account = ?",
			account).intValue();
	}

}
