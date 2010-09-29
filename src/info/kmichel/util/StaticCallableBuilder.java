package info.kmichel.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

public class StaticCallableBuilder {

	public Collection<Callable> build(final Collection<Callable> callables, final Class<?> clazz) {
		for (final Method method: clazz.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				if (Modifier.isPublic(method.getModifiers())) {
					if (!Modifier.isAbstract(method.getModifiers())) {
						callables.add(new CallableMethod(null, method));
					}
				}
			}
		}
		for (final Constructor constructor: clazz.getConstructors()) {
			if (Modifier.isPublic(constructor.getModifiers())) {
				if (!Modifier.isAbstract(constructor.getModifiers())) {
					callables.add(new CallableConstructor(constructor));
				}
			}
		}
		return callables;
	}

	public Collection<Callable> build(final Collection<Callable> callables, final Class<?>... classes) {
		for (final Class<?> clazz: classes) {
			build(callables, clazz);
		}
		return callables;
	}

}
