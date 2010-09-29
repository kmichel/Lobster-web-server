package info.kmichel.authent;

public interface LoginAuthenticator {

	Integer authenticate(final Integer account, final String password);
	
	void setPassword(final Integer account, final String password);
}
