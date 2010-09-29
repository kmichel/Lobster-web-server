package info.kmichel.lobster.wrappers;

public abstract class TypeWrappers {
	
	private TypeWrappers() {}

	public static Integer toInt(final String value, final ToInt annotation) {
		if (value == null) {
			return annotation.ifNull();
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int toIntNative(final String value, final ToInt annotation) {
		if (value == null) {
			return annotation.ifNull();
		} else {
			return Integer.parseInt(value);
		}
	}

}
