package info.kmichel.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CallableAdapter implements Callable {

	private final Callable callable;
	private final List<ParameterAdapter> adapters;

	public CallableAdapter(final Callable callable) {
		this.callable = callable;
		this.adapters = new ArrayList<ParameterAdapter>();
	}

	void add(final ParameterAdapter adapter) {
		adapters.add(adapter);
	}

	public Object invoke(final Object... parameters)
		throws
			InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		final Object endParameters[] = new Object[adapters.size()];
		for (int i=0; i<endParameters.length; ++i) {
			endParameters[i] = adapters.get(i).invoke(parameters);
		}
		return callable.invoke(endParameters);
	}

	public Class<?> getReturnType() {
		return callable.getReturnType();
	}

	public Class<?>[] getParameterTypes() {
		// XXX: should return the common subtypes asked for all wrappers
		// for each parameter, but I'm lazy tonight.
		throw new UnsupportedOperationException();
	}

	public Annotation[][] getParameterAnnotations() {
		// We can't do an union of all annotation for each parameter,
		// thus we decide to silently ignore all of them.
		return new Annotation[adapters.size()][0];
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}
	
	public Annotation[] getAnnotations() {
		return new Annotation[0];
	}

	public Annotation[] getDeclaredAnnotations() {
		return new Annotation[0];
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return false;
	}

}
