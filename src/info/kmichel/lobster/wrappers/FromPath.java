package info.kmichel.lobster.wrappers;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromPath {

	int value() default 0;

}
