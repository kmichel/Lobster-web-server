package info.kmichel.babel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An {@code Update} annotation specifies the SQL prepared statement to execute for a given
 * method.
 *
 * @see java.sql.PreparedStatement
 *
 * @author Michel Kevin
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {

	String value();

}
