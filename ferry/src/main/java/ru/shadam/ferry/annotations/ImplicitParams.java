package ru.shadam.ferry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Collection annotation for {@link ImplicitParam}
 *
 * @author sala
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplicitParams {
    ImplicitParam[] value();
}
