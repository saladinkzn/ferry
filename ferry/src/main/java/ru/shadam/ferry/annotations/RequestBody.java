package ru.shadam.ferry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark that method parameter should be passed as a request body to remote api
 *
 * <p><b>Example:</b>
 * <pre><code>
 *     public void testMethod(@RequestBody String body);
 * </code></pre>
 *
 * @author sala
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
}
