package info.kmichel.lobster.demo;

import info.kmichel.lobster.Exchange;
import info.kmichel.lobster.Handler;
import info.kmichel.lobster.MethodHandlerBuilder;
import info.kmichel.lobster.Server;
import info.kmichel.lobster.front.Errors;
import info.kmichel.lobster.front.HTMLRenderer;
import info.kmichel.lobster.wrappers.ExchangeWrappers;
import info.kmichel.lobster.wrappers.TypeWrappers;
import info.kmichel.util.Callable;
import info.kmichel.util.CallableAdapterBuilder;
import info.kmichel.util.CallableBuilder;
import info.kmichel.util.MatchMap;
import info.kmichel.util.ParameterAdapterBuilder;
import info.kmichel.util.StaticCallableBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import org.antlr.stringtemplate.AttributeRenderer;
import org.antlr.stringtemplate.StringTemplateGroup;

public class Main {

	public static void main(final String[] arguments) throws IOException, ClassNotFoundException {

		org.apache.log4j.BasicConfigurator.configure();

		final StringTemplateGroup templates = new StringTemplateGroup("base", "assets/templates");
		templates.setAttributeRenderers(new MatchMap<AttributeRenderer>());
		templates.registerRenderer(Object.class, new HTMLRenderer());
		templates.setRefreshInterval(0);

        final Demo demo = new Demo(templates);

		final Server server = new Server(
			new InetSocketAddress(8008),
			new MethodHandlerBuilder(
				new CallableAdapterBuilder(
					new ParameterAdapterBuilder(Exchange.class){{
						addAll(
							new StaticCallableBuilder().build(
								new ArrayList<Callable>(),
								ExchangeWrappers.class,
								TypeWrappers.class));
					}}))
			.buildAll(
				new ArrayList<Handler>(),
				new CallableBuilder().build(
					new ArrayList<Callable>(),
					demo,
					new Errors(templates))));

		server.start();
	}

}
