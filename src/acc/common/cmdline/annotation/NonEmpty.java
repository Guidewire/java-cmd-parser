package acc.common.cmdline.annotation;

import java.lang.annotation.*;

/**
 * Annotation for marking the method to display help text.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface NonEmpty {
}
