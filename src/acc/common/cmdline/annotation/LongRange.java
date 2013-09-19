package acc.common.cmdline.annotation;

import java.lang.annotation.*;

/**
 * Annotation for defining long validator.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface LongRange {
    long min() default Long.MIN_VALUE;

    long max() default Long.MAX_VALUE;
}
