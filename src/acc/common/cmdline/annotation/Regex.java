package acc.common.cmdline.annotation;

import java.lang.annotation.*;

/**
 * Annotation for defining value validator.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Regex {
    String value();
}
