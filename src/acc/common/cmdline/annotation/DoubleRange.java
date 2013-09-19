package acc.common.cmdline.annotation;

import java.lang.annotation.*;

/**
 * Annotation for defining double validator.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface DoubleRange {
    double min() default Double.MIN_VALUE;

    double max() default Double.MAX_VALUE;
}
