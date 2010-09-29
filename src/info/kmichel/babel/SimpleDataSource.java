package info.kmichel.babel;

import java.sql.SQLException;
import javax.sql.DataSource;

public class SimpleDataSource implements BabelDataSource {

	private final DataSource datasource;

	public SimpleDataSource(final DataSource datasource) {
		this.datasource = datasource;
	}

	public BabelConnection getConnection() {
		return new SimpleConnection(datasource);
	}

}
