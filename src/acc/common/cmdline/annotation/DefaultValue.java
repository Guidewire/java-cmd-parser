package acc.common.cmdline.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for defining a default value for a parameter.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
    String value();
}
