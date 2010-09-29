package info.kmichel.babel;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Builder<T> {

	T build(final ResultSet result) throws SQLException;

}
