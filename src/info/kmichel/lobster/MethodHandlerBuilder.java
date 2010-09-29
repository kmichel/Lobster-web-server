package info.kmichel.lobster;

import info.kmichel.util.Callable;
import info.kmichel.util.CallableMethod;
import info.kmichel.util.CallableAdapterBuilder;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class MethodHandlerBuilder {

	private final CallableAdapterBuilder builder;

	public MethodHandlerBuilder(final CallableAdapterBuilder builder) {
		this.builder = builder;
	}

	public Collection<Handler> buildAll(final Collection<Handler> handlers, final Collection<Callable> callables) {
		for (final Callable callable: callables) {
			if (callable.isAnnotationPresent(Path.class)) {
				handlers.add(build(callable));
			}
		}
		return handlers;
	}
	
	public MethodHandler build(final Callable callable) {
		if (!callable.isAnnotationPresent(Path.class)) {
			throw new IllegalArgumentException("Callable must be annotated with a Path annotation");
		}
		final String pattern = callable.getAnnotation(Path.class).value();
		final Set<String> allowedMethods = new TreeSet<String>();
		if (callable.isAnnotationPresent(DoGet.class)) {
			allowedMethods.add("GET");
		}
		if (callable.isAnnotationPresent(DoPost.class)) {
			allowedMethods.add("POST");
		}
		return new MethodHandler(
			builder.build(callable),
			allowedMethods,
			Pattern.compile(pattern));
	}

}
