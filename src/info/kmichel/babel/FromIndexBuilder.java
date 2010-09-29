package info.kmichel.babel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FromIndexBuilder<T> implements Builder<T> {

	private final int index;
	private final Class<T> builtType;

	public FromIndexBuilder(final int index, final Class<T> builtType) {
		this.index = index;
		this.builtType = builtType;
	}

	public T build(final ResultSet result) throws SQLException {
		return builtType.cast(result.getObject(index));
	}

}
