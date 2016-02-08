package ru.shadam.ferry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark that method parameter should be passed as a path variable.
 * <p>Path variable syntax: :<em>paramName</em>
 *
 * <p><b>Example:</b>
 * <pre><code>
 *     &#064;Url("http://example.com/api/entities/:id")
 *     public Entity getOne(&#064;PathVariable("id") Long id);
 * </code></pre>
 *
 * @author sala
 * @see Url
 * @see Param
 * @see RequestBody
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value();
}
