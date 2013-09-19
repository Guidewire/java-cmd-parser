package acc.common.cmdline.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for marking the method to be a default command.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultCommand {
}
