package acc.common.cmdline.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for marking the method to display help text.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
}
