package info.kmichel.lobster.front;

import info.kmichel.lobster.DoGet;
import info.kmichel.lobster.DoPost;
import info.kmichel.lobster.Exchange;
import info.kmichel.lobster.Path;
import info.kmichel.lobster.ResponseCode;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplate;

public class Errors {

	private final StringTemplateGroup templateFactory;

	public Errors(
			final StringTemplateGroup templateFactory) {
		this.templateFactory = templateFactory;
	}

	@DoGet @DoPost @Path(".*")
	public StringTemplate notFound(final Exchange exchange) {
		exchange.setResponseCode(ResponseCode.NOT_FOUND);
		final StringTemplate template = templateFactory.getInstanceOf("error/notFound");
		template.setAttribute("path", exchange.getRequestPath());
		return template;
	}
}
