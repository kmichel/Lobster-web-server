package info.kmichel.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CallableConstructor implements Callable {

	private final Constructor<?> constructor;

	public CallableConstructor(final Constructor<?> constructor) {
		this.constructor = constructor;
	}

	public Object invoke(final Object... parameters)
		throws
			InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return constructor.newInstance(parameters);	
	}

	public Class<?> getReturnType() {
		return constructor.getDeclaringClass();
	}

	public Class<?>[] getParameterTypes() {
		return constructor.getParameterTypes();
	}

	public Annotation[][] getParameterAnnotations() {
		return constructor.getParameterAnnotations();
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return constructor.getAnnotation(annotationClass);
	}
	
	public Annotation[] getAnnotations() {
		return constructor.getAnnotations();
	}

	public Annotation[] getDeclaredAnnotations() {
		return constructor.getDeclaredAnnotations();
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return constructor.isAnnotationPresent(annotationClass);
	}

	public String toString() {
		return constructor.toString();
	}

}
