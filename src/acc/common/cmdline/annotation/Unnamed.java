package acc.common.cmdline.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for defining a name of a command or option.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Unnamed {
    String description() default "";
}
