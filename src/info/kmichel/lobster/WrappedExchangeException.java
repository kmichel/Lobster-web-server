package info.kmichel.lobster;

/**
 * Exception used as a wrapper inside {@link Exchange} methods to allow them to
 * bubble through handler objects without forcing them to know about the
 * internals of the {@link Exchange} error handling.
 *
 * @author Michel Kevin
 */
class WrappedExchangeException extends RuntimeException {
	private static final long serialVersionUID = 0L;
	WrappedExchangeException(Throwable cause) {
		super(cause);
	}
}
