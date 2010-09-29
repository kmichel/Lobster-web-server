package info.kmichel.lobster;

/**
 * A ResponseCode is to be used with
 * {@link Exchange#setResponseCode(ResponseCode)} to inform the client of the
 * result of the server execution for a given {@link Exchange}.
 *
 * @see Exchange
 *
 * @author Michel Kevin
 */
public enum ResponseCode {
	CONTINUE(100),
	SWITCHING_PROTOCOLS(101),

	OK(200),
	CREATED(201),
	ACCEPTED(202),
	NON_AUTHORITATIVE_INFORMATION(203),
	NO_CONTENT(204),
	RESET_CONTENT(205),
	PARTIAL_CONTENT(206),
	MULTIPLE_CHOICES(300),
	MOVED_PERMANENTLY(301),
	FOUND(302),
	SEE_OTHER(303),
	NOT_MODIFIED(304),
	USE_PROXY(305),
	// UNUSED_306(306), // Existed only by the past but still reserved
	TEMPORARY_REDIRECT(307),
	
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	// PAYMENT_REQUIRED(402), // for future use
	FORBIDDEN(403),
	NOT_FOUND(404),
	METHOD_NOT_ALLOWED(405),
	NOT_ACCEPTABLE(406),
	PROXY_AUTHENTICATION_REQUIRED(407),
	REQUEST_TIMEOUT(408),
	CONFLICT(409),
	GONE(410),
	LENGTH_REQUIRED(411),
	PRECONDITION_FAILED(412),
	REQUEST_ENTITY_TOO_LARGE(413),
	REQUEST_URI_TOO_LONG(414),
	UNSUPPORTED_MEDIA_TYPE(415),
	REQUESTED_RANGE_NOT_SATISFIABLE(416),
	EXPECTATION_FAILED(417),

	INTERNAL_SERVER_ERROR(500),
	NOT_IMPLEMENTED(501),
	BAD_GATEWAY(502),
	SERVICE_UNAVAILABLE(503),
	GATEWAY_TIMEOUT(504),
	HTTP_VERSION_NOT_SUPPORTED(505);

	private final int code;

	private ResponseCode(final int code) {
		this.code = code;
	}

	/**
	 * @return the HTTP response code as an integer
	 */
	public int getCode() {
		return this.code;
	}

}
