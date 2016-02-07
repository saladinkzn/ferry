package ru.shadam.ferry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide request method to implementation generator.
 *
 * <pre><code>
 * &#64;RequestMethod("GET")
 * List&lt;Entity&gt; getAll();
 * </code></pre>
 *
 * @author sala
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMethod {
    String value();
}
