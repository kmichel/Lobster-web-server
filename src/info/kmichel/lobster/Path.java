package info.kmichel.lobster;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

	/**
	 * The regular expression {@link java.util.regex.Pattern} to be used for matching
	 * request paths, defaults to {@code (.*)}.
	 */
	String value() default "(.*)";

}
