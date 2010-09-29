package info.kmichel.babel;

public class BabelException extends RuntimeException {

	private final static long serialVersionUID = 0;

	public BabelException() {
		super();
	}

	public BabelException(final String message) {
		super(message);
	}
	
	public BabelException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public BabelException(final Throwable cause) {
		super(cause);
	}

}
