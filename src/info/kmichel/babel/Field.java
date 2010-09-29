package info.kmichel.babel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@code Field} annotation is used to override the field name which is
 * otherwise guessed from the method name (which must look like a standard
 * getter, for instance {@code getFoo} will be mapped to the field name
 * {@code foo}).
 *
 * @author Michel Kevin
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

	String value();

}
