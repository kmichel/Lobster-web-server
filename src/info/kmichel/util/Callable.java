package info.kmichel.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;

public interface Callable extends AnnotatedElement {

	Object invoke(final Object... parameters)
		throws
			InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException;

	Class<?> getReturnType();

	Class<?>[] getParameterTypes();

	Annotation[][] getParameterAnnotations();

}
