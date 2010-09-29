package info.kmichel.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

public class CallableBuilder {

	public Collection<Callable> build(final Collection<Callable> callables, final Object instance) {
		for (final Method method: instance.getClass().getMethods()) {
			if (!Modifier.isStatic(method.getModifiers())) {
				if (Modifier.isPublic(method.getModifiers())) {
					if (!Modifier.isAbstract(method.getModifiers())) {
						callables.add(new CallableMethod(instance, method));
					}
				}
			}
		}
		return callables;
	}

	public Collection<Callable> build(final Collection<Callable> callables, final Object... instances) {
		for (final Object instance: instances) {
			build(callables, instance);
		}
		return callables;
	}

}
