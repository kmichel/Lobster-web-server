package info.kmichel.authent;

public interface AuthenticationResponse {

	Integer getAccount();

	String getSalt();

	String getSaltedPasswordHash();

}
