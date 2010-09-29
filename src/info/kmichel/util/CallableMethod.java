package info.kmichel.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class CallableMethod implements Callable {

	private final Object instance;
	private final Method method;

	public CallableMethod(final Object instance, final Method method) {
		this.instance = instance;
		this.method = method;
	}

	public Object invoke(final Object... parameters)
		throws
			InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return method.invoke(instance, parameters);
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}

	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	public Annotation[][] getParameterAnnotations() {
		return method.getParameterAnnotations();
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return method.getAnnotation(annotationClass);
	}
	
	public Annotation[] getAnnotations() {
		return method.getAnnotations();
	}

	public Annotation[] getDeclaredAnnotations() {
		return method.getDeclaredAnnotations();
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return method.isAnnotationPresent(annotationClass);
	}

	public String toString() {
		if (instance == null) {
			return method.toString();
		} else {
			return method.toString()+" bound to "+instance.toString();
		}
	}

}
