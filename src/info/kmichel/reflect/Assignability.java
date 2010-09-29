package info.kmichel.reflect;

import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Assignability {

	Assignability()
			throws InstantiationException {
		throw new InstantiationException("Assignability is a static-only utility class");
	}

	public static boolean isAssignableFrom(
			final Type destination,
			final Type source,
			final Map<TypeVariable, Type> typeEnvironment) {
		if (destination == null) {
			throw new IllegalArgumentException("destination may not be null");
		}
		if (source == null) {
			throw new IllegalArgumentException("source may not be null");
		}
		if (typeEnvironment == null) {
			throw new IllegalArgumentException("type environment may not be null");
		}
		return isAssignableFrom(
			destination,
			source,
			true,
			typeEnvironment);
	}

	private static boolean isAssignableFrom(
			final Type destination,
			final Type source,
			final boolean allowWidening,
			final Map<TypeVariable, Type> typeEnvironment) {
		if (destination instanceof TypeVariable) {
			return isAssignableFrom(
				resolveVariables(destination, typeEnvironment),
				source,
				allowWidening,
				typeEnvironment);
		} else if (source instanceof TypeVariable) {
			return isAssignableFrom(
				destination,
				resolveVariables(source, typeEnvironment),
				allowWidening,
				typeEnvironment);
		} else if (destination instanceof GenericArrayType){
			if (source instanceof GenericArrayType) {
				return isAssignableFrom(
					((GenericArrayType)destination).getGenericComponentType(),
					((GenericArrayType)source).getGenericComponentType(),
					false,
					typeEnvironment);
			} else {
				return false;
			}
		} else if (source instanceof GenericArrayType) {
			return false;
		} else if (destination instanceof WildcardType) {
			final WildcardType wildcardDestination = WildcardType.class.cast(destination);
			if (source instanceof WildcardType) {
				final WildcardType wildcardSource = WildcardType.class.cast(source);
				for (final Type upperDestType: wildcardDestination.getUpperBounds()) {
					boolean any = false;
					for (final Type upperSourceType: wildcardSource.getUpperBounds()) {
						if (isAssignableFrom(upperDestType, upperSourceType, true, typeEnvironment)) {
							any = true;
							break;
						}
					}
					if (!any) {
						return false;
					}
				}
				return true;
			} else {
				for (final Type upperType: wildcardDestination.getUpperBounds()) {
					if (!isAssignableFrom(upperType, source, true, typeEnvironment)) {
						return false;
					}
				}
				if (!allowWidening) {
					for (final Type lowerType: wildcardDestination.getLowerBounds()) {
						if (!isAssignableFrom(source, lowerType, true, typeEnvironment)) {
							return false;
						}
					}
				}
				return true;
			}
		} else if (source instanceof WildcardType) {
			final WildcardType wildcardSource = WildcardType.class.cast(source);
			for (final Type upperType: wildcardSource.getUpperBounds()) {
				if (isAssignableFrom(destination, upperType, true, typeEnvironment)) {
					return true;
				}
			}
			// If the Wildcard has no valid upper bound, we can still be valid if
			// the destination allows any object, thus the test against Object.
			// We don't check lower bounds since they are constraining nothing when
			// used on a source type.
			return isAssignableFrom(destination, Object.class, true, typeEnvironment);
		} else {
			return isAssignableFrom(
				parameterize(destination),
				parameterize(source),
				allowWidening,
				typeEnvironment);
		}
	}

	private static boolean isAssignableFrom(
			final ParameterizedType destination,
			final ParameterizedType source,
			final boolean allowWidening,
			final Map<TypeVariable, Type> typeEnvironment) {
		final Class rawDestination = getRawType(destination);
		final Queue<ParameterizedType> supers = new LinkedList<ParameterizedType>();
		supers.add(source);
		while (!supers.isEmpty()) {
			final ParameterizedType current = supers.remove();
			final Class rawCurrent = getRawType(current);
			if (rawCurrent.equals(rawDestination)) {
				// Note that on type parameter comparison failure you
				// are guaranteed that this raw type won't appear again in the
				// supertypes hierarchy because of type erasure (a type can't
				// implement an parameterized interface twice with different
				// parameters since those parameters disappear on runtime).
				final Type[] destinationParams = destination.getActualTypeArguments();
				final Type[] currentParams = current.getActualTypeArguments();
				for (int i=0; i<currentParams.length; ++i) {
					if (!isAssignableFrom(destinationParams[i], currentParams[i], false, typeEnvironment)) {
						return false;
					}
				}
				return true;
			} else if (allowWidening) {
				final Type[] currentParams = current.getActualTypeArguments();
				final TypeVariable[] formalParams = rawCurrent.getTypeParameters();
				for (int i=0; i<currentParams.length; ++i) {
					typeEnvironment.put(formalParams[i], currentParams[i]);
				}
				final Type superclass = rawCurrent.getGenericSuperclass();
				if (superclass != null) {
					supers.add(parameterize(superclass));
				}
				for (final Type superinterface: rawCurrent.getGenericInterfaces()) {
					supers.add(parameterize(superinterface));
				}
			}
		}
		return false;
	}

	private static Class getRawType(final ParameterizedType type) {
		return Class.class.cast(type.getRawType());
	}

	private static ParameterizedType parameterize(final Type type) {
		if (type instanceof ParameterizedType) {
			return ParameterizedType.class.cast(type);
		} else if (type instanceof Class) {
			return new SimpleParameterizedType(Class.class.cast(type));
		} else {
			throw new RuntimeException("unexpected type: "+type);
		}
	}

	private static Type resolveVariables(
			final Type type,
			final Map<TypeVariable, Type> environment) {
		Type currentType = type;
		while (currentType instanceof TypeVariable) {
			final TypeVariable typeVariable = TypeVariable.class.cast(currentType);
			final Type typeValue = environment.get(typeVariable);
			if (typeValue == null) {
				throw new RuntimeException("failed to resolve type variable");
			}
			currentType = typeValue;
		}
		return currentType;
	}

}
