package info.kmichel.babel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import javax.sql.DataSource;

public class SimpleConnection implements BabelConnection {

	private final DataSource dataSource;
	private Connection connection;

	public SimpleConnection(
			final DataSource dataSource) {
		this.dataSource = dataSource;
		connection = null;
	}

	public boolean isOpen() {
		return connection != null;
	}

	public void open() throws SQLException {
		connection = dataSource.getConnection();
	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (final SQLException e) {
				throw new BabelException(e);
			}
		}
	}

	public <T> Collection<T> query(
			final Collection<T> destination,
			final Builder<T> builder,
			final String query,
			final Object... parameters) {
		final boolean isOneshot = !isOpen();
		try {
			if (isOneshot) {
				open();
			}
			PreparedStatement statement = null;
			try {
				statement = connection.prepareStatement(query);
				bindParameters(statement, parameters);
				final ResultSet result = statement.executeQuery();
				while (result.next()) {
					destination.add(builder.build(result));
				}
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
			return destination;
		} catch (final SQLException e) {
			throw new BabelException(e);
		} finally {
			if (isOneshot) {
				close();
			}
		}
	}

	public <T> T queryFirst(
			final Builder<T> builder,
			final String query,
			final Object... parameters) {
		final boolean isOneshot = !isOpen();
		try {
			if (isOneshot) {
				open();
			}
			PreparedStatement statement = null;
			try {
				statement = connection.prepareStatement(query);
				bindParameters(statement, parameters);
				final ResultSet result = statement.executeQuery();
				if (result.next()) {
					return builder.build(result);
				} else {
					return null;
				}
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		} catch (final SQLException e) {
			throw new BabelException(e);
		} finally {
			if (isOneshot) {
				close();
			}
		}

	}

	public int update(
			final String update,
			final Object... parameters) {
		final boolean isOneshot = !isOpen();
		try {
			if (isOneshot) {
				open();
			}
			PreparedStatement statement = null;
			try {
				statement = connection.prepareStatement(update);
				bindParameters(statement, parameters);
				return statement.executeUpdate();
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		} catch (final SQLException e) {
			throw new BabelException(e);
		} finally {
			if (isOneshot) {
				close();
			}
		}
	}

	private static void bindParameters(
			final PreparedStatement statement,
			final Object... parameters)
			throws SQLException {
		int i=1;
		for (final Object parameter : parameters) {
			statement.setObject(i, parameter);
			i += 1;
		}
	}

}
