package info.kmichel.util;

import info.kmichel.util.Callable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ParameterAdapter implements Callable {

	private final List<Callable> wrappers;
	private final List<Annotation> annotations;

	public ParameterAdapter() {
		wrappers = new ArrayList<Callable>();
		annotations = new ArrayList<Annotation>();
	}

	void add(final Callable wrapper, final Annotation annotation) {
		wrappers.add(wrapper);
		annotations.add(annotation);
	}

	public Object invoke(final Object... parameters)
		throws
			InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		if (parameters.length != 1) {
			throw new IllegalArgumentException("This adapter takes exactly one parameter");
		}
		Object output = parameters[0];
		for (int i=0; i<wrappers.size(); ++i) {
			if (annotations.get(i) != null) {
				output = wrappers.get(i).invoke(output, annotations.get(i));
			} else {
				output = wrappers.get(i).invoke(output);
			}
		}
		return output;
	}

	public Class<?> getReturnType() {
		return wrappers.get(wrappers.size()-1).getReturnType();
	}

	public Class<?>[] getParameterTypes() {
		return wrappers.get(0).getParameterTypes();
	}

	public Annotation[][] getParameterAnnotations() {
		return wrappers.get(0).getParameterAnnotations();
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
