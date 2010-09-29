package info.kmichel.lobster;

public class Redirection {

	private final Exchange exchange;

	public Redirection(final Exchange exchange) {
		this.exchange = exchange;
	}

	// Unless the request method was HEAD, the response body SHOULD
	// contain a short hypertext note with a hyperlink to the new URI(s). 
	public void to(final String path) {
		exchange.setResponseCode(ResponseCode.SEE_OTHER);
		// XXX Do we rely too much on the Host header without checking it ?
		// XXX No support for relative path
		// XXX No support for anything else than http (such as https)
		exchange.setHeader("Location", "http://"+exchange.getRequestHeader("Host")+path);
		exchange.unsetHeader("Content-Type");
	}

}
