package info.kmichel.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

class SimpleParameterizedType implements ParameterizedType {

	private final Class<?> rawType;
	private final Type[] actualTypeArguments;

	// Set worst-cast parameterisation
	SimpleParameterizedType(final Class<?> rawType) {
		final TypeVariable<?>[] parameters = rawType.getTypeParameters();
		this.actualTypeArguments = new Type[parameters.length];
		for (int i=0; i<parameters.length; ++i) {
			actualTypeArguments[i] = new SimpleWildcardType(parameters[i].getBounds(), new Type[0]);
		}
		this.rawType = rawType;
	}

	SimpleParameterizedType(final Class<?> rawType, final Type... actualTypeArguments) {
		final TypeVariable<?>[] parameters = rawType.getTypeParameters();
		if (parameters.length != actualTypeArguments.length) {
			throw new IllegalArgumentException(
				"expected " + parameters.length + " parameters for " + rawType
				+ ", got " + actualTypeArguments.length);
		}
		for (int i=0; i<parameters.length; ++i) {
			for (final Type bound: parameters[i].getBounds()) {
				if (!Types.isAssignableFrom(bound, actualTypeArguments[i])) {
					throw new IllegalArgumentException(
						rawType + " : " + actualTypeArguments[i] + " is not assignable to " + bound);
				}
			}
		}
		this.rawType = rawType;
		this.actualTypeArguments = actualTypeArguments;
	}

	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	public Type getOwnerType() {
		return null;
	}

	public Type getRawType() {
		return rawType;
	}

	public String toString() {
		final StringBuilder builder = new StringBuilder();
		/*
		if (ownerType != null) {
			builder.append(ownerType.toString());
			builder.append('.');
		}
		*/
		builder.append(rawType.toString());
		builder.append('<');
		for (int i=0; i<actualTypeArguments.length; ++i) {
			builder.append(actualTypeArguments[i].toString());
			if (i != actualTypeArguments.length - 1) {
				builder.append(", ");
			}
		}
		builder.append('>');
		return builder.toString();
	}

}
