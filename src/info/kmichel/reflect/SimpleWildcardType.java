package info.kmichel.reflect;

import java.lang.reflect.WildcardType;
import java.lang.reflect.Type;

class SimpleWildcardType implements WildcardType {

	private final Type[] upperBounds;
	private final Type[] lowerBounds;

	SimpleWildcardType(final Type[] upperBounds, final Type[] lowerBounds) {
		this.upperBounds = upperBounds;
		this.lowerBounds = lowerBounds;
	}

	public Type[] getUpperBounds() {
		return upperBounds;
	}

	public Type[] getLowerBounds() {
		return lowerBounds;
	}

	public String toString() {
		final StringBuilder builder = new StringBuilder();
		if (this.upperBounds.length > 0) {
			builder.append("? extends ");
			for (int i=0; i<upperBounds.length-1; ++i) {
				builder.append(upperBounds[i].toString());
				builder.append(" & ");
			}
			builder.append(upperBounds[upperBounds.length - 1].toString());
		} else if (this.lowerBounds.length > 0) {
			builder.append("? super ");
			for (int i=0; i<lowerBounds.length-1; ++i) {
				builder.append(lowerBounds[i].toString());
				builder.append(" & ");
			}
			builder.append(lowerBounds[lowerBounds.length - 1].toString());
		} else {
			builder.append("?");
		}
		return builder.toString();
	}

}
