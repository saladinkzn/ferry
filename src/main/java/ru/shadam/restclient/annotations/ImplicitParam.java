package ru.shadam.restclient.annotations;

import java.lang.annotation.*;

/**
 * @author sala
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplicitParam {
    String paramName();

    String constValue() default "";

    String providerName() default "";
}
