package ru.shadam.ferry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark that method parameter should be passed as a request parameter
 *
 * <p><b>Example:</b>
 * <pre><code>
 *     Entity getOne(@Param("id") Long id);
 * </code></pre>
 *
 * @author sala
 * @see PathVariable
 * @see RequestBody
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String value();
}
