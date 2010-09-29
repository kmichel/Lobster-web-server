package info.kmichel.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

class SimpleGenericArrayType implements GenericArrayType {

	private final Type genericComponentType; 

	SimpleGenericArrayType(final Type genericComponentType) {
		this.genericComponentType = genericComponentType;
	}

	public Type getGenericComponentType() {
		return genericComponentType;
	}

	public String toString() {
		return genericComponentType.toString()+"[]";
	}

}
