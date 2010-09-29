package info.kmichel.babel;

public class Babel {

	public static <T> Builder<T> fromField(
			final String fieldLabel,
			final Class<T> builtType) {
		return new FromLabelBuilder<T>(fieldLabel, builtType);
	}

	public static <T> Builder<T> fromField(
			final int fieldIndex,
			final Class<T> builtType) {
		return new FromIndexBuilder<T>(fieldIndex, builtType);
	}

	public static <T> Builder<T> fromRow(
			final Class<T> intface) {
		final InterfaceImplementer implementer = new InterfaceImplementer();
		return new FromConstructorBuilder<T>(implementer.implement(intface));
	}

}
