package info.kmichel.lobster.demo;

import info.kmichel.lobster.DoGet;
import info.kmichel.lobster.Exchange;
import info.kmichel.lobster.Path;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

public class Demo {

    private final StringTemplateGroup templateFactory;

    public Demo(
        final StringTemplateGroup templateFactory) {
        this.templateFactory = templateFactory;
    }

    @DoGet @Path("/")
    public StringTemplate displayHome(final Exchange exchange) {
        final StringTemplate template = templateFactory.getInstanceOf("home");
        return template;
    }

}
