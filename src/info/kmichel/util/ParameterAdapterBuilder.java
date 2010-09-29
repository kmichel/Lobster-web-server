package info.kmichel.util;

import info.kmichel.util.Callable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterAdapterBuilder {

	private final Class<?> startType;
	private final Map<Class<? extends Annotation>, List<Callable>> wrappers;
	private final List<Callable> endWrappers;

	public ParameterAdapterBuilder(final Class<?> startType) {
		this.startType = startType;
		wrappers = new HashMap<Class<? extends Annotation>, List<Callable>>();
		endWrappers = new ArrayList<Callable>();
	}

	public void addAll(final Collection<Callable> callables) {
		for (final Callable callable: callables) {
			add(callable);
		}
	}

	public void add(final Callable callable) {
		final Class<?> output = callable.getReturnType();
		if (output.equals(Void.TYPE)) {
			throw new IllegalArgumentException("Bad wrapper signature : "+callable);
		}
		final Class<?>[] parameters = callable.getParameterTypes();
		if (parameters.length == 2
			&& Annotation.class.isAssignableFrom(parameters[1])) {
			if (wrappers.get(parameters[1]) == null) {
				wrappers.put(parameters[1].asSubclass(Annotation.class), new ArrayList<Callable>());
			}
			wrappers.get(parameters[1]).add(callable);
		} else if (parameters.length == 1 && parameters[0].isAssignableFrom(startType)) {
			if (output.isAssignableFrom(startType)) {
				throw new IllegalArgumentException(
					"Adding this wrapper would always generate more than one path : "+callable);
			} else {
				endWrappers.add(callable);
			}
		} else {
			throw new IllegalArgumentException("Bad wrapper signature : "+callable);
		}
	}

	// This code could be simpler if it weren't trying to give proper error
	// message on multiple-path error.
	public ParameterAdapter build(final Class<?> endType, final List<Annotation> annotations) {
		if (annotations == null || annotations.size() == 0) {
			if (endType.isAssignableFrom(startType)) {
				return new ParameterAdapter();
			} else {
				ParameterAdapter path = null;
				for (final Callable wrapper: endWrappers) {
					if (endType.isAssignableFrom(wrapper.getReturnType())) {
						if (path == null) {
							path = new ParameterAdapter();
							path.add(wrapper, null);
						} else {
							throw new IllegalArgumentException("More than one path available");
						}
					}
				}
				return path;
			}
		} else {
			final Annotation annotation = annotations.get(annotations.size()-1);
			// XXX it's a bit weak way to do it, but works, should be replaced ASAP
			// with a type-matching-map structure ;)
			final Class<?> annotationType = annotation.getClass().getInterfaces()[0];
			if (wrappers.get(annotationType) != null) {
				ParameterAdapter path = null;
				for (final Callable wrapper: wrappers.get(annotationType)) {
					if (endType.isAssignableFrom(wrapper.getReturnType())) {
						final ParameterAdapter newPath = build(
							wrapper.getParameterTypes()[0],
							annotations.subList(0, annotations.size()-1));
						if (newPath != null) {
							if (path == null) {
								path = newPath;
								path.add(wrapper, annotation);
							} else {
								throw new IllegalArgumentException("More than one path available");
							}
						}
					}
				}
				return path;
			} else {
				// This annotation is completely unknown, we can safely ignore it
				return build(
					endType,
					annotations.subList(0, annotations.size()-1));
			}
		}
	}

}
