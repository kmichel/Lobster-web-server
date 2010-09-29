package info.kmichel.reflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;

/**
 * Toolbox class for manipulating and handling types.
 *
 * @author Michel Kevin
 */
public class Types {

	Types()
			throws InstantiationException {
		throw new InstantiationException("Types is a static-only utility class");
	}

	/**
	 * Create a Type instance from a description string.
	 *
	 */
	public static Type forName(final String name) throws TypeNotFoundException {
		return TypeParser.parse(TypeToken.tokenize(name));
	}

	public static boolean isAssignableFrom(
			final Type destination,
			final Type source) {
		return Assignability.isAssignableFrom(destination, source, new HashMap<TypeVariable, Type>());
	}

}
