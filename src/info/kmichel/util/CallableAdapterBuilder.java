package info.kmichel.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class CallableAdapterBuilder {

	private final ParameterAdapterBuilder builder;

	public CallableAdapterBuilder(final ParameterAdapterBuilder builder) {
		this.builder = builder;
	}

	public CallableAdapter build(final Callable callable) {
		final Class<?>[] parameters = callable.getParameterTypes();
		final Annotation[][] annotations = callable.getParameterAnnotations();
		final CallableAdapter adapter = new CallableAdapter(callable);
		for (int i=0; i<parameters.length; ++i) {
			final ParameterAdapter parameterAdapter = builder.build(parameters[i], Arrays.asList(annotations[i]));
			if (parameterAdapter == null) {
				throw new IllegalArgumentException("Unable to adapt parameter "+i+" of "+callable);
			} else {
				adapter.add(parameterAdapter);
			}
		}
		return adapter;
	}

}
