package info.kmichel.babel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FromLabelBuilder<T> implements Builder<T> {

	private final String label;
	private final Class<T> builtType;

	public FromLabelBuilder(final String label, final Class<T> builtType) {
		this.label = label;
		this.builtType = builtType;
	}

	public T build(final ResultSet result) throws SQLException {
		try {
			return builtType.cast(result.getObject(label));
		} catch (final ClassCastException e) {
			// Since the objet type can be hard to predict (it comes from the
			// database), we improve the message to allow easier debugging.
			throw new ClassCastException("Unable to assign field "+label+" of type "
				+ result.getObject(label).getClass()+" to type "+builtType);
		}
	}

}
