package ru.shadam.ferry.annotations;

import java.lang.annotation.*;

/**
 * @author sala
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplicitParam {
    String paramName();

    String constValue() default "";

    String providerName() default "";
}
