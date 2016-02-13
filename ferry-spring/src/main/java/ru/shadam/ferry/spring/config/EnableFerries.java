package ru.shadam.ferry.spring.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author sala
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(FerryRegistrar.class)
public @interface EnableFerries {
    String[] value() default {};
}
