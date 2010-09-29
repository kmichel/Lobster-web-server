package info.kmichel.babel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FromConstructorBuilder<T> implements Builder<T> {

	private final Constructor<? extends T> constructor;

	public FromConstructorBuilder(final Constructor<? extends T> constructor) {
		this.constructor = constructor;
	}

	public T build(final ResultSet result) throws SQLException {
		try {
			return constructor.newInstance(result);	
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		}
	}

}
