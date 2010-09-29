package info.kmichel.authent;

public interface InviteAuthenticator {

	Integer authenticate(final String mail, final String friendCode);

	void add(final Integer account, final String mail, final String friendCode);

	boolean hasInvited(final Integer account, final String mail);

	int invitesLeft(final Integer account);

}
