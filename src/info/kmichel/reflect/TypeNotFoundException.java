package info.kmichel.reflect;

public class TypeNotFoundException extends Exception {

	private final static long serialVersionUID = 0L;

	public TypeNotFoundException(final String message) {
		super(message);
	}

	public TypeNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
