package info.kmichel.lobster;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.Collection;
import org.apache.log4j.Logger;

public class Server {

	private final static Logger logger = Logger.getLogger("info.kmichel.lobster.Server");

	private final InetSocketAddress address;
	private final Collection<Handler> handlers;
	
	public Server(final InetSocketAddress address, final Collection<Handler> handlers) {
		this.address = address;
		this.handlers = handlers;
	}

	public void start() throws IOException {
		final HttpServer server = HttpServer.create(address, 10);
		logger.info("Starting server on "+address);
		for (final Handler handler: handlers) {
			logger.info("Using handler: "+handler);
		}
		server.setExecutor(Executors.newCachedThreadPool());
		server.createContext("/", new HttpHandler() {
			public void handle(final HttpExchange httpExchange) {
				try {
					final Exchange exchange = new SimpleExchange(httpExchange);
					exchange.setHeader("Content-Type", "text/html; charset=UTF-8");
					try {
						boolean handled = false;
						for (final Handler handler: handlers) {
							handled = handler.handle(exchange);
							if (handled) {
								break;
							}
						}
						if (!handled) {
							exchange.setResponseCode(ResponseCode.NOT_FOUND);
						}
						logger.info(exchange);
					} catch (final Exception e) {
						logger.error(exchange, e);
						exchange.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR);
						try {
							// WARNING: if error message is not text/plain we must take
							// care of injection through exception messages.
							exchange.setHeader("Content-Type", "text/plain; charset=UTF-8");
							e.printStackTrace(new PrintWriter(exchange.getWriter()));
						} catch (final IOException ee) {
							logger.error(exchange, ee);
							exchange.write("500 - Internal Server Error");
						}
					}
					try {
						exchange.finish();
					} catch (final IOException e) {
						logger.error(exchange, e);
					}
				} catch (final RuntimeException e) {
					logger.error(e);
				}
			}
		});
		server.start();
	}

}
