package info.kmichel.babel;

import java.sql.SQLException;
import java.util.Collection;

public interface BabelConnection {

	boolean isOpen();

	void open() throws SQLException;

	/**
	 * Add results to the destination collection, building them from ResultSet
	 * using the builder.
	 */
	<T> Collection<T> query(
			final Collection<T> destination,
			final Builder<T> builder,
			final String query,
			final Object... parameters);

	/**
	 * Returns the first built result, or null of no result.
	 */
	<T> T queryFirst(
			final Builder<T> builder,
			final String query,
			final Object... parameters);

	int update(
			final String statement,
			final Object... parameters);

	void close();

}
