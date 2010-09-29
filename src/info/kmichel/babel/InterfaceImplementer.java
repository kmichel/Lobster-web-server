package info.kmichel.babel;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;

public class InterfaceImplementer {

	private static class MyClassLoader extends ClassLoader {
		public Class defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
	}

	private final MyClassLoader classLoader;

	public InterfaceImplementer() {
		this.classLoader = new MyClassLoader();
	}

	/**
	 * Create a class implementing the interface {@code intface} with
	 * a constructor taking a ResultSet.
	 */
	@SuppressWarnings("unchecked")
	public <T> Constructor<? extends T> implement(final Class<T> intface) {
		final String internalName = intface.getName().replace('.', '/');
		final String implName = internalName+"Babel";
		final Map<String, String> fields = new HashMap<String, String>();
		final ClassWriter writer = new ClassWriter(0);
		writer.visit(V1_5, ACC_PUBLIC, implName, null, "java/lang/Object", new String[] {internalName} );
		for (final Method method: intface.getMethods()) {
			buildGetter(writer, method, implName, fields);
		}
		buildConstructor(writer, implName, fields);
		writer.visitEnd();
		final Class<? extends T> implementation = classLoader.defineClass(intface.getName()+"Babel", writer.toByteArray());
		try {
			return implementation.getConstructor(ResultSet.class);
		} catch (final NoSuchMethodException e) {
			// Can't happen since we just created this constructor
			throw new RuntimeException(e);
		}
	}

	private void buildGetter(
			final ClassWriter writer,
			final Method method,
			final String className,
			final Map<String, String> fields) {
		final String fieldName;
		if (method.getParameterTypes().length > 0) {
			throw new IllegalArgumentException("Don't know how to implement "+method);
		}
		if (method.isAnnotationPresent(Field.class)) {
			fieldName = method.getAnnotation(Field.class).value();
		} else if (method.getName().matches("get[A-Z][a-zA-Z]*")) {
			fieldName = method.getName().substring(3).replaceAll("(.)([A-Z][a-z])", "$1_$2").toLowerCase(Locale.ROOT);
		} else {
			throw new IllegalArgumentException("Don't know how to implement "+method);
		}
		final Type type = Type.getReturnType(method);
		final String fieldType = type.getDescriptor();
		writer.visitField(
			ACC_PRIVATE + ACC_FINAL, fieldName, fieldType, null, null).visitEnd();
		final MethodVisitor methodVisitor = writer.visitMethod(
			ACC_PUBLIC, method.getName(), "()"+fieldType, null, null);
		// the method implementation: just return the field value
		methodVisitor.visitCode();
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitFieldInsn(GETFIELD, className, fieldName, fieldType);
		methodVisitor.visitInsn(type.getOpcode(IRETURN));
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();
		// Store the field in the list needed for the constructor
		fields.put(fieldName, fieldType);
	}

	private void buildConstructor(
			final ClassWriter writer,
			final String className,
			final Map<String, String> fields) {
		final MethodVisitor constructor = writer.visitMethod(
			ACC_PUBLIC, "<init>", "(Ljava/sql/ResultSet;)V", null, new String[] { "java/sql/SQLException"} );
		constructor.visitCode();
		// Call to super
		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		// Initialize all fields
		for (final Map.Entry<String, String> field: fields.entrySet()) {
			// Push this, for the PUTFIELD done later
			constructor.visitVarInsn(ALOAD, 0);
			// Push the resultset and the field name
			constructor.visitVarInsn(ALOAD, 1);
			constructor.visitLdcInsn(field.getKey());
			// Call ResultSet.getXXXX which pops 2 and push field value
			switch (field.getValue().charAt(0)) {
				case 'B':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getByte",
						"(Ljava/lang/String;)B");
					break;
				case 'C':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getChar",
						"(Ljava/lang/String;)C");
					break;
				case 'D':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getDouble",
						"(Ljava/lang/String;)D");
					break;
				case 'F':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getFloat",
						"(Ljava/lang/String;)F");
					break;
				case 'I':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getInt",
						"(Ljava/lang/String;)I");
					break;
				case 'J':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getLong",
						"(Ljava/lang/String;)J");
					break;
				case 'L':
					// We could use special getter for Date, BigDecimal, Blob, Clob...
					final String fieldType = field.getValue().substring(1, field.getValue().length()-1);
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getObject",
						"(Ljava/lang/String;)Ljava/lang/Object;");
					constructor.visitTypeInsn(CHECKCAST, fieldType);
					break;
				case 'S':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getShort",
						"(Ljava/lang/String;)S");
					break;
				case 'Z':
					constructor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getBoolean",
						"(Ljava/lang/String;)Z");
					break;
				default:
					throw new IllegalArgumentException("Don't know how to handle field "+field.getKey()+
						" of type "+field.getValue());
			}
			// Pop the field value, write it to the field
			constructor.visitFieldInsn(PUTFIELD, className, field.getKey(), field.getValue());
		}
		constructor.visitInsn(RETURN);
		constructor.visitMaxs(3, 2);
		constructor.visitEnd();
	}

}
